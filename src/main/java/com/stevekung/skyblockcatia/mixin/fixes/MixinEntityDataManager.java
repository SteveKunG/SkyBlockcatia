package com.stevekung.skyblockcatia.mixin.fixes;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.utils.Utils;

import net.minecraft.network.datasync.EntityDataManager;

@Mixin(EntityDataManager.class)
public abstract class MixinEntityDataManager
{
    @Inject(method = "setEntryValue(Lnet/minecraft/network/datasync/EntityDataManager$DataEntry;Lnet/minecraft/network/datasync/EntityDataManager$DataEntry;)V", cancellable = true, at = @At("HEAD"))
    private <T> void setEntryValue(EntityDataManager.DataEntry<T> target, EntityDataManager.DataEntry<?> source, CallbackInfo info)
    {
        if (Utils.isHypixel() && !Objects.equals(source.getKey().getSerializer(), target.getKey().getSerializer())) // prevent error
        {
            info.cancel();
        }
    }
}