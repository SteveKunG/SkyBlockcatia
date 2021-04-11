package com.stevekung.skyblockcatia.event.handler;

import com.stevekung.skyblockcatia.utils.GuiChatRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ClientEventHandler
{
    private final Minecraft mc;
    public static int ticks;
    public static int rainbowTicks;
    public static float renderPartialTicks;

    public ClientEventHandler()
    {
        this.mc = Minecraft.getMinecraft();
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
        if (this.mc.currentScreen instanceof GuiMainMenu)
        {
            ClientEventHandler.ticks = ClientEventHandler.rainbowTicks = 0;
        }
        if (event.phase == Phase.START)
        {
            ClientEventHandler.rainbowTicks += 5;
            ClientEventHandler.ticks++;
            ClientEventHandler.renderPartialTicks = ClientEventHandler.ticks + this.mc.timer.renderPartialTicks;
        }
    }

    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event)
    {
        GuiChatRegistry.getGuiChatList().forEach(gui -> gui.actionPerformed(event.button));
    }
}