package com.stevekung.skyblockcatia.config;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;

public class SkyBlockcatiaConfig
{
    public static final ForgeConfigSpec.Builder GENERAL_BUILDER = new ForgeConfigSpec.Builder();
    public static final SkyBlockcatiaConfig.General GENERAL = new SkyBlockcatiaConfig.General(SkyBlockcatiaConfig.GENERAL_BUILDER);

    public static class General
    {
        // General
        public final ForgeConfigSpec.BooleanValue enableSkinRenderingFix;
        public final ForgeConfigSpec.BooleanValue disableHurtCameraEffect;
        public final ForgeConfigSpec.BooleanValue enableChatInContainerScreen;
        public final ForgeConfigSpec.ConfigValue<String> hypixelApiKey;

        General(ForgeConfigSpec.Builder builder)
        {
            builder.comment("General settings").push("general");

            this.hypixelApiKey = builder.translation("skyblockcatia.configgui.hypixel_api_key").define("hypixelApiKey", "");

            this.enableSkinRenderingFix = builder.translation("skyblockcatia.configgui.enable_skin_rendering_fix").define("enableSkinRenderingFix", true);

            this.disableHurtCameraEffect = builder.translation("skyblockcatia.configgui.disable_hurt_camera_effect").define("disableHurtCameraEffect", false);

            this.enableChatInContainerScreen = builder.translation("skyblockcatia.configgui.enable_chat_in_container_screen").define("enableChatInContainerScreen", true);

            builder.pop();
        }
    }

    @SubscribeEvent
    public static void onLoad(ModConfig.Loading event)
    {
        SBAPIUtils.setApiKey();
        SkyBlockcatiaMod.LOGGER.info("Loaded config file {}", event.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(ModConfig.Reloading event)
    {
        SBAPIUtils.setApiKey();
        SkyBlockcatiaMod.LOGGER.info("SkyBlockcatia config just got changed on the file system");
    }
}