package com.stevekung.skyblockcatia.gui.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
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

public class ItemDropsToast implements Toast
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/drop_toasts.png");
    private static final ResourceLocation MAGIC_FIND_GLINT = new ResourceLocation("skyblockcatia:textures/gui/magic_find_glint.png");
    private final ToastUtils.ItemDrop rareDropOutput;
    private final String magicFind;
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
        this.magicFind = this.hasMagicFind ? ChatFormatting.AQUA + " (" + magicFind + "% Magic Find!)" : "";
        this.maxDrawTime = this.hasMagicFind ? SkyBlockcatiaSettings.INSTANCE.specialDropToastTime * 1000L : type.getTime();
    }

    @SuppressWarnings("deprecation")
    @Override
    public Toast.Visibility render(PoseStack poseStack, ToastComponent toastGui, long delta)
    {
        ToastUtils.ItemDrop drop = this.rareDropOutput;
        ItemStack itemStack = drop.getItemStack();
        Component itemName = itemStack.getHoverName().copy().append(this.magicFind);

        if (itemStack.getItem() == Items.ENCHANTED_BOOK)
        {
            itemName = itemStack.getTooltipLines(null, TooltipFlag.Default.NORMAL).get(1);
        }

        if (this.hasMagicFind)
        {
//            toastGui.getMinecraft().getTextureManager().bind(TEXTURE);TODO
//            RenderSystem.color3f(1.0F, 1.0F, 1.0F);
//            RenderSystem.enableDepthTest();
//            GuiComponent.blit(poseStack, 0, 0, 0, 0, 160, 32, 160, 32);
//
//            RenderSystem.enableBlend();
//            RenderSystem.depthFunc(514);
//
//            for (int i = 0; i < 2; ++i)
//            {
//                RenderSystem.disableLighting();
//                RenderSystem.blendFunc(768, 1);
//                float[] rgb = ColorUtils.toFloatArray(85, 255, 255);
//                RenderSystem.color4f(rgb[0], rgb[1], rgb[2], 0.25F);
//                RenderSystem.matrixMode(5890);
//                RenderSystem.loadIdentity();
//                RenderSystem.scalef(0.2F, 0.2F, 0.2F);
//                RenderSystem.translatef(0.0F, LibClientProxy.renderPartialTicks / 2 * (0.004F + i * 0.003F) * 20.0F, 0.0F);
//                RenderSystem.matrixMode(5888);
//
//                toastGui.getMinecraft().getTextureManager().bind(MAGIC_FIND_GLINT);
//                RenderSystem.blendFunc(770, 771);
//                RenderSystem.blendFuncSeparate(770, 771, 1, 0);
//                GuiComponent.blit(poseStack, 0, 0, 0, 0, 160, 32, 160, 32);
//            }
//            RenderSystem.matrixMode(5890);
//            RenderSystem.loadIdentity();
//            RenderSystem.matrixMode(5888);
//            RenderSystem.enableLighting();
//            RenderSystem.depthFunc(515);
//            RenderSystem.disableBlend();
        }
        else
        {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, TEXTURE);
            GuiComponent.blit(poseStack, 0, 0, 0, 0, 160, 32, 160, 32);
        }

        if (this.hasMagicFind)
        {
            toastGui.getMinecraft().font.drawShadow(poseStack, TextComponentUtils.formatted(drop.getType().getName(), ChatFormatting.BOLD), 30, 7, ColorUtils.rgbToDecimal(drop.getType().getColor()));
        }
        else
        {
            toastGui.getMinecraft().font.draw(poseStack, TextComponentUtils.formatted(drop.getType().getName(), ChatFormatting.BOLD), 30, 7, ColorUtils.rgbToDecimal(drop.getType().getColor()));
        }

        SBRenderUtils.drawLongItemName(toastGui, poseStack, delta, 0L, this.maxDrawTime, itemName, this.hasMagicFind);

        toastGui.getMinecraft().getItemRenderer().renderAndDecorateItem(itemStack, 8, 8);
        PoseStack poseStack1 = RenderSystem.getModelViewStack();
        poseStack1.pushPose();
        poseStack1.translate(0.0F, 0.0F, -32.0F);
        toastGui.getMinecraft().getItemRenderer().renderGuiItemDecorations(toastGui.getMinecraft().font, itemStack, 8, 8);
        RenderSystem.applyModelViewMatrix();
        poseStack1.popPose();
        return delta >= this.maxDrawTime ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }
}