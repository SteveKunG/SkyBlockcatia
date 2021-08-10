package com.stevekung.skyblockcatia.mixin.fixes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.stevekung.skyblockcatia.utils.Utils;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

@Mixin(Scoreboard.class)
public class MixinScoreboard
{
    @Inject(method = "removePlayerTeam", cancellable = true, at = @At("HEAD"))
    private void disableLog(PlayerTeam playerTeam, CallbackInfo info)
    {
        if (Utils.isHypixel() && playerTeam == null)
        {
            info.cancel();
        }
    }

    @Inject(method = "addPlayerTeam", cancellable = true, at = @At(value = "NEW", target = "java/lang/IllegalArgumentException", remap = false, shift = At.Shift.BEFORE))
    private void disableLog(String name, CallbackInfoReturnable<PlayerTeam> info)
    {
        var scoreplayerteam = ((Scoreboard) (Object) this).getPlayerTeam(name);

        if (scoreplayerteam != null)
        {
            info.setReturnValue(scoreplayerteam);
        }
    }
}