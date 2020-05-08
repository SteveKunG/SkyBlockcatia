package com.stevekung.skyblockcatia.handler;

import org.lwjgl.glfw.GLFW;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.stevekungslib.keybinding.KeyBindingBase;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyBindingHandler
{
    public static KeyBinding KEY_SB_API_VIEWER;
    public static KeyBinding KEY_SB_ENDER_CHEST;
    public static KeyBinding KEY_SB_CRAFTED_MINIONS;
    public static KeyBinding KEY_SB_CRAFTING_TABLE;
    public static KeyBinding KEY_SB_VIEW_RECIPE;
    public static KeyBinding KEY_SB_MENU;
    public static KeyBinding KEY_SB_OPEN_WIKI;

    public static void init()
    {
        KeyBindingHandler.KEY_SB_API_VIEWER = new KeyBindingBase("key.sb_api_viewer.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_F6);
        KeyBindingHandler.KEY_SB_ENDER_CHEST = new KeyBindingBase("key.sb_ender_chest.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_KP_5);
        KeyBindingHandler.KEY_SB_CRAFTED_MINIONS = new KeyBindingBase("key.sb_crafted_minions.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_KP_2);
        KeyBindingHandler.KEY_SB_CRAFTING_TABLE = new KeyBindingBase("key.sb_crafting_table.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_KP_ADD);
        KeyBindingHandler.KEY_SB_VIEW_RECIPE = new KeyBindingBase("key.sb_view_recipe.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_B);
        KeyBindingHandler.KEY_SB_MENU = new KeyBindingBase("key.sb_menu.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_M);
        KeyBindingHandler.KEY_SB_OPEN_WIKI = new KeyBindingBase("key.sb_open_wiki.desc", SkyBlockcatiaMod.MOD_ID, GLFW.GLFW_KEY_APOSTROPHE);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_SB_API_VIEWER);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_SB_ENDER_CHEST);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_SB_CRAFTED_MINIONS);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_SB_CRAFTING_TABLE);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_SB_VIEW_RECIPE);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_SB_MENU);
        ClientRegistry.registerKeyBinding(KeyBindingHandler.KEY_SB_OPEN_WIKI);
    }
}