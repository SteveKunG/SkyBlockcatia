package stevekung.mods.indicatia.renderer;

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

    public static enum Side
    {
        LEFT, RIGHT;
    }
}