package com.stevekung.skyblockcatia.mixin.resources;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.utils.SupportedPack;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.*;
import net.minecraft.util.Unit;

@Mixin(SimpleReloadableResourceManager.class)
public class MixinSimpleReloadableResourceManager
{
    @Shadow
    @Final
    private ResourcePackType type;

    @Inject(method = "reloadResources(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)Lnet/minecraft/resources/IAsyncReloader;", at = @At("HEAD"))
    private void reloadResources(Executor backgroundExecutor, Executor gameExecutor, CompletableFuture<Unit> waitingFor, List<IResourcePack> resourcePacks, CallbackInfoReturnable<IAsyncReloader> info)
    {
        if (this.type == ResourcePackType.CLIENT_RESOURCES)
        {
            if (SBAPIUtils.PACKS != null && SBAPIUtils.PACKS.length > 0)
            {
                boolean found = false;

                for (ResourcePackInfo entry : Minecraft.getInstance().getResourcePackList().getEnabledPacks())
                {
                    String packName = entry.getResourcePack().getName();
                    String packDesc = entry.getDescription().getUnformattedComponentText();

                    for (SupportedPack pack : SBAPIUtils.PACKS)
                    {
                        if (pack.getPack16().stream().anyMatch(name -> packName.contains(name)))
                        {
                            SupportedPack.RESOLUTION = "16";
                        }
                        if (pack.getPack32().stream().anyMatch(name -> packName.contains(name)))
                        {
                            SupportedPack.RESOLUTION = "32";
                        }

                        Matcher nameMat = Pattern.compile(pack.getName()).matcher(packName);
                        Matcher descMat = Pattern.compile(pack.getDescription()).matcher(packDesc);

                        if (nameMat.find() && descMat.find())
                        {
                            SupportedPack.FOUND = found = true;
                            SupportedPack.TYPE = pack.getType();
                            break;
                        }
                    }
                }
                if (found)
                {
                    SkyBlockcatiaMod.LOGGER.info("Found '{}' Skyblock Pack with x" + SupportedPack.RESOLUTION + "! Loaded Glowing Texture for Dragon Armor Set", SupportedPack.TYPE);
                }
                else
                {
                    SupportedPack.FOUND = false;
                    SkyBlockcatiaMod.LOGGER.info("No Skyblock Pack detected! Disable Glowing Texture for Dragon Armor Set");
                }
            }
            else
            {
                SkyBlockcatiaMod.LOGGER.warning("SupportedPack is 'null'! Disable Glowing Texture for Dragon Armor Set");
            }
        }
    }
}