package com.stevekung.skyblockcatia.mixin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.LoggerIN;
import com.stevekung.skyblockcatia.utils.SupportedPack;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;

@Mixin(Minecraft.class)
public class MinecraftMixin
{
    private final Minecraft that = (Minecraft) (Object) this;

    @Inject(method = "startGame()V", at = @At("HEAD"))
    private void startGame(CallbackInfo info)
    {
        SBAPIUtils.getMisc();
    }

    @Inject(method = "runGameLoop()V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/EntityRenderer.updateCameraAndRender(FJ)V", shift = At.Shift.AFTER))
    private void runGameLoop(CallbackInfo info)
    {
        HUDRenderEventHandler.INSTANCE.getToastGui().drawToast(new ScaledResolution(this.that));
    }

    @Inject(method = "refreshResources()V", at = @At("HEAD"))
    private void refreshResources(CallbackInfo info)
    {
        if (SBAPIUtils.PACKS != null && SBAPIUtils.PACKS.length > 0)
        {
            boolean found = false;

            for (ResourcePackRepository.Entry entry : this.that.getResourcePackRepository().getRepositoryEntries())
            {
                String packName = entry.getResourcePack().getPackName();
                String packDesc = entry.getTexturePackDescription();

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
                LoggerIN.info("Found '{}' Skyblock Pack with x" + SupportedPack.RESOLUTION + "! Loaded Glowing Texture for Dragon Armor Set", SupportedPack.TYPE);
            }
            else
            {
                SupportedPack.FOUND = false;
                LoggerIN.info("No Skyblock Pack detected! Disable Glowing Texture for Dragon Armor Set");
            }
        }
        else
        {
            LoggerIN.warning("SupportedPack is 'null'! Disable Glowing Texture for Dragon Armor Set");
        }
    }

    @Redirect(method = "runTick()V", at = @At(value = "INVOKE", target = "net/minecraft/client/settings/KeyBinding.isPressed()Z", ordinal = 3))
    private boolean disableInventory(KeyBinding key)
    {
        boolean foundDragon = false;

        for (Entity entity : this.that.theWorld.loadedEntityList)
        {
            if (entity instanceof EntityDragon)
            {
                foundDragon = true;
                break;
            }
        }
        if (SkyBlockEventHandler.isSkyBlock && SkyBlockcatiaSettings.INSTANCE.sneakToOpenInventoryWhileFightDragon && foundDragon)
        {
            return key.isPressed() && this.that.thePlayer.isSneaking();
        }
        return key.isPressed();
    }
}