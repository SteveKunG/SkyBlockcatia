package com.stevekung.skyblockcatia.config;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.utils.SkyBlockAPIUtils;

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
        public final ForgeConfigSpec.ConfigValue<String> hypixelApiKey;

        General(ForgeConfigSpec.Builder builder)
        {
            builder.comment("General settings")
            .push("general");

            this.hypixelApiKey = builder
                    .translation("skyblockcatia.configgui.hypixel_api_key")
                    .define("hypixelApiKey", "");

            this.enableSkinRenderingFix = builder
                    .translation("skyblockcatia.configgui.enable_skin_rendering_fix")
                    .define("enableSkinRenderingFix", false);

            this.disableHurtCameraEffect = builder
                    .translation("skyblockcatia.configgui.disable_hurt_camera_effect")
                    .define("disableHurtCameraEffect", false);

            builder.pop();
        }
    }

    @SubscribeEvent
    public static void onLoad(ModConfig.Loading event)
    {
        SkyBlockAPIUtils.setApiKey();
        SkyBlockcatiaMod.LOGGER.info("Loaded config file {}", event.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(ModConfig.Reloading event)
    {
        SkyBlockAPIUtils.setApiKey();
        SkyBlockcatiaMod.LOGGER.info("Indicatia config just got changed on the file system");
    }
}