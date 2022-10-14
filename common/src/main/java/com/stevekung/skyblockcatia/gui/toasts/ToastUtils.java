package com.stevekung.skyblockcatia.gui.toasts;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.utils.CoinType;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.ItemUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

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

        public Component getDisplayName(String value)
        {
            if (this.type.matches(ToastUtils.DropCondition.COINS))
            {
                return TextComponentUtils.formatted(value + " Coins", ColorUtils.rgbToDecimal("255,223,0"));
            }
            else if (this.type == ToastUtils.DropType.PET_LEVEL_UP)
            {
                return this.itemStack.getHoverName().copy().append(TextComponentUtils.formatted(" is now level ", ChatFormatting.GREEN)).append(TextComponentUtils.formatted(value, ChatFormatting.BLUE)).append(TextComponentUtils.formatted("!", ChatFormatting.GREEN));
            }
            else if (this.type.matches(ToastUtils.DropCondition.GIFT))
            {
                if (ItemStack.tagMatches(this.itemStack, ItemUtils.getSkullItemStack(CoinType.TYPE_1.getId(), CoinType.TYPE_1.getValue())))
                {
                    return TextComponentUtils.formatted(value + " Coins", ColorUtils.rgbToDecimal("255,255,85"));
                }
                else
                {
                    return TextComponentUtils.formatted(value + " ", ColorUtils.rgbToDecimal("255,255,85")).append(this.itemStack.getHoverName()).append(TextComponentUtils.formatted(" XP", ColorUtils.rgbToDecimal("255,255,85")));
                }
            }
            else
            {
                return this.itemStack.getHoverName();
            }
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
        MYTHOS_COINS("COINS!", "255,170,0", ImmutableList.of(DropCondition.COINS)),
        BANK_INTEREST("BANK INTEREST!", "255,170,0", ImmutableList.of(DropCondition.COINS)),
        ALLOWANCE("ALLOWANCE!", "255,170,0", ImmutableList.of(DropCondition.COINS)),
        GOOD_CATCH("GOOD CATCH!", "255,170,0"),
        GREAT_CATCH("GREAT CATCH!", "170,0,170"),
        GOOD_CATCH_COINS("GOOD CATCH!", "255,170,0", ImmutableList.of(DropCondition.COINS)),
        GREAT_CATCH_COINS("GREAT CATCH!", "170,0,170", ImmutableList.of(DropCondition.COINS)),
        SLAYER_RARE_DROP("RARE DROP!", "85,255,255", ImmutableList.of(DropCondition.FORMAT, DropCondition.SPECIAL_DROP)),
        SLAYER_VERY_RARE_DROP_BLUE("VERY RARE DROP!", "85,85,255", ImmutableList.of(DropCondition.FORMAT, DropCondition.SPECIAL_DROP)),
        SLAYER_VERY_RARE_DROP_PURPLE("VERY RARE DROP!", "170,0,170", ImmutableList.of(DropCondition.FORMAT, DropCondition.SPECIAL_DROP)),
        SLAYER_CRAZY_RARE_DROP("CRAZY RARE DROP!", "255,85,255", ImmutableList.of(DropCondition.FORMAT, DropCondition.SPECIAL_DROP)),
        COMMON_GIFT("COMMON GIFT!", "255,255,255", ImmutableList.of(DropCondition.GIFT)),
        SWEET_GIFT("SWEET GIFT!", "255,255,85", ImmutableList.of(DropCondition.GIFT)),
        RARE_GIFT("RARE GIFT!", "85,85,255", ImmutableList.of(DropCondition.GIFT)),
        SANTA_TIER("SANTA GIFT!", "255,85,85", ImmutableList.of(DropCondition.SPECIAL_DROP)),
        PET_LEVEL_UP("PET LEVEL UP!", "85,85,255"),
        DUNGEON_QUALITY_DROP("DUNGEON DROP!", "255,69,0", ImmutableList.of(DropCondition.SPECIAL_DROP, DropCondition.CONTAINS)),
        DUNGEON_REWARD_DROP("DUNGEON REWARD!", "255,69,0", ImmutableList.of(DropCondition.SPECIAL_DROP, DropCondition.CONTAINS)),
        ;

        private final String name;
        private final String color;
        private final List<DropCondition> properties;

        DropType(String name, String color)
        {
            this(name, color, ImmutableList.of());
        }

        DropType(String name, String color, List<DropCondition> properties)
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
            return this.color;
        }

        public ResourceLocation getTexture()
        {
            return new ResourceLocation(this.matches(ToastUtils.DropCondition.COINS) || this == PET_LEVEL_UP ? "skyblockcatia:textures/gui/drop_toasts.png" : "skyblockcatia:textures/gui/gift_toasts_" + (1 + new Random().nextInt(2)) + ".png");
        }

        public long getTime()
        {
            switch (this)
            {
                case RARE_DROP:
                case MYTHOS_COINS:
                case PET_DROP:
                case DRAGON_CRYSTAL_FRAGMENT:
                    return SkyBlockcatiaSettings.INSTANCE.rareDropToastTime * 1000L;
                case BOSS_DROP:
                case SLAYER_RARE_DROP:
                case SLAYER_VERY_RARE_DROP_BLUE:
                case SLAYER_VERY_RARE_DROP_PURPLE:
                case SLAYER_CRAZY_RARE_DROP:
                case SANTA_TIER:
                case DUNGEON_QUALITY_DROP:
                case DUNGEON_REWARD_DROP:
                case GOOD_CATCH:
                case GREAT_CATCH:
                case GOOD_CATCH_COINS:
                case GREAT_CATCH_COINS:
                    return SkyBlockcatiaSettings.INSTANCE.specialDropToastTime * 1000L;
                case COMMON_GIFT:
                case SWEET_GIFT:
                case RARE_GIFT:
                    return SkyBlockcatiaSettings.INSTANCE.giftToastTime * 1000L;
                case PET_LEVEL_UP:
                    return SkyBlockcatiaSettings.INSTANCE.petToastTime * 1000L;
                default:
                    return 5000L;
            }
        }

        public boolean matches(DropCondition... condition)
        {
            return this.properties.containsAll(Arrays.asList(condition));
        }
    }

    public enum ToastType
    {
        DROP,
        GIFT
    }

    public enum DropCondition
    {
        COINS,
        SPECIAL_DROP,
        CONTAINS,
        GIFT,
        FORMAT
    }
}