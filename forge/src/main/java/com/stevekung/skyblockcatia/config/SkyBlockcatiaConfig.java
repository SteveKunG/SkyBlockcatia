package com.stevekung.skyblockcatia.config;

import org.apache.commons.lang3.tuple.Pair;
import com.stevekung.skyblockcatia.core.SkyBlockcatia;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;

public class SkyBlockcatiaConfig
{
    public static final ForgeConfigSpec GENERAL_SPEC;
    public static final General GENERAL;

    static
    {
        Pair<General, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(General::new);
        GENERAL_SPEC = specPair.getRight();
        GENERAL = specPair.getLeft();
    }

    public static class General
    {
        // General
        public final ForgeConfigSpec.BooleanValue enableSkinRenderingFix;
        public final ForgeConfigSpec.BooleanValue disableHurtCameraEffect;
        public final ForgeConfigSpec.BooleanValue enableChatInContainerScreen;
        public final ForgeConfigSpec.ConfigValue<String> hypixelApiKey;

        General(ForgeConfigSpec.Builder builder)
        {
            builder.comment("General settings");
            builder.push("general");

            this.hypixelApiKey = builder.translation("text.autoconfig.skyblockcatia.option.general.hypixelApiKey").define("hypixelApiKey", "");

            this.enableSkinRenderingFix = builder.translation("text.autoconfig.skyblockcatia.option.general.enableSkinRenderingFix").define("enableSkinRenderingFix", true);

            this.disableHurtCameraEffect = builder.translation("text.autoconfig.skyblockcatia.option.general.disableHurtCameraEffect").define("disableHurtCameraEffect", false);

            this.enableChatInContainerScreen = builder.translation("text.autoconfig.skyblockcatia.option.general.enableChatInContainerScreen").define("enableChatInContainerScreen", true);

            builder.pop();
        }
    }

    @SubscribeEvent
    public static void onLoad(ModConfig.Loading event)
    {
        SBAPIUtils.setApiKey();
        SkyBlockcatia.LOGGER.info("Loaded config file {}", event.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(ModConfig.Reloading event)
    {
        SBAPIUtils.setApiKey();
        SkyBlockcatia.LOGGER.info("SkyBlockcatia config just got changed on the file system");
    }
}