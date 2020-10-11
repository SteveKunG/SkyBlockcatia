package com.stevekung.skyblockcatia.gui;

import java.lang.reflect.Field;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.gui.api.GuiSkyBlockData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class GuiSmallArrow extends GuiButton
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/small_arrow.png");
    private final int originalX;
    private final int potionX;
    private final Minecraft mc;
    private final int type;

    // Patcher Compatibility
    private static Class<?> patcherConfig;
    private boolean patcherInventoryPosition;

    // Vanilla Enhancements Compatibility
    private static Class<?> vanillaEnConfig;
    private boolean vanillaEnFixInventory;

    // Not Enough Updates Compatibility
    private static Class<?> neuConfig;
    private boolean neuhidePotionEffect;

    public GuiSmallArrow(int buttonID, int xPos, int yPos, int type)
    {
        this(buttonID, xPos, yPos, xPos, type);
    }

    public GuiSmallArrow(int buttonID, int xPos, int yPos, int potionX, int type)
    {
        super(buttonID, xPos, yPos, 7, 11, "");
        this.mc = Minecraft.getMinecraft();
        this.originalX = xPos;
        this.potionX = potionX;
        this.type = type;

        if (SkyBlockcatiaMod.isPatcherLoaded)
        {
            try
            {
                patcherConfig = Class.forName("club.sk1er.patcher.config.PatcherConfig");
                Field inventoryPosition = patcherConfig.getDeclaredField("inventoryPosition");
                this.patcherInventoryPosition = inventoryPosition.getBoolean(patcherConfig);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (SkyBlockcatiaMod.isVanillaEnhancementsLoaded)
        {
            try
            {
                vanillaEnConfig = Class.forName("com.orangemarshall.enhancements.config.Config");
                Object instance = vanillaEnConfig.getDeclaredMethod("instance").invoke(vanillaEnConfig);
                Field fixInventory = instance.getClass().getDeclaredField("fixInventory");
                this.vanillaEnFixInventory = fixInventory.getBoolean(instance);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (SkyBlockcatiaMod.isNotEnoughUpdates)
        {
            try
            {
                neuConfig = Class.forName("io.github.moulberry.notenoughupdates.NotEnoughUpdates");
                Object instance = neuConfig.getDeclaredMethod("INSTANCE").invoke(neuConfig);
                Field manager = instance.getClass().getDeclaredField("manager");
                Field config = manager.getClass().getDeclaredField("config");
                Field hidePotionEffect = config.getClass().getDeclaredField("hidePotionEffect");
                Field value = hidePotionEffect.getClass().getDeclaredField("value");
                this.neuhidePotionEffect = value.getBoolean(hidePotionEffect);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if (!(this.vanillaEnFixInventory || this.patcherInventoryPosition || this.neuhidePotionEffect || this.mc.currentScreen instanceof GuiSkyBlockData))
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