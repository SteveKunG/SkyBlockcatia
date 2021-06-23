package com.stevekung.skyblockcatia.handler;

import org.lwjgl.glfw.GLFW;
import com.stevekung.skyblockcatia.core.SkyBlockcatia;
import com.stevekung.stevekungslib.client.KeyMappingBase;
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
        KeyBindingHandler.KEY_SB_SETTINGS = new KeyMappingBase("key.sb_settings.desc", GLFW.GLFW_KEY_O, SkyBlockcatia.MOD_ID);
        KeyBindingHandler.KEY_SB_API_VIEWER = new KeyMappingBase("key.sb_api_viewer.desc", GLFW.GLFW_KEY_F6, SkyBlockcatia.MOD_ID);
        KeyBindingHandler.KEY_SB_ENDER_CHEST = new KeyMappingBase("key.sb_ender_chest.desc", GLFW.GLFW_KEY_KP_5, SkyBlockcatia.MOD_ID);
        KeyBindingHandler.KEY_SB_CRAFTED_MINIONS = new KeyMappingBase("key.sb_crafted_minions.desc", GLFW.GLFW_KEY_KP_2, SkyBlockcatia.MOD_ID);
        KeyBindingHandler.KEY_SB_CRAFTING_TABLE = new KeyMappingBase("key.sb_crafting_table.desc", GLFW.GLFW_KEY_KP_ADD, SkyBlockcatia.MOD_ID);
        KeyBindingHandler.KEY_SB_VIEW_RECIPE = new KeyMappingBase("key.sb_view_recipe.desc", GLFW.GLFW_KEY_B, SkyBlockcatia.MOD_ID);
        KeyBindingHandler.KEY_SB_MENU = new KeyMappingBase("key.sb_menu.desc", GLFW.GLFW_KEY_M, SkyBlockcatia.MOD_ID);
        KeyBindingHandler.KEY_SB_OPEN_WIKI = new KeyMappingBase("key.sb_open_wiki.desc", GLFW.GLFW_KEY_APOSTROPHE, SkyBlockcatia.MOD_ID);
        KeyBindingHandler.KEY_SB_PETS = new KeyMappingBase("key.sb_pets.desc", GLFW.GLFW_KEY_P, SkyBlockcatia.MOD_ID);
        KeyBindingHandler.KEY_SB_WARDROBE = new KeyMappingBase("key.sb_wardrobe.desc", GLFW.GLFW_KEY_R, SkyBlockcatia.MOD_ID);
        KeyBindingHandler.KEY_SB_HOTM = new KeyMappingBase("key.sb_hotm.desc", GLFW.GLFW_KEY_M, SkyBlockcatia.MOD_ID);
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