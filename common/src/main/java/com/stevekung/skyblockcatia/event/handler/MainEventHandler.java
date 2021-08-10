package com.stevekung.skyblockcatia.event.handler;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.gui.widget.button.ItemButton;
import com.stevekung.skyblockcatia.gui.widget.button.SmallArrowButton;
import com.stevekung.skyblockcatia.utils.GuiScreenUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils.APIUrl;
import com.stevekung.skyblockcatia.utils.skyblock.api.Bazaar;
import com.stevekung.stevekungslib.utils.CalendarUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.hooks.client.screen.ScreenAccess;
import dev.architectury.hooks.client.screen.ScreenHooks;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
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
    public static final Map<String, Bazaar.Data> BAZAAR_DATA = Maps.newHashMap();
    public static boolean bidHighlight = true;
    private static boolean showAdditionalButtons;
    public static int rainbowTicks;

    public MainEventHandler()
    {
        this.mc = Minecraft.getInstance();
        ClientTickEvent.CLIENT_PRE.register(this::onClientTick);
        ClientGuiEvent.INIT_POST.register(this::onInitGui);
        ClientGuiEvent.SET_SCREEN.register(this::onGuiOpen);
        ClientGuiEvent.RENDER_POST.register(this::onPostGuiDrawScreen);
    }

    private void onClientTick(Minecraft mc)
    {
        if (this.mc.screen instanceof TitleScreen)
        {
            rainbowTicks = 0;
        }
        rainbowTicks += 5;
    }

    private void onInitGui(Screen screen, ScreenAccess access)
    {
        var width = screen.width / 2;
        var height = screen.height / 2 - 106;

        if (SkyBlockEventHandler.isSkyBlock)
        {
            var wardRobeItem = new ItemStack(Items.LEATHER_CHESTPLATE);
            ((DyeableArmorItem) wardRobeItem.getItem()).setColor(wardRobeItem, 8339378);

            if (SkyBlockcatiaSettings.INSTANCE.shortcutButtonInInventory && screen instanceof InventoryScreen)
            {
                this.addRenderableWidgetsToInventory(screen, wardRobeItem, width, height);
            }
            else if (screen instanceof ContainerScreen chest)
            {
                var title = chest.getTitle();

                if (SkyBlockcatiaSettings.INSTANCE.shortcutButtonInInventory)
                {
                    var skyBlockMenu = new ItemStack(Items.NETHER_STAR);
                    var list = new ListTag();
                    skyBlockMenu.setHoverName(TextComponentUtils.component("SkyBlock Menu"));
                    list.add(StringTag.valueOf(TextComponentUtils.toJson(ChatFormatting.GRAY + "View all of your SkyBlock")));
                    skyBlockMenu.getTag().getCompound("display").put("Lore", list);

                    if (GuiScreenUtils.isChatable(title))
                    {
                        var chat = MainEventHandler.showChat ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF";

                        ScreenHooks.addRenderableWidget(screen, new ItemButton(2, screen.height - 35, Items.ENDER_EYE, TextComponentUtils.component("Toggle Inventory Chat: " + chat), button ->
                        {
                            MainEventHandler.showChat = !MainEventHandler.showChat;
                            ((ItemButton) button).setName(TextComponentUtils.component("Toggle Inventory Chat: " + (MainEventHandler.showChat ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF")));
                        }));
                    }

                    if (GuiScreenUtils.contains(GuiScreenUtils.INVENTORY, title) && !title.getString().startsWith("Ender Chest"))
                    {
                        ScreenHooks.addRenderableWidget(screen, new ItemButton(width + 88, height + 47, Blocks.CRAFTING_TABLE, button -> this.mc.player.chat("/craft")));
                        ScreenHooks.addRenderableWidget(screen, new ItemButton(width + 88, height + 66, Blocks.CHEST, TextComponentUtils.component("Storage"), button -> this.mc.player.chat("/storage")));
                        ScreenHooks.addRenderableWidget(screen, new ItemButton(width + 88, height + 85, skyBlockMenu, button -> this.mc.player.chat("/sbmenu")));
                    }
                    else if (title.getString().equals("Craft Item"))
                    {
                        ScreenHooks.addRenderableWidget(screen, new ItemButton(width + 88, height + 47, Blocks.CHEST, TextComponentUtils.component("Storage"), button -> this.mc.player.chat("/storage")));
                        ScreenHooks.addRenderableWidget(screen, new ItemButton(width + 88, height + 66, skyBlockMenu, button -> this.mc.player.chat("/sbmenu")));
                    }
                    else if (title.getString().startsWith("Ender Chest"))
                    {
                        ScreenHooks.addRenderableWidget(screen, new ItemButton(width + 88, height + 47, Blocks.CRAFTING_TABLE, button -> this.mc.player.chat("/craft")));
                        ScreenHooks.addRenderableWidget(screen, new ItemButton(width + 88, height + 66, skyBlockMenu, button -> this.mc.player.chat("/sbmenu")));
                    }
                    else if (title.getString().contains("Wardrobe"))
                    {
                        ScreenHooks.addRenderableWidget(screen, new ItemButton(width + 88, height + 47, Items.BONE, TextComponentUtils.component("Pets"), button -> this.mc.player.chat("/pets")));
                    }
                    else if (title.getString().contains("Pets"))
                    {
                        ScreenHooks.addRenderableWidget(screen, new ItemButton(width + 88, height + 47, wardRobeItem, TextComponentUtils.component("Wardrobe"), button -> this.mc.player.chat("/wardrobe")));
                    }
                }

                if (GuiScreenUtils.isAuctionBrowser(title.getString()))
                {
                    var bid = MainEventHandler.bidHighlight ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF";
                    ScreenHooks.addRenderableWidget(screen, new ItemButton(width + 89, GuiScreenUtils.isOtherAuction(title.getString()) ? chest.topPos + 4 : height + 60, Blocks.REDSTONE_BLOCK, TextComponentUtils.component("Toggle Bid Highlight: " + bid), button ->
                    {
                        MainEventHandler.bidHighlight = !MainEventHandler.bidHighlight;
                        ((ItemButton) button).setName(TextComponentUtils.component("Toggle Bid Highlight: " + (MainEventHandler.bidHighlight ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF")));
                    }));
                }
                else if (title.getString().contains("Hub Selector"))
                {
                    var overlay = SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF";
                    ScreenHooks.addRenderableWidget(screen, new ItemButton(width + 89, height + 29, Items.COMPASS, TextComponentUtils.component("Lobby Player Overlay: " + overlay), button ->
                    {
                        SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer = !SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer;
                        ((ItemButton) button).setName(TextComponentUtils.component("Lobby Player Overlay: " + (SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer ? ChatFormatting.GREEN + "ON" : ChatFormatting.RED + "OFF")));
                        SkyBlockcatiaSettings.INSTANCE.save();
                    }));
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void onPostGuiDrawScreen(Screen screen, PoseStack poseStack, int mouseX, int mouseY, float delta)
    {
        if (screen != null)
        {
            for (ItemButton button : ScreenHooks.getRenderables(screen).stream().filter(ItemButton.class::isInstance).map(ItemButton.class::cast).collect(Collectors.toList()))
            {
                var hover = mouseX >= button.x && mouseY >= button.y && mouseX < button.x + button.getWidth() && mouseY < button.y + button.getHeight();

                if (hover && button.visible)
                {
                    screen.renderTooltip(poseStack, button.getName(), mouseX, mouseY);
                    break;
                }
            }
        }
    }

    private CompoundEventResult<Screen> onGuiOpen(Screen screen)
    {
        if (screen instanceof TitleScreen titleScreen)
        {
            if (CalendarUtils.isMyBirthDay())
            {
                titleScreen.splash = "Happy birthday, SteveKunG!"; // why not
            }
        }
        return CompoundEventResult.pass();
    }

    public static void getBazaarData()
    {
        if (!SkyBlockcatiaSettings.INSTANCE.bazaarOnItemTooltip)
        {
            return;
        }

        try
        {
            var url = new URL(APIUrl.BAZAAR.getUrl());
            var bazaar = TextComponentUtils.GSON.fromJson(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8), Bazaar.class);
            var lastUpdated = bazaar.lastUpdated();

            for (var product : bazaar.products().entrySet())
            {
                var productName = product.getKey();
                var currentProduct = product.getValue();
                var quickStatus = currentProduct.quickStatus();
                var buyPrice = quickStatus.buyPrice();
                var sellPrice = quickStatus.sellPrice();
                var buyArray = currentProduct.buySummary();
                var sellArray = currentProduct.sellSummary();

                if (sellArray.length == 0 || buyArray.length == 0)
                {
                    BAZAAR_DATA.put(productName, new Bazaar.Data(lastUpdated, new Bazaar.Status(buyPrice, sellPrice)));
                }
                else
                {
                    BAZAAR_DATA.put(productName, new Bazaar.Data(lastUpdated, new Bazaar.Status(buyArray[0].pricePerUnit(), sellArray[0].pricePerUnit())));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void addRenderableWidgetsToInventory(Screen screen, ItemStack wardRobeItem, int width, int height)
    {
        ScreenHooks.addRenderableWidget(screen, new ItemButton(width - 9, height + 86, Blocks.CHEST, TextComponentUtils.component("Storage"), button -> this.mc.player.chat("/storage")));
        ScreenHooks.addRenderableWidget(screen, new ItemButton(width + 10, height + 86, Blocks.CRAFTING_TABLE, button -> this.mc.player.chat("/craft")));
        ScreenHooks.addRenderableWidget(screen, new ItemButton(width + 29, height + 86, Items.BONE, TextComponentUtils.component("Pets"), button -> this.mc.player.chat("/pets")));
        ScreenHooks.addRenderableWidget(screen, new ItemButton(width + 48, height + 86, wardRobeItem, TextComponentUtils.component("Wardrobe"), button -> this.mc.player.chat("/wardrobe")));
        ScreenHooks.addRenderableWidget(screen, new SmallArrowButton(width + 72, height + 90, button -> this.changeButtonPage(screen, wardRobeItem, width, height)));

        var item = new ItemButton(width + 88, height + 86, new ItemStack(Items.GOLDEN_HORSE_ARMOR), TextComponentUtils.component("Auction House"), button -> this.mc.player.chat("/ah"));
        item.visible = showAdditionalButtons;
        ScreenHooks.addRenderableWidget(screen, item);
        item = new ItemButton(width + 88, height + 105, new ItemStack(Blocks.GOLD_ORE), TextComponentUtils.component("Bazaar"), button -> this.mc.player.chat("/bz"));
        item.visible = showAdditionalButtons;
        ScreenHooks.addRenderableWidget(screen, item);
        item = new ItemButton(width + 107, height + 86, new ItemStack(Blocks.ENCHANTING_TABLE), TextComponentUtils.component("Enchanting"), button -> this.mc.player.chat("/et"));
        item.visible = showAdditionalButtons;
        ScreenHooks.addRenderableWidget(screen, item);
        item = new ItemButton(width + 107, height + 105, new ItemStack(Blocks.ANVIL), TextComponentUtils.component("Anvil"), button -> this.mc.player.chat("/av"));
        item.visible = showAdditionalButtons;
        ScreenHooks.addRenderableWidget(screen, item);
    }

    private void changeButtonPage(Screen screen, ItemStack wardRobeItem, int width, int height)
    {
        showAdditionalButtons = !showAdditionalButtons;
        ScreenHooks.getRenderables(screen).removeIf(widget -> widget instanceof ItemButton || widget instanceof SmallArrowButton);
        screen.children().removeIf(widget -> widget instanceof ItemButton || widget instanceof SmallArrowButton);
        this.addRenderableWidgetsToInventory(screen, wardRobeItem, width, height);
    }
}