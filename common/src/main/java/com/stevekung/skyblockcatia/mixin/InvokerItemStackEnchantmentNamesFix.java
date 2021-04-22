package com.stevekung.skyblockcatia.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.datafix.fixes.EntityBlockStateFix;
import net.minecraft.util.datafix.fixes.ItemStackEnchantmentNamesFix;

@Mixin(ItemStackEnchantmentNamesFix.class)
public interface InvokerItemStackEnchantmentNamesFix
{
    @Accessor("MAP")
    static Int2ObjectMap<String> getMap()
    {
        throw new Error();
    }
}