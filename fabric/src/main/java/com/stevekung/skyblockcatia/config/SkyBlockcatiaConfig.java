package com.stevekung.skyblockcatia.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "skyblockcatia")
@Config.Gui.Background("minecraft:textures/block/stone_bricks.png")
public final class SkyBlockcatiaConfig implements ConfigData
{
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.TransitiveObject
    public GeneralCategory general;

    public SkyBlockcatiaConfig()
    {
        this.general = new GeneralCategory();
    }

    public static class GeneralCategory
    {
        public String hypixelApiKey = "";
        @Comment("Some players has black overlay on their skin. Enable this will cutout the black overlay.\n" + "(default value: true)")
        @ConfigEntry.Gui.RequiresRestart
        public boolean enableSkinRenderingFix = true;
        public boolean disableHurtCameraEffect = false;
        public boolean enableChatInContainerScreen = true;
    }
}