package com.stevekung.skyblockcatia.mixin.fixes;

import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.utils.Utils;

import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

@Mixin(Scoreboard.class)
public abstract class MixinScoreboard
{
    @Shadow
    @Final
    private Map<String, ScorePlayerTeam> teams;

    @Shadow
    public abstract ScorePlayerTeam getTeam(String teamName);

    @Shadow
    public abstract void onTeamAdded(ScorePlayerTeam playerTeam);

    @Inject(method = "removeTeam", cancellable = true, at = @At("HEAD"))
    private void disableLog(ScorePlayerTeam playerTeam, CallbackInfo info)
    {
        if (Utils.isHypixel() && playerTeam == null)
        {
            info.cancel();
        }
    }

    @Overwrite
    public ScorePlayerTeam createTeam(String name)
    {
        if (name.length() > 16)
        {
            throw new IllegalArgumentException("The team name '" + name + "' is too long!");
        }
        else
        {
            ScorePlayerTeam scoreplayerteam = this.getTeam(name);

            if (scoreplayerteam != null)
            {
                if (Utils.isHypixel())
                {
                    return scoreplayerteam;
                }
                else
                {
                    throw new IllegalArgumentException("A team with the name '" + name + "' already exists!");
                }
            }
            else
            {
                scoreplayerteam = new ScorePlayerTeam((Scoreboard)(Object)this, name);
                this.teams.put(name, scoreplayerteam);
                this.onTeamAdded(scoreplayerteam);
                return scoreplayerteam;
            }
        }
    }
}