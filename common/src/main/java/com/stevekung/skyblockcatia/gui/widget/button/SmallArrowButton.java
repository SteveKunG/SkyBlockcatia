package com.stevekung.skyblockcatia.gui.widget.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class SmallArrowButton extends Button
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/small_arrow.png");
    private final Minecraft mc;

    public SmallArrowButton(int xPos, int yPos, Button.OnPress onPress)
    {
        super(xPos, yPos, 7, 11, TextComponent.EMPTY, onPress);
        this.mc = Minecraft.getInstance();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            this.mc.getTextureManager().bind(TEXTURE);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GuiComponent.blit(poseStack, this.x, this.y, this.isHovered() ? 7 : 0, 0, this.width, this.height, 14, 11);
        }
    }
}