package com.stevekung.skyblockcatia.handler;

import org.lwjgl.glfw.GLFW;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.stevekungslib.keybinding.KeyBindingBase;
import com.stevekung.stevekungslib.utils.client.ClientRegistryUtils;
import net.minecraft.client.KeyMapping;

public class KeyBindingHandler
{
    public static KeyMapping KEY_SB_SETTINGS;
    public static KeyMapping KEY_SB_API_VIEWER;
    public static KeyMapping KEY_SB_ENDER_CHEST;
    public static KeyMapping KEY_SB_CRAFTED_MINIONS;
    public static KeyMapping KEY_SB_CRAFTING_TABLE;
    public static KeyMapping KEY_SB_VIEW_RECIPE;
    public static KeyMapping KEY_SB_MENU;
    public static KeyMapping KEY_SB_OPEN_WIKI;
    public static KeyMapping KEY_SB_PETS;
    public static KeyMapping KEY_SB_WARDROBE;
    public static KeyMapping KEY_SB_HOTM;

    public static void init()
    {
        KeyBindingHandler.KEY_SB_SETTINGS = new KeyBindingBase("key.sb_settings.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_O);
        KeyBindingHandler.KEY_SB_API_VIEWER = new KeyBindingBase("key.sb_api_viewer.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_F6);
        KeyBindingHandler.KEY_SB_ENDER_CHEST = new KeyBindingBase("key.sb_ender_chest.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_KP_5);
        KeyBindingHandler.KEY_SB_CRAFTED_MINIONS = new KeyBindingBase("key.sb_crafted_minions.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_KP_2);
        KeyBindingHandler.KEY_SB_CRAFTING_TABLE = new KeyBindingBase("key.sb_crafting_table.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_KP_ADD);
        KeyBindingHandler.KEY_SB_VIEW_RECIPE = new KeyBindingBase("key.sb_view_recipe.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_B);
        KeyBindingHandler.KEY_SB_MENU = new KeyBindingBase("key.sb_menu.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_M);
        KeyBindingHandler.KEY_SB_OPEN_WIKI = new KeyBindingBase("key.sb_open_wiki.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_APOSTROPHE);
        KeyBindingHandler.KEY_SB_PETS = new KeyBindingBase("key.sb_pets.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_P);
        KeyBindingHandler.KEY_SB_WARDROBE = new KeyBindingBase("key.sb_wardrobe.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_R);
        KeyBindingHandler.KEY_SB_HOTM = new KeyBindingBase("key.sb_hotm.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_M);
        ClientRegistryUtils.registerKeyBinding(KeyBindingHandler.KEY_SB_SETTINGS);
        ClientRegistryUtils.registerKeyBinding(KeyBindingHandler.KEY_SB_API_VIEWER);
        ClientRegistryUtils.registerKeyBinding(KeyBindingHandler.KEY_SB_ENDER_CHEST);
        ClientRegistryUtils.registerKeyBinding(KeyBindingHandler.KEY_SB_CRAFTED_MINIONS);
        ClientRegistryUtils.registerKeyBinding(KeyBindingHandler.KEY_SB_CRAFTING_TABLE);
        ClientRegistryUtils.registerKeyBinding(KeyBindingHandler.KEY_SB_VIEW_RECIPE);
        ClientRegistryUtils.registerKeyBinding(KeyBindingHandler.KEY_SB_MENU);
        ClientRegistryUtils.registerKeyBinding(KeyBindingHandler.KEY_SB_OPEN_WIKI);
        ClientRegistryUtils.registerKeyBinding(KeyBindingHandler.KEY_SB_PETS);
        ClientRegistryUtils.registerKeyBinding(KeyBindingHandler.KEY_SB_WARDROBE);
        ClientRegistryUtils.registerKeyBinding(KeyBindingHandler.KEY_SB_HOTM);
    }
}