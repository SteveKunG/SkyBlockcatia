package com.stevekung.skyblockcatia.utils.skyblock;

import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.skyblockcatia.utils.RenderUtils;

import net.minecraft.item.ItemStack;

public class SBMinions
{
    private static final Gson GSON = new Gson();
    public static SBMinions MINIONS;

    @SerializedName("unique_minions")
    private final int uniqueMinions;
    @SerializedName("crafted_minions")
    private final Map<Integer, Integer> craftedMinions;
    private final Type[] type;

    public SBMinions(int uniqueMinions, Map<Integer, Integer> craftedMinions, Type[] type)
    {
        this.uniqueMinions = uniqueMinions;
        this.craftedMinions = craftedMinions;
        this.type = type;
    }

    public int getUniqueMinions()
    {
        return this.uniqueMinions;
    }

    public Map<Integer, Integer> getCraftedMinions()
    {
        return this.craftedMinions;
    }

    public Type[] getType()
    {
        return this.type;
    }

    public static void getMinions() throws IOException
    {
        MINIONS = GSON.fromJson(DataUtils.getData("minions.json"), SBMinions.class);
    }

    public SBMinions.Type getTypeByName(String name)
    {
        for (SBMinions.Type type : this.type)
        {
            if (type.getType().equals(name))
            {
                return type;
            }
        }
        return null;
    }

    public static class Type
    {
        private final String type;
        private final String category;
        private final String displayName;
        private final String uuid;
        private final String texture;
        @SerializedName("has_tier_12")
        private final boolean hasTier12;

        public Type(String type, String category, String displayName, String uuid, String texture, boolean hasTier12)
        {
            this.type = type;
            this.category = category;
            this.displayName = displayName;
            this.uuid = uuid;
            this.texture = texture;
            this.hasTier12 = hasTier12;
        }

        public String getType()
        {
            return this.type;
        }

        public SBSkills.Type getCategory()
        {
            return SBSkills.Type.byName(this.category);
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public ItemStack getMinionItem()
        {
            return RenderUtils.getSkullItemStack(this.uuid, this.texture);
        }

        public boolean hasTier12()
        {
            return this.hasTier12;
        }
    }

    public static class Info
    {
        private final String minionType;
        private final String displayName;
        private final ItemStack minionItem;
        private final int minionMaxTier;
        private final SBSkills.Type category;

        public Info(String minionType, String displayName, ItemStack minionItem, int minionMaxTier, SBSkills.Type category)
        {
            this.minionType = minionType;
            this.displayName = displayName;
            this.minionItem = minionItem;
            this.minionMaxTier = minionMaxTier;
            this.category = category;
        }

        public String getMinionType()
        {
            return this.minionType;
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public ItemStack getMinionItem()
        {
            return this.minionItem;
        }

        public int getMinionMaxTier()
        {
            return this.minionMaxTier;
        }

        public SBSkills.Type getMinionCategory()
        {
            return this.category;
        }
    }

    public static class Data
    {
        private final String minionType;
        private final String craftedTiers;

        public Data(String minionType, String craftedTiers)
        {
            this.minionType = minionType;
            this.craftedTiers = craftedTiers;
        }

        public String getMinionType()
        {
            return this.minionType;
        }

        public String getCraftedTiers()
        {
            return this.craftedTiers;
        }
    }

    public static class CraftedInfo
    {
        private final String minionName;
        private final String displayName;
        private final int minionMaxTier;
        private final String craftedTiers;
        private final ItemStack minionItem;
        private final SBSkills.Type category;

        public CraftedInfo(String minionName, String displayName, int minionMaxTier, String craftedTiers, ItemStack minionItem, SBSkills.Type category)
        {
            this.minionName = minionName;
            this.displayName = displayName;
            this.minionMaxTier = minionMaxTier;
            this.craftedTiers = craftedTiers;
            this.minionItem = minionItem;
            this.category = category;
        }

        public String getMinionName()
        {
            return this.minionName;
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public int getMinionMaxTier()
        {
            return this.minionMaxTier;
        }

        public String getCraftedTiers()
        {
            return this.craftedTiers;
        }

        public ItemStack getMinionItem()
        {
            return this.minionItem;
        }

        public SBSkills.Type getMinionCategory()
        {
            return this.category;
        }
    }
}