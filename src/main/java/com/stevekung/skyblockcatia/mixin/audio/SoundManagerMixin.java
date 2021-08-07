package com.stevekung.skyblockcatia.mixin.audio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;

@Mixin(SoundManager.class)
public class SoundManagerMixin
{
    private final List<String> pausedSounds = new ArrayList<>();

    @Shadow
    @Final
    Map<String, ISound> playingSounds;

    @Shadow
    SoundManager.SoundSystemStarterThread sndSystem;

    @Overwrite
    public void pauseAllSounds()
    {
        for (Map.Entry<String, ISound> entry : this.playingSounds.entrySet())
        {
            String sound = entry.getKey();
            boolean flag = ((SoundManager) (Object) this).isSoundPlaying(entry.getValue());

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