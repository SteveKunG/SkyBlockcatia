package com.stevekung.skyblockcatia.gui.toasts;

import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.Random;

import com.stevekung.skyblockcatia.event.HypixelEventHandler;
import com.stevekung.skyblockcatia.renderer.EquipmentOverlay;
import com.stevekung.skyblockcatia.utils.ColorUtils;
import com.stevekung.skyblockcatia.utils.JsonUtils;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class NumericToast implements IToast<NumericToast>
{
    private final Random rand = new Random();
    private final ResourceLocation texture;
    private final ToastUtils.ItemDrop output;
    private int value;
    private final String object;
    private final boolean isCoins;
    private final boolean isFishingCoins;
    private final boolean isPet;
    private long firstDrawTime;
    private boolean hasNewValue;
    private final long maxDrawTime;
    private final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
    private static final DecimalFormat FORMAT = new DecimalFormat("###,###");

    public NumericToast(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
    {
        this.output = new ToastUtils.ItemDrop(itemStack, rarity);
        this.value = value;
        this.object = object;
        this.isCoins = this.object.equals("Coins");
        this.isPet = this.object.equals("Pet");
        this.isFishingCoins = rarity.isFishingCoins();
        this.maxDrawTime = this.isFishingCoins || this.isPet ? 15000L : 10000L;
        this.texture = new ResourceLocation(this.isFishingCoins || this.isPet ? "skyblockcatia:textures/gui/drop_toasts.png" : "skyblockcatia:textures/gui/gift_toasts_" + Integer.valueOf(1 + this.rand.nextInt(2)) + ".png");
    }

    @Override
    public IToast.Visibility draw(GuiToast toastGui, long delta)
    {
        if (this.hasNewValue)
        {
            this.firstDrawTime = delta;
            this.hasNewValue = false;
        }

        ToastUtils.ItemDrop drop = this.output;
        String value = FORMAT.format(this.value);
        ItemStack itemStack = this.isCoins || this.isPet ? drop.getItemStack() : HypixelEventHandler.getSkillItemStack(value, this.object);
        String itemName = this.isCoins ? ColorUtils.stringToRGB("255,223,0").toColoredFont() + value + " Coins" : this.isPet ? EnumChatFormatting.GREEN + itemStack.getDisplayName() + EnumChatFormatting.GREEN + " is now level " + EnumChatFormatting.BLUE + value + EnumChatFormatting.GREEN + "!" : itemStack.getDisplayName();
        toastGui.mc.getTextureManager().bindTexture(this.texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 160, 32, 160, 32);
        toastGui.mc.fontRendererObj.drawString(drop.getType().getColor() + JsonUtils.create(drop.getType().getName()).setChatStyle(JsonUtils.style().setBold(true)).getFormattedText(), 30, 7, 16777215);
        GuiToast.drawLongItemName(toastGui, delta, this.firstDrawTime, this.buffer, itemName, this.isFishingCoins || this.isPet ? 1000L : 500L, this.maxDrawTime, 5000L, 8000L, false);
        EquipmentOverlay.renderItem(itemStack, 8, 8);
        return delta - this.firstDrawTime >= this.maxDrawTime ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
    }

    public void addValue(int value)
    {
        this.value += value;
        this.hasNewValue = true;
    }

    public void setValue(int value)
    {
        this.value = value;
        this.hasNewValue = true;
    }

    public static void addValueOrUpdate(GuiToast toastGui, ToastUtils.DropType rarity, int value, ItemStack itemStack, String object)
    {
        NumericToast.addValueOrUpdate(toastGui, rarity, value, itemStack, object, false);
    }

    public static void addValueOrUpdate(GuiToast toastGui, ToastUtils.DropType rarity, int value, ItemStack itemStack, String object, boolean set)
    {
        try
        {
            Class<? extends NumericToast> toastClass = getToastClass(rarity, object);
            NumericToast toast = toastGui.getToast(toastClass, NO_TOKEN);

            if (toast == null)
            {
                toastGui.add(toastClass.getDeclaredConstructor(int.class, ItemStack.class, String.class, ToastUtils.DropType.class).newInstance(value, itemStack, object, rarity));
            }
            else
            {
                if (set)
                {
                    toast.setValue(value);
                }
                else
                {
                    toast.addValue(value);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static Class<? extends NumericToast> getToastClass(ToastUtils.DropType rarity, String object) throws ClassNotFoundException
    {
        StringBuilder builder = new StringBuilder();

        try
        {
            switch (rarity)
            {
            default:
            case COMMON_GIFT:
                builder.append(GiftCommonToast.class.getName());
                break;
            case SWEET_GIFT:
                builder.append(GiftSweetToast.class.getName());
                break;
            case RARE_GIFT:
                builder.append(GiftRareToast.class.getName());
                break;
            case GOOD_CATCH_COINS:
                builder.append(GoodToast.class.getName());
                break;
            case GREAT_CATCH_COINS:
                builder.append(GreatToast.class.getName());
                break;
            case PET_LEVEL_UP:
                builder.append(PetLevelUpToast.class.getName());
                break;
            }
            if (!rarity.isFishingCoins() && rarity != ToastUtils.DropType.PET_LEVEL_UP)
            {
                builder.append("$" + object);
            }
            return (Class<? extends NumericToast>)Class.forName(builder.toString());
        }
        catch (Exception e)
        {
            throw new ClassNotFoundException();
        }
    }

    static class PetLevelUpToast extends NumericToast
    {
        public PetLevelUpToast(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
        {
            super(value, itemStack, object, rarity);
        }
    }

    static class GoodToast extends NumericToast
    {
        public GoodToast(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
        {
            super(value, itemStack, object, rarity);
        }
    }

    static class GreatToast extends NumericToast
    {
        public GreatToast(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
        {
            super(value, itemStack, object, rarity);
        }
    }

    static class GiftCommonToast
    {
        static class Farming extends NumericToast
        {
            public Farming(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Mining extends NumericToast
        {
            public Mining(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Combat extends NumericToast
        {
            public Combat(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Foraging extends NumericToast
        {
            public Foraging(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Fishing extends NumericToast
        {
            public Fishing(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Enchanting extends NumericToast
        {
            public Enchanting(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Alchemy extends NumericToast
        {
            public Alchemy(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Coins extends NumericToast
        {
            public Coins(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
    }

    static class GiftSweetToast
    {
        static class Farming extends NumericToast
        {
            public Farming(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Mining extends NumericToast
        {
            public Mining(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Combat extends NumericToast
        {
            public Combat(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Foraging extends NumericToast
        {
            public Foraging(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Fishing extends NumericToast
        {
            public Fishing(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Enchanting extends NumericToast
        {
            public Enchanting(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Alchemy extends NumericToast
        {
            public Alchemy(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Coins extends NumericToast
        {
            public Coins(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
    }

    static class GiftRareToast
    {
        static class Farming extends NumericToast
        {
            public Farming(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Mining extends NumericToast
        {
            public Mining(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Combat extends NumericToast
        {
            public Combat(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Foraging extends NumericToast
        {
            public Foraging(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Fishing extends NumericToast
        {
            public Fishing(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Enchanting extends NumericToast
        {
            public Enchanting(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Alchemy extends NumericToast
        {
            public Alchemy(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
        static class Coins extends NumericToast
        {
            public Coins(int value, ItemStack itemStack, String object, ToastUtils.DropType rarity)
            {
                super(value, itemStack, object, rarity);
            }
        }
    }
}