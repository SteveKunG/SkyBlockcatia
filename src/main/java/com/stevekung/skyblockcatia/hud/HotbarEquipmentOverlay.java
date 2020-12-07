package com.stevekung.skyblockcatia.hud;

import net.minecraft.item.ItemStack;

public class HotbarEquipmentOverlay extends EquipmentOverlay
{
    private final Side side;

    public HotbarEquipmentOverlay(ItemStack itemStack, Side side)
    {
        super(itemStack);
        this.side = side;
    }

    public Side getSide()
    {
        return this.side;
    }

    public enum Side
    {
        LEFT, RIGHT;
    }
}