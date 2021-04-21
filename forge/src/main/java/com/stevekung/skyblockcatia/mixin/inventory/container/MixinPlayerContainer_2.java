package com.stevekung.skyblockcatia.mixin.inventory.container;

import org.spongepowered.asm.mixin.Mixin;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.Utils;
import net.minecraft.world.inventory.Slot;

@Mixin(targets = "net.minecraft.world.inventory.InventoryMenu$2")
public class MixinPlayerContainer_2 extends Slot
{
    MixinPlayerContainer_2()
    {
        super(null, 0, 0, 0);
    }

    @Override
    public boolean isActive()
    {
        return !(Utils.isHypixel() && SkyBlockEventHandler.isSkyBlock);
    }
}