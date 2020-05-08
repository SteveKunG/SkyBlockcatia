package com.stevekung.skyblockcatia.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.stevekungslib.utils.client.RenderUtils;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;

public class APISearchButton extends Button
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/search.png");

    public APISearchButton(int xPos, int yPos, Button.IPressable button)
    {
        super(xPos, yPos, 18, 18, "API Search Button", button);
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks)
    {
        RenderUtils.bindTexture(TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        AbstractGui.blit(this.x, this.y, this.active && this.isHovered() ? 18 : !this.active ? 36 : 0, 0, this.width, this.height, 54, 18);
    }
}