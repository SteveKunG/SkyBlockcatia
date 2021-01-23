package com.stevekung.skyblockcatia.event.handler;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.gui.screen.SkyBlockProfileSelectorScreen;
import com.stevekung.skyblockcatia.gui.widget.button.ItemButton;
import com.stevekung.skyblockcatia.gui.widget.button.SmallArrowButton;
import com.stevekung.skyblockcatia.utils.GuiScreenUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils.APIUrl;
import com.stevekung.skyblockcatia.utils.skyblock.SBFakePlayerEntity;
import com.stevekung.skyblockcatia.utils.skyblock.api.BazaarData;
import com.stevekung.stevekungslib.utils.CalendarUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiUtils;

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
    }

    @SubscribeEvent
    public void onRenderNameplate(RenderNameplateEvent event)
    {
        if (event.getEntity() instanceof SBFakePlayerEntity)
        {
            event.setResult(Result.DENY);
        }
    }

    @SubscribeEvent
    public void onRenderTooltipEvent(RenderTooltipEvent.Pre event)
    {
        if (SkyBlockEventHandler.isSkyBlock && TextFormatting.getTextWithoutFormattingCodes(event.getLines().get(0).getString()).equals(" "))
        {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (this.mc.currentScreen instanceof MainMenuScreen)
        {
            rainbowTicks = 0;
        }
        if (event.phase == Phase.START)
        {
            rainbowTicks += 5;
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            if (MainEventHandler.playerToView != null)
            {
                this.mc.displayGuiScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.PLAYER, MainEventHandler.playerToView, "", ""));
                MainEventHandler.playerToView = null;
            }
        }
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event)
    {
        Screen gui = event.getGui();
        int width = gui.width / 2;
        int height = gui.height / 2 - 106;

        if (SkyBlockEventHandler.isSkyBlock)
        {
            ItemStack wardRobeItem = new ItemStack(Items.LEATHER_CHESTPLATE);
            ((DyeableArmorItem)wardRobeItem.getItem()).setColor(wardRobeItem, 8339378);

            if (SkyBlockcatiaSettings.INSTANCE.shortcutButtonInInventory && gui instanceof InventoryScreen)
            {
                event.removeWidget(event.getWidgetList().get(0));
                this.addButtonsToInventory(event, wardRobeItem, width, height);
            }
            else if (gui instanceof ChestScreen)
            {
                ChestScreen chest = (ChestScreen)gui;
                ITextComponent title = chest.getTitle();

                if (SkyBlockcatiaSettings.INSTANCE.shortcutButtonInInventory)
                {
                    ItemStack skyBlockMenu = new ItemStack(Items.NETHER_STAR);
                    ListNBT list = new ListNBT();
                    skyBlockMenu.setDisplayName(TextComponentUtils.component("SkyBlock Menu"));
                    list.add(StringNBT.valueOf(TextComponentUtils.toJson(TextFormatting.GRAY + "View all of your SkyBlock")));
                    skyBlockMenu.getTag().getCompound("display").put("Lore", list);

                    if (GuiScreenUtils.isChatable(title))
                    {
                        String chat = MainEventHandler.showChat ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF";

                        event.addWidget(new ItemButton(2, event.getGui().height - 35, Items.ENDER_EYE, TextComponentUtils.component("Toggle Inventory Chat: " + chat), button ->
                        {
                            MainEventHandler.showChat = !MainEventHandler.showChat;
                            ((ItemButton)button).setName(TextComponentUtils.component("Toggle Inventory Chat: " + (MainEventHandler.showChat ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF")));
                        }));
                    }

                    if (GuiScreenUtils.contains(GuiScreenUtils.INVENTORY, title) && !title.getString().startsWith("Ender Chest"))
                    {
                        event.addWidget(new ItemButton(width + 88, height + 47, Blocks.CRAFTING_TABLE, button -> this.mc.player.sendChatMessage("/craft")));
                        event.addWidget(new ItemButton(width + 88, height + 66, Blocks.ENDER_CHEST, button -> this.mc.player.sendChatMessage("/enderchest")));
                        event.addWidget(new ItemButton(width + 88, height + 85, skyBlockMenu, button -> this.mc.player.sendChatMessage("/sbmenu")));
                    }
                    else if (title.getString().equals("Craft Item"))
                    {
                        event.addWidget(new ItemButton(width + 88, height + 47, Blocks.ENDER_CHEST, button -> this.mc.player.sendChatMessage("/enderchest")));
                        event.addWidget(new ItemButton(width + 88, height + 66, skyBlockMenu, button -> this.mc.player.sendChatMessage("/sbmenu")));
                    }
                    else if (title.getString().startsWith("Ender Chest"))
                    {
                        event.addWidget(new ItemButton(width + 88, height + 47, Blocks.CRAFTING_TABLE, button -> this.mc.player.sendChatMessage("/craft")));
                        event.addWidget(new ItemButton(width + 88, height + 66, skyBlockMenu, button -> this.mc.player.sendChatMessage("/sbmenu")));
                    }
                    else if (title.getString().contains("Wardrobe"))
                    {
                        event.addWidget(new ItemButton(width + 88, height + 47, Items.BONE, TextComponentUtils.component("Pets"), button -> this.mc.player.sendChatMessage("/pets")));
                    }
                    else if (title.getString().contains("Pets"))
                    {
                        event.addWidget(new ItemButton(width + 88, height + 47, wardRobeItem, TextComponentUtils.component("Wardrobe"), button -> this.mc.player.sendChatMessage("/wardrobe")));
                    }
                }

                if (GuiScreenUtils.isAuctionBrowser(title.getString()))
                {
                    String bid = MainEventHandler.bidHighlight ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF";
                    event.addWidget(new ItemButton(width + 89, GuiScreenUtils.isOtherAuction(title.getString()) ? chest.getGuiTop() + 4 : height + 60, Blocks.REDSTONE_BLOCK, TextComponentUtils.component("Toggle Bid Highlight: " + bid), button ->
                    {
                        MainEventHandler.bidHighlight = !MainEventHandler.bidHighlight;
                        ((ItemButton)button).setName(TextComponentUtils.component("Toggle Bid Highlight: " + (MainEventHandler.bidHighlight ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF")));
                    }));
                }
                else if (title.getString().contains("Hub Selector"))
                {
                    String overlay = SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF";
                    event.addWidget(new ItemButton(width + 89, height + 29, Items.COMPASS, TextComponentUtils.component("Lobby Player Overlay: " + overlay), button ->
                    {
                        SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer = !SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer;
                        ((ItemButton)button).setName(TextComponentUtils.component("Lobby Player Overlay: " + (SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer ? TextFormatting.GREEN + "ON" : TextFormatting.RED + "OFF")));
                        SkyBlockcatiaSettings.INSTANCE.save();
                    }));
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public void onPostGuiDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        Screen gui = event.getGui();

        for (ItemButton button : gui.buttons.stream().filter(button -> button != null && button instanceof ItemButton).map(button -> (ItemButton)button).collect(Collectors.toList()))
        {
            boolean hover = event.getMouseX() >= button.x && event.getMouseY() >= button.y && event.getMouseX() < button.x + button.getWidth() && event.getMouseY() < button.y + button.getHeightRealms();

            if (hover && button.visible)
            {
                GuiUtils.drawHoveringText(event.getMatrixStack(), Collections.singletonList(button.getName()), event.getMouseX(), event.getMouseY(), gui.width, gui.height, -1, this.mc.fontRenderer);
                RenderSystem.disableLighting();
                break;
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event)
    {
        if (event.getGui() instanceof MainMenuScreen)
        {
            MainMenuScreen menu = (MainMenuScreen)event.getGui();

            if (CalendarUtils.isMyBirthDay())
            {
                menu.splashText = "Happy birthday, SteveKunG!";
            }
        }
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

    private void addButtonsToInventory(GuiScreenEvent.InitGuiEvent.Post event, ItemStack wardRobeItem, int width, int height)
    {
        //        if (inventoryPage == 0)TODO
        {
            event.addWidget(new ItemButton(width - 9, height + 86, Blocks.ENDER_CHEST, button -> this.mc.player.sendChatMessage("/enderchest")));
            event.addWidget(new ItemButton(width + 10, height + 86, Blocks.CRAFTING_TABLE, button -> this.mc.player.sendChatMessage("/craft")));
            event.addWidget(new ItemButton(width + 29, height + 86, Items.BONE, TextComponentUtils.component("Pets"), button -> this.mc.player.sendChatMessage("/pets")));
            event.addWidget(new ItemButton(width + 48, height + 86, wardRobeItem, TextComponentUtils.component("Wardrobe"), button -> this.mc.player.sendChatMessage("/wardrobe")));
            event.addWidget(new SmallArrowButton(width + 72, height + 90, button -> this.changeButtonPage(event, wardRobeItem, width, height)));
        }
        //        else
        //        {
        //            event.addWidget(new ItemButton(width - 9, height + 86, new ItemStack(Items.GOLDEN_HORSE_ARMOR), TextComponentUtils.component("Auction House"), button -> this.mc.player.sendChatMessage("/ah")));
        //            event.addWidget(new ItemButton(width + 10, height + 86, new ItemStack(Blocks.GOLD_ORE), TextComponentUtils.component("Bazaar"), button -> this.mc.player.sendChatMessage("/bz")));
        //        }

        ItemButton item = new ItemButton(width + 88, height + 86, new ItemStack(Items.GOLDEN_HORSE_ARMOR), TextComponentUtils.component("Auction House"), button -> this.mc.player.sendChatMessage("/ah"));
        item.visible = showAdditionalButtons;
        event.addWidget(item);
        item = new ItemButton(width + 88, height + 105, new ItemStack(Blocks.GOLD_ORE), TextComponentUtils.component("Bazaar"), button -> this.mc.player.sendChatMessage("/bz"));
        item.visible = showAdditionalButtons;
        event.addWidget(item);
        item = new ItemButton(width + 107, height + 86, new ItemStack(Blocks.ENCHANTING_TABLE), TextComponentUtils.component("Enchanting"), button -> this.mc.player.sendChatMessage("/et"));
        item.visible = showAdditionalButtons;
        event.addWidget(item);
        item = new ItemButton(width + 107, height + 105, new ItemStack(Blocks.ANVIL), TextComponentUtils.component("Anvil"), button -> this.mc.player.sendChatMessage("/av"));
        item.visible = showAdditionalButtons;
        event.addWidget(item);
    }

    private void changeButtonPage(GuiScreenEvent.InitGuiEvent.Post event, ItemStack wardRobeItem, int width, int height)
    {
        showAdditionalButtons = !showAdditionalButtons;
        event.getGui().buttons.removeIf(widget -> widget instanceof ItemButton || widget instanceof SmallArrowButton);
        event.getGui().getEventListeners().removeIf(widget -> widget instanceof ItemButton || widget instanceof SmallArrowButton);
        this.addButtonsToInventory(event, wardRobeItem, width, height);
    }
}