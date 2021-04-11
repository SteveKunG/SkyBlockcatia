package com.stevekung.skyblockcatia.event.handler;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.gui.GuiDisconnectConfirmation;
import com.stevekung.skyblockcatia.gui.config.GuiSkyBlockSettings;
import com.stevekung.skyblockcatia.gui.screen.GuiSkyBlockProfileSelector;
import com.stevekung.skyblockcatia.gui.widget.button.GuiButtonItem;
import com.stevekung.skyblockcatia.gui.widget.button.GuiSmallArrowButton;
import com.stevekung.skyblockcatia.keybinding.KeyBindingsSB;
import com.stevekung.skyblockcatia.utils.*;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import com.stevekung.skyblockcatia.utils.skyblock.api.BazaarData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MainEventHandler
{
    private final Minecraft mc;
    private long lastButtonClick = -1;
    public static String auctionPrice = "";
    public static boolean showChat;
    public static String playerToView;
    public static final Map<String, BazaarData> BAZAAR_DATA = new HashMap<>();
    public static boolean bidHighlight = true;
    private static boolean showAdditionalButtons;

    public MainEventHandler()
    {
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            if (MainEventHandler.playerToView != null)
            {
                this.mc.displayGuiScreen(new GuiSkyBlockProfileSelector(GuiSkyBlockProfileSelector.GuiState.PLAYER, MainEventHandler.playerToView, "", ""));
                MainEventHandler.playerToView = null;
            }
        }
    }

    @SubscribeEvent
    public void onPressKey(InputEvent.KeyInputEvent event)
    {
        if (KeyBindingsSB.KEY_QUICK_CONFIG.isKeyDown())
        {
            this.mc.displayGuiScreen(new GuiSkyBlockSettings());
        }
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event)
    {
        int width = event.gui.width / 2;
        int height = event.gui.height / 2 - 106;

        if (SkyBlockEventHandler.isSkyBlock)
        {
            ItemStack wardRobeItem = new ItemStack(Items.leather_chestplate);
            ((ItemArmor)wardRobeItem.getItem()).setColor(wardRobeItem, 8339378);

            if (SkyBlockcatiaSettings.INSTANCE.shortcutButtonInInventory && event.gui instanceof GuiInventory)
            {
                event.buttonList.add(new GuiButtonItem(1000, width - 9, height + 86, width + 51, new ItemStack(Blocks.ender_chest)));
                event.buttonList.add(new GuiButtonItem(1001, width + 10, height + 86, width + 70, new ItemStack(Blocks.crafting_table)));
                event.buttonList.add(new GuiButtonItem(1002, width + 29, height + 86, width + 89, new ItemStack(Items.bone), "Pets"));
                event.buttonList.add(new GuiButtonItem(1003, width + 48, height + 86, width + 108, wardRobeItem, "Wardrobe"));

                GuiButtonItem item = new GuiButtonItem(1010, width + 88, height + 86, width + 148, new ItemStack(Items.golden_horse_armor), "Auction House");
                item.visible = showAdditionalButtons;
                event.buttonList.add(item);
                item = new GuiButtonItem(1011, width + 88, height + 105, width + 148, new ItemStack(Blocks.gold_ore), "Bazaar");
                item.visible = showAdditionalButtons;
                event.buttonList.add(item);
                item = new GuiButtonItem(1012, width + 107, height + 86, width + 167, new ItemStack(Blocks.enchanting_table), "Enchanting");
                item.visible = showAdditionalButtons;
                event.buttonList.add(item);
                item = new GuiButtonItem(1013, width + 107, height + 105, width + 167, new ItemStack(Blocks.anvil), "Anvil");
                item.visible = showAdditionalButtons;
                event.buttonList.add(item);

                event.buttonList.add(new GuiSmallArrowButton(1100, width + 72, height + 90, width + 132));
            }
            if (event.gui instanceof GuiChest)
            {
                GuiChest chest = (GuiChest)event.gui;
                IInventory lowerChestInventory = chest.lowerChestInventory;
                ItemStack skyBlockMenu = new ItemStack(Items.nether_star);
                NBTTagList list = new NBTTagList();
                skyBlockMenu.setStackDisplayName("SkyBlock Menu");
                list.appendTag(new NBTTagString(EnumChatFormatting.GRAY + "View all of your SkyBlock"));
                skyBlockMenu.getTagCompound().getCompoundTag("display").setTag("Lore", list);

                if (GuiScreenUtils.isChatable(lowerChestInventory))
                {
                    String chat = MainEventHandler.showChat ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
                    event.buttonList.add(new GuiButtonItem(500, 2, event.gui.height - 35, new ItemStack(Items.ender_eye), "Toggle Inventory Chat: " + chat));
                }

                if (SkyBlockcatiaSettings.INSTANCE.shortcutButtonInInventory)
                {
                    if (GuiScreenUtils.contains(GuiScreenUtils.INVENTORY, lowerChestInventory) && !lowerChestInventory.getDisplayName().getUnformattedText().startsWith("Ender Chest"))
                    {
                        event.buttonList.add(new GuiButtonItem(1001, width + 88, height + 47, new ItemStack(Blocks.crafting_table)));
                        event.buttonList.add(new GuiButtonItem(1000, width + 88, height + 66, new ItemStack(Blocks.ender_chest)));
                        event.buttonList.add(new GuiButtonItem(1004, width + 88, height + 85, width + 88, skyBlockMenu));
                    }
                    else if (lowerChestInventory.getDisplayName().getUnformattedText().equals("Craft Item"))
                    {
                        event.buttonList.add(new GuiButtonItem(1000, width + 88, height + 47, new ItemStack(Blocks.ender_chest)));
                        event.buttonList.add(new GuiButtonItem(1004, width + 88, height + 65, skyBlockMenu));
                    }
                    else if (lowerChestInventory.getDisplayName().getUnformattedText().startsWith("Ender Chest"))
                    {
                        event.buttonList.add(new GuiButtonItem(1001, width + 88, height + 47, new ItemStack(Blocks.crafting_table)));
                        event.buttonList.add(new GuiButtonItem(1004, width + 88, height + 65, skyBlockMenu));
                    }
                    else if (lowerChestInventory.getDisplayName().getUnformattedText().contains("Wardrobe"))
                    {
                        event.buttonList.add(new GuiButtonItem(1002, width + 88, height + 47, width + 89, new ItemStack(Items.bone), "Pets"));
                    }
                    else if (lowerChestInventory.getDisplayName().getUnformattedText().contains("Pets"))
                    {
                        event.buttonList.add(new GuiButtonItem(1003, width + 88, height + 47, width + 89, wardRobeItem, "Wardrobe"));
                    }
                }

                if (GuiScreenUtils.isAuctionBrowser(lowerChestInventory))
                {
                    String bid = MainEventHandler.bidHighlight ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
                    event.buttonList.add(new GuiButtonItem(1005, width + 89, GuiScreenUtils.isOtherAuction(lowerChestInventory) ? chest.guiTop + 4 : height + 60, new ItemStack(Blocks.redstone_block), "Toggle Bid Highlight: " + bid));
                }
                else if (lowerChestInventory.getDisplayName().getUnformattedText().contains("Hub Selector"))
                {
                    String overlay = SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
                    event.buttonList.add(new GuiButtonItem(1006, width + 89, height + 29, new ItemStack(Items.compass), "Lobby Player Overlay: " + overlay));
                }
            }
        }
        if (event.gui instanceof GuiControls)
        {
            event.buttonList.removeIf(button -> button.id == 200 || button.id == 201);
            event.buttonList.add(new GuiButton(200, width - 155 + 160, event.gui.height - 29, 150, 20, LangUtils.translate("gui.done")));
            event.buttonList.add(new GuiButton(201, width - 155, event.gui.height - 29, 150, 20, LangUtils.translate("controls.resetAll")));
        }
        if (event.gui instanceof GuiOptions)
        {
            event.buttonList.removeIf(button -> button.id == 107);
        }
        if (!CompatibilityUtils.isPatcherLoaded && event.gui instanceof GuiMainMenu)
        {
            int j = event.gui.height / 4 + 48;
            event.buttonList.removeIf(button -> button.id == 14 || button.id == 6);
            event.buttonList.add(new GuiButton(6, width - 100, j + 24 * 2, LangUtils.translate("fml.menu.mods")));
        }
    }

    @SubscribeEvent
    public void onPreActionPerformedGui(GuiScreenEvent.ActionPerformedEvent.Pre event)
    {
        if (SkyBlockcatiaConfig.enableConfirmToDisconnect && event.gui instanceof GuiIngameMenu && !this.mc.isSingleplayer())
        {
            if (event.button.id == 1)
            {
                event.setCanceled(true);
                event.button.playPressSound(this.mc.getSoundHandler());
                this.mc.displayGuiScreen(new GuiDisconnectConfirmation());
            }
        }
    }

    @SubscribeEvent
    public void onPostActionPerformedGui(GuiScreenEvent.ActionPerformedEvent.Post event)
    {
        long now = System.currentTimeMillis();

        if ((event.gui instanceof GuiInventory || event.gui instanceof GuiChest) && SkyBlockEventHandler.isSkyBlock)
        {
            if (now - this.lastButtonClick > 100L)
            {
                if (event.button.id == 1000)
                {
                    this.mc.thePlayer.sendChatMessage("/enderchest");
                }
                else if (event.button.id == 1001)
                {
                    this.mc.thePlayer.sendChatMessage("/craft");
                }
                else if (event.button.id == 1002)
                {
                    this.mc.thePlayer.sendChatMessage("/pets");
                }
                else if (event.button.id == 1003)
                {
                    this.mc.thePlayer.sendChatMessage("/wardrobe");
                }
                else if (event.button.id == 1004)
                {
                    this.mc.thePlayer.sendChatMessage("/viewsbmenu");
                }
                else if (event.button.id == 1010)
                {
                    this.mc.thePlayer.sendChatMessage("/ah");
                }
                else if (event.button.id == 1011)
                {
                    this.mc.thePlayer.sendChatMessage("/bz");
                }
                else if (event.button.id == 1012)
                {
                    this.mc.thePlayer.sendChatMessage("/et");
                }
                else if (event.button.id == 1013)
                {
                    this.mc.thePlayer.sendChatMessage("/av");
                }
                else if (event.button.id == 1100)
                {
                    showAdditionalButtons = !showAdditionalButtons;

                    for (GuiButton button : event.buttonList)
                    {
                        if (button.id == 1010 || button.id == 1011 || button.id == 1012 || button.id == 1013)
                        {
                            button.visible = showAdditionalButtons;
                        }
                    }
                }
                this.lastButtonClick = now;
            }
            if (event.button.id == 500)
            {
                MainEventHandler.showChat = !MainEventHandler.showChat;
                String chat = MainEventHandler.showChat ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
                ((GuiButtonItem)event.button).setName("Toggle Inventory Chat: " + chat);
            }
            else if (event.button.id == 1005)
            {
                MainEventHandler.bidHighlight = !MainEventHandler.bidHighlight;
                String bid = MainEventHandler.bidHighlight ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
                ((GuiButtonItem)event.button).setName("Toggle Bid Highlight: " + bid);
            }
            else if (event.button.id == 1006)
            {
                SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer = !SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer;
                String overlay = SkyBlockcatiaSettings.INSTANCE.lobbyPlayerViewer ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
                ((GuiButtonItem)event.button).setName("Lobby Player Overlay: " + overlay);
                SkyBlockcatiaSettings.INSTANCE.save();
            }
        }
    }

    @SubscribeEvent
    public void onPostGuiDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event)
    {
        for (GuiButton button : event.gui.buttonList.stream().filter(button -> button != null && button instanceof GuiButtonItem).collect(Collectors.toList()))
        {
            boolean hover = event.mouseX >= button.xPosition && event.mouseY >= button.yPosition && event.mouseX < button.xPosition + button.width && event.mouseY < button.yPosition + button.height;

            if (hover && button.visible)
            {
                GuiUtils.drawHoveringText(Collections.singletonList(((GuiButtonItem)button).getName()), event.mouseX, event.mouseY, event.gui.width, event.gui.height, -1, this.mc.fontRendererObj);
                GlStateManager.disableLighting();
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event)
    {
        if (event.gui instanceof GuiMainMenu)
        {
            GuiMainMenu menu = (GuiMainMenu)event.gui;

            if (CalendarUtils.isSteveKunGBirthDay())
            {
                menu.splashText = "Happy birthday, SteveKunG!";
            }
            if (CompatibilityUtils.isPatcherLoaded)
            {
                menu.drawString(ColorUtils.unicodeFontRenderer, "dummy", 1000, 1000, -1);
            }
        }
    }

    public static void getBazaarData()
    {
        if (!SkyBlockcatiaSettings.INSTANCE.bazaarOnTooltips)
        {
            return;
        }

        try
        {
            URL url = new URL(SBAPIUtils.BAZAAR);
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
}