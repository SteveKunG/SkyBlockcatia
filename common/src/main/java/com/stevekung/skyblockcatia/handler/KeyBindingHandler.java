package com.stevekung.skyblockcatia.handler;

import org.lwjgl.glfw.GLFW;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import me.shedaniel.architectury.registry.KeyBindings;
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
        KeyBindingHandler.KEY_SB_SETTINGS = new KeyMapping("key.sb_settings.desc", GLFW.GLFW_KEY_O, SkyBlockcatiaMod.MOD_ID);
        KeyBindingHandler.KEY_SB_API_VIEWER = new KeyMapping("key.sb_api_viewer.desc", GLFW.GLFW_KEY_F6, SkyBlockcatiaMod.MOD_ID);
        KeyBindingHandler.KEY_SB_ENDER_CHEST = new KeyMapping("key.sb_ender_chest.desc", GLFW.GLFW_KEY_KP_5, SkyBlockcatiaMod.MOD_ID);
        KeyBindingHandler.KEY_SB_CRAFTED_MINIONS = new KeyMapping("key.sb_crafted_minions.desc", GLFW.GLFW_KEY_KP_2, SkyBlockcatiaMod.MOD_ID);
        KeyBindingHandler.KEY_SB_CRAFTING_TABLE = new KeyMapping("key.sb_crafting_table.desc", GLFW.GLFW_KEY_KP_ADD, SkyBlockcatiaMod.MOD_ID);
        KeyBindingHandler.KEY_SB_VIEW_RECIPE = new KeyMapping("key.sb_view_recipe.desc", GLFW.GLFW_KEY_B, SkyBlockcatiaMod.MOD_ID);
        KeyBindingHandler.KEY_SB_MENU = new KeyMapping("key.sb_menu.desc", GLFW.GLFW_KEY_M, SkyBlockcatiaMod.MOD_ID);
        KeyBindingHandler.KEY_SB_OPEN_WIKI = new KeyMapping("key.sb_open_wiki.desc", GLFW.GLFW_KEY_APOSTROPHE, SkyBlockcatiaMod.MOD_ID);
        KeyBindingHandler.KEY_SB_PETS = new KeyMapping("key.sb_pets.desc", GLFW.GLFW_KEY_P, SkyBlockcatiaMod.MOD_ID);
        KeyBindingHandler.KEY_SB_WARDROBE = new KeyMapping("key.sb_wardrobe.desc", GLFW.GLFW_KEY_R, SkyBlockcatiaMod.MOD_ID);
        KeyBindingHandler.KEY_SB_HOTM = new KeyMapping("key.sb_hotm.desc", GLFW.GLFW_KEY_M, SkyBlockcatiaMod.MOD_ID);
        KeyBindings.registerKeyBinding(KeyBindingHandler.KEY_SB_SETTINGS);
        KeyBindings.registerKeyBinding(KeyBindingHandler.KEY_SB_API_VIEWER);
        KeyBindings.registerKeyBinding(KeyBindingHandler.KEY_SB_ENDER_CHEST);
        KeyBindings.registerKeyBinding(KeyBindingHandler.KEY_SB_CRAFTED_MINIONS);
        KeyBindings.registerKeyBinding(KeyBindingHandler.KEY_SB_CRAFTING_TABLE);
        KeyBindings.registerKeyBinding(KeyBindingHandler.KEY_SB_VIEW_RECIPE);
        KeyBindings.registerKeyBinding(KeyBindingHandler.KEY_SB_MENU);
        KeyBindings.registerKeyBinding(KeyBindingHandler.KEY_SB_OPEN_WIKI);
        KeyBindings.registerKeyBinding(KeyBindingHandler.KEY_SB_PETS);
        KeyBindings.registerKeyBinding(KeyBindingHandler.KEY_SB_WARDROBE);
        KeyBindings.registerKeyBinding(KeyBindingHandler.KEY_SB_HOTM);
    }
}