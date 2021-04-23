package com.stevekung.skyblockcatia.event.handler;

import org.lwjgl.glfw.GLFW;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaForgeMod;
import com.stevekung.skyblockcatia.gui.screen.SkyBlockProfileSelectorScreen;
import com.stevekung.skyblockcatia.utils.GuiScreenUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBFakePlayerEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.player.RemotePlayer;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SBClientEventHandler
{
    private final Minecraft mc;

    public SBClientEventHandler()
    {
        this.mc = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (mc.player != null)
        {
            if (!SkyBlockcatiaForgeMod.CHECKER.hasChecked())
            {
                SkyBlockcatiaForgeMod.CHECKER.checkFail();
                SkyBlockcatiaForgeMod.CHECKER.printInfo();
                SkyBlockcatiaForgeMod.CHECKER.setChecked(true);
            }
        }
    }

    @SubscribeEvent
    public void onRenderNameplate(RenderNameplateEvent event)
    {
        if (event.getEntity() instanceof SBFakePlayerEntity)
        {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            if (MainEventHandler.playerToView != null)
            {
                this.mc.setScreen(new SkyBlockProfileSelectorScreen(SkyBlockProfileSelectorScreen.Mode.PLAYER, MainEventHandler.playerToView, "", ""));
                MainEventHandler.playerToView = null;
            }
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event)
    {
        HUDRenderEventHandler.INSTANCE.onChunkLoad(event.getChunk().getPos().x, event.getChunk().getPos().z);
    }

    @SubscribeEvent
    public void onRenderChat(RenderGameOverlayEvent.Chat event)
    {
        if (this.mc.screen != null && this.mc.screen instanceof ContainerScreen)
        {
            ContainerScreen chest = (ContainerScreen)this.mc.screen;

            if (MainEventHandler.showChat && GuiScreenUtils.isChatable(chest.getTitle()))
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onDisconnectedFromServerEvent(ClientPlayerNetworkEvent.LoggedOutEvent event)
    {
        SkyBlockEventHandler.INSTANCE.onDisconnectedFromServerEvent();
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event)
    {
        SkyBlockEventHandler.INSTANCE.onItemTooltip(event.getItemStack(), event.getFlags(), event.getToolTip());
    }

    @SubscribeEvent
    public void onMouseClick(InputEvent.MouseInputEvent event)
    {
        if (event.getButton() == GLFW.GLFW_PRESS && event.getAction() == GLFW.GLFW_MOUSE_BUTTON_2 && mc.crosshairPickEntity != null && mc.crosshairPickEntity instanceof RemotePlayer && mc.player.isShiftKeyDown() && SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.sneakToTradeOtherPlayerIsland && SkyBlockEventHandler.otherPlayerIsland)
        {
            RemotePlayer player = (RemotePlayer)mc.crosshairPickEntity;
            mc.player.chat("/trade " + ChatFormatting.stripFormatting(player.getName().getString()));
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event)
    {
        SkyBlockEventHandler.INSTANCE.onPressKey();
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event)
    {
        HUDRenderEventHandler.INSTANCE.onPreInfoRender(event.getMatrixStack(), event.getWindow());
    }
}