package com.stevekung.skyblockcatia.gui.toasts;

import java.nio.FloatBuffer;

import com.stevekung.skyblockcatia.utils.JsonUtils;
import com.stevekung.skyblockcatia.utils.ModDecimalFormat;
import com.stevekung.skyblockcatia.utils.RenderUtils;
import com.stevekung.skyblockcatia.utils.ToastUtils;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class NumericToast implements IToast
{
    private static final ModDecimalFormat FORMAT = new ModDecimalFormat("#,###");
    private final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
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

    @Override
    public IToast.Visibility draw(GuiToast toastGui, long delta)
    {
        if (this.hasNewValue)
        {
            this.firstDrawTime = delta;
            this.hasNewValue = false;
        }

        toastGui.mc.getTextureManager().bindTexture(this.texture);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 160, 32, 160, 32);
        toastGui.mc.fontRendererObj.drawString(this.output.getType().getColor() + JsonUtils.create(this.output.getType().getName()).setChatStyle(JsonUtils.style().setBold(true)).getFormattedText(), 30, 7, 16777215);
        GuiToast.drawLongItemName(toastGui, delta, this.firstDrawTime, this.maxDrawTime, this.buffer, this.output.getDisplayName(FORMAT.format(this.value)), false);
        RenderUtils.renderItem(this.output.getItemStack(), 8, 8);
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

    public static void addValueOrUpdate(GuiToast toastGui, ToastUtils.DropType rarity, int value, ItemStack itemStack, Object obj)
    {
        NumericToast.addValueOrUpdate(toastGui, rarity, value, itemStack, false, obj);
    }

    public static void addValueOrUpdate(GuiToast toastGui, ToastUtils.DropType rarity, int value, ItemStack itemStack, Boolean set, Object obj)
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