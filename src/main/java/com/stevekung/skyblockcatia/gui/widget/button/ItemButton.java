package com.stevekung.skyblockcatia.gui.widget.button;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.stevekung.stevekungslib.utils.JsonUtils;
import com.stevekung.stevekungslib.utils.client.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class ItemButton extends Button
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/blank.png");
    private final Item item;
    private final Minecraft mc;
    private String customName;

    public ItemButton(int xPos, int yPos, Item item, Button.IPressable onPress)
    {
        this(xPos, yPos, item, true, item.getDisplayName(new ItemStack(item)).getFormattedText(), onPress);
    }

    public ItemButton(int xPos, int yPos, Item item, boolean condition, Button.IPressable onPress)
    {
        this(xPos, yPos, item, condition, item.getDisplayName(new ItemStack(item)).getFormattedText(), onPress);
    }

    public ItemButton(int xPos, int yPos, Item item, String customName, Button.IPressable onPress)
    {
        this(xPos, yPos, item, true, customName, onPress);
    }

    public ItemButton(int xPos, int yPos, Item item, boolean condition, String customName, Button.IPressable onPress)
    {
        super(xPos, yPos, 18, 18, "", onPress);
        this.item = item;
        this.mc = Minecraft.getInstance();
        this.visible = condition;
        this.customName = customName;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks)
    {
        ItemStack itemStack = new ItemStack(this.item);

        if (this.item == Items.NETHER_STAR)
        {
            ItemStack skyBlockMenu = itemStack.copy();
            ListNBT list = new ListNBT();
            skyBlockMenu.setDisplayName(JsonUtils.create("SkyBlock Menu"));
            list.add(StringNBT.valueOf(TextFormatting.GRAY + "View all of your SkyBlock"));
            skyBlockMenu.getTag().getCompound("display").put("Lore", list);
            itemStack = skyBlockMenu;
        }

        RenderUtils.bindTexture(TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        AbstractGui.blit(this.x, this.y, this.isHovered() ? 18 : 0, 0, this.width, this.height, 36, 18);
        this.mc.getItemRenderer().renderItemAndEffectIntoGUI(itemStack, this.x + 1, this.y + 1);
    }

    @Override
    public void onPress()
    {
        this.onPress.onPress(this);
    }

    public String getName()
    {
        return this.customName;
    }

    public void setName(String name)
    {
        this.customName = name;
    }
}