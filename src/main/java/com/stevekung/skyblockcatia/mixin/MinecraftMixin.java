package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.event.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.event.HypixelEventHandler;
import com.stevekung.skyblockcatia.utils.LoggerIN;
import com.stevekung.skyblockcatia.utils.SkyBlockAPIUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.InventoryPlayer;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin
{
    private final Minecraft that = (Minecraft) (Object) this;

    @Inject(method = "runGameLoop()V", at = @At(value = "INVOKE", target = "net/minecraft/client/renderer/EntityRenderer.updateCameraAndRender(FJ)V", shift = At.Shift.AFTER))
    private void runGameLoop(CallbackInfo info)
    {
        HUDRenderEventHandler.INSTANCE.getToastGui().drawToast(new ScaledResolution(this.that));
    }

    @Inject(method = "refreshResources()V", at = @At("HEAD"))
    private void refreshResources(CallbackInfo info)
    {
        boolean found = false;

        for (ResourcePackRepository.Entry entry : this.that.getResourcePackRepository().getRepositoryEntries())
        {
            String packName = entry.getResourcePack().getPackName();
            String packDesc = entry.getTexturePackDescription();

            if (SkyBlockAPIUtils.PACKS.getPack16().stream().anyMatch(name -> packName.contains(name)))
            {
                HypixelEventHandler.skyBlockPackResolution = "16";
            }
            if (SkyBlockAPIUtils.PACKS.getPack32().stream().anyMatch(name -> packName.contains(name)))
            {
                HypixelEventHandler.skyBlockPackResolution = "32";
            }

            if ((packName.contains("Hypixel Skyblock Pack") || packName.contains("Skyblock_Pack")) && (packDesc.contains("by Hypixel Packs HQ") || packDesc.contains("by Packs HQ")))
            {
                HypixelEventHandler.foundSkyBlockPack = true;
                found = true;
                break;
            }
        }
        if (found)
        {
            LoggerIN.info("Found SkyBlock Pack with x" + HypixelEventHandler.skyBlockPackResolution + "! Loaded Glowing Texture for Dragon Set Armor");
        }
        else
        {
            HypixelEventHandler.foundSkyBlockPack = false;
            LoggerIN.info("SkyBlock Pack not found! Glowing Texture will not loaded for Dragon Set Armor");
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
        if (HypixelEventHandler.isSkyBlock && ExtendedConfig.instance.sneakToOpenInventoryWhileFightDragon && foundDragon)
        {
            return key.isPressed() && this.that.thePlayer.isSneaking();
        }
        return key.isPressed();
    }

    @Redirect(method = "runTick()V", at = @At(value = "INVOKE", target = "net/minecraft/client/entity/EntityPlayerSP.sendHorseInventory()V"))
    private void openPlayerInventory(EntityPlayerSP player)
    {
        if (HypixelEventHandler.isSkyBlock)
        {
            this.that.displayGuiScreen(new GuiInventory(player));
        }
    }

    @Redirect(method = "runTick()V", at = @At(value = "INVOKE", target = "net/minecraft/entity/player/InventoryPlayer.changeCurrentItem(I)V"))
    private void changeCurrentItem(InventoryPlayer invPlayer, int slot)
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
        if (!(HypixelEventHandler.isSkyBlock && ExtendedConfig.instance.preventScrollHotbarWhileFightDragon && foundDragon))
        {
            invPlayer.changeCurrentItem(slot);
        }
    }
}