package com.stevekung.skyblockcatia.utils;

import com.stevekung.skyblockcatia.command.ClientCommandBase;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class ClientUtils
{
    public static boolean isClient()
    {
        return FMLCommonHandler.instance().getSide() == Side.CLIENT;
    }

    public static boolean isEffectiveClient()
    {
        return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
    }

    public static boolean isShiftKeyDown()
    {
        return GuiScreen.isShiftKeyDown();
    }

    public static boolean isControlKeyDown()
    {
        return GuiScreen.isCtrlKeyDown();
    }

    public static void setOverlayMessage(String message)
    {
        Minecraft.getMinecraft().ingameGUI.setRecordPlaying(message, false);
    }

    public static void setOverlayMessage(IChatComponent component)
    {
        Minecraft.getMinecraft().ingameGUI.setRecordPlaying(component.getFormattedText(), false);
    }

    public static void printClientMessage(String text)
    {
        ClientUtils.printClientMessage(JsonUtils.create(text));
    }

    public static void printClientMessage(String text, ChatStyle color)
    {
        ClientUtils.printClientMessage(JsonUtils.create(text).setChatStyle(color));
    }

    public static void printClientMessage(IChatComponent component)
    {
        if (Minecraft.getMinecraft().thePlayer != null)
        {
            Minecraft.getMinecraft().thePlayer.addChatMessage(component);
        }
    }

    public static void registerCommand(ClientCommandBase command)
    {
        ClientCommandHandler.instance.registerCommand(command);
    }
}