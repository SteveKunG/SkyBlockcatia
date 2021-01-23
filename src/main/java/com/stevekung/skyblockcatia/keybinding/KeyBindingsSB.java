package com.stevekung.skyblockcatia.keybinding;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindingsSB
{
    public static KeyBinding KEY_QUICK_CONFIG;
    public static KeyBinding KEY_SB_API_VIEWER;
    public static KeyBinding KEY_SB_ENDER_CHEST;
    public static KeyBinding KEY_SB_CRAFTED_MINIONS;
    public static KeyBinding KEY_SB_CRAFTING_TABLE;
    public static KeyBinding KEY_SB_VIEW_RECIPE;
    public static KeyBinding KEY_SB_MENU;
    public static KeyBinding KEY_SB_OPEN_WIKI;
    public static KeyBinding KEY_SB_PETS;
    public static KeyBinding KEY_SB_WARDROBE;
    public static KeyBinding KEY_SB_HOTM;

    public static void init()
    {
        KeyBindingsSB.KEY_QUICK_CONFIG = new KeyBindingBase("key.quick_config.desc", Keyboard.KEY_F4);
        KeyBindingsSB.KEY_SB_API_VIEWER = new KeyBindingBase("key.sb_api_viewer.desc", Keyboard.KEY_F8);
        KeyBindingsSB.KEY_SB_ENDER_CHEST = new KeyBindingBase("key.sb_ender_chest.desc", Keyboard.KEY_NUMPAD5);
        KeyBindingsSB.KEY_SB_CRAFTED_MINIONS = new KeyBindingBase("key.sb_crafted_minions.desc", Keyboard.KEY_NUMPAD2);
        KeyBindingsSB.KEY_SB_CRAFTING_TABLE = new KeyBindingBase("key.sb_crafting_table.desc", Keyboard.KEY_ADD);
        KeyBindingsSB.KEY_SB_VIEW_RECIPE = new KeyBindingBase("key.sb_view_recipe.desc", Keyboard.KEY_B);
        KeyBindingsSB.KEY_SB_MENU = new KeyBindingBase("key.sb_menu.desc", Keyboard.KEY_M);
        KeyBindingsSB.KEY_SB_OPEN_WIKI = new KeyBindingBase("key.sb_open_wiki.desc", Keyboard.KEY_APOSTROPHE);
        KeyBindingsSB.KEY_SB_PETS = new KeyBindingBase("key.sb_pets.desc", Keyboard.KEY_P);
        KeyBindingsSB.KEY_SB_WARDROBE = new KeyBindingBase("key.sb_wardrobe.desc", Keyboard.KEY_R);
        KeyBindingsSB.KEY_SB_HOTM = new KeyBindingBase("key.sb_hotm.desc", Keyboard.KEY_M);
        ClientRegistry.registerKeyBinding(KeyBindingsSB.KEY_QUICK_CONFIG);
        ClientRegistry.registerKeyBinding(KeyBindingsSB.KEY_SB_API_VIEWER);
        ClientRegistry.registerKeyBinding(KeyBindingsSB.KEY_SB_ENDER_CHEST);
        ClientRegistry.registerKeyBinding(KeyBindingsSB.KEY_SB_CRAFTED_MINIONS);
        ClientRegistry.registerKeyBinding(KeyBindingsSB.KEY_SB_CRAFTING_TABLE);
        ClientRegistry.registerKeyBinding(KeyBindingsSB.KEY_SB_VIEW_RECIPE);
        ClientRegistry.registerKeyBinding(KeyBindingsSB.KEY_SB_MENU);
        ClientRegistry.registerKeyBinding(KeyBindingsSB.KEY_SB_OPEN_WIKI);
        ClientRegistry.registerKeyBinding(KeyBindingsSB.KEY_SB_PETS);
        ClientRegistry.registerKeyBinding(KeyBindingsSB.KEY_SB_WARDROBE);
        ClientRegistry.registerKeyBinding(KeyBindingsSB.KEY_SB_HOTM);
    }
}