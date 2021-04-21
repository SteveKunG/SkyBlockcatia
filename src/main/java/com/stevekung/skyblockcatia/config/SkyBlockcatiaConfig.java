package com.stevekung.skyblockcatia.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;

public class SkyBlockcatiaConfig
{
    private static Configuration config;
    public static final String MAIN_SETTINGS = "skyblockcatia_main_settings";

    // Main Settings
    public static String hypixelApiKey;
    public static boolean enableConfirmToDisconnect;
    public static boolean enableSkinRenderingFix;
    public static boolean disableHurtCameraEffect;
    public static boolean enableShortcutGameButton;
    public static boolean enable1_15ArmorEnchantedGlint;
    public static boolean enableEnchantedGlintForSkull;
    public static boolean enableOverwriteSignEditing;
    public static boolean enableSignSelectionList;
    public static boolean enableChatInContainerScreen;
    public static boolean disableErrorLog;

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

        if (SkyBlockcatiaConfig.config.hasChanged())
        {
            SkyBlockcatiaConfig.config.save();
        }
    }

    private static List<String> addMainSetting()
    {
        Property prop;
        List<String> propOrder = new ArrayList<>();

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Hypixel API Key", "");
        SkyBlockcatiaConfig.hypixelApiKey = prop.getString();
        propOrder.add(prop.getName());

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Enable Confirm to Disconnect", false);
        SkyBlockcatiaConfig.enableConfirmToDisconnect = prop.getBoolean();
        prop.comment = LangUtils.translate("gui.config.skyblockcatia.confirm_to_disconnect");
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

        prop = SkyBlockcatiaConfig.getProperty(SkyBlockcatiaConfig.MAIN_SETTINGS, "Disable Error Log", true);
        SkyBlockcatiaConfig.disableErrorLog = prop.getBoolean();
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
        return list;
    }

    public static Configuration getConfig()
    {
        return SkyBlockcatiaConfig.config;
    }
}