package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.util.datafix.fixes.ItemPotionFix;

@Mixin(ItemPotionFix.class)
public interface InvokerItemPotionFix
{
    @Accessor("POTIONS")
    static String[] getPotions()
    {
        throw new Error();
    }
}