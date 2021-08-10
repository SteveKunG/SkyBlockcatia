package com.stevekung.skyblockcatia.mixin.server.packs.resources;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.stevekung.skyblockcatia.core.SkyBlockcatia;
import com.stevekung.skyblockcatia.utils.SupportedPack;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.SimpleReloadableResourceManager;
import net.minecraft.util.Unit;

@Mixin(SimpleReloadableResourceManager.class)
public class MixinSimpleReloadableResourceManager
{
    @Shadow
    @Final
    PackType type;

    @Inject(method = "createReload", at = @At("HEAD"))
    private void reloadResources(Executor executor, Executor executor2, CompletableFuture<Unit> completableFuture, List<PackResources> list, CallbackInfoReturnable<ReloadInstance> info)
    {
        if (this.type == PackType.CLIENT_RESOURCES)
        {
            if (SBAPIUtils.PACKS != null && SBAPIUtils.PACKS.length > 0)
            {
                var found = false;

                for (var entry : Minecraft.getInstance().getResourcePackRepository().getSelectedPacks())
                {
                    var packName = entry.open().getName();
                    var packDesc = entry.getDescription().getContents();

                    for (var pack : SBAPIUtils.PACKS)
                    {
                        if (pack.getPack16().stream().anyMatch(packName::contains))
                        {
                            SupportedPack.RESOLUTION = "16";
                        }
                        if (pack.getPack32().stream().anyMatch(packName::contains))
                        {
                            SupportedPack.RESOLUTION = "32";
                        }

                        var nameMat = Pattern.compile(pack.getName()).matcher(packName);
                        var descMat = Pattern.compile(pack.getDescription()).matcher(packDesc);

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
                    SkyBlockcatia.LOGGER.info("Found '{}' Skyblock Pack with x" + SupportedPack.RESOLUTION + "! Loaded Glowing Texture for Dragon Armor Set", SupportedPack.TYPE);
                }
                else
                {
                    SupportedPack.FOUND = false;
                    SkyBlockcatia.LOGGER.info("No Skyblock Pack detected! Disable Glowing Texture for Dragon Armor Set");
                }
            }
            else
            {
                SkyBlockcatia.LOGGER.warning("SupportedPack is 'null'! Disable Glowing Texture for Dragon Armor Set");
            }
        }
    }
}