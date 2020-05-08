package com.stevekung.skyblockcatia.gui;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.stevekungslib.utils.LangUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.math.MathHelper;

public abstract class ConfigSliderWidget extends ExtendedWidget
{
    protected double value;

    protected ConfigSliderWidget(int x, int y, int width, int height, double value)
    {
        super(x, y, width, height, "");
        this.value = value;
    }

    @Override
    protected int getYImage(boolean hover)
    {
        return 0;
    }

    @Override
    protected String getNarrationMessage()
    {
        return LangUtils.translate("gui.narrate.slider", this.getMessage());
    }

    @Override
    protected void renderBg(Minecraft mc, int x, int y)
    {
        mc.getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int index = (this.isHovered() ? 2 : 1) * 20;
        this.blit(this.x + (int)(this.value * (this.width - 8)), this.y, 0, 46 + index, 4, 20);
        this.blit(this.x + (int)(this.value * (this.width - 8)) + 4, this.y, 196, 46 + index, 4, 20);
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        this.setValueFromMouse(mouseX);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        boolean left = keyCode == GLFW.GLFW_KEY_LEFT;

        if (left || keyCode == GLFW.GLFW_KEY_RIGHT)
        {
            float value = left ? -1.0F : 1.0F;
            this.setValue(this.value + value / (this.width - 8));
        }
        return false;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY)
    {
        this.setValueFromMouse(mouseX);
        super.onDrag(mouseX, mouseY, dragX, dragY);
    }

    @Override
    public void playDownSound(SoundHandler manager) {}

    @Override
    public void onRelease(double mouseX, double mouseY)
    {
        super.playDownSound(Minecraft.getInstance().getSoundHandler());
    }

    private void setValueFromMouse(double mouseX)
    {
        this.setValue((mouseX - (this.x + 4)) / (this.width - 8));
    }

    private void setValue(double value)
    {
        double currentValue = this.value;
        this.value = MathHelper.clamp(value, 0.0D, 1.0D);

        if (currentValue != this.value)
        {
            this.applyValue();
        }
        this.updateMessage();
    }

    protected abstract void updateMessage();

    protected abstract void applyValue();
}