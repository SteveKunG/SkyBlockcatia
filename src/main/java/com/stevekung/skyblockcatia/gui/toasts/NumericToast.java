package com.stevekung.skyblockcatia.gui.toasts;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.NumberUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class NumericToast implements IToast
{
    private final ToastUtils.ItemDrop output;
    private final Object obj;
    private final long maxDrawTime;
    private final ResourceLocation texture;
    private int value;
    private long firstDrawTime;
    private boolean hasNewValue;

    public NumericToast(int value, ItemStack itemStack, ToastUtils.DropType dropType, Object obj)
    {
        this.output = new ToastUtils.ItemDrop(itemStack, dropType);
        this.value = value;
        this.obj = obj;
        this.maxDrawTime = this.output.getType().getTime();
        this.texture = this.output.getType().getTexture();
    }

    @SuppressWarnings("deprecation")
    @Override
    public IToast.Visibility func_230444_a_(MatrixStack matrixStack, ToastGui toastGui, long delta)
    {
        if (this.hasNewValue)
        {
            this.firstDrawTime = delta;
            this.hasNewValue = false;
        }

        toastGui.getMinecraft().getTextureManager().bindTexture(this.texture);
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        AbstractGui.blit(matrixStack, 0, 0, 0, 0, 160, 32, 160, 32);
        toastGui.getMinecraft().fontRenderer.drawText(matrixStack, TextComponentUtils.formatted(this.output.getType().getName(), TextFormatting.BOLD), 30, 7, ColorUtils.rgbToDecimal(this.output.getType().getColor()));
        SBRenderUtils.drawLongItemName(toastGui, matrixStack, delta, this.firstDrawTime, this.maxDrawTime, this.output.getDisplayName(NumberUtils.NUMBER_FORMAT.format(this.value)), false);
        toastGui.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(this.output.getItemStack(), 8, 8);
        return delta - this.firstDrawTime >= this.maxDrawTime ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
    }

    @Override
    public Object getType()
    {
        return this.obj;
    }

    private void addValue(int value)
    {
        this.value += value;
        this.hasNewValue = true;
    }

    private void setValue(int value)
    {
        this.value = value;
        this.hasNewValue = true;
    }

    public static void addValueOrUpdate(ToastGui toastGui, ToastUtils.DropType rarity, int value, ItemStack itemStack, Object obj)
    {
        NumericToast.addValueOrUpdate(toastGui, rarity, value, itemStack, false, obj);
    }

    public static void addValueOrUpdate(ToastGui toastGui, ToastUtils.DropType rarity, int value, ItemStack itemStack, Boolean set, Object obj)
    {
        try
        {
            NumericToast toast = toastGui.getToast(NumericToast.class, obj);

            if (toast == null)
            {
                toastGui.add(new NumericToast(value, itemStack, rarity, obj));
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
}