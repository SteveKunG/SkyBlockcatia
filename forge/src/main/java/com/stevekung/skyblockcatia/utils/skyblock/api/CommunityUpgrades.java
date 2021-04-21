package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;
import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import net.minecraft.ChatFormatting;

public class CommunityUpgrades
{
    @SerializedName("currently_upgrading")
    private final CommunityUpgrades.Upgrading currentUpgrade;
    @SerializedName("upgrade_states")
    private final List<CommunityUpgrades.States> upgradeStates;

    public CommunityUpgrades(CommunityUpgrades.Upgrading currentUpgrade, List<CommunityUpgrades.States> upgradeStates)
    {
        this.currentUpgrade = currentUpgrade;
        this.upgradeStates = upgradeStates;
    }

    public CommunityUpgrades.Upgrading getCurrentUpgrade()
    {
        return this.currentUpgrade;
    }

    public List<CommunityUpgrades.States> getUpgradeStates()
    {
        return this.upgradeStates;
    }

    public static class Upgrading
    {
        private final String upgrade;
        @SerializedName("new_tier")
        private final int tier;

        public Upgrading(String upgrade, int tier)
        {
            this.upgrade = upgrade;
            this.tier = tier;
        }

        @Override
        public String toString()
        {
            return WordUtils.capitalize(this.upgrade.replace("_", " ") + ": Tier " + this.tier);
        }
    }

    public static class States
    {
        private final String upgrade;
        private final int tier;

        public States(String upgrade, int tier)
        {
            this.upgrade = upgrade;
            this.tier = tier;
        }

        public String getUpgrade()
        {
            return this.upgrade;
        }

        public int getTier()
        {
            return this.tier;
        }
    }

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
                SkyBlockcatiaMod.LOGGER.warning("Found an unknown upgrade! type: {}", upgradeType);
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