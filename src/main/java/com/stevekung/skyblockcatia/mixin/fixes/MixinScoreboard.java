package com.stevekung.skyblockcatia.mixin.fixes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.utils.Utils;

import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

@Mixin(Scoreboard.class)
public abstract class MixinScoreboard
{
    private final Scoreboard that = (Scoreboard) (Object) this;

    @Inject(method = "createTeam(Ljava/lang/String;)Lnet/minecraft/scoreboard/ScorePlayerTeam;", cancellable = true, at = @At(value = "INVOKE", target = "net/minecraft/scoreboard/Scoreboard.getTeam(Ljava/lang/String;)Lnet/minecraft/scoreboard/ScorePlayerTeam;", shift = Shift.AFTER))
    private void createTeam(String name, CallbackInfoReturnable<ScorePlayerTeam> info)
    {
        ScorePlayerTeam scoreplayerteam = this.that.getTeam(name);

        if (Utils.isHypixel() && scoreplayerteam != null) // prevent error
        {
            info.setReturnValue(scoreplayerteam);
        }
    }
}