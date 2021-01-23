package com.stevekung.skyblockcatia.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

public class SmallArrowButton extends Button
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/small_arrow.png");
    private final Minecraft mc;

    public SmallArrowButton(int xPos, int yPos, Button.IPressable onPress)
    {
        super(xPos, yPos, 7, 11, StringTextComponent.EMPTY, onPress);
        this.mc = Minecraft.getInstance();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            this.mc.getTextureManager().bindTexture(TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            AbstractGui.blit(matrixStack, this.x, this.y, this.isHovered() ? 7 : 0, 0, this.width, this.height, 14, 11);
        }
    }
}