package com.stevekung.skyblockcatia.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

public class APISearchButton extends Button
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/search.png");

    public APISearchButton(int xPos, int yPos, Button.IPressable button)
    {
        super(xPos, yPos, 18, 18, TextComponentUtils.component("API Search Button"), button);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        AbstractGui.blit(matrixStack, this.x, this.y, this.active && this.isHovered() ? 18 : !this.active ? 36 : 0, 0, this.width, this.height, 54, 18);
    }
}