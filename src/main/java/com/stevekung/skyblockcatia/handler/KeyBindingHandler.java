package com.stevekung.skyblockcatia.handler;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindingHandler
{
    public static KeyBinding KEY_QUICK_CONFIG;
    public static KeyBinding KEY_SB_API_VIEWER;
    public static KeyBinding KEY_SB_ENDER_CHEST;
    public static KeyBinding KEY_SB_CRAFTED_MINIONS;
    public static KeyBinding KEY_SB_CRAFTING_TABLE;
    public static KeyBinding KEY_SB_VIEW_RECIPE;
    public static KeyBinding KEY_SB_MENU;
    public static KeyBinding KEY_SB_OPEN_WIKI;

    public static void init()
    {
        KeyBindingHandler.KEY_QUICK_CONFIG = new KeyBindingIU("key.quick_config.desc", Keyboard.KEY_F4);
        KeyBindingHandler.KEY_SB_API_VIEWER = new KeyBindingIU("key.sb_api_viewer.desc", Keyboard.KEY_F6);
        KeyBindingHandler.KEY_SB_ENDER_CHEST = new KeyBindingIU("key.sb_ender_chest.desc", Keyboard.KEY_NUMPAD5);
        KeyBindingHandler.KEY_SB_CRAFTED_MINIONS = new KeyBindingIU("key.sb_crafted_minions.desc", Keyboard.KEY_NUMPAD2);
        KeyBindingHandler.KEY_SB_CRAFTING_TABLE = new KeyBindingIU("key.sb_crafting_table.desc", Keyboard.KEY_ADD);
        KeyBindingHandler.KEY_SB_VIEW_RECIPE = new KeyBindingIU("key.sb_view_recipe.desc", Keyboard.KEY_B);
        KeyBindingHandler.KEY_SB_MENU = new KeyBindingIU("key.sb_menu.desc", Keyboard.KEY_M);
        KeyBindingHandler.KEY_SB_OPEN_WIKI = new KeyBindingIU("key.sb_open_wiki.desc", Keyboard.KEY_APOSTROPHE);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_QUICK_CONFIG);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_SB_API_VIEWER);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_SB_ENDER_CHEST);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_SB_CRAFTED_MINIONS);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_SB_CRAFTING_TABLE);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_SB_VIEW_RECIPE);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_SB_MENU);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_SB_OPEN_WIKI);
    }
}