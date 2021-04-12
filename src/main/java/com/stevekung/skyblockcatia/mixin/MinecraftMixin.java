package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.LoggerIN;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
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
        SBAPIUtils.getSupportedPackNames();
    }

    @Inject(method = "runGameLoop()V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/EntityRenderer.updateCameraAndRender(FJ)V", shift = At.Shift.AFTER))
    private void runGameLoop(CallbackInfo info)
    {
        HUDRenderEventHandler.INSTANCE.getToastGui().drawToast(new ScaledResolution(this.that));
    }

    @Inject(method = "refreshResources()V", at = @At("HEAD"))
    private void refreshResources(CallbackInfo info)
    {
        if (SBAPIUtils.PACKS != null)
        {
            boolean found = false;

            for (ResourcePackRepository.Entry entry : this.that.getResourcePackRepository().getRepositoryEntries())
            {
                String packName = entry.getResourcePack().getPackName();
                String packDesc = entry.getTexturePackDescription();

                if (SBAPIUtils.PACKS.getPack16().stream().anyMatch(name -> packName.contains(name)))
                {
                    SkyBlockEventHandler.skyBlockPackResolution = "16";
                }
                if (SBAPIUtils.PACKS.getPack32().stream().anyMatch(name -> packName.contains(name)))
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
                LoggerIN.info("Found SkyBlock Pack with x" + SkyBlockEventHandler.skyBlockPackResolution + "! Loaded Glowing Texture for Dragon Set Armor");
            }
            else
            {
                SkyBlockEventHandler.foundSkyBlockPack = false;
                LoggerIN.info("SkyBlock Pack not found! Glowing Texture will not loaded for Dragon Set Armor");
            }
        }
        else
        {
            LoggerIN.warning("SupportedPack is null, Glowing Armor Overlay will not loaded!");
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

    @Redirect(method = "runTick()V", at = @At(value = "INVOKE", target = "net/minecraft/client/entity/EntityPlayerSP.sendHorseInventory()V"))
    private void openPlayerInventory(EntityPlayerSP player)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            this.that.displayGuiScreen(new GuiInventory(player));
        }
    }
}