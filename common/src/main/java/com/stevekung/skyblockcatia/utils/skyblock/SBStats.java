package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.core.SkyBlockcatia;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.stevekungslib.utils.NumberUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;

public record SBStats(List<String> blacklist, @SerializedName("current_locations") Map<String, String> currentLocations, @SerializedName("sea_creatures") List<String> seaCreatures, Map<String, String> renamed)
{
    public static SBStats STATS;

    public static void getStats()
    {
        STATS = SkyBlockcatia.GSON.fromJson(DataUtils.getData("stats.json"), SBStats.class);
    }

    public static class Display
    {
        private Component component;
        private String name;
        private final double value;
        private String valueString;

        public Display(Component component, double value)
        {
            this.component = component;
            this.value = value;
        }

        public Display(String name, double value)
        {
            this.name = name;
            this.value = value;
        }

        public Display(String name, String valueString)
        {
            this.name = name;
            this.value = 0;
            this.valueString = valueString;
        }

        public Component getName()
        {
            return Objects.requireNonNullElseGet(this.component, () -> TextComponentUtils.component(StringUtil.isNullOrEmpty(this.name) ? "" : this.name));
        }

        public double getValue()
        {
            return this.value;
        }

        public String getValueByString()
        {
            if (this.name == null || this.name.startsWith(ChatFormatting.YELLOW.toString()))
            {
                return "";
            }
            else if (!StringUtil.isNullOrEmpty(this.valueString))
            {
                return this.valueString;
            }
            return NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(this.value);
        }
    }
}