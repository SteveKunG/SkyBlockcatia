package com.stevekung.skyblockcatia.hud;

import com.stevekung.skyblockcatia.config.Equipments;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.utils.ColorUtils;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;

public class HorizontalEquipmentOverlay extends EquipmentOverlay
{
    private int width;
    private int itemDamageWidth;

    public HorizontalEquipmentOverlay(ItemStack itemStack)
    {
        super(itemStack);
        this.initSize();
    }

    public int getWidth()
    {
        return this.width;
    }

    public void render(int x, int y)
    {
        boolean right = Equipments.Position.byId(SkyBlockcatiaSettings.INSTANCE.equipmentPosition) == Equipments.Position.RIGHT;
        String arrowInfo = this.renderArrowInfo();
        EquipmentOverlay.renderItem(this.itemStack, right ? x - 18 : x, y);
        this.mc.fontRendererObj.drawStringWithShadow(ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.equipmentStatusColor).toColoredFont() + this.renderInfo(), right ? x - 20 - this.itemDamageWidth : x + 18, y + 4, 16777215);

        if (this.itemStack.getItem() instanceof ItemBow)
        {
            GlStateManager.disableDepth();
            ColorUtils.unicodeFontRenderer.drawStringWithShadow(ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.arrowCountColor).toColoredFont() + arrowInfo, right ? x - ColorUtils.unicodeFontRenderer.getStringWidth(arrowInfo) : x + 6, y + 8, 16777215);
            GlStateManager.enableDepth();
        }
    }

    private void initSize()
    {
        this.itemDamageWidth = this.mc.fontRendererObj.getStringWidth(this.renderInfo());
        this.width = 20 + this.itemDamageWidth;
    }
}