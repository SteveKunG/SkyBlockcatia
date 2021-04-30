package com.stevekung.skyblockcatia.event.handler;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.gui.widget.button.ItemButton;
import com.stevekung.skyblockcatia.gui.widget.button.SmallArrowButton;
import com.stevekung.skyblockcatia.mixin.AccessorScreen;
import com.stevekung.skyblockcatia.mixin.InvokerAbstractContainerScreen;
import com.stevekung.skyblockcatia.mixin.InvokerTitleScreen;
import com.stevekung.skyblockcatia.utils.GuiScreenUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils.APIUrl;
import com.stevekung.skyblockcatia.utils.skyblock.api.BazaarData;
import com.stevekung.stevekungslib.utils.CalendarUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import me.shedaniel.architectury.event.events.GuiEvent;
import me.shedaniel.architectury.event.events.client.ClientTickEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class MainEventHandler
{
    private final Minecraft mc;
    public static String auctionPrice = "";
    public static boolean showChat;
    public static String playerToView;
    public static final Map<String, BazaarData> BAZAAR_DATA = Maps.newHashMap();
    public static boolean bidHighlight = true;
    private static boolean showAdditionalButtons;
    public static int rainbowTicks;

    public MainEventHandler()
    {
        this.mc = Minecraft.getInstance();
        ClientTickEvent.CLIENT_PRE.register(this::onClientTick);
        GuiEvent.INIT_POST.register(this::onInitGui);
        GuiEvent.SET_SCREEN.register(this::onGuiOpen);
        GuiEvent.RENDER_POST.register(this::onPostGuiDrawScreen);
    }

    public void onClientTick(Minecraft mc)
    {
        if (this.mc.screen instanceof TitleScreen)
        {
            rainbowTicks = 0;
        }
        rainbowTicks += 5;
    }

    public void onInitGui(Screen screen, List<AbstractWidget> widgets, List<GuiEventListener> children)
    {
        int width = screen.width / 2;
        int height = screen.height / 2 - 106;

        if (SkyBlockEventHandler.isSkyBlock)
        {
            ItemStack wardRobeItem = new ItemStack(Items.LEATHER_CHESTPLATE);
            ((DyeableArmorItem)wardRobeItem.getItem()).setColor(wardRobeItem, 8339378);

            if (SkyBlockcatiaSettings.INSTANCE.shortcutButtonInInventory && screen instanceof InventoryScreen)
            {
                this.addButtonsToInventory(screen, wardRobeItem, width, height);
            }
            else if (screen instanceof ContainerScreen)
            {
                ContainerScreen chest = (ContainerScreen)screen;
                Component title = chest.getTitle();

                if (SkyBlockcatiaSettings.INSTANCE.shortcutButtonInInventory)
                {
                    ItemStack skyBlockMenu = new ItemStack(Items.NETHER_STAR);
                    ListTag list = new ListTag();
                    skyBlockMenu.setHoverName(TextComponentUtils.component("SkyBlock Menu"));
                    list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.GRAY + "View all of your SkyBlock")));
                    skyBlockMenu.getTag().getCompound("display").put("Lore", list);

                    if (GuiScreenUtils.isChatable(title))
                    {
                        String chat = MainEventHandler.showChat ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF";

                        ((AccessorScreen)screen).invokeAddButton(new ItemButton(2, screen.height - 35, Items.ENDER_EYE, TextComponentUtils.component("Toggle Inventory Chat: " + chat), button ->
                        {
                            MainEventHandler.showChat = !MainEventHandler.showChat;
                            ((ItemButton)button).setName(TextComponentUtils.component("Toggle Inventory Chat: " + (MainEventHandler.showChat ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF")));
                        }));
                    }

                    if (GuiScreenUtils.contains(GuiScreenUtils.INVENTORY, title) && !title.getString().startsWith("Ender Chest"))
                    {
                        ((AccessorScreen)screen).invokeAddButton(new ItemButton(width + 88, height + 47, Blocks.CRAFTING_TABLE, button -> this.mc.player.chat("/craft")));
                        ((AccessorScreen)screen).invokeAddButton(new ItemButton(width + 88, height + 66, Blocks.ENDER_CHEST, button -> this.mc.player.chat("/enderchest")));
                        ((AccessorScreen)screen).invokeAddButton(new ItemButton(width + 88, height + 85, skyBlockMenu, button -> this.mc.player.chat("/sbmenu")));
                    }
                    else if (title.getString().equals("Craft Item"))
                    {
                        ((AccessorScreen)screen).invokeAddButton(new ItemButton(width + 88, height + 47, Blocks.ENDER_CHEST, button -> this.mc.player.chat("/enderchest")));
                        ((AccessorScreen)screen).invokeAddButton(new ItemButton(width + 88, height + 66, skyBlockMenu, button -> this.mc.player.chat("/sbmenu")));
                    }
                    else if (title.getString().startsWith("Ender Chest"))
                    {
                        ((AccessorScreen)screen).invokeAddButton(new ItemButton(width + 88, height + 47, Blocks.CRAFTING_TABLE, button -> this.mc.player.chat("/craft")));
                        ((AccessorScreen)screen).invokeAddButton(new ItemButton(width + 88, height + 66, skyBlockMenu, button -> this.mc.player.chat("/sbmenu")));
                    }
                    else if (title.getString().contains("Wardrobe"))
                    {
                        ((AccessorScreen)screen).invokeAddButton(new ItemButton(width + 88, height + 47, Items.BONE, TextComponentUtils.component("Pets"), button -> this.mc.player.chat("/pets")));
                    }
                    else if (title.getString().contains("Pets"))
                    {
                        ((AccessorScreen)screen).invokeAddButton(new ItemButton(width + 88, height + 47, wardRobeItem, TextComponentUtils.component("Wardrobe"), button -> this.mc.player.chat("/wardrobe")));
                    }
                }

                if (GuiScreenUtils.isAuctionBrowser(title.getString()))
                {
                    String bid = MainEventHandler.bidHighlight ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF";
                    ((AccessorScreen)screen).invokeAddButton(new ItemButton(width + 89, GuiScreenUtils.isOtherAuction(title.getString()) ? ((InvokerAbstractContainerScreen)chest).getTopPos() + 4 : height + 60, Blocks.REDSTONE_BLOCK, TextComponentUtils.component("Toggle Bid Highlight: " + bid), button ->
                    {
                        MainEventHandler.bidHighlight = !MainEventHandler.bidHighlight;
                        ((ItemButton)button).setName(TextComponentUtils.component("Toggle Bid Highlight: " + (MainEventHandler.bidHighlight ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF")));
                    }));
                }
                else if (title.getString().contains("Hub Selector"))
                {
                    String overlay = SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF";
                    ((AccessorScreen)screen).invokeAddButton(new ItemButton(width + 89, height + 29, Items.COMPASS, TextComponentUtils.component("Lobby Player Overlay: " + overlay), button ->
                    {
                        SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer = !SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer;
                        ((ItemButton)button).setName(TextComponentUtils.component("Lobby Player Overlay: " + (SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF")));
                        SkyBlockcatiaSettings.INSTANCE.save();
                    }));
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void onPostGuiDrawScreen(Screen screen, PoseStack poseStack, int mouseX, int mouseY, float delta)
    {
        if (screen != null)
        {
            for (ItemButton button : ((AccessorScreen)screen).getButtons().stream().filter(button -> button instanceof ItemButton).map(button -> (ItemButton)button).collect(Collectors.toList()))
            {
                boolean hover = mouseX >= button.x && mouseY >= button.y && mouseX < button.x + button.getWidth() && mouseY < button.y + button.getHeight();

                if (hover && button.visible)
                {
                    screen.renderTooltip(poseStack, button.getName(), mouseX, mouseY);
                    RenderSystem.disableLighting();
                    break;
                }
            }
        }
    }

    public InteractionResultHolder<Screen> onGuiOpen(Screen screen)
    {
        if (screen instanceof TitleScreen)
        {
            TitleScreen menu = (TitleScreen)screen;

            if (CalendarUtils.isMyBirthDay())
            {
                ((InvokerTitleScreen)menu).setSplash("Happy birthday, SteveKunG!");
            }
        }
        return InteractionResultHolder.pass(screen);
    }

    public static void getBazaarData()
    {
        if (!SkyBlockcatiaSettings.INSTANCE.bazaarOnItemTooltip)
        {
            return;
        }

        try
        {
            URL url = new URL(APIUrl.BAZAAR.getUrl());
            JsonObject obj = new JsonParser().parse(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
            JsonElement lastUpdated = obj.get("lastUpdated");

            for (Map.Entry<String, JsonElement> product : obj.get("products").getAsJsonObject().entrySet())
            {
                String productName = product.getKey();
                JsonElement currentProduct = product.getValue();
                JsonElement quickStatus = currentProduct.getAsJsonObject().get("quick_status");
                JsonElement buyPrice = quickStatus.getAsJsonObject().get("buyPrice");
                JsonElement sellPrice = quickStatus.getAsJsonObject().get("sellPrice");
                JsonArray buyArray = currentProduct.getAsJsonObject().get("buy_summary").getAsJsonArray();
                JsonArray sellArray = currentProduct.getAsJsonObject().get("sell_summary").getAsJsonArray();

                if (sellArray.size() == 0 && buyArray.size() == 0 || sellArray.size() == 0 || buyArray.size() == 0)
                {
                    BAZAAR_DATA.put(productName, new BazaarData(lastUpdated.getAsLong(), new BazaarData.Product(buyPrice.getAsDouble(), sellPrice.getAsDouble())));
                }
                else
                {
                    BAZAAR_DATA.put(productName, new BazaarData(lastUpdated.getAsLong(), new BazaarData.Product(buyArray.get(0).getAsJsonObject().get("pricePerUnit").getAsDouble(), sellArray.get(0).getAsJsonObject().get("pricePerUnit").getAsDouble())));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void addButtonsToInventory(Screen screen, ItemStack wardRobeItem, int width, int height)
    {
        ((AccessorScreen)screen).invokeAddButton(new ItemButton(width - 9, height + 86, Blocks.ENDER_CHEST, button -> this.mc.player.chat("/enderchest")));
        ((AccessorScreen)screen).invokeAddButton(new ItemButton(width + 10, height + 86, Blocks.CRAFTING_TABLE, button -> this.mc.player.chat("/craft")));
        ((AccessorScreen)screen).invokeAddButton(new ItemButton(width + 29, height + 86, Items.BONE, TextComponentUtils.component("Pets"), button -> this.mc.player.chat("/pets")));
        ((AccessorScreen)screen).invokeAddButton(new ItemButton(width + 48, height + 86, wardRobeItem, TextComponentUtils.component("Wardrobe"), button -> this.mc.player.chat("/wardrobe")));
        ((AccessorScreen)screen).invokeAddButton(new SmallArrowButton(width + 72, height + 90, button -> this.changeButtonPage(screen, wardRobeItem, width, height)));

        ItemButton item = new ItemButton(width + 88, height + 86, new ItemStack(Items.GOLDEN_HORSE_ARMOR), TextComponentUtils.component("Auction House"), button -> this.mc.player.chat("/ah"));
        item.visible = showAdditionalButtons;
        ((AccessorScreen)screen).invokeAddButton(item);
        item = new ItemButton(width + 88, height + 105, new ItemStack(Blocks.GOLD_ORE), TextComponentUtils.component("Bazaar"), button -> this.mc.player.chat("/bz"));
        item.visible = showAdditionalButtons;
        ((AccessorScreen)screen).invokeAddButton(item);
        item = new ItemButton(width + 107, height + 86, new ItemStack(Blocks.ENCHANTING_TABLE), TextComponentUtils.component("Enchanting"), button -> this.mc.player.chat("/et"));
        item.visible = showAdditionalButtons;
        ((AccessorScreen)screen).invokeAddButton(item);
        item = new ItemButton(width + 107, height + 105, new ItemStack(Blocks.ANVIL), TextComponentUtils.component("Anvil"), button -> this.mc.player.chat("/av"));
        item.visible = showAdditionalButtons;
        ((AccessorScreen)screen).invokeAddButton(item);
    }

    private void changeButtonPage(Screen screen, ItemStack wardRobeItem, int width, int height)
    {
        showAdditionalButtons = !showAdditionalButtons;
        ((AccessorScreen)screen).getButtons().removeIf(widget -> widget instanceof ItemButton || widget instanceof SmallArrowButton);
        screen.children().removeIf(widget -> widget instanceof ItemButton || widget instanceof SmallArrowButton);
        this.addButtonsToInventory(screen, wardRobeItem, width, height);
    }
}