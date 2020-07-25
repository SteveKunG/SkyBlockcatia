package com.stevekung.skyblockcatia.gui.toasts;

import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;
import com.stevekung.stevekungslib.utils.JsonUtils;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GiftToast implements IToast
{
    private final Random rand = new Random();
    private final ResourceLocation texture;
    private final ToastUtils.ItemDrop itemDrop;
    private final long drawTime;

    public GiftToast(ItemStack itemStack, ToastUtils.DropType rarity, boolean santaGift)
    {
        this.itemDrop = new ToastUtils.ItemDrop(itemStack, rarity);
        this.drawTime = this.itemDrop.getType() == ToastUtils.DropType.SANTA_TIER ? 20000L : 10000L;
        this.texture = new ResourceLocation("skyblockcatia:textures/gui/gift_toasts_" + (santaGift ? 1 : Integer.valueOf(1 + this.rand.nextInt(2))) + ".png");
    }

    @Override
    public IToast.Visibility draw(ToastGui toastGui, long delta)
    {
        ToastUtils.ItemDrop drop = this.itemDrop;
        boolean isSanta = this.itemDrop.getType() == ToastUtils.DropType.SANTA_TIER;
        ItemStack itemStack = drop.getItemStack();
        String itemName = itemStack.getDisplayName().getFormattedText();

        if (itemStack.getItem() == Items.ENCHANTED_BOOK)
        {
            itemName = itemStack.getTooltip(null, ITooltipFlag.TooltipFlags.NORMAL).get(1).getFormattedText();
        }

        toastGui.getMinecraft().getTextureManager().bindTexture(this.texture);
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        AbstractGui.blit(0, 0, 0, 0, 160, 32, 160, 32);
        toastGui.getMinecraft().fontRenderer.drawString(drop.getType().getColor() + JsonUtils.create(drop.getType().getName()).applyTextStyle(TextFormatting.BOLD).getFormattedText(), 30, 7, 16777215);
        SBRenderUtils.drawLongItemName(toastGui, delta, 0L, itemName, isSanta ? 2000L : 500L, this.drawTime, 5000L, 8000L, false);
        RenderSystem.translatef(0.0F, 0.0F, -32.0F);
        toastGui.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(itemStack, 8, 8);
        toastGui.getMinecraft().getItemRenderer().renderItemOverlayIntoGUI(toastGui.getMinecraft().fontRenderer, itemStack, 8, 8, null);
        RenderSystem.disableLighting();
        return delta >= this.drawTime ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
    }
}