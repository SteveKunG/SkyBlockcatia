package com.stevekung.skyblockcatia.mixin.fixes;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.stevekung.skyblockcatia.utils.Utils;
import net.minecraft.network.syncher.SynchedEntityData;

@Mixin(SynchedEntityData.class)
public class MixinSynchedEntityData
{
    @Shadow
    private <T> void assignValue(SynchedEntityData.DataItem<T> target, SynchedEntityData.DataItem<?> source) {}

    @Redirect(method = "assignValues", at = @At(value = "INVOKE", target = "net/minecraft/network/syncher/SynchedEntityData.assignValue(Lnet/minecraft/network/syncher/SynchedEntityData$DataItem;Lnet/minecraft/network/syncher/SynchedEntityData$DataItem;)V"))
    private <T> void assignValue(SynchedEntityData manager, SynchedEntityData.DataItem<T> target, SynchedEntityData.DataItem<?> source)
    {
        if (Utils.isHypixel() && !Objects.equals(source.getAccessor().getSerializer(), target.getAccessor().getSerializer()))
        {
            return;
        }
        this.assignValue(target, source);
    }
}