package com.stevekung.skyblockcatia.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
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
            if (SBAPIUtils.PACKS != null)
            {
                boolean found = false;

                for (ResourcePackInfo entry : Minecraft.getInstance().getResourcePackList().getEnabledPacks())
                {
                    String packName = entry.getResourcePack().getName();
                    String packDesc = entry.getDescription().getUnformattedComponentText();

                    if (SBAPIUtils.PACKS.getPack16().stream().anyMatch(packName::contains))
                    {
                        SkyBlockEventHandler.skyBlockPackResolution = "16";
                    }
                    if (SBAPIUtils.PACKS.getPack32().stream().anyMatch(packName::contains))
                    {
                        SkyBlockEventHandler.skyBlockPackResolution = "32";
                    }

                    if ((packName.contains("Hypixel Skyblock Pack") || packName.contains("Skyblock_Pack")) && (packDesc.contains("by Hypixel Packs HQ") || packDesc.contains("by Packs HQ")))
                    {
                        SkyBlockEventHandler.foundSkyBlockPack = true;
                        found = true;
                        break;
                    }
                }
                if (found)
                {
                    SkyBlockcatiaMod.LOGGER.info("Found SkyBlock Pack with x" + SkyBlockEventHandler.skyBlockPackResolution + "! Loaded Glowing Texture for Dragon Set Armor");
                }
                else
                {
                    SkyBlockEventHandler.foundSkyBlockPack = false;
                    SkyBlockcatiaMod.LOGGER.info("SkyBlock Pack not found! Glowing Texture will not loaded for Dragon Set Armor");
                }
            }
            else
            {
                SkyBlockcatiaMod.LOGGER.warning("SupportedPack is null, Glowing Armor Overlay will not loaded!");
            }
        }
    }
}