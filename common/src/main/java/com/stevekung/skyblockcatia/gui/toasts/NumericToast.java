package com.stevekung.skyblockcatia.gui.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.NumberUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class NumericToast implements Toast
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
        this.maxDrawTime = this.output.type().getTime();
        this.texture = this.output.type().getTexture();
    }

    @Override
    public Toast.Visibility render(PoseStack poseStack, ToastComponent toastGui, long delta)
    {
        if (this.hasNewValue)
        {
            this.firstDrawTime = delta;
            this.hasNewValue = false;
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        GuiComponent.blit(poseStack, 0, 0, 0, 0, 160, 32, 160, 32);
        toastGui.getMinecraft().font.draw(poseStack, TextComponentUtils.formatted(this.output.type().getName(), ChatFormatting.BOLD), 30, 7, ColorUtils.rgbToDecimal(this.output.type().getColor()));
        SBRenderUtils.drawLongItemName(toastGui, poseStack, delta, this.firstDrawTime, this.maxDrawTime, this.output.getDisplayName(NumberUtils.NUMBER_FORMAT.format(this.value)), false);
        toastGui.getMinecraft().getItemRenderer().renderAndDecorateItem(this.output.itemStack(), 8, 8);
        return delta - this.firstDrawTime >= this.maxDrawTime ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    @Override
    public Object getToken()
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

    public static void addValueOrUpdate(ToastComponent toastGui, ToastUtils.DropType rarity, int value, ItemStack itemStack, Object obj)
    {
        NumericToast.addValueOrUpdate(toastGui, rarity, value, itemStack, false, obj);
    }

    public static void addValueOrUpdate(ToastComponent toastGui, ToastUtils.DropType rarity, int value, ItemStack itemStack, Boolean set, Object obj)
    {
        try
        {
            var toast = toastGui.getToast(NumericToast.class, obj);

            if (toast == null)
            {
                toastGui.addToast(new NumericToast(value, itemStack, rarity, obj));
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