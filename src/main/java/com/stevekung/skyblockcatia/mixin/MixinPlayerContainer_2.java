package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.Utils;

import net.minecraft.inventory.container.Slot;

@Mixin(targets = "net.minecraft.inventory.container.PlayerContainer$2")
public class MixinPlayerContainer_2 extends Slot
{
    private MixinPlayerContainer_2()
    {
        super(null, 0, 0, 0);
    }

    @Override
    public boolean isEnabled()
    {
        return !(Utils.isHypixel() && SkyBlockEventHandler.isSkyBlock);
    }
}