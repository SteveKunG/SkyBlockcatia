package com.stevekung.skyblockcatia.utils;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.text.WordUtils;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.EnumChatFormatting;

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
            return WordUtils.capitalize(this.upgrade.replace("_", " ") + " " + NumberUtils.intToRoman(this.tier));
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
                this.displayName = EnumChatFormatting.RED + upgradeType;
                LoggerIN.warning("Found an unknown upgrade! type: {}", upgradeType);
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
        ISLAND_SIZE("Island Size"),
        MINION_SLOTS("Minion Slots"),
        GUESTS_LIMIT("Guests Limit"),
        COOP_SLOTS("Co-op Slots"),
        COINS_ALLOWANCE("Coins Allowance");

        String name;

        private Type(String name)
        {
            this.name = name;
        }
    }
}