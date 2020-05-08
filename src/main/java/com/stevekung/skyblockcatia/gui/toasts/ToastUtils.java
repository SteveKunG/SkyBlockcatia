package com.stevekung.skyblockcatia.gui.toasts;

import javax.annotation.Nullable;

import com.stevekung.stevekungslib.utils.ColorUtils;

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

        public ItemDropCheck(String name, ToastUtils.DropType type, ToastUtils.ToastType toastType)
        {
            this.name = name;
            this.type = type;
            this.toastType = toastType;
        }

        public ItemDropCheck(String name, String magicFind, ToastUtils.DropType type, ToastUtils.ToastType toastType)
        {
            this(name, type, toastType);
            this.magicFind = magicFind;
        }

        public String getName()
        {
            return this.name.replaceAll("\\s+$", "");
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
    }

    public enum DropType
    {
        RARE_DROP("RARE DROP!", "255,170,0"),
        PET_DROP("PET DROP!", "255,170,0"),
        DRAGON_CRYSTAL_FRAGMENT("RARE DROP!", "170,0,170"),
        DRAGON_DROP("DRAGON DROP!", "127,255,212"),
        GOOD_CATCH("GOOD CATCH!", "255,170,0"),
        GREAT_CATCH("GREAT CATCH!", "170,0,170"),
        GOOD_CATCH_COINS("GOOD CATCH!", "255,170,0"),
        GREAT_CATCH_COINS("GREAT CATCH!", "170,0,170"),
        SLAYER_RARE_DROP("RARE DROP!", "85,255,255"),
        SLAYER_VERY_RARE_DROP("VERY RARE DROP!", "85,85,255"),
        SLAYER_CRAZY_RARE_DROP("CRAZY RARE DROP!", "255,85,255"),
        COMMON_GIFT("COMMON GIFT!", "255,255,255"),
        SWEET_GIFT("SWEET GIFT!", "255,255,85"),
        RARE_GIFT("RARE GIFT!", "85,85,255"),
        SANTA_TIER("SANTA GIFT!", "255,85,85"),
        PET_LEVEL_UP("PET LEVEL UP!", "85,85,255");

        private final String name;
        private final String color;

        private DropType(String name, String color)
        {
            this.name = name;
            this.color = color;
        }

        public String getName()
        {
            return this.name;
        }

        public String getColor()
        {
            return ColorUtils.stringToRGB(this.color).toColoredFont();
        }

        public boolean isSpecialDrop()
        {
            return this == DRAGON_DROP || this == SLAYER_RARE_DROP || this == SLAYER_VERY_RARE_DROP || this == SLAYER_CRAZY_RARE_DROP || this == SANTA_TIER;
        }

        public boolean isFishingCoins()
        {
            return this == GOOD_CATCH_COINS || this == GREAT_CATCH_COINS;
        }
    }

    public enum ToastType
    {
        DROP, GIFT;
    }
}