package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;
import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.core.SkyBlockcatia;
import net.minecraft.ChatFormatting;

public record CommunityUpgrades(@SerializedName("currently_upgrading") com.stevekung.skyblockcatia.utils.skyblock.api.CommunityUpgrades.Upgrading currentUpgrade, @SerializedName("upgrade_states") List<States> upgradeStates)
{
    public record Upgrading(String upgrade, @SerializedName("new_tier") int tier)
    {
        @Override
        public String toString()
        {
            return WordUtils.capitalize(this.upgrade.replace("_", " ") + ": Tier " + this.tier);
        }
    }

    public record States(String upgrade, int tier) {}

    public static class Data
    {
        private final int tier;
        private String displayName;
        private Type type;

        public Data(String upgradeType, int tier)
        {
            this.tier = tier;

            try
            {
                this.type = CommunityUpgrades.Type.valueOf(upgradeType.toUpperCase(Locale.ROOT));
                this.displayName = this.type.name;
            }
            catch (Exception e)
            {
                this.displayName = ChatFormatting.RED + upgradeType;
                SkyBlockcatia.LOGGER.warning("Found an unknown upgrade! type: {}", upgradeType);
            }
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public int getTier()
        {
            return this.tier;
        }

        public CommunityUpgrades.Type getType()
        {
            return this.type;
        }

        public static CommunityUpgrades.Data getData(String type, int tier)
        {
            return new CommunityUpgrades.Data(type, tier);
        }
    }

    public enum Type
    {
        ISLAND_SIZE("Island Size", 10),
        MINION_SLOTS("Minion Slots", 5),
        GUESTS_COUNT("Guests Limit", 5),
        COOP_SLOTS("Co-op Slots", 3),
        COINS_ALLOWANCE("Coins Allowance", 5);

        String name;
        private final int maxed;

        Type(String name, int maxed)
        {
            this.name = name;
            this.maxed = maxed;
        }

        public int getMaxed()
        {
            return this.maxed;
        }
    }
}