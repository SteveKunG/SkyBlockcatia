package com.stevekung.skyblockcatia.gui.widget.button;

import com.stevekung.skyblockcatia.gui.screen.GuiSkyBlockAPIViewer;
import com.stevekung.skyblockcatia.utils.CompatibilityUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class GuiSmallArrowButton extends GuiButton
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/small_arrow.png");
    private final int originalX;
    private final int potionX;
    private final Minecraft mc;
    private final int type;

    public GuiSmallArrowButton(int buttonID, int xPos, int yPos, int type)
    {
        this(buttonID, xPos, yPos, xPos, type);
    }

    public GuiSmallArrowButton(int buttonID, int xPos, int yPos, int potionX, int type)
    {
        super(buttonID, xPos, yPos, 7, 11, "");
        this.mc = Minecraft.getMinecraft();
        this.originalX = xPos;
        this.potionX = potionX;
        this.type = type;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (!(CompatibilityUtils.INSTANCE.hasInventoryFix() || this.mc.currentScreen instanceof GuiSkyBlockAPIViewer))
        {
            boolean hasVisibleEffect = false;

            for (PotionEffect potioneffect : this.mc.thePlayer.getActivePotionEffects())
            {
                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];

                if (potion.shouldRender(potioneffect))
                {
                    hasVisibleEffect = true;
                    break;
                }
            }

            if (!this.mc.thePlayer.getActivePotionEffects().isEmpty() && hasVisibleEffect)
            {
                this.xPosition = this.potionX;
            }
            else
            {
                this.xPosition = this.originalX;
            }
        }

        boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

        if (this.visible)
        {
            mc.getTextureManager().bindTexture(TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Gui.drawModalRectWithCustomSizedTexture(this.xPosition, this.yPosition, flag ? 7 : 0, this.type == 0 ? 0 : 11, this.width, this.height, 14, 22);
        }
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {}
}