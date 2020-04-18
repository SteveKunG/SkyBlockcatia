package stevekung.mods.indicatia.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;

@Mixin(SoundManager.class)
public abstract class SoundManagerMixin
{
    private List<String> pausedSounds = new ArrayList<>();

    @Shadow
    @Final
    @Mutable
    private Map<String, ISound> playingSounds;

    @Shadow
    private SoundManager.SoundSystemStarterThread sndSystem;

    @Shadow
    public abstract boolean isSoundPlaying(ISound sound);

    @Overwrite
    public void pauseAllSounds()
    {
        for (Map.Entry<String, ISound> entry : this.playingSounds.entrySet())
        {
            String sound = entry.getKey();
            boolean flag = this.isSoundPlaying(entry.getValue());

            if (flag)
            {
                this.sndSystem.pause(sound);
                this.pausedSounds.add(sound);
            }
        }
    }

    @Overwrite
    public void resumeAllSounds()
    {
        for (String s : this.pausedSounds)
        {
            this.sndSystem.play(s);
        }
        this.pausedSounds.clear();
    }

    @Inject(method = "stopAllSounds()V", at = @At(value = "INVOKE", target = "java/util/Map.clear()V", shift = At.Shift.AFTER, remap = false, ordinal = 0))
    private void stopAllSounds(CallbackInfo info)
    {
        this.pausedSounds.clear();
    }
}