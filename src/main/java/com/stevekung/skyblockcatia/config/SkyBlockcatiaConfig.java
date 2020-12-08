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

public class SkyBlockcatiaConfig
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
    public static boolean enableChatInContainerScreen;

    // Key Binding Settings
    public static String keyToggleSprint;
    public static String keyToggleSneak;

    public static void init(File file)
    {
        SkyBlockcatiaConfig.config = new Configuration(file);
        SkyBlockcatiaConfig.syncConfig(true);
    }

    public static void syncConfig(boolean load)
    {
        if (!SkyBlockcatiaConfig.config.isChild)
        {
            if (load)
            {
                SkyBlockcatiaConfig.config.load();
            }
        }

        SkyBlockcatiaConfig.config.setCategoryPropertyOrder(SkyBlockcatiaConfig.MAIN_SETTINGS, SkyBlockcatiaConfig.addMainSetting());
        SkyBlockcatiaConfig.config.setCategoryPropertyOrder(SkyBlockcatiaConfig.KEY_BINDING_SETTINGS, SkyBlockcatiaConfig.addKeyBindingSetting());

        if (SkyBlockcatiaConfig.config.hasChanged())
        {
            SkyBlockcatiaConfig.config.save();
        }
    }

    private static List<String> addMainSetting()
    {
        Property prop;
        List<String> propOrder = new ArrayList<>();

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Render Info", true);
        SkyBlockcatiaConfig.enableRenderInfo = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Hypixel API Key", "");
        SkyBlockcatiaConfig.hypixelApiKey = prop.getString();
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Blockhit Animation", false);
        SkyBlockcatiaConfig.enableBlockhitAnimation = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.blockhit_animation");
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Additional Blockhit Animation", false);
        SkyBlockcatiaConfig.enableAdditionalBlockhitAnimation = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.additional_blockhit_animation");
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Armor Hurt Overlay", false);
        SkyBlockcatiaConfig.enableOldArmorRender = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.old_armor_render");
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Custom Player List", false);
        SkyBlockcatiaConfig.enableCustomPlayerList = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.custom_player_list");
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Custom Server Selection GUI", false);
        SkyBlockcatiaConfig.enableCustomServerSelectionGui = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.custom_server_selection");
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Confirm to Disconnect", false);
        SkyBlockcatiaConfig.enableConfirmToDisconnect = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.confirm_to_disconnect");
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Smooth Sneaking View", false);
        SkyBlockcatiaConfig.enableSmoothSneakingView = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.smooth_eye_height");
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Transparent Skin Render", false);
        SkyBlockcatiaConfig.enableTransparentSkinRender = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.alternate_player_model");
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Chat Mode", false);
        SkyBlockcatiaConfig.enableChatMode = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Skin Rendering Fix", false).setRequiresMcRestart(true);
        SkyBlockcatiaConfig.enableSkinRenderingFix = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Disable Hurt Camera Effect", false);
        SkyBlockcatiaConfig.disableHurtCameraEffect = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Shortcut Game Button", true);
        SkyBlockcatiaConfig.enableShortcutGameButton = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable 1.15 Armor Enchanted Glint", false);
        SkyBlockcatiaConfig.enable1_15ArmorEnchantedGlint = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Movement Handler", false);
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.movement_handler");
        SkyBlockcatiaConfig.enableMovementHandler = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Enchanted Glint for Skulls", true);
        SkyBlockcatiaConfig.enableEnchantedGlintForSkull = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Overwrite Sign Editing", true);
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.overwrite_sign_editing");
        SkyBlockcatiaConfig.enableOverwriteSignEditing = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Sign Selection List", true);
        SkyBlockcatiaConfig.enableSignSelectionList = prop.getBoolean();
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Chat in Container Screen", true);
        SkyBlockcatiaConfig.enableChatInContainerScreen = prop.getBoolean();
        propOrder.add(prop.getName());

        return propOrder;
    }

    private static List<String> addKeyBindingSetting()
    {
        Property prop;
        List<String> propOrder = new ArrayList<>();
        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.KEY_BINDING_SETTINGS, "Key Toggle Sprint (Ctrl) + (Key)", "29,31");
        SkyBlockcatiaConfig.keyToggleSprint = prop.getString();
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.KEY_BINDING_SETTINGS, "Key Toggle Sneak (Ctrl) + (Key)", "29,42");
        SkyBlockcatiaConfig.keyToggleSneak = prop.getString();
        propOrder.add(prop.getName());
        return propOrder;
    }

    public static Property getProperty(String category, String name, boolean defaultValue)
    {
        return SkyBlockcatiaConfig.config.get(category, name, defaultValue);
    }

    public static Property getProperty(String category, String name, String defaultValue)
    {
        return SkyBlockcatiaConfig.config.get(category, name, defaultValue);
    }

    public static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> list = new ArrayList<>();
        list.add(new ConfigElement(SkyBlockcatiaConfig.config.getCategory(SkyBlockcatiaConfig.MAIN_SETTINGS)));
        list.add(new DummyConfigElement("Key Code Example", "http://minecraft.gamepedia.com/Key_codes", ConfigGuiType.STRING, "gui.config.key_code_example"));
        list.add(new ConfigElement(SkyBlockcatiaConfig.config.getCategory(SkyBlockcatiaConfig.KEY_BINDING_SETTINGS)));
        return list;
    }

    public static Configuration getConfig()
    {
        return SkyBlockcatiaConfig.config;
    }
}