package com.stevekung.skyblockcatia.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.resources.IAsyncReloader;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.Unit;

@Mixin(SimpleReloadableResourceManager.class)
public abstract class MixinSimpleReloadableResourceManager
{
    @Shadow
    @Final
    @Mutable
    private ResourcePackType type;

    @Inject(method = "reloadResources(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Ljava/util/List;)Lnet/minecraft/resources/IAsyncReloader;", at = @At("HEAD"))
    private void reloadResources(Executor backgroundExecutor, Executor gameExecutor, CompletableFuture<Unit> waitingFor, List<IResourcePack> resourcePacks, CallbackInfoReturnable<IAsyncReloader> info)
    {
        if (this.type == ResourcePackType.CLIENT_RESOURCES)
        {
            boolean found = false;

            for (ClientResourcePackInfo entry : Minecraft.getInstance().getResourcePackList().getEnabledPacks())
            {
                String packName = entry.getResourcePack().getName();
                String packDesc = entry.getDescription().getUnformattedComponentText();

                if (MainEventHandler.SKYBLOCK_PACK_16.stream().anyMatch(name -> packName.contains(name)))
                {
                    SkyBlockEventHandler.skyBlockPackResolution = "16";
                }
                if (MainEventHandler.SKYBLOCK_PACK_32.stream().anyMatch(name -> packName.contains(name)))
                {
                    SkyBlockEventHandler.skyBlockPackResolution = "32";
                }

                if ((packName.contains("Hypixel Skyblock Pack") || packName.contains("Skyblock_Pack")) && packDesc.contains("by Hypixel Packs HQ"))
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
    }
}