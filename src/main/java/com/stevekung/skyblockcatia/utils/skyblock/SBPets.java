package com.stevekung.skyblockcatia.utils.skyblock;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.DataGetter;
import com.stevekung.stevekungslib.utils.ItemUtils;
import com.stevekung.stevekungslib.utils.NumberUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class SBPets
{
    public static SBPets PETS;

    @SerializedName("held_item")
    private final HeldItem[] heldItem;
    private final Map<String, Integer> index;
    private final int[] leveling;
    private final Score[] score;
    private final Skin[] skin;
    private final Type[] type;

    public SBPets(HeldItem[] heldItem, Map<String, Integer> index, int[] leveling, Score[] score, Skin[] skin, Type[] type)
    {
        this.heldItem = heldItem;
        this.index = index;
        this.leveling = leveling;
        this.score = score;
        this.skin = skin;
        this.type = type;
    }

    public static void getPets() throws IOException
    {
        PETS = TextComponentUtils.GSON.fromJson(DataGetter.getData("pets.json"), SBPets.class);
    }

    public HeldItem getHeldItemByName(String name)
    {
        for (HeldItem item : this.heldItem)
        {
            if (item.getType().equals(name))
            {
                return item;
            }
        }
        return null;
    }

    public Map<String, Integer> getIndex()
    {
        return this.index;
    }

    public int[] getLeveling()
    {
        return this.leveling;
    }

    public Score[] getScore()
    {
        return this.score;
    }

    public Skin[] getSkin()
    {
        return this.skin;
    }

    public Type getTypeByName(String name)
    {
        for (Type type : this.type)
        {
            if (type.getType().equals(name))
            {
                return type;
            }
        }
        return null;
    }

    public class Skin
    {
        private final String type;
        @SerializedName("displayName")
        private final String name;
        private final String color;
        private final String uuid;
        private final String texture;

        public Skin(String type, String name, String color, String uuid, String texture)
        {
            this.type = type;
            this.name = name;
            this.color = color;
            this.uuid = uuid;
            this.texture = texture;
        }

        public String getType()
        {
            return this.type;
        }

        public String getName()
        {
            return this.name;
        }

        public TextFormatting getColor()
        {
            return TextFormatting.getValueByName(this.color);
        }

        public String getUUID()
        {
            return this.uuid;
        }

        public String getTexture()
        {
            return this.texture;
        }
    }

    public enum Tier
    {
        COMMON(TextFormatting.WHITE),
        UNCOMMON(TextFormatting.GREEN),
        RARE(TextFormatting.BLUE),
        EPIC(TextFormatting.DARK_PURPLE),
        LEGENDARY(TextFormatting.GOLD),
        MYTHIC(TextFormatting.LIGHT_PURPLE);

        private static final Tier[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(Tier::ordinal)).toArray(size -> new Tier[size]);
        private final TextFormatting color;

        static
        {
            for (Tier rarity : values())
            {
                VALUES[rarity.ordinal()] = rarity;
            }
        }

        Tier(TextFormatting color)
        {
            this.color = color;
        }

        public TextFormatting getTierColor()
        {
            return this.color;
        }

        public Tier getNextRarity()
        {
            return VALUES[(this.ordinal() + 1) % VALUES.length];
        }
    }

    public class Type
    {
        private final String type;
        private final String skill;
        private final String uuid;
        private final String texture;

        public Type(String type, String skill, String uuid, String texture)
        {
            this.type = type;
            this.skill = skill;
            this.uuid = uuid;
            this.texture = texture;
        }

        public String getType()
        {
            return this.type;
        }

        public SBSkills.Type getSkill()
        {
            return SBSkills.Type.byName(this.skill);
        }

        public ItemStack getPetItem()
        {
            return ItemUtils.getSkullItemStack(this.uuid, this.texture);
        }
    }

    public class HeldItem
    {
        private final String type;
        private final String name;
        private final String color;
        private final boolean isUpgrade;

        public HeldItem(String type, String name, String color, boolean isUpgrade)
        {
            this.type = type;
            this.name = name;
            this.color = color;
            this.isUpgrade = isUpgrade;
        }

        public String getType()
        {
            return this.type;
        }

        public String getName()
        {
            return this.name;
        }

        public String getColor()
        {
            return this.color;
        }

        public boolean isUpgrade()
        {
            return this.isUpgrade;
        }
    }

    public class Score
    {
        private final int score;
        @SerializedName("magic_find")
        private final int magicFind;

        public Score(int score, int magicFind)
        {
            this.score = score;
            this.magicFind = magicFind;
        }

        public int getScore()
        {
            return this.score;
        }

        public int getMagicFind()
        {
            return this.magicFind;
        }
    }

    public static class Info
    {
        private final int currentPetLevel;
        private final int nextPetLevel;
        private final double currentPetXp;
        private final int xpRequired;
        private final double petXp;
        private final int totalPetTypeXp;

        public Info(int currentPetLevel, int nextPetLevel, double currentPetXp, int xpRequired, double petXp, int totalPetTypeXp)
        {
            this.currentPetLevel = currentPetLevel;
            this.nextPetLevel = nextPetLevel;
            this.currentPetXp = currentPetXp;
            this.xpRequired = xpRequired;
            this.petXp = petXp;
            this.totalPetTypeXp = totalPetTypeXp;
        }

        public int getCurrentPetLevel()
        {
            return this.currentPetLevel;
        }

        public int getNextPetLevel()
        {
            return this.nextPetLevel;
        }

        public double getCurrentPetXp()
        {
            return this.currentPetXp;
        }

        public int getXpRequired()
        {
            return this.xpRequired;
        }

        public double getPetXp()
        {
            return this.petXp;
        }

        public int getTotalPetTypeXp()
        {
            return this.totalPetTypeXp;
        }

        public String getPercent()
        {
            if (this.xpRequired > 0)
            {
                double percent = this.currentPetXp * 100.0D / this.xpRequired;
                return NumberUtils.NUMBER_FORMAT_WITH_DECIMAL.format(percent) + "%";
            }
            else
            {
                return TextFormatting.AQUA.toString() + TextFormatting.BOLD + "MAX LEVEL";
            }
        }
    }

    public static class Data
    {
        private final SBPets.Tier tier;
        private final int currentLevel;
        private final ITextComponent name;
        private final boolean isActive;
        private final List<ItemStack> itemStack;

        public Data(SBPets.Tier tier, int currentLevel, ITextComponent name, boolean isActive, List<ItemStack> itemStack)
        {
            this.tier = tier;
            this.currentLevel = currentLevel;
            this.name = name;
            this.isActive = isActive;
            this.itemStack = itemStack;
        }

        public SBPets.Tier getTier()
        {
            return this.tier;
        }

        public List<ItemStack> getItemStack()
        {
            return this.itemStack;
        }

        public int getCurrentLevel()
        {
            return this.currentLevel;
        }

        public ITextComponent getName()
        {
            return this.name;
        }

        public boolean isActive()
        {
            return this.isActive;
        }
    }
}