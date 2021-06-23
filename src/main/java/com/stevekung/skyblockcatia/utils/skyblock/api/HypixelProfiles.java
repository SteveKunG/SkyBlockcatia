package com.stevekung.skyblockcatia.utils.skyblock.api;

import com.google.gson.annotations.SerializedName;

public class HypixelProfiles
{
    private final boolean success;
    private final String cause;
    private final HypixelPlayerProfile player;

    public HypixelProfiles(boolean success, String cause, HypixelPlayerProfile player)
    {
        this.success = success;
        this.cause = cause;
        this.player = player;
    }

    public boolean isSuccess()
    {
        return this.success;
    }

    public String getCause()
    {
        return this.cause;
    }

    public HypixelPlayerProfile getPlayer()
    {
        return this.player;
    }

    public static class HypixelPlayerProfile
    {
        private final String newPackageRank;
        private final String rank;
        private final String rankPlusColor;
        private final String monthlyPackageRank;
        private final String monthlyRankColor;
        private final String prefix;

        @SerializedName("displayname")
        private final String displayName;
        private final String uuid;

        public HypixelPlayerProfile(String newPackageRank, String rank, String rankPlusColor, String monthlyPackageRank, String monthlyRankColor, String prefix, String displayname, String uuid)
        {
            this.newPackageRank = newPackageRank;
            this.rank = rank;
            this.rankPlusColor = rankPlusColor;
            this.monthlyPackageRank = monthlyPackageRank;
            this.monthlyRankColor = monthlyRankColor;
            this.prefix = prefix;
            this.displayName = displayname;
            this.uuid = uuid;
        }

        public String getNewPackageRank()
        {
            return this.newPackageRank;
        }

        public String getRank()
        {
            return this.rank;
        }

        public String getRankPlusColor()
        {
            return this.rankPlusColor;
        }

        public String getMonthlyPackageRank()
        {
            return this.monthlyPackageRank;
        }

        public String getMonthlyRankColor()
        {
            return this.monthlyRankColor;
        }

        public String getPrefix()
        {
            return this.prefix;
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public String getUUID()
        {
            return this.uuid;
        }
    }
}