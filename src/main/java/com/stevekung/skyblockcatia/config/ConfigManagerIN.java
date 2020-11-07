package com.stevekung.skyblockcatia.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ConfigManagerIN
{
    private static Configuration config;
    public static final String MAIN_SETTINGS = "skyblockcatia_main_settings";
    public static final String KEY_BINDING_SETTINGS = "skyblockcatia_key_binding_settings";

    // Main Settings
    public static String hypixelApiKey;
    public static boolean enableRenderInfo;
    public static boolean enableBlockhitAnimation;
    public static boolean enableAdditionalBlockhitAnimation;
    public static boolean enableOldArmorRender;
    public static boolean enableCustomPlayerList;
    public static boolean enableCustomServerSelectionGui;
    public static boolean enableConfirmToDisconnect;
    public static boolean enableSmoothSneakingView;
    public static boolean enableTransparentSkinRender;
    public static boolean enableChatMode;
    public static boolean enableSkinRenderingFix;
    public static boolean disableHurtCameraEffect;
    public static boolean enableShortcutGameButton;
    public static boolean enable1_15ArmorEnchantedGlint;
    public static boolean enableMovementHandler;
    public static boolean enableEnchantedGlintForSkull;
    public static boolean enableOverwriteSignEditing;
    public static boolean enableSignSelectionList;

    // Key Binding Settings
    public static String keyToggleSprint;
    public static String keyToggleSneak;

    public static void init(File file)
    {
        ConfigManagerIN.config = new Configuration(file);
        ConfigManagerIN.syncConfig(true);
    }

    public static void syncConfig(boolean load)
    {
        if (!ConfigManagerIN.config.isChild)
        {
            if (load)
            {
                ConfigManagerIN.config.load();
            }
        }

        ConfigManagerIN.config.setCategoryPropertyOrder(ConfigManagerIN.MAIN_SETTINGS, ConfigManagerIN.addMainSetting());
        ConfigManagerIN.config.setCategoryPropertyOrder(ConfigManagerIN.KEY_BINDING_SETTINGS, ConfigManagerIN.addKeyBindingSetting());

        if (ConfigManagerIN.config.hasChanged())
        {
            ConfigManagerIN.config.save();
        }
    }

    private static List<String> addMainSetting()
    {
        Property prop;
        List<String> propOrder = new ArrayList<>();

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Render Info", true);
        ConfigManagerIN.enableRenderInfo = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Hypixel API Key", "");
        ConfigManagerIN.hypixelApiKey = prop.getString();
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Blockhit Animation", false);
        ConfigManagerIN.enableBlockhitAnimation = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.blockhit_animation");
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Additional Blockhit Animation", false);
        ConfigManagerIN.enableAdditionalBlockhitAnimation = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.additional_blockhit_animation");
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Armor Hurt Overlay", false);
        ConfigManagerIN.enableOldArmorRender = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.old_armor_render");
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Custom Player List", false);
        ConfigManagerIN.enableCustomPlayerList = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.custom_player_list");
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Custom Server Selection GUI", false);
        ConfigManagerIN.enableCustomServerSelectionGui = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.custom_server_selection");
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Confirm to Disconnect", false);
        ConfigManagerIN.enableConfirmToDisconnect = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.confirm_to_disconnect");
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Smooth Sneaking View", false);
        ConfigManagerIN.enableSmoothSneakingView = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.smooth_eye_height");
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Transparent Skin Render", false);
        ConfigManagerIN.enableTransparentSkinRender = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.alternate_player_model");
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Chat Mode", false);
        ConfigManagerIN.enableChatMode = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Skin Rendering Fix", false).setRequiresMcRestart(true);
        ConfigManagerIN.enableSkinRenderingFix = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Disable Hurt Camera Effect", false);
        ConfigManagerIN.disableHurtCameraEffect = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Shortcut Game Button", true);
        ConfigManagerIN.enableShortcutGameButton = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable 1.15 Armor Enchanted Glint", false);
        ConfigManagerIN.enable1_15ArmorEnchantedGlint = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Movement Handler", false);
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.movement_handler");
        ConfigManagerIN.enableMovementHandler = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Enchanted Glint for Skulls", true);
        ConfigManagerIN.enableEnchantedGlintForSkull = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Overwrite Sign Editing", true);
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.overwrite_sign_editing");
        ConfigManagerIN.enableOverwriteSignEditing = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.MAIN_SETTINGS, "Enable Sign Selection List", true);
        ConfigManagerIN.enableSignSelectionList = prop.getBoolean();
        propOrder.add(prop.getName());

        return propOrder;
    }

    private static List<String> addKeyBindingSetting()
    {
        Property prop;
        List<String> propOrder = new ArrayList<>();
        prop = ConfigManagerIN.getProperty(ConfigManagerIN.KEY_BINDING_SETTINGS, "Key Toggle Sprint (Ctrl) + (Key)", "29,31");
        ConfigManagerIN.keyToggleSprint = prop.getString();
        propOrder.add(prop.getName());

        prop = ConfigManagerIN.getProperty(ConfigManagerIN.KEY_BINDING_SETTINGS, "Key Toggle Sneak (Ctrl) + (Key)", "29,42");
        ConfigManagerIN.keyToggleSneak = prop.getString();
        propOrder.add(prop.getName());
        return propOrder;
    }

    public static Property getProperty(String category, String name, boolean defaultValue)
    {
        return ConfigManagerIN.config.get(category, name, defaultValue);
    }

    public static Property getProperty(String category, String name, String defaultValue)
    {
        return ConfigManagerIN.config.get(category, name, defaultValue);
    }

    public static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> list = new ArrayList<>();
        list.add(new ConfigElement(ConfigManagerIN.config.getCategory(ConfigManagerIN.MAIN_SETTINGS)));
        list.add(new DummyConfigElement("Key Code Example", "http://minecraft.gamepedia.com/Key_codes", ConfigGuiType.STRING, "gui.config.key_code_example"));
        list.add(new ConfigElement(ConfigManagerIN.config.getCategory(ConfigManagerIN.KEY_BINDING_SETTINGS)));
        return list;
    }

    public static Configuration getConfig()
    {
        return ConfigManagerIN.config;
    }
}