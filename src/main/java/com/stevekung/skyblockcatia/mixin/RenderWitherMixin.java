package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.event.HypixelEventHandler;

import net.minecraft.client.renderer.entity.RenderWither;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;

@Mixin(RenderWither.class)
public abstract class RenderWitherMixin
{
    @Redirect(method = "doRender(Lnet/minecraft/entity/boss/EntityWither;DDDFF)V", at = @At(value = "INVOKE", target = "net/minecraft/entity/boss/BossStatus.setBossStatus(Lnet/minecraft/entity/boss/IBossDisplayData;Z)V"))
    private void setBossStatus(IBossDisplayData displayData, boolean hasColorModifier)
    {
        BossStatus.setBossStatus(displayData, !HypixelEventHandler.isSkyBlock);
    }
}