package com.stevekung.skyblockcatia.gui.toasts;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.stevekung.skyblockcatia.utils.ColorUtils;

import net.minecraft.item.ItemStack;

public class ToastUtils
{
    public static class ItemDrop
    {
        private final ItemStack itemStack;
        private final DropType type;

        public ItemDrop(ItemStack itemStack, DropType type)
        {
            this.itemStack = itemStack;
            this.type = type;
        }

        public ItemStack getItemStack()
        {
            return this.itemStack;
        }

        public DropType getType()
        {
            return this.type;
        }
    }

    public static class ItemDropCheck
    {
        private final String name;
        private String magicFind;
        private final ToastUtils.DropType type;
        private final ToastUtils.ToastType toastType;
        private final long timestamp;

        public ItemDropCheck(String name, ToastUtils.DropType type, ToastUtils.ToastType toastType)
        {
            this.name = name;
            this.type = type;
            this.toastType = toastType;
            this.timestamp = System.currentTimeMillis();
        }

        public ItemDropCheck(String name, String magicFind, ToastUtils.DropType type, ToastUtils.ToastType toastType)
        {
            this(name, type, toastType);
            this.magicFind = magicFind;
        }

        public String getName()
        {
            return this.name.replaceAll("\\s+$", "").replace("\u00a7r", "");
        }

        @Nullable
        public String getMagicFind()
        {
            return this.magicFind;
        }

        public ToastUtils.DropType getType()
        {
            return this.type;
        }

        public ToastUtils.ToastType getToastType()
        {
            return this.toastType;
        }

        public long getTimestamp()
        {
            return this.timestamp;
        }
    }

    public enum DropType
    {
        RARE_DROP("RARE DROP!", "255,170,0", ImmutableList.of(DropCondition.FORMAT)),
        PET_DROP("PET DROP!", "255,170,0"),
        DRAGON_CRYSTAL_FRAGMENT("RARE DROP!", "170,0,170"),
        BOSS_DROP("BOSS DROP!", "127,255,212", ImmutableList.of(DropCondition.SPECIAL_DROP)),
        GOOD_CATCH("GOOD CATCH!", "255,170,0"),
        GREAT_CATCH("GREAT CATCH!", "170,0,170"),
        GOOD_CATCH_COINS("GOOD CATCH!", "255,170,0", ImmutableList.of(DropCondition.FISHING_COINS)),
        GREAT_CATCH_COINS("GREAT CATCH!", "170,0,170", ImmutableList.of(DropCondition.FISHING_COINS)),
        SLAYER_RARE_DROP("RARE DROP!", "85,255,255", ImmutableList.of(DropCondition.FORMAT, DropCondition.SPECIAL_DROP)),
        SLAYER_VERY_RARE_DROP_BLUE("VERY RARE DROP!", "85,85,255", ImmutableList.of(DropCondition.FORMAT, DropCondition.SPECIAL_DROP)),
        SLAYER_VERY_RARE_DROP_PURPLE("VERY RARE DROP!", "170,0,170", ImmutableList.of(DropCondition.FORMAT, DropCondition.SPECIAL_DROP)),
        SLAYER_CRAZY_RARE_DROP("CRAZY RARE DROP!", "255,85,255", ImmutableList.of(DropCondition.FORMAT, DropCondition.SPECIAL_DROP)),
        COMMON_GIFT("COMMON GIFT!", "255,255,255"),
        SWEET_GIFT("SWEET GIFT!", "255,255,85"),
        RARE_GIFT("RARE GIFT!", "85,85,255"),
        SANTA_TIER("SANTA GIFT!", "255,85,85", ImmutableList.of(DropCondition.SPECIAL_DROP)),
        PET_LEVEL_UP("PET LEVEL UP!", "85,85,255"),
        DUNGEON_QUALITY_DROP("DUNGEON DROP!", "255,69,0", ImmutableList.of(DropCondition.SPECIAL_DROP, DropCondition.CONTAINS)),
        DUNGEON_REWARD_DROP("DUNGEON REWARD!", "255,69,0", ImmutableList.of(DropCondition.SPECIAL_DROP, DropCondition.CONTAINS)),
        ;

        private final String name;
        private final String color;
        private final List<DropCondition> properties;

        private DropType(String name, String color)
        {
            this(color, color, ImmutableList.of());
        }

        private DropType(String name, String color, List<DropCondition> properties)
        {
            this.name = name;
            this.color = color;
            this.properties = properties;
        }

        public String getName()
        {
            return this.name;
        }

        public String getColor()
        {
            return ColorUtils.stringToRGB(this.color).toColoredFont();
        }

        public boolean matches(DropCondition... condition)
        {
            return this.properties.containsAll(Arrays.asList(condition));
        }
    }

    public enum ToastType
    {
        DROP, GIFT;
    }

    public enum DropCondition
    {
        FISHING_COINS,
        SPECIAL_DROP,
        CONTAINS,
        FORMAT;
    }
}