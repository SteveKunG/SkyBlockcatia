package com.stevekung.skyblockcatia.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;

public class APISearchButton extends Button
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/search.png");

    public APISearchButton(int xPos, int yPos, Button.OnPress button)
    {
        super(xPos, yPos, 18, 18, TextComponentUtils.component("API Search Button"), button);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        Minecraft.getInstance().getTextureManager().bind(TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GuiComponent.blit(matrixStack, this.x, this.y, this.active && this.isHovered() ? 18 : !this.active ? 36 : 0, 0, this.width, this.height, 54, 18);
    }
}