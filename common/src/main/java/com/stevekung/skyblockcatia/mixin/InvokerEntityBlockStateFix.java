package com.stevekung.skyblockcatia.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.util.datafix.fixes.EntityBlockStateFix;

@Mixin(EntityBlockStateFix.class)
public interface InvokerEntityBlockStateFix
{
    @Accessor("MAP")
    static Map<String, Integer> getMap()
    {
        throw new Error();
    }
}