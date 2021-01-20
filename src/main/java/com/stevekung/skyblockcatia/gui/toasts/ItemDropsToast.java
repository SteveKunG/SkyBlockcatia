package com.stevekung.skyblockcatia.gui.toasts;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;
import com.stevekung.stevekungslib.client.event.handler.ClientEventHandler;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class ItemDropsToast implements IToast
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/drop_toasts.png");
    private static final ResourceLocation MAGIC_FIND_GLINT = new ResourceLocation("skyblockcatia:textures/gui/magic_find_glint.png");
    private final ToastUtils.ItemDrop rareDropOutput;
    private String magicFind;
    private final boolean hasMagicFind;
    private final long maxDrawTime;

    public ItemDropsToast(ItemStack itemStack, ToastUtils.DropType type)
    {
        this(itemStack, type, null);
    }

    public ItemDropsToast(ItemStack itemStack, ToastUtils.DropType type, String magicFind)
    {
        this.rareDropOutput = new ToastUtils.ItemDrop(itemStack, type);
        this.hasMagicFind = magicFind != null;
        this.magicFind = this.hasMagicFind ? TextFormatting.AQUA + " (" + magicFind + "% Magic Find!)" : "";
        this.maxDrawTime = this.hasMagicFind ? SkyBlockcatiaSettings.INSTANCE.specialDropToastTime * 1000L : type.getTime();
    }

    @SuppressWarnings("deprecation")
    @Override
    public IToast.Visibility func_230444_a_(MatrixStack matrixStack, ToastGui toastGui, long delta)
    {
        ToastUtils.ItemDrop drop = this.rareDropOutput;
        ItemStack itemStack = drop.getItemStack();
        String itemName = itemStack.getDisplayName().getString() + this.magicFind;

        if (itemStack.getItem() == Items.ENCHANTED_BOOK)
        {
            itemName = itemStack.getTooltip(null, ITooltipFlag.TooltipFlags.NORMAL).get(1).getString();
        }

        if (this.hasMagicFind)
        {
            toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE);
            RenderSystem.color3f(1.0F, 1.0F, 1.0F);
            RenderSystem.enableDepthTest();
            AbstractGui.blit(matrixStack, 0, 0, 0, 0, 160, 32, 160, 32);

            RenderSystem.enableBlend();
            RenderSystem.depthFunc(514);

            for (int i = 0; i < 2; ++i)
            {
                RenderSystem.disableLighting();
                RenderSystem.blendFunc(768, 1);
                float[] rgb = ColorUtils.toFloatArray(85, 255, 255);
                RenderSystem.color4f(rgb[0], rgb[1], rgb[2], 0.25F);
                RenderSystem.matrixMode(5890);
                RenderSystem.loadIdentity();
                RenderSystem.scalef(0.2F, 0.2F, 0.2F);
                RenderSystem.translatef(0.0F, ClientEventHandler.renderPartialTicks / 2 * (0.004F + i * 0.003F) * 20.0F, 0.0F);
                RenderSystem.matrixMode(5888);

                toastGui.getMinecraft().getTextureManager().bindTexture(MAGIC_FIND_GLINT);
                RenderSystem.blendFunc(770, 771);
                RenderSystem.blendFuncSeparate(770, 771, 1, 0);
                AbstractGui.blit(matrixStack, 0, 0, 0, 0, 160, 32, 160, 32);
            }
            RenderSystem.matrixMode(5890);
            RenderSystem.loadIdentity();
            RenderSystem.matrixMode(5888);
            RenderSystem.enableLighting();
            RenderSystem.depthFunc(515);
            RenderSystem.disableBlend();
        }
        else
        {
            toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE);
            RenderSystem.color3f(1.0F, 1.0F, 1.0F);
            AbstractGui.blit(matrixStack, 0, 0, 0, 0, 160, 32, 160, 32);
        }

        RenderHelper.disableStandardItemLighting();

        if (this.hasMagicFind)
        {
            toastGui.getMinecraft().fontRenderer.func_243246_a(matrixStack, TextComponentUtils.formatted(drop.getType().getName(), TextFormatting.BOLD), 30, 7, ColorUtils.rgbToDecimal(drop.getType().getColor()));
        }
        else
        {
            toastGui.getMinecraft().fontRenderer.func_243248_b(matrixStack, TextComponentUtils.formatted(drop.getType().getName(), TextFormatting.BOLD), 30, 7, ColorUtils.rgbToDecimal(drop.getType().getColor()));
        }

        SBRenderUtils.drawLongItemName(toastGui, matrixStack, delta, 0L, this.maxDrawTime, itemName, this.hasMagicFind);

        toastGui.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(itemStack, 8, 8);
        RenderSystem.translatef(0.0F, 0.0F, -32.0F);
        toastGui.getMinecraft().getItemRenderer().renderItemOverlays(toastGui.getMinecraft().fontRenderer, itemStack, 8, 8);
        RenderSystem.disableLighting();
        return delta >= this.maxDrawTime ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
    }
}