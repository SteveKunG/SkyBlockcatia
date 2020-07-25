package com.stevekung.skyblockcatia.gui.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;
import com.stevekung.stevekungslib.client.event.ClientEventHandler;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.JsonUtils;

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
    private final boolean isSpecialDrop;
    private final long maxDrawTime;

    public ItemDropsToast(ItemStack itemStack, ToastUtils.DropType type)
    {
        this(itemStack, type, null);
    }

    public ItemDropsToast(ItemStack itemStack, ToastUtils.DropType type, String magicFind)
    {
        this.rareDropOutput = new ToastUtils.ItemDrop(itemStack, type);
        this.hasMagicFind = magicFind != null;
        this.isSpecialDrop = this.hasMagicFind || type.isSpecialDrop();
        this.magicFind = this.hasMagicFind ? TextFormatting.AQUA + " (" + magicFind + "% Magic Find!)" : "";
        this.maxDrawTime = this.isSpecialDrop ? 30000L : 15000L;
    }

    @Override
    public IToast.Visibility draw(ToastGui toastGui, long delta)
    {
        ToastUtils.ItemDrop drop = this.rareDropOutput;
        ItemStack itemStack = drop.getItemStack();
        String itemName = itemStack.getDisplayName().getFormattedText() + this.magicFind;

        if (itemStack.getItem() == Items.ENCHANTED_BOOK)
        {
            itemName = itemStack.getTooltip(null, ITooltipFlag.TooltipFlags.NORMAL).get(1).getFormattedText();
        }

        if (this.hasMagicFind)
        {
            toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE);
            RenderSystem.color3f(1.0F, 1.0F, 1.0F);
            RenderSystem.enableDepthTest();
            AbstractGui.blit(0, 0, 0, 0, 160, 32, 160, 32);

            RenderSystem.enableBlend();
            RenderSystem.depthFunc(514);

            for (int i = 0; i < 2; ++i)
            {
                RenderSystem.disableLighting();
                RenderSystem.blendFunc(768, 1);
                ColorUtils.RGB rgb = ColorUtils.stringToRGB("85,255,255");
                RenderSystem.color4f(rgb.floatRed(), rgb.floatGreen(), rgb.floatBlue(), 0.25F);
                RenderSystem.matrixMode(5890);
                RenderSystem.loadIdentity();
                RenderSystem.scalef(0.2F, 0.2F, 0.2F);
                RenderSystem.translatef(0.0F, ClientEventHandler.renderPartialTicks / 2 * (0.004F + i * 0.003F) * 20.0F, 0.0F);
                RenderSystem.matrixMode(5888);

                toastGui.getMinecraft().getTextureManager().bindTexture(MAGIC_FIND_GLINT);
                RenderSystem.blendFunc(770, 771);
                RenderSystem.blendFuncSeparate(770, 771, 1, 0);
                AbstractGui.blit(0, 0, 0, 0, 160, 32, 160, 32);
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
            AbstractGui.blit(0, 0, 0, 0, 160, 32, 160, 32);
        }

        RenderHelper.disableStandardItemLighting();

        if (this.hasMagicFind)
        {
            toastGui.getMinecraft().fontRenderer.drawStringWithShadow(drop.getType().getColor() + JsonUtils.create(drop.getType().getName()).applyTextStyle(TextFormatting.BOLD).getFormattedText(), 30, 7, 16777215);
        }
        else
        {
            toastGui.getMinecraft().fontRenderer.drawString(drop.getType().getColor() + JsonUtils.create(drop.getType().getName()).applyTextStyle(TextFormatting.BOLD).getFormattedText(), 30, 7, 16777215);
        }

        SBRenderUtils.drawLongItemName(toastGui, delta, 0L, itemName, this.isSpecialDrop ? 5000L : 1000L, this.maxDrawTime, this.isSpecialDrop ? 10000L : 5000L, this.isSpecialDrop ? 10000L : 8000L, this.hasMagicFind);

        toastGui.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(itemStack, 8, 8);
        RenderSystem.translatef(0.0F, 0.0F, -32.0F);
        toastGui.getMinecraft().getItemRenderer().renderItemOverlays(toastGui.getMinecraft().fontRenderer, itemStack, 8, 8);
        RenderSystem.disableLighting();
        return delta >= this.maxDrawTime ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
    }
}