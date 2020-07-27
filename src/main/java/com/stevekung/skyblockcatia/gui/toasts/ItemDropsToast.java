package com.stevekung.skyblockcatia.gui.toasts;

import java.nio.FloatBuffer;

import com.stevekung.skyblockcatia.event.ClientEventHandler;
import com.stevekung.skyblockcatia.renderer.EquipmentOverlay;
import com.stevekung.skyblockcatia.utils.ColorUtils;
import com.stevekung.skyblockcatia.utils.JsonUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class ItemDropsToast implements IToast<ItemDropsToast>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("skyblockcatia:textures/gui/drop_toasts.png");
    private static final ResourceLocation MAGIC_FIND_GLINT = new ResourceLocation("skyblockcatia:textures/gui/magic_find_glint.png");
    private final ToastUtils.ItemDrop rareDropOutput;
    private final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
    private String magicFind;
    private final boolean hasMagicFind;
    private final boolean isSpecialDrop;
    private final long maxDrawTime;

    public ItemDropsToast(ItemStack itemStack, ToastUtils.DropType type)
    {
        this(itemStack, type, null);
    }

    public ItemDropsToast(ItemStack itemStack, ToastUtils.DropType type, String magicFind)
    {
        this.rareDropOutput = new ToastUtils.ItemDrop(itemStack, type);
        this.hasMagicFind = magicFind != null;
        this.isSpecialDrop = this.hasMagicFind || type.matches(ToastUtils.DropCondition.SPECIAL_DROP);
        this.magicFind = this.hasMagicFind ? EnumChatFormatting.AQUA + " (" + magicFind + "% Magic Find!)" : "";
        this.maxDrawTime = this.isSpecialDrop ? 30000L : 15000L;
    }

    @Override
    public IToast.Visibility draw(GuiToast toastGui, long delta)
    {
        ToastUtils.ItemDrop drop = this.rareDropOutput;
        ItemStack itemStack = drop.getItemStack();
        String itemName = itemStack.getDisplayName() + this.magicFind;

        if (itemStack.getItem() == Items.enchanted_book)
        {
            itemName = itemStack.getTooltip(null, false).get(1) + this.magicFind;
        }

        if (this.hasMagicFind)
        {
            toastGui.mc.getTextureManager().bindTexture(TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            GlStateManager.enableDepth();
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 160, 32, 160, 32);

            GlStateManager.enableBlend();
            GlStateManager.depthFunc(514);

            for (int i = 0; i < 2; ++i)
            {
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(768, 1);
                ColorUtils.RGB rgb = ColorUtils.stringToRGB("85,255,255");
                GlStateManager.color(rgb.floatRed(), rgb.floatGreen(), rgb.floatBlue(), 0.25F);
                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                GlStateManager.scale(0.2F, 0.2F, 0.2F);
                GlStateManager.translate(0.0F, ClientEventHandler.renderPartialTicks / 2 * (0.004F + i * 0.003F) * 20.0F, 0.0F);
                GlStateManager.matrixMode(5888);

                toastGui.mc.getTextureManager().bindTexture(MAGIC_FIND_GLINT);
                GlStateManager.blendFunc(770, 771);
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 160, 32, 160, 32);
            }
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthFunc(515);
            GlStateManager.disableBlend();
        }
        else
        {
            toastGui.mc.getTextureManager().bindTexture(TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 160, 32, 160, 32);
        }

        RenderHelper.disableStandardItemLighting();

        if (this.hasMagicFind)
        {
            toastGui.mc.fontRendererObj.drawStringWithShadow(drop.getType().getColor() + JsonUtils.create(drop.getType().getName()).setChatStyle(JsonUtils.style().setBold(true)).getFormattedText(), 30, 7, 16777215);
        }
        else
        {
            toastGui.mc.fontRendererObj.drawString(drop.getType().getColor() + JsonUtils.create(drop.getType().getName()).setChatStyle(JsonUtils.style().setBold(true)).getFormattedText(), 30, 7, 16777215);
        }

        GuiToast.drawLongItemName(toastGui, delta, 0L, this.buffer, itemName, this.isSpecialDrop ? 5000L : 1000L, this.maxDrawTime, this.isSpecialDrop ? 10000L : 5000L, this.isSpecialDrop ? 10000L : 8000L, this.hasMagicFind);
        RenderHelper.enableGUIStandardItemLighting();

        EquipmentOverlay.renderItem(itemStack, 8, 8);
        Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(toastGui.mc.fontRendererObj, itemStack, 8, 8, null);
        GlStateManager.disableLighting();
        return delta >= this.maxDrawTime ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
    }
}