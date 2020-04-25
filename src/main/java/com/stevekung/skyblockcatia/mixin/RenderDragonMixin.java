package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.utils.SkyBlockBossStatus;

import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.entity.boss.IBossDisplayData;

@Mixin(RenderDragon.class)
public abstract class RenderDragonMixin
{
    @Redirect(method = "doRender(Lnet/minecraft/entity/boss/EntityDragon;DDDFF)V", at = @At(value = "INVOKE", target = "net/minecraft/entity/boss/BossStatus.setBossStatus(Lnet/minecraft/entity/boss/IBossDisplayData;Z)V"))
    private void setBossStatus(IBossDisplayData displayData, boolean hasColorModifier)
    {
        SkyBlockBossStatus.setBossStatus(displayData, false);
    }
}