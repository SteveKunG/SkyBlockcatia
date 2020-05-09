package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.gui.screen.SkyBlockcatiaErrorScreen;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.ClientModLoader;

@Mixin(value = ClientModLoader.class, remap = false)
public abstract class MixinClientModLoader
{
    @Shadow(remap = false)
    private static Minecraft mc;

    @Inject(method = "completeModLoading()Z", cancellable = true, remap = false, at = @At("RETURN"))
    private static void completeModLoading(CallbackInfoReturnable<Boolean> info)
    {
        if (SkyBlockcatiaMod.NO_UUID_MATCHED)
        {
            MixinClientModLoader.mc.displayGuiScreen(new SkyBlockcatiaErrorScreen("No UUID Matched", "SkyBlockcatia couldn't find a matched UUID in our database", "Make sure you have already tell your IGN in #ign-verify! :)"));
            info.setReturnValue(true);
        }
        if (SkyBlockcatiaMod.GITHUB_DOWN)
        {
            Minecraft.getInstance().displayGuiScreen(new SkyBlockcatiaErrorScreen("GitHub Down", "SkyBlockcatia couldn't connect to GitHub database", "Please restart your game or check website status: https://www.githubstatus.com/"));
            info.setReturnValue(true);
        }
    }
}