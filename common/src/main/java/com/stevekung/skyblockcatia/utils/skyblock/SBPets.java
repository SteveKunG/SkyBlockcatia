package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.core.SkyBlockcatia;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.stevekungslib.utils.ItemUtils;
import com.stevekung.stevekungslib.utils.NumberUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public record SBPets(@SerializedName("held_item") com.stevekung.skyblockcatia.utils.skyblock.SBPets.HeldItem[] heldItem, Map<String, Integer> index, int[] leveling, Map<Integer, Integer> score, com.stevekung.skyblockcatia.utils.skyblock.SBPets.Skin[] skin, com.stevekung.skyblockcatia.utils.skyblock.SBPets.Type[] type)
{
    public static SBPets PETS;

    public static void getPets()
    {
        PETS = SkyBlockcatia.GSON.fromJson(DataUtils.getData("pets.json"), SBPets.class);
    }

    public HeldItem getHeldItemByName(String name)
    {
        for (var item : this.heldItem)
        {
            if (item.type().equals(name))
            {
                return item;
            }
        }
        return null;
    }

    public Type getTypeByName(String name)
    {
        for (var type : this.type)
        {
            if (type.type().equals(name))
            {
                return type;
            }
        }
        return null;
    }

    public record Skin(String type, @SerializedName("displayName") String name, String color, String uuid, String texture)
    {
        public ChatFormatting getColor()
        {
            return ChatFormatting.getByName(this.color);
        }
    }

    public enum Tier
    {
        COMMON(ChatFormatting.WHITE),
        UNCOMMON(ChatFormatting.GREEN),
        RARE(ChatFormatting.BLUE),
        EPIC(ChatFormatting.DARK_PURPLE),
        LEGENDARY(ChatFormatting.GOLD),
        MYTHIC(ChatFormatting.LIGHT_PURPLE);

        private static final Tier[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(Tier::ordinal)).toArray(Tier[]::new);
        private final ChatFormatting color;

        static
        {
            for (var rarity : values())
            {
                VALUES[rarity.ordinal()] = rarity;
            }
        }

        Tier(ChatFormatting color)
        {
            this.color = color;
        }

        public ChatFormatting getTierColor()
        {
            return this.color;
        }

        public Tier getNextRarity()
        {
            return VALUES[(this.ordinal() + 1) % VALUES.length];
        }
    }

    public record Type(String type, String skill, String uuid, String texture, Stats stats, List<String> description, Map<String, StatsPropertyArray> statsLore, Map<String, List<String>> lore, @SerializedName("lore_mode") String loreMode)
    {
        public SBSkills.Type getSkill()
        {
            return SBSkills.Type.byName(this.skill);
        }

        public ItemStack getPetItem()
        {
            return ItemUtils.getSkullItemStack(this.uuid, this.texture);
        }
    }

    public record Stats(StatsProperty damage, StatsProperty health, StatsProperty strength, @SerializedName("crit_damage") StatsProperty critDamage, @SerializedName("crit_chance") StatsProperty critChance, @SerializedName("attack_speed") StatsProperty attackSpeed, StatsProperty ferocity, StatsProperty defense, @SerializedName("true_defense") StatsProperty trueDefense, StatsProperty speed, StatsProperty intelligence, @SerializedName("sea_creature_chance") StatsProperty seaCreatureChance, @SerializedName("magic_find") StatsProperty magicFind, @SerializedName("ability_damage") StatsProperty abilityDamage) {}

    public record StatsProperty(double base, double multiply, boolean percent)
    {
        public int getValue(int level)
        {
            return (int) (this.base + this.multiply * level);
        }

        public String getString(String type, int level)
        {
            var value = this.getValue(level);
            return ChatFormatting.RESET.toString() + ChatFormatting.GRAY + type + ": " + ChatFormatting.GREEN + (value < 0 ? "" : "+") + value + (this.percent ? "%" : "");
        }
    }

    public record StatsPropertyArray(double[] base, double[] multiply, double[] additional, @SerializedName("rounding_mode") String roundingMode, @SerializedName("display_mode") String displayMode) {}
    public record HeldItem(String type, String name, String color, boolean isUpgrade, List<String> lore, Stats stats) {}

    public record Info(int currentPetLevel, int nextPetLevel, double currentPetXp, int xpRequired, double petXp, int totalPetTypeXp)
    {
        public String getPercent()
        {
            if (this.xpRequired > 0)
            {
                double percent = this.currentPetXp * 100.0D / this.xpRequired;
                return NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(percent) + "%";
            }
            else
            {
                return ChatFormatting.AQUA.toString() + ChatFormatting.BOLD + "MAX LEVEL";
            }
        }
    }

    public record Data(Tier tier, int currentLevel, Component name, boolean isActive, List<ItemStack> itemStack) {}
}