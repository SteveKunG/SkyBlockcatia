package com.stevekung.skyblockcatia.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stats.StatisticsManager;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity
{
    public MixinClientPlayerEntity(Minecraft mc, ClientWorld world, ClientPlayNetHandler handler, StatisticsManager manager, ClientRecipeBook recipeBook)
    {
        super(world, handler.getGameProfile());
    }

    @Redirect(method = "setPlayerSPHealth(F)V", at = @At(value = "FIELD", target = "net/minecraft/client/entity/player/ClientPlayerEntity.hurtTime:I", opcode = Opcodes.PUTFIELD))
    private void setNoHurtTime(ClientPlayerEntity entity, int oldValue)
    {
        entity.hurtTime = SkyBlockEventHandler.isSkyBlock ? 0 : oldValue;
    }
}