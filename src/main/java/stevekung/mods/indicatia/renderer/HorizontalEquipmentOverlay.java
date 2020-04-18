package stevekung.mods.indicatia.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import stevekung.mods.indicatia.config.EnumEquipment;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.indicatia.utils.ColorUtils;

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
        boolean right = EnumEquipment.Position.getById(ExtendedConfig.instance.equipmentPosition).equalsIgnoreCase("right");
        String arrowInfo = this.renderArrowInfo();
        String baitInfo = this.renderBaitInfo();
        EquipmentOverlay.renderItem(this.itemStack, right ? x - 18 : x, y);
        this.mc.fontRendererObj.drawStringWithShadow(ColorUtils.stringToRGB(ExtendedConfig.instance.equipmentStatusColor).toColoredFont() + this.renderInfo(), right ? x - 20 - this.itemDamageWidth : x + 18, y + 4, 16777215);

        if (this.itemStack.getItem() instanceof ItemBow)
        {
            GlStateManager.disableDepth();
            ColorUtils.unicodeFontRenderer.drawStringWithShadow(ColorUtils.stringToRGB(ExtendedConfig.instance.arrowCountColor).toColoredFont() + arrowInfo, right ? x - ColorUtils.unicodeFontRenderer.getStringWidth(arrowInfo) : x + 6, y + 8, 16777215);
            GlStateManager.enableDepth();
        }
        else if (this.itemStack.getItem() instanceof ItemFishingRod && this.itemStack.hasTagCompound() && FISHING_ROD_LIST.stream().anyMatch(id -> this.itemStack.getTagCompound().getCompoundTag("ExtraAttributes").getString("id").equals(id)))
        {
            GlStateManager.disableDepth();
            ColorUtils.unicodeFontRenderer.drawStringWithShadow(ColorUtils.stringToRGB(ExtendedConfig.instance.baitCountColor).toColoredFont() + baitInfo, right ? x - ColorUtils.unicodeFontRenderer.getStringWidth(baitInfo) : x + 6, y + 8, 16777215);
            GlStateManager.enableDepth();
        }
    }

    private void initSize()
    {
        this.itemDamageWidth = this.mc.fontRendererObj.getStringWidth(this.renderInfo());
        this.width = 20 + this.itemDamageWidth;
    }
}