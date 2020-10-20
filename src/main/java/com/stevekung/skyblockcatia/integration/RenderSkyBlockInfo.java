package com.stevekung.skyblockcatia.integration;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.stevekung.indicatia.hud.InfoOverlay;
import com.stevekung.indicatia.utils.event.InfoOverlayEvent;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.skyblock.SBLocation;
import com.stevekung.stevekungslib.utils.ColorUtils;

import net.minecraft.block.Blocks;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.overlay.PlayerTabOverlayGui;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderSkyBlockInfo
{
    private static final ImmutableList<BlockPos> END_PORTAL_FRAMES = ImmutableList.of(new BlockPos(-669, 9, -277), new BlockPos(-669, 9, -275), new BlockPos(-670, 9, -278), new BlockPos(-672, 9, -278), new BlockPos(-673, 9, -277), new BlockPos(-673, 9, -275), new BlockPos(-672, 9, -274), new BlockPos(-670, 9, -274));
    private final Minecraft mc;

    public RenderSkyBlockInfo()
    {
        this.mc = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void onInfoRender(InfoOverlayEvent event)
    {
        if (SkyBlockcatiaSettings.INSTANCE.placedSummoningEyeTracker && SkyBlockEventHandler.SKY_BLOCK_LOCATION.isTheEnd())
        {
            int summoningEyeCount = 0;

            for (BlockPos pos : END_PORTAL_FRAMES)
            {
                if (this.mc.world.getBlockState(pos).getBlock() == Blocks.END_PORTAL_FRAME && this.mc.world.getBlockState(pos).get(EndPortalFrameBlock.EYE))
                {
                    ++summoningEyeCount;
                }
            }
            event.getInfos().add(new InfoOverlay("Placed Eye", summoningEyeCount + "/8", SkyBlockcatiaSettings.INSTANCE.placedSummoningEyeColor, SkyBlockcatiaSettings.INSTANCE.placedSummoningEyeValueColor, InfoOverlay.Position.RIGHT));
        }
        if (SkyBlockcatiaSettings.INSTANCE.lobbyPlayerCount && SkyBlockEventHandler.SKY_BLOCK_LOCATION != SBLocation.YOUR_ISLAND && !this.mc.isSingleplayer())
        {
            List<NetworkPlayerInfo> list = PlayerTabOverlayGui.ENTRY_ORDERING.sortedCopy(this.mc.player.connection.getPlayerInfoMap());
            event.getInfos().add(new InfoOverlay("Lobby Players Count", String.valueOf(HUDRenderEventHandler.getPlayerCount(list)), ColorUtils.decimalToRgb(TextFormatting.GOLD.getColor()), ColorUtils.decimalToRgb(TextFormatting.GREEN.getColor()), InfoOverlay.Position.RIGHT));
        }
    }
}