package com.stevekung.skyblockcatia.config;

import java.io.IOException;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaFabricMod;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;

public class SkyBlockcatiaConfig
{
    public boolean enableSkinRenderingFix = false;
    public boolean disableHurtCameraEffect = false;
    public boolean enableChatInContainerScreen = true;
    public String hypixelApiKey = "";

    public static Screen createConfigScreen(Screen parent)
    {
        SkyBlockcatiaConfig config = SkyBlockcatiaFabricMod.CONFIG.getConfig();
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(LangUtils.translate("ui.skyblockcatia.config.title"));
        builder.setSavingRunnable(() ->
        {
            try
            {
                SkyBlockcatiaFabricMod.CONFIG.saveConfig();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        });
        ConfigEntryBuilder entry = ConfigEntryBuilder.create();
        ConfigCategory generalCategory = builder.getOrCreateCategory(TextComponentUtils.component("General Settings"));
        generalCategory.addEntry(entry.startBooleanToggle(LangUtils.translate("skyblockcatia.configgui.enable_skin_rendering_fix"), config.enableSkinRenderingFix).setSaveConsumer(value -> config.enableSkinRenderingFix = value).setDefaultValue(false).build());
        generalCategory.addEntry(entry.startBooleanToggle(LangUtils.translate("skyblockcatia.configgui.disable_hurt_camera_effect"), config.disableHurtCameraEffect).setSaveConsumer(value -> config.disableHurtCameraEffect = value).setDefaultValue(false).build());
        generalCategory.addEntry(entry.startBooleanToggle(LangUtils.translate("skyblockcatia.configgui.enable_chat_in_container_screen"), config.enableChatInContainerScreen).setSaveConsumer(value -> config.enableChatInContainerScreen = value).setDefaultValue(true).build());
        generalCategory.addEntry(entry.startStrField(LangUtils.translate("skyblockcatia.configgui.hypixel_api_key"), config.hypixelApiKey).setSaveConsumer(value -> config.hypixelApiKey = value).setDefaultValue("").build());
        return builder.build();
    }
}