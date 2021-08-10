package com.stevekung.skyblockcatia.mixin.renderer.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.HitboxRenderMode;
import com.stevekung.skyblockcatia.utils.skyblock.SBLocation;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher
{
    @Inject(method = "renderHitbox", cancellable = true, at = @At("HEAD"))
    private static void renderDebugBoundingBox(PoseStack poseStack, VertexConsumer buffer, Entity entity, float partialTicks, CallbackInfo info)
    {
        var mode = SkyBlockcatiaSettings.INSTANCE.hitboxRenderMode;

        if (SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION == SBLocation.DRAGON_NEST)
        {
            if (mode == HitboxRenderMode.DRAGON && !(entity instanceof EnderDragon) || mode == HitboxRenderMode.CRYSTAL && !(entity instanceof EndCrystal) || mode == HitboxRenderMode.DRAGON_AND_CRYSTAL && !(entity instanceof EnderDragon || entity instanceof EndCrystal))
            {
                info.cancel();
            }
        }
    }
}