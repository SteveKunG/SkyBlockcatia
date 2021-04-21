package com.stevekung.skyblockcatia.mixin.fixes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;

import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

@Mixin(Scoreboard.class)
public class ScoreboardMixin
{
    @Inject(method = "removeObjective", cancellable = true, at = @At("HEAD"))
    private void disableLog(ScoreObjective score, CallbackInfo info)
    {
        if (SkyBlockcatiaConfig.disableErrorLog && score == null)
        {
            info.cancel();
        }
    }

    @Inject(method = "removeTeam", cancellable = true, at = @At("HEAD"))
    private void disableLog(ScorePlayerTeam score, CallbackInfo info)
    {
        if (SkyBlockcatiaConfig.disableErrorLog && score == null)
        {
            info.cancel();
        }
    }

    @Inject(method = "createTeam", cancellable = true, at = @At(value = "NEW", target = "java/lang/IllegalArgumentException", remap = false, shift = Shift.BEFORE))
    private void disableLog(String name, CallbackInfoReturnable<ScorePlayerTeam> info)
    {
        ScorePlayerTeam scoreplayerteam = ((Scoreboard)(Object)this).getTeam(name);

        if (SkyBlockcatiaConfig.disableErrorLog && scoreplayerteam != null)
        {
            info.setReturnValue(scoreplayerteam);
        }
    }
}