package com.stevekung.skyblockcatia.gui.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.ItemUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class VisitIslandToast implements Toast
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/visit_island_toasts.png");
    private final ItemStack itemStack;
    private final String name;

    public VisitIslandToast(String name)
    {
        this.itemStack = ItemUtils.getPlayerHead(name);
        this.name = name;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Toast.Visibility render(PoseStack matrixStack, ToastComponent toastGui, long delta)
    {
        toastGui.getMinecraft().getTextureManager().bind(TEXTURE);
        RenderSystem.color3f(1.0F, 1.0F, 1.0F);
        GuiComponent.blit(matrixStack, 0, 0, 0, 0, 160, 32, 160, 32);
        toastGui.getMinecraft().font.draw(matrixStack, TextComponentUtils.formatted(this.name, ChatFormatting.BOLD), 30, 7, ColorUtils.toDecimal(255, 255, 85));
        toastGui.getMinecraft().font.draw(matrixStack, "is visiting Your Island!", 30, 18, ColorUtils.toDecimal(255, 255, 255));
        toastGui.getMinecraft().getItemRenderer().renderAndDecorateItem(this.itemStack, 8, 8);
        return delta >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }
}