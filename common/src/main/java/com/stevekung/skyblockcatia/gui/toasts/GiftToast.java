package com.stevekung.skyblockcatia.gui.toasts;

import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.utils.skyblock.SBRenderUtils;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

public class GiftToast implements Toast
{
    private final ResourceLocation texture;
    private final ToastUtils.ItemDrop itemDrop;
    private final long maxDrawTime;

    public GiftToast(ItemStack itemStack, ToastUtils.DropType rarity, boolean santaGift)
    {
        this.itemDrop = new ToastUtils.ItemDrop(itemStack, rarity);
        this.maxDrawTime = this.itemDrop.getType().getTime();
        Random rand = new Random();
        this.texture = new ResourceLocation("skyblockcatia:textures/gui/gift_toasts_" + (santaGift ? 1 : 1 + rand.nextInt(2)) + ".png");
    }

    @SuppressWarnings("deprecation")
    @Override
    public Toast.Visibility render(PoseStack poseStack, ToastComponent toastGui, long delta)
    {
        ToastUtils.ItemDrop drop = this.itemDrop;
        ItemStack itemStack = drop.getItemStack();
        Component itemName = itemStack.getHoverName();

        if (itemStack.getItem() == Items.ENCHANTED_BOOK)
        {
            itemName = itemStack.getTooltipLines(null, TooltipFlag.Default.NORMAL).get(1);
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        GuiComponent.blit(poseStack, 0, 0, 0, 0, 160, 32, 160, 32);
        toastGui.getMinecraft().font.draw(poseStack, TextComponentUtils.formatted(drop.getType().getName(), ChatFormatting.BOLD), 30, 7, ColorUtils.rgbToDecimal(drop.getType().getColor()));
        SBRenderUtils.drawLongItemName(toastGui, poseStack, delta, 0L, this.maxDrawTime, itemName, false);
        PoseStack poseStack2 = RenderSystem.getModelViewStack();
        poseStack2.pushPose();
        poseStack2.scale(0.0F, 0.0F, -32.0F);
        RenderSystem.applyModelViewMatrix();
        toastGui.getMinecraft().getItemRenderer().renderAndDecorateItem(itemStack, 8, 8);
        toastGui.getMinecraft().getItemRenderer().renderGuiItemDecorations(toastGui.getMinecraft().font, itemStack, 8, 8, null);
        poseStack2.popPose();
        return delta >= this.maxDrawTime ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }
}