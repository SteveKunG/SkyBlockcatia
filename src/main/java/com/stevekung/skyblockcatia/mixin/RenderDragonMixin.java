package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.skyblock.SBBossBar;
import com.stevekung.skyblockcatia.utils.skyblock.SBBossBar.DragonType;

import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.util.EnumChatFormatting;

@Mixin(RenderDragon.class)
public class RenderDragonMixin
{
    @Inject(method = "doRender(Lnet/minecraft/entity/boss/EntityDragon;DDDFF)V", at = @At("HEAD"))
    private void setBossStatus(EntityDragon entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo info)
    {
        if (SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION.isTheEnd())
        {
            String name = EnumChatFormatting.getTextWithoutFormattingCodes(entity.getDisplayName().getUnformattedText());
            DragonType type = null;

            for (DragonType typeList : DragonType.values())
            {
                if (typeList.getName().equals(name))
                {
                    type = typeList;
                    break;
                }
            }
            if (type != null)
            {
                SBBossBar.healthScale = SkyBlockEventHandler.dragonHealth / type.getMaxHealth();
                SBBossBar.bossName = entity.getDisplayName().getFormattedText();
            }
        }
    }
}