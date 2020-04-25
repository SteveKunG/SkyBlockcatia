package com.stevekung.skyblockcatia.utils;

import java.util.ArrayList;
import java.util.List;

public class GuiChatRegistry
{
    private static final List<IGuiChat> guiChat = new ArrayList<>();

    public static void register(IGuiChat chat)
    {
        GuiChatRegistry.guiChat.add(chat);
    }

    public static List<IGuiChat> getGuiChatList()
    {
        return GuiChatRegistry.guiChat;
    }
}