package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.stevekungslib.utils.ItemUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public record SBMinions(@SerializedName("unique_minions") int uniqueMinions, @SerializedName("crafted_minions") Map<Integer, Integer> craftedMinions, com.stevekung.skyblockcatia.utils.skyblock.SBMinions.Type[] type)
{
    private static final Gson GSON = new Gson();
    public static SBMinions MINIONS;

    public static void getMinions()
    {
        MINIONS = GSON.fromJson(DataUtils.getData("minions.json"), SBMinions.class);
    }

    public SBMinions.Type getTypeByName(String name)
    {
        for (SBMinions.Type type : this.type)
        {
            if (type.type().equals(name))
            {
                return type;
            }
        }
        return null;
    }

    public record Type(String type, String category, String displayName, String uuid, String texture, @SerializedName("has_tier_12") boolean hasTier12)
    {
        public SBSkills.Type getCategory()
        {
            return SBSkills.Type.byName(this.category);
        }

        public ItemStack getMinionItem()
        {
            return ItemUtils.getSkullItemStack(this.uuid, this.texture);
        }
    }

    public record Info(String minionType, String displayName, ItemStack minionItem, int minionMaxTier, SBSkills.Type category) {}
    public record Data(String minionType, String craftedTiers) {}
    public record CraftedInfo(Component minionName, String displayName, int minionMaxTier, String craftedTiers, ItemStack minionItem) {}
}