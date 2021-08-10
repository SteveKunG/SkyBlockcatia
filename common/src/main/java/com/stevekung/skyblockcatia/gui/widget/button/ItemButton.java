package com.stevekung.skyblockcatia.gui.widget.button;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ItemButton extends Button
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/blank.png");
    private ItemStack itemStack;
    private final Minecraft mc;
    private Component customName;

    public ItemButton(int xPos, int yPos, ItemLike item, Button.OnPress onPress)
    {
        this(xPos, yPos, new ItemStack(item), onPress);
    }

    public ItemButton(int xPos, int yPos, ItemStack item, Button.OnPress onPress)
    {
        this(xPos, yPos, item, item.getHoverName(), onPress);
    }

    public ItemButton(int xPos, int yPos, ItemLike item, Component component, Button.OnPress onPress)
    {
        this(xPos, yPos, new ItemStack(item), component, onPress);
    }

    public ItemButton(int xPos, int yPos, ItemStack item, Component customName, Button.OnPress onPress)
    {
        super(xPos, yPos, 18, 18, TextComponent.EMPTY, onPress);
        this.itemStack = item;
        this.mc = Minecraft.getInstance();
        this.customName = customName;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GuiComponent.blit(poseStack, this.x, this.y, this.isHovered() ? 18 : 0, 0, this.width, this.height, 36, 18);
        this.mc.getItemRenderer().renderAndDecorateItem(this.itemStack, this.x + 1, this.y + 1);
    }

    @Override
    public void onPress()
    {
        this.onPress.onPress(this);
    }

    public Component getName()
    {
        return this.customName;
    }

    public void setName(Component name)
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