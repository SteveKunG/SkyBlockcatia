package com.stevekung.skyblockcatia.event;

import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stevekung.skyblockcatia.config.ConfigManagerIN;
import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.gui.GuiButtonItem;
import com.stevekung.skyblockcatia.gui.GuiButtonMojangStatus;
import com.stevekung.skyblockcatia.gui.GuiConfirmDisconnect;
import com.stevekung.skyblockcatia.gui.GuiMojangStatusChecker;
import com.stevekung.skyblockcatia.gui.api.GuiSkyBlockAPIViewer;
import com.stevekung.skyblockcatia.gui.config.GuiExtendedConfig;
import com.stevekung.skyblockcatia.gui.config.GuiRenderPreview;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.utils.*;
import com.stevekung.skyblockcatia.utils.JsonUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class MainEventHandler
{
    private final Minecraft mc;
    public static int currentServerPing;
    private static final ThreadPoolExecutor REALTIME_PINGER = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Real Time Server Pinger #%d").setDaemon(true).build());
    private long lastPinger = -1L;
    private long lastButtonClick = -1;
    private static final List<String> INVENTORY_LIST = new ArrayList<>(Arrays.asList("Trades", "Shop Trading Options", "Backpack", "Chest"));
    public static String auctionPrice = "";
    public static final List<String> CHATABLE_LIST = new ArrayList<>(Arrays.asList("You                  ", "Ender Chest", "Craft Item", "Anvil", "Trades", "Shop Trading Options", "Runic Pedestal", "Your Bids", "Bank", "Bank Deposit", "Bank Withdrawal", "Reforge Accessory Bag", "Catacombs Gate"));
    public static boolean showChat;
    private static long sneakTimeOld;
    private static boolean sneakingOld;
    public static String playerToView;
    public static final Map<String, BazaarData> BAZAAR_DATA = new HashMap<>();
    public static boolean bidHighlight = true;

    public MainEventHandler()
    {
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (this.mc.thePlayer != null)
        {
            if (event.phase == TickEvent.Phase.START)
            {
                if (this.mc.getCurrentServerData() != null)
                {
                    long now = System.currentTimeMillis();

                    if (this.lastPinger == -1L || now - this.lastPinger > 5000L)
                    {
                        this.lastPinger = now;
                        MainEventHandler.getRealTimeServerPing(this.mc.getCurrentServerData());
                    }
                }

                for (EnumAction action : CachedEnum.actionValues)
                {
                    if (action != EnumAction.NONE)
                    {
                        if (ConfigManagerIN.enableAdditionalBlockhitAnimation && this.mc.gameSettings.keyBindAttack.isKeyDown() && this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.thePlayer.getCurrentEquippedItem() != null && this.mc.thePlayer.getCurrentEquippedItem().getItemUseAction() == action)
                        {
                            this.mc.thePlayer.swingItem();
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onInputUpdate(InputUpdateEvent event)
    {
        MovementInput movement = event.getMovementInput();
        EntityPlayer player = event.getEntityPlayer();
        String[] keyTS = ConfigManagerIN.keyToggleSprint.split(",");
        int keyTGCtrl = InfoUtils.INSTANCE.parseInt(keyTS[0], "Toggle Sprint");
        int keyTGOther = InfoUtils.INSTANCE.parseInt(keyTS[1], "Toggle Sprint");

        if (this.mc.currentScreen == null && this.mc.gameSettings.keyBindSneak.getKeyCode() != Keyboard.KEY_LCONTROL && keyTGCtrl == Keyboard.KEY_LCONTROL && keyTGOther == Keyboard.KEY_S && Keyboard.isKeyDown(keyTGCtrl) && Keyboard.isKeyDown(keyTGOther))
        {
            ++movement.moveForward;
        }

        // toggle sneak
        movement.sneak = this.mc.gameSettings.keyBindSneak.isKeyDown() || ExtendedConfig.instance.toggleSneak && !event.getEntityPlayer().isSpectator();

        if (ExtendedConfig.instance.toggleSneak && !this.mc.gameSettings.keyBindSneak.isKeyDown() && !player.isSpectator() && !player.capabilities.isCreativeMode)
        {
            movement.moveStrafe = (float)(movement.moveStrafe * 0.3D);
            movement.moveForward = (float)(movement.moveForward * 0.3D);
        }

        // toggle sprint
        if (ExtendedConfig.instance.toggleSprint && !player.isPotionActive(Potion.blindness) && !ExtendedConfig.instance.toggleSneak)
        {
            player.setSprinting(true);
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            if (ConfigManagerIN.enableSmoothSneakingView)
            {
                if (this.mc.thePlayer != null)
                {
                    this.mc.thePlayer.eyeHeight = MainEventHandler.getSmoothEyeHeight(this.mc.thePlayer);
                }
            }
            if (MainEventHandler.playerToView != null)
            {
                this.mc.displayGuiScreen(new GuiSkyBlockAPIViewer(GuiSkyBlockAPIViewer.GuiState.PLAYER, MainEventHandler.playerToView, "", ""));
                MainEventHandler.playerToView = null;
            }
        }
    }

    @SubscribeEvent
    public void onPressKey(InputEvent.KeyInputEvent event)
    {
        if (KeyBindingHandler.KEY_QUICK_CONFIG.isKeyDown())
        {
            this.mc.displayGuiScreen(new GuiExtendedConfig());
        }

        if (ExtendedConfig.instance.toggleSprintUseMode.equals("key_binding"))
        {
            String[] keyTS = ConfigManagerIN.keyToggleSprint.split(",");
            int keyTGCtrl = InfoUtils.INSTANCE.parseInt(keyTS[0], "Toggle Sprint");
            int keyTGOther = InfoUtils.INSTANCE.parseInt(keyTS[1], "Toggle Sprint");

            if (Keyboard.isKeyDown(keyTGCtrl) && Keyboard.isKeyDown(keyTGOther))
            {
                ExtendedConfig.instance.toggleSprint = !ExtendedConfig.instance.toggleSprint;
                ClientUtils.setOverlayMessage(JsonUtils.create(ExtendedConfig.instance.toggleSprint ? LangUtils.translate("message.toggle_sprint_enabled") : LangUtils.translate("message.toggle_sprint_disabled")).getFormattedText());
                ExtendedConfig.instance.save();
            }
        }
        if (ExtendedConfig.instance.toggleSneakUseMode.equals("key_binding"))
        {
            String[] keyTS = ConfigManagerIN.keyToggleSneak.split(",");
            int keyTGCtrl = InfoUtils.INSTANCE.parseInt(keyTS[0], "Toggle Sneak");
            int keyTGOther = InfoUtils.INSTANCE.parseInt(keyTS[1], "Toggle Sneak");

            if (Keyboard.isKeyDown(keyTGCtrl) && Keyboard.isKeyDown(keyTGOther))
            {
                ExtendedConfig.instance.toggleSneak = !ExtendedConfig.instance.toggleSneak;
                ClientUtils.setOverlayMessage(JsonUtils.create(ExtendedConfig.instance.toggleSneak ? LangUtils.translate("message.toggle_sneak_enabled") : LangUtils.translate("message.toggle_sneak_disabled")).getFormattedText());
                ExtendedConfig.instance.save();
            }
        }
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event)
    {
        int width = event.gui.width / 2;
        int height = event.gui.height / 2 - 106;

        if (event.gui instanceof GuiMainMenu)
        {
            height = event.gui.height / 4 + 48;
            event.buttonList.add(new GuiButtonMojangStatus(200, width + 104, height + (SkyBlockcatiaMod.isIngameAccountSwitcherLoaded ? 63 : 84)));
        }
        if (HypixelEventHandler.isSkyBlock)
        {
            if (ExtendedConfig.instance.shortcutButtonInInventory && event.gui instanceof GuiInventory)
            {
                event.buttonList.add(new GuiButtonItem(1000, width - 9, height + 86, width + 51, new ItemStack(Blocks.ender_chest)));
                event.buttonList.add(new GuiButtonItem(1001, width + 10, height + 86, width + 70, new ItemStack(Blocks.crafting_table)));
                event.buttonList.add(new GuiButtonItem(1002, width + 29, height + 86, width + 89, new ItemStack(Items.bone), "Pets"));
                event.buttonList.add(new GuiButtonItem(1003, width + 48, height + 86, width + 108, new ItemStack(Items.leather_chestplate), "Wardrobe"));
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

                if (MainEventHandler.isSuitableForGUI(MainEventHandler.CHATABLE_LIST, lowerChestInventory))
                {
                    event.buttonList.add(new GuiButton(500, width - 108, height + 190, 20, 20, "C"));
                }

                if (ExtendedConfig.instance.shortcutButtonInInventory)
                {
                    if (MainEventHandler.isSuitableForGUI(MainEventHandler.INVENTORY_LIST, lowerChestInventory) && !lowerChestInventory.getDisplayName().getUnformattedText().equals("Ender Chest"))
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
                    else if (lowerChestInventory.getDisplayName().getUnformattedText().equals("Ender Chest"))
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
                        event.buttonList.add(new GuiButtonItem(1003, width + 88, height + 47, width + 89, new ItemStack(Items.leather_chestplate), "Wardrobe"));
                    }
                }

                if (lowerChestInventory.getDisplayName().getUnformattedText().equals("Auctions Browser"))
                {
                    String bid = MainEventHandler.bidHighlight ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
                    event.buttonList.add(new GuiButtonItem(1005, width + 89, height + 60, new ItemStack(Blocks.redstone_block), "Toggle Bid Highlight: " + bid));
                }
                else if (lowerChestInventory.getDisplayName().getUnformattedText().contains("Hub Selector"))
                {
                    String overlay = ExtendedConfig.instance.lobbyPlayerViewer ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
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
        if (!SkyBlockcatiaMod.isPatcherLoaded && event.gui instanceof GuiMainMenu)
        {
            int j = event.gui.height / 4 + 48;
            event.buttonList.removeIf(button -> button.id == 14 || button.id == 6);
            event.buttonList.add(new GuiButton(6, width - 100, j + 24 * 2, LangUtils.translate("fml.menu.mods")));
        }
    }

    @SubscribeEvent
    public void onPreActionPerformedGui(GuiScreenEvent.ActionPerformedEvent.Pre event)
    {
        if (ConfigManagerIN.enableConfirmToDisconnect && event.gui instanceof GuiIngameMenu && !this.mc.isSingleplayer())
        {
            if (event.button.id == 1)
            {
                event.setCanceled(true);
                event.button.playPressSound(this.mc.getSoundHandler());
                this.mc.displayGuiScreen(new GuiConfirmDisconnect());
            }
        }
    }

    @SubscribeEvent
    public void onPostActionPerformedGui(GuiScreenEvent.ActionPerformedEvent.Post event)
    {
        long now = System.currentTimeMillis();

        if (event.gui instanceof GuiMainMenu)
        {
            if (event.button.id == 200)
            {
                this.mc.displayGuiScreen(new GuiMojangStatusChecker(event.gui));
            }
        }
        if ((event.gui instanceof GuiInventory || event.gui instanceof GuiChest) && HypixelEventHandler.isSkyBlock)
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
                this.lastButtonClick = now;
            }
            if (event.button.id == 500)
            {
                MainEventHandler.showChat = !MainEventHandler.showChat;
            }
            else if (event.button.id == 1005)
            {
                MainEventHandler.bidHighlight = !MainEventHandler.bidHighlight;
                String bid = MainEventHandler.bidHighlight ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
                ((GuiButtonItem)event.button).setName("Toggle Bid Highlight: " + bid);
            }
            else if (event.button.id == 1006)
            {
                ExtendedConfig.instance.lobbyPlayerViewer = !ExtendedConfig.instance.lobbyPlayerViewer;
                String overlay = ExtendedConfig.instance.lobbyPlayerViewer ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
                ((GuiButtonItem)event.button).setName("Lobby Player Overlay: " + overlay);
                ExtendedConfig.instance.save();
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
    public void onRenderHand(RenderHandEvent event)
    {
        if (this.mc.currentScreen instanceof GuiRenderPreview)
        {
            event.setCanceled(true);
            return;
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
        }
    }

    public static void getBazaarData()
    {
        if (!ExtendedConfig.instance.bazaarOnTooltips)
        {
            return;
        }

        if (StringUtils.isNullOrEmpty(ConfigManagerIN.hypixelApiKey))
        {
            ClientUtils.printClientMessage("Couldn't get bazaar data, Empty text in the Config!", JsonUtils.red());
            ClientUtils.printClientMessage(JsonUtils.create("Make sure you're in the Hypixel!").setChatStyle(JsonUtils.yellow()).appendSibling(JsonUtils.create(" Click Here to create an API key").setChatStyle(JsonUtils.gold().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/api new")))));
            return;
        }
        if (!ConfigManagerIN.hypixelApiKey.matches(HypixelEventHandler.UUID_PATTERN_STRING))
        {
            ClientUtils.printClientMessage("Invalid UUID for Hypixel API Key!", JsonUtils.red());
            ClientUtils.printClientMessage("Example UUID pattern: " + UUID.randomUUID(), JsonUtils.yellow());
            return;
        }

        try
        {
            URL url = new URL(SkyBlockAPIUtils.BAZAAR);
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

    private static void getRealTimeServerPing(ServerData server)
    {
        MainEventHandler.REALTIME_PINGER.submit(() ->
        {
            try
            {
                ServerAddress address = ServerAddress.fromString(server.serverIP);
                NetworkManager manager = NetworkManager.func_181124_a(InetAddress.getByName(address.getIP()), address.getPort(), false);

                manager.setNetHandler(new INetHandlerStatusClient()
                {
                    private long currentSystemTime = 0L;

                    @Override
                    public void handleServerInfo(S00PacketServerInfo packet)
                    {
                        this.currentSystemTime = Minecraft.getSystemTime();
                        manager.sendPacket(new C01PacketPing(this.currentSystemTime));
                    }

                    @Override
                    public void handlePong(S01PacketPong packet)
                    {
                        long i = this.currentSystemTime;
                        long j = Minecraft.getSystemTime();
                        MainEventHandler.currentServerPing = (int) (j - i);
                    }

                    @Override
                    public void onDisconnect(IChatComponent component) {}
                });
                manager.sendPacket(new C00Handshake(47, address.getIP(), address.getPort(), EnumConnectionState.STATUS));
                manager.sendPacket(new C00PacketServerQuery());
            }
            catch (Exception e) {}
        });
    }

    private static float getSmoothEyeHeight(EntityPlayer player)
    {
        if (MainEventHandler.sneakingOld != player.isSneaking() || MainEventHandler.sneakTimeOld <= 0L)
        {
            MainEventHandler.sneakTimeOld = System.currentTimeMillis();
        }

        MainEventHandler.sneakingOld = player.isSneaking();
        float defaultEyeHeight = 1.62F;
        double sneakPress = 0.0006D;
        double sneakValue = 0.005D;
        int sneakTime = -35;
        long smoothRatio = 88L;

        if (player.isSneaking())
        {
            int sneakSystemTime = (int)(MainEventHandler.sneakTimeOld + smoothRatio - System.currentTimeMillis());

            if (sneakSystemTime > sneakTime)
            {
                defaultEyeHeight += (float)(sneakSystemTime * sneakPress);

                if (defaultEyeHeight < 0.0F || defaultEyeHeight > 10.0F)
                {
                    defaultEyeHeight = 1.54F;
                }
            }
            else
            {
                defaultEyeHeight = (float)(defaultEyeHeight - sneakValue);
            }
        }
        else
        {
            int sneakSystemTime = (int)(MainEventHandler.sneakTimeOld + smoothRatio - System.currentTimeMillis());

            if (sneakSystemTime > sneakTime)
            {
                defaultEyeHeight -= (float)(sneakSystemTime * sneakPress);
                defaultEyeHeight = (float)(defaultEyeHeight - sneakValue);

                if (defaultEyeHeight < 0.0F)
                {
                    defaultEyeHeight = 1.62F;
                }
            }
            else
            {
                defaultEyeHeight -= 0.0F;
            }
        }
        return defaultEyeHeight;
    }

    public static boolean isSuitableForGUI(List<String> invList, IInventory lowerChestInventory)
    {
        return invList.stream().anyMatch(invName -> lowerChestInventory.getDisplayName().getUnformattedText().contains(invName));
    }
}