package com.stevekung.skyblockcatia.mixin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.event.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.event.HypixelEventHandler;
import com.stevekung.skyblockcatia.utils.LoggerIN;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    private final Minecraft that = (Minecraft) (Object) this;

    @Shadow
    private int rightClickDelayTimer;

    @Shadow
    private boolean isGamePaused;

    @Shadow
    private int leftClickCounter;

    @Shadow
    private int joinPlayerCounter;

    @Shadow
    private SoundHandler mcSoundHandler;

    @Shadow
    private MusicTicker mcMusicTicker;

    @Shadow
    private NetworkManager myNetworkManager;

    @Shadow
    long systemTime;

    @Shadow
    private long debugCrashKeyPressTime;

    @Shadow
    private RenderManager renderManager;

    @Shadow
    private Framebuffer framebufferMc;

    @Shadow
    protected abstract void updateDebugProfilerName(int keyCount);

    @Shadow
    protected abstract void clickMouse();

    @Shadow
    protected abstract void rightClickMouse();

    @Shadow
    protected abstract void middleClickMouse();

    @Shadow
    protected abstract void sendClickBlockToController(boolean leftClick);

    private boolean actionKeyF3;

    private static final List<String> SKYBLOCK_PACK_16 = new ArrayList<>(Arrays.asList("v8F1.8 Hypixel Skyblock Pack (16x)", "v8O1.8 Hypixel Skyblock Pack(16x)", "v9F1.8 Hypixel Skyblock Pack (16x)", "v9O1.8 Hypixel Skyblock Pack (16x)"));
    private static final List<String> SKYBLOCK_PACK_32 = new ArrayList<>(Arrays.asList("v8F1.8 Hypixel Skyblock Pack (x32)", "v8O1.8 Hypixel Skyblock Pack (32x)", "v9F1.8 Hypixel Skyblock Pack (32x)", "v9.1O1.8 Hypixel Skyblock Pack (32x)"));

    @Overwrite
    public void runTick() throws IOException
    {
        if (this.rightClickDelayTimer > 0)
        {
            --this.rightClickDelayTimer;
        }

        FMLCommonHandler.instance().onPreClientTick();

        this.that.mcProfiler.startSection("gui");

        if (!this.isGamePaused)
        {
            this.that.ingameGUI.updateTick();
        }

        this.that.mcProfiler.endSection();
        this.that.entityRenderer.getMouseOver(1.0F);
        this.that.mcProfiler.startSection("gameMode");

        if (!this.isGamePaused && this.that.theWorld != null)
        {
            this.that.playerController.updateController();
        }

        this.that.mcProfiler.endStartSection("textures");

        if (!this.isGamePaused)
        {
            this.that.renderEngine.tick();
        }

        if (this.that.currentScreen == null && this.that.thePlayer != null)
        {
            if (this.that.thePlayer.getHealth() <= 0.0F)
            {
                this.that.displayGuiScreen(null);
            }
            else if (this.that.thePlayer.isPlayerSleeping() && this.that.theWorld != null)
            {
                this.that.displayGuiScreen(new GuiSleepMP());
            }
        }
        else if (this.that.currentScreen != null && this.that.currentScreen instanceof GuiSleepMP && !this.that.thePlayer.isPlayerSleeping())
        {
            this.that.displayGuiScreen(null);
        }

        if (this.that.currentScreen != null)
        {
            this.leftClickCounter = 10000;
        }

        if (this.that.currentScreen != null)
        {
            try
            {
                this.that.currentScreen.handleInput();
            }
            catch (Throwable throwable1)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
                crashreportcategory.addCrashSectionCallable("Screen name", () -> this.that.currentScreen.getClass().getCanonicalName());
                throw new ReportedException(crashreport);
            }

            if (this.that.currentScreen != null)
            {
                try
                {
                    this.that.currentScreen.updateScreen();
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Ticking screen");
                    CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Affected screen");
                    crashreportcategory1.addCrashSectionCallable("Screen name", () -> this.that.currentScreen.getClass().getCanonicalName());
                    throw new ReportedException(crashreport1);
                }
            }
        }

        if (this.that.currentScreen == null || this.that.currentScreen.allowUserInput)
        {
            this.that.mcProfiler.endStartSection("mouse");
            this.runTickMouse();

            if (this.leftClickCounter > 0)
            {
                --this.leftClickCounter;
            }

            this.that.mcProfiler.endStartSection("keyboard");
            this.runTickKeyboard();
        }

        if (this.that.theWorld != null)
        {
            if (this.that.thePlayer != null)
            {
                ++this.joinPlayerCounter;

                if (this.joinPlayerCounter == 30)
                {
                    this.joinPlayerCounter = 0;
                    this.that.theWorld.joinEntityInSurroundings(this.that.thePlayer);
                }
            }

            this.that.mcProfiler.endStartSection("gameRenderer");

            if (!this.isGamePaused)
            {
                this.that.entityRenderer.updateRenderer();
            }

            this.that.mcProfiler.endStartSection("levelRenderer");

            if (!this.isGamePaused)
            {
                this.that.renderGlobal.updateClouds();
            }

            this.that.mcProfiler.endStartSection("level");

            if (!this.isGamePaused)
            {
                if (this.that.theWorld.getLastLightningBolt() > 0)
                {
                    this.that.theWorld.setLastLightningBolt(this.that.theWorld.getLastLightningBolt() - 1);
                }
                this.that.theWorld.updateEntities();
            }
        }
        else if (this.that.entityRenderer.isShaderActive())
        {
            this.that.entityRenderer.stopUseShader();
        }

        if (!this.isGamePaused)
        {
            this.mcMusicTicker.update();
            this.mcSoundHandler.update();
        }

        if (this.that.theWorld != null)
        {
            if (!this.isGamePaused)
            {
                this.that.theWorld.setAllowedSpawnTypes(this.that.theWorld.getDifficulty() != EnumDifficulty.PEACEFUL, true);

                try
                {
                    this.that.theWorld.tick();
                }
                catch (Throwable throwable2)
                {
                    CrashReport crashreport2 = CrashReport.makeCrashReport(throwable2, "Exception in world tick");

                    if (this.that.theWorld == null)
                    {
                        CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Affected level");
                        crashreportcategory2.addCrashSection("Problem", "Level is null!");
                    }
                    else
                    {
                        this.that.theWorld.addWorldInfoToCrashReport(crashreport2);
                    }
                    throw new ReportedException(crashreport2);
                }
            }

            this.that.mcProfiler.endStartSection("animateTick");

            if (!this.isGamePaused && this.that.theWorld != null)
            {
                this.that.theWorld.doVoidFogParticles(MathHelper.floor_double(this.that.thePlayer.posX), MathHelper.floor_double(this.that.thePlayer.posY), MathHelper.floor_double(this.that.thePlayer.posZ));
            }

            this.that.mcProfiler.endStartSection("particles");

            if (!this.isGamePaused)
            {
                this.that.effectRenderer.updateEffects();
            }
        }
        else if (this.myNetworkManager != null)
        {
            this.that.mcProfiler.endStartSection("pendingConnection");
            this.myNetworkManager.processReceivedPackets();
        }
        FMLCommonHandler.instance().onPostClientTick();
        this.that.mcProfiler.endSection();
        this.systemTime = Minecraft.getSystemTime();
    }

    @Redirect(method = "dispatchKeypresses()V", at = @At(value = "INVOKE", target = "org/lwjgl/input/Keyboard.getEventCharacter()C"))
    private char getEventCharacter()
    {
        return (char)(Keyboard.getEventCharacter() + 256);
    }

    @Overwrite
    public void displayGuiScreen(GuiScreen guiScreenIn)
    {
        if (guiScreenIn == null && this.that.theWorld == null)
        {
            guiScreenIn = new GuiMainMenu();
        }
        else if (guiScreenIn == null && this.that.thePlayer.getHealth() <= 0.0F)
        {
            guiScreenIn = new GuiGameOver();
        }

        GuiScreen old = this.that.currentScreen;
        GuiOpenEvent event = new GuiOpenEvent(guiScreenIn);

        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return;
        }

        guiScreenIn = event.gui;

        if (old != null && guiScreenIn != old)
        {
            old.onGuiClosed();
        }

        if (guiScreenIn instanceof GuiMainMenu || guiScreenIn instanceof GuiMultiplayer)
        {
            this.that.gameSettings.showDebugInfo = false;
            this.that.ingameGUI.getChatGUI().clearChatMessages();
        }

        this.that.currentScreen = guiScreenIn;

        if (guiScreenIn != null)
        {
            this.that.setIngameNotInFocus();
            KeyBinding.unPressAllKeys();

            while (Mouse.next()) {}
            while (Keyboard.next()) {}

            ScaledResolution scaledresolution = new ScaledResolution(this.that);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            guiScreenIn.setWorldAndResolution(this.that, i, j);
            this.that.skipRenderWorld = false;
        }
        else
        {
            this.mcSoundHandler.resumeSounds();
            this.that.setIngameFocus();
        }
    }

    @Overwrite
    public void setIngameFocus()
    {
        if (Display.isActive())
        {
            if (!this.that.inGameHasFocus)
            {
                if (!Minecraft.isRunningOnMac)
                {
                    this.updateKeyBindState();
                }
                this.that.inGameHasFocus = true;
                this.that.mouseHelper.grabMouseCursor();
                this.displayGuiScreen(null);
                this.leftClickCounter = 10000;
            }
        }
    }

    @Overwrite
    public void setIngameNotInFocus()
    {
        if (this.that.inGameHasFocus)
        {
            this.that.inGameHasFocus = false;
            this.that.mouseHelper.ungrabMouseCursor();
        }
    }

    @Inject(method = "runGameLoop()V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/EntityRenderer.updateCameraAndRender(FJ)V", shift = At.Shift.AFTER))
    private void runGameLoop(CallbackInfo info)
    {
        HUDRenderEventHandler.INSTANCE.getToastGui().drawToast(new ScaledResolution(this.that));
    }

    @Inject(method = "refreshResources()V", at = @At("HEAD"))
    private void refreshResources(CallbackInfo info)
    {
        boolean found = false;

        for (ResourcePackRepository.Entry entry : this.that.getResourcePackRepository().getRepositoryEntries())
        {
            String packName = entry.getResourcePack().getPackName();
            String packDesc = entry.getTexturePackDescription();

            if (SKYBLOCK_PACK_16.stream().anyMatch(name -> packName.contains(name)))
            {
                HypixelEventHandler.skyBlockPackResolution = "16";
            }
            if (SKYBLOCK_PACK_32.stream().anyMatch(name -> packName.contains(name)))
            {
                HypixelEventHandler.skyBlockPackResolution = "32";
            }

            if (packName.contains("Hypixel Skyblock Pack") && packDesc.contains("by Hypixel Packs HQ"))
            {
                HypixelEventHandler.foundSkyBlockPack = true;
                found = true;
                break;
            }
        }
        if (found)
        {
            LoggerIN.info("Found SkyBlock Pack with x" + HypixelEventHandler.skyBlockPackResolution + "! Loaded Glowing Texture for Dragon Set Armor");
        }
        else
        {
            HypixelEventHandler.foundSkyBlockPack = false;
            LoggerIN.info("SkyBlock Pack not found! Glowing Texture will not loaded for Dragon Set Armor");
        }
    }

    private void updateKeyBindState()
    {
        for (KeyBinding keybinding : KeyBinding.keybindArray)
        {
            try
            {
                KeyBinding.setKeyBindState(keybinding.getKeyCode(), keybinding.getKeyCode() < 256 && Keyboard.isKeyDown(keybinding.getKeyCode()));
            }
            catch (IndexOutOfBoundsException e) {}
        }
    }

    private void runTickKeyboard() throws IOException
    {
        while (Keyboard.next())
        {
            int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

            if (this.debugCrashKeyPressTime > 0L)
            {
                if (Minecraft.getSystemTime() - this.debugCrashKeyPressTime >= 6000L)
                {
                    throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                }

                if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61))
                {
                    this.debugCrashKeyPressTime = -1L;
                }
            }
            else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61))
            {
                this.actionKeyF3 = true;
                this.debugCrashKeyPressTime = Minecraft.getSystemTime();
            }

            this.that.dispatchKeypresses();

            if (this.that.currentScreen != null)
            {
                this.that.currentScreen.handleKeyboardInput();
            }

            boolean flag = Keyboard.getEventKeyState();

            if (flag)
            {
                if (i == 62 && this.that.entityRenderer != null)
                {
                    this.that.entityRenderer.switchUseShader();
                }

                boolean flag1 = false;

                if (this.that.currentScreen == null)
                {
                    if (i == 1)
                    {
                        this.that.displayInGameMenu();
                    }

                    flag1 = Keyboard.isKeyDown(61) && this.processKeyF3(i);
                    this.actionKeyF3 |= flag1;

                    if (i == 59)
                    {
                        this.that.gameSettings.hideGUI = !this.that.gameSettings.hideGUI;
                    }
                }

                if (flag1)
                {
                    KeyBinding.setKeyBindState(i, false);
                }
                else
                {
                    KeyBinding.setKeyBindState(i, true);
                    KeyBinding.onTick(i);
                }

                if (this.that.gameSettings.showDebugProfilerChart)
                {
                    if (i == 11)
                    {
                        this.updateDebugProfilerName(0);
                    }

                    for (int j = 0; j < 9; ++j)
                    {
                        if (i == 2 + j)
                        {
                            this.updateDebugProfilerName(j + 1);
                        }
                    }
                }
            }
            else
            {
                KeyBinding.setKeyBindState(i, false);

                if (i == 61)
                {
                    if (this.actionKeyF3)
                    {
                        this.actionKeyF3 = false;
                    }
                    else
                    {
                        this.that.gameSettings.showDebugInfo = !this.that.gameSettings.showDebugInfo;
                        this.that.gameSettings.showDebugProfilerChart = this.that.gameSettings.showDebugInfo && GuiScreen.isShiftKeyDown();
                        this.that.gameSettings.showLagometer = this.that.gameSettings.showDebugInfo && GuiScreen.isAltKeyDown();
                    }
                }
            }
            FMLCommonHandler.instance().fireKeyInput();
        }
        this.processKeyBinds();
    }

    private boolean processKeyF3(int auxKey)
    {
        if (auxKey == 30)
        {
            this.that.renderGlobal.loadRenderers();
            return true;
        }
        else if (auxKey == 48)
        {
            boolean flag1 = !this.renderManager.isDebugBoundingBox();
            this.renderManager.setDebugBoundingBox(flag1);
            return true;
        }
        else if (auxKey == 32)
        {
            if (this.that.ingameGUI != null)
            {
                this.that.ingameGUI.getChatGUI().clearChatMessages();
            }
            return true;
        }
        else if (auxKey == 35)
        {
            this.that.gameSettings.advancedItemTooltips = !this.that.gameSettings.advancedItemTooltips;
            this.that.gameSettings.saveOptions();
            return true;
        }
        else if (auxKey == 25)
        {
            this.that.gameSettings.pauseOnLostFocus = !this.that.gameSettings.pauseOnLostFocus;
            this.that.gameSettings.saveOptions();
            return true;
        }
        else if (auxKey == 20)
        {
            this.that.refreshResources();
            return true;
        }
        else
        {
            return false;
        }
    }

    private void processKeyBinds()
    {
        for (; this.that.gameSettings.keyBindTogglePerspective.isPressed(); this.that.renderGlobal.setDisplayListEntitiesDirty())
        {
            ++this.that.gameSettings.thirdPersonView;

            if (this.that.gameSettings.thirdPersonView > 2)
            {
                this.that.gameSettings.thirdPersonView = 0;
            }

            if (this.that.gameSettings.thirdPersonView == 0)
            {
                this.that.entityRenderer.loadEntityShader(this.that.getRenderViewEntity());
            }
            else if (this.that.gameSettings.thirdPersonView == 1)
            {
                this.that.entityRenderer.loadEntityShader(null);
            }
        }

        while (this.that.gameSettings.keyBindSmoothCamera.isPressed())
        {
            this.that.gameSettings.smoothCamera = !this.that.gameSettings.smoothCamera;
        }

        for (int l = 0; l < 9; ++l)
        {
            if (this.that.gameSettings.keyBindsHotbar[l].isPressed())
            {
                if (this.that.thePlayer.isSpectator())
                {
                    this.that.ingameGUI.getSpectatorGui().func_175260_a(l);
                }
                else
                {
                    this.that.thePlayer.inventory.currentItem = l;
                }
            }
        }

        boolean foundDragon = false;

        for (Entity entity : this.that.theWorld.loadedEntityList)
        {
            if (entity instanceof EntityDragon)
            {
                foundDragon = true;
                break;
            }
        }

        if (HypixelEventHandler.isSkyBlock && ExtendedConfig.instance.sneakToOpenInventoryWhileFightDragon && foundDragon)
        {
            while (this.that.gameSettings.keyBindInventory.isPressed() && this.that.thePlayer.isSneaking())
            {
                if (this.that.playerController.isRidingHorse())
                {
                    this.that.thePlayer.sendHorseInventory();
                }
                else
                {
                    this.that.displayGuiScreen(new GuiInventory(this.that.thePlayer));
                }
            }
        }
        else
        {
            while (this.that.gameSettings.keyBindInventory.isPressed())
            {
                if (this.that.playerController.isRidingHorse())
                {
                    this.that.thePlayer.sendHorseInventory();
                }
                else
                {
                    this.that.displayGuiScreen(new GuiInventory(this.that.thePlayer));
                }
            }
        }

        while (this.that.gameSettings.keyBindDrop.isPressed())
        {
            if (!this.that.thePlayer.isSpectator())
            {
                this.that.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
            }
        }

        boolean flag2 = this.that.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

        if (flag2)
        {
            while (this.that.gameSettings.keyBindChat.isPressed())
            {
                this.that.displayGuiScreen(new GuiChat());
            }

            if (this.that.currentScreen == null && this.that.gameSettings.keyBindCommand.isPressed())
            {
                this.that.displayGuiScreen(new GuiChat("/"));
            }
        }

        if (this.that.thePlayer.isUsingItem())
        {
            if (!this.that.gameSettings.keyBindUseItem.isKeyDown())
            {
                this.that.playerController.onStoppedUsingItem(this.that.thePlayer);
            }

            label109:
                while (true)
                {
                    if (!this.that.gameSettings.keyBindAttack.isPressed())
                    {
                        while (this.that.gameSettings.keyBindUseItem.isPressed()) {}

                        while (true)
                        {
                            if (this.that.gameSettings.keyBindPickBlock.isPressed())
                            {
                                continue;
                            }
                            break label109;
                        }
                    }
                }
        }
        else
        {
            while (this.that.gameSettings.keyBindAttack.isPressed())
            {
                this.clickMouse();
            }

            while (this.that.gameSettings.keyBindUseItem.isPressed())
            {
                this.rightClickMouse();
            }

            while (this.that.gameSettings.keyBindPickBlock.isPressed())
            {
                this.middleClickMouse();
            }
        }

        if (this.that.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.that.thePlayer.isUsingItem())
        {
            this.rightClickMouse();
        }

        this.sendClickBlockToController(this.that.currentScreen == null && this.that.gameSettings.keyBindAttack.isKeyDown() && this.that.inGameHasFocus);
    }

    private void runTickMouse() throws IOException
    {
        while (Mouse.next())
        {
            if (ForgeHooksClient.postMouseEvent())
            {
                continue;
            }

            int i = Mouse.getEventButton();
            KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());

            if (Mouse.getEventButtonState())
            {
                if (this.that.thePlayer.isSpectator() && i == 2)
                {
                    this.that.ingameGUI.getSpectatorGui().func_175261_b();
                }
                else
                {
                    KeyBinding.onTick(i - 100);
                }
            }

            long j = Minecraft.getSystemTime() - this.systemTime;

            if (j <= 200L)
            {
                int k = Mouse.getEventDWheel();

                if (k != 0)
                {
                    if (this.that.thePlayer.isSpectator())
                    {
                        k = k < 0 ? -1 : 1;

                        if (this.that.ingameGUI.getSpectatorGui().func_175262_a())
                        {
                            this.that.ingameGUI.getSpectatorGui().func_175259_b(-k);
                        }
                        else
                        {
                            float f = MathHelper.clamp_float(this.that.thePlayer.capabilities.getFlySpeed() + k * 0.005F, 0.0F, 0.2F);
                            this.that.thePlayer.capabilities.setFlySpeed(f);
                        }
                    }
                    else
                    {
                        this.that.thePlayer.inventory.changeCurrentItem(k);
                    }
                }

                if (this.that.currentScreen == null)
                {
                    if (!this.that.inGameHasFocus && Mouse.getEventButtonState())
                    {
                        this.that.setIngameFocus();
                    }
                }
                else if (this.that.currentScreen != null)
                {
                    this.that.currentScreen.handleMouseInput();
                }
            }
            FMLCommonHandler.instance().fireMouseInput();
        }
    }
}