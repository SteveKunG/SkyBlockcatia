package com.stevekung.skyblockcatia.mixin.fixes;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.utils.Utils;

import net.minecraft.network.datasync.EntityDataManager;

@Mixin(EntityDataManager.class)
public class MixinEntityDataManager
{
    @Shadow
    private <T> void setEntryValue(EntityDataManager.DataEntry<T> target, EntityDataManager.DataEntry<?> source) {}

    @Redirect(method = "setEntryValues", at = @At(value = "INVOKE", target = "net/minecraft/network/datasync/EntityDataManager.setEntryValue(Lnet/minecraft/network/datasync/EntityDataManager$DataEntry;Lnet/minecraft/network/datasync/EntityDataManager$DataEntry;)V"))
    private <T> void setEntryValue(EntityDataManager manager, EntityDataManager.DataEntry<T> target, EntityDataManager.DataEntry<?> source)
    {
        if (Utils.isHypixel() && !Objects.equals(source.getKey().getSerializer(), target.getKey().getSerializer()))
        {
            return;
        }
        this.setEntryValue(target, source);
    }
}