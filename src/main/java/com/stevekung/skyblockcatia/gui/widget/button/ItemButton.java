package com.stevekung.skyblockcatia.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.stevekungslib.utils.client.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ItemButton extends Button
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/blank.png");
    private ItemStack itemStack;
    private final Minecraft mc;
    private ITextComponent customName;

    public ItemButton(int xPos, int yPos, Item item, Button.IPressable onPress)
    {
        this(xPos, yPos, new ItemStack(item), onPress);
    }

    public ItemButton(int xPos, int yPos, ItemStack item, Button.IPressable onPress)
    {
        this(xPos, yPos, item, item.getDisplayName(), onPress);
    }

    public ItemButton(int xPos, int yPos, Item item, ITextComponent component, Button.IPressable onPress)
    {
        this(xPos, yPos, new ItemStack(item), component, onPress);
    }

    public ItemButton(int xPos, int yPos, ItemStack item, ITextComponent customName, Button.IPressable onPress)
    {
        super(xPos, yPos, 18, 18, StringTextComponent.EMPTY, onPress);
        this.itemStack = item;
        this.mc = Minecraft.getInstance();
        this.customName = customName;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderUtils.bindTexture(TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        AbstractGui.blit(matrixStack, this.x, this.y, this.isHovered() ? 18 : 0, 0, this.width, this.height, 36, 18);
        this.mc.getItemRenderer().renderItemAndEffectIntoGUI(this.itemStack, this.x + 1, this.y + 1);
    }

    @Override
    public void onPress()
    {
        this.onPress.onPress(this);
    }

    public ITextComponent getName()
    {
        return this.customName;
    }

    public void setName(ITextComponent name)
    {
        this.customName = name;
    }

    public ItemStack getItemStack()
    {
        return this.itemStack;
    }

    public void setItemStack(ItemStack itemStack)
    {
        this.itemStack = itemStack;        
    }
}