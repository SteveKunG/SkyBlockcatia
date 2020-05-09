package com.stevekung.skyblockcatia.event.handler;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import com.google.common.collect.ImmutableList;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.event.ClientBlockBreakEvent;
import com.stevekung.skyblockcatia.event.GrapplingHookEvent;
import com.stevekung.skyblockcatia.utils.CoordsPair;
import com.stevekung.skyblockcatia.utils.TimeUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBLocation;
import com.stevekung.stevekungslib.utils.ColorUtils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HUDRenderEventHandler
{
    private final Minecraft mc;
    private long lastBlockBreak = -1;
    private long lastGrapplingHookUse = -1;
    private long lastZealotRespawn = -1;
    private boolean foundDragon;
    private Set<CoordsPair> recentlyLoadedChunks = new HashSet<>();
    private static final ImmutableList<AxisAlignedBB> ZEALOT_SPAWN_AREA = ImmutableList.of(new AxisAlignedBB(-609, 9, -303, -631, 5, -320), new AxisAlignedBB(-622, 5, -321, -640, 5, -334), new AxisAlignedBB(-631, 7, -293, -648, 7, -312), new AxisAlignedBB(-658, 8, -308, -672, 7, -320), new AxisAlignedBB(-709, 9, -325, -694, 10, -315), new AxisAlignedBB(-702, 10, -303, -738, 5, -261), new AxisAlignedBB(-705, 5, -257, -678, 5, -296), new AxisAlignedBB(-657, 5, -210, -624, 8, -242), new AxisAlignedBB(-625, 7, -256, -662, 5, -286));
    private static final ImmutableList<BlockPos> END_PORTAL_FRAMES = ImmutableList.of(new BlockPos(-669, 9, -277), new BlockPos(-669, 9, -275), new BlockPos(-670, 9, -278), new BlockPos(-672, 9, -278), new BlockPos(-673, 9, -277), new BlockPos(-673, 9, -275), new BlockPos(-672, 9, -274), new BlockPos(-670, 9, -274));

    public HUDRenderEventHandler()
    {
        this.mc = Minecraft.getInstance();
    }

    @SubscribeEvent
    public void onGrapplingHookUse(GrapplingHookEvent event)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            long now = System.currentTimeMillis();
            boolean isHook = event.getItemStack().getDisplayName().getString().equals("Grappling Hook");

            if (now - this.lastGrapplingHookUse > 2000L && isHook)
            {
                this.lastGrapplingHookUse = now;
            }
        }
    }

    @SubscribeEvent
    public void onClientBlockBreak(ClientBlockBreakEvent event)
    {
        long now = System.currentTimeMillis();

        if (SkyBlockEventHandler.isSkyBlock && !this.mc.player.getHeldItemMainhand().isEmpty() && this.mc.player.getHeldItemMainhand().hasTag() && (this.mc.player.getHeldItemMainhand().getTag().getCompound("ExtraAttributes").getString("id").equals("JUNGLE_AXE") || this.mc.player.getHeldItemMainhand().getTag().getCompound("ExtraAttributes").getString("id").equals("TREECAPITATOR_AXE")))
        {
            Block block = event.getWorld().getBlockState(event.getPos()).getBlock();

            if (now - this.lastBlockBreak > 2000L && block.isIn(BlockTags.LOGS))
            {
                this.lastBlockBreak = now;
            }
        }
    }

    @SubscribeEvent
    public void onRenderChat(RenderGameOverlayEvent.Chat event)
    {
        if (this.mc.currentScreen != null && this.mc.currentScreen instanceof ChestScreen)
        {
            ChestScreen chest = (ChestScreen)this.mc.currentScreen;

            if (MainEventHandler.showChat && MainEventHandler.CHATABLE_LIST.stream().anyMatch(invName -> chest.getTitle().getUnformattedComponentText().contains(invName)))
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPreInfoRender(RenderGameOverlayEvent.Pre event)
    {
        ForgeIngameGui.renderObjective = !this.mc.gameSettings.showDebugInfo;
        double jungleAxeDelay = 0;
        double grapplingHookDelay = 0;
        double zealotRespawnDelay = 0;

        if (SBExtendedConfig.INSTANCE.axeCooldown)
        {
            jungleAxeDelay = this.getItemDelay(2000, this.lastBlockBreak);
        }
        if (SBExtendedConfig.INSTANCE.grapplingHookCooldown)
        {
            grapplingHookDelay = this.getItemDelay(2000, this.lastGrapplingHookUse);
        }
        if (SBExtendedConfig.INSTANCE.zealotRespawnCooldown)
        {
            zealotRespawnDelay = this.getItemDelay(15000, this.lastZealotRespawn);
        }

        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT)
        {
            if (this.mc.gameSettings.showDebugInfo || this.mc.player == null && this.mc.world == null)
            {
                return;
            }

            List<CrosshairOverlay> crosshairInfo = new LinkedList<>();
            int center = 0;

            if (SBExtendedConfig.INSTANCE.axeCooldown && jungleAxeDelay >= 0.01D)
            {
                crosshairInfo.add(new CrosshairOverlay(SBExtendedConfig.INSTANCE.axeCooldownColor, jungleAxeDelay));
            }
            if (SBExtendedConfig.INSTANCE.grapplingHookCooldown && grapplingHookDelay >= 0.01D)
            {
                crosshairInfo.add(new CrosshairOverlay(SBExtendedConfig.INSTANCE.grapplingHookCooldownColor, grapplingHookDelay));
            }
            if (SBExtendedConfig.INSTANCE.zealotRespawnCooldown && zealotRespawnDelay >= 0.01D && !this.foundDragon)
            {
                crosshairInfo.add(new CrosshairOverlay(SBExtendedConfig.INSTANCE.zealotRespawnCooldownColor, zealotRespawnDelay));
            }

            for (CrosshairOverlay overlay : crosshairInfo)
            {
                float fontHeight = this.mc.fontRenderer.FONT_HEIGHT + 1;
                float width = event.getWindow().getScaledWidth() / 2 + 1.0625F;
                float height = event.getWindow().getScaledHeight() / 2 + 6 + fontHeight * center;
                this.mc.fontRenderer.drawStringWithShadow(overlay.getColor() + overlay.getDelay(), width - this.mc.fontRenderer.getStringWidth(overlay.getDelay()) / 2, height, 16777215);
                center++;
            }

            /*if (SkyBlockcatiaConfig.enableRenderInfo)// TODO Indicatia support
            {
                List<String> leftInfo = new LinkedList<>();
                List<String> rightInfo = new LinkedList<>();

                if (SBExtendedConfig.INSTANCE.placedSummoningEyeTracker && SkyBlockEventHandler.SKY_BLOCK_LOCATION.isTheEnd())
                {
                    int summoningEyeCount = 0;

                    for (BlockPos pos : END_PORTAL_FRAMES)
                    {
                        if (this.mc.world.getBlockState(pos).getBlock() == Blocks.END_PORTAL_FRAME && this.mc.world.getBlockState(pos).get(EndPortalFrameBlock.EYE))
                        {
                            ++summoningEyeCount;
                        }
                    }
                    String color = ColorUtils.stringToRGB(SBExtendedConfig.INSTANCE.placedSummoningEyeValueColor).toColoredFont();
                    rightInfo.add(ColorUtils.stringToRGB(SBExtendedConfig.INSTANCE.placedSummoningEyeColor).toColoredFont() + "Placed Eye: " + color + summoningEyeCount + "/8");
                }
                if (SBExtendedConfig.INSTANCE.golemStageTracker && SkyBlockEventHandler.SKY_BLOCK_LOCATION.isTheEnd())
                {
                    int golemStage = 0;

                    for (int headPos = 1; headPos < 5; headPos++)
                    {
                        if (this.mc.world.getBlockState(new BlockPos(-689, 5 + headPos, -273)).getBlock() == Blocks.PLAYER_HEAD)
                        {
                            golemStage = headPos + 1;
                        }
                    }
                    String color = ColorUtils.stringToRGB(SBExtendedConfig.INSTANCE.golemStageValueColor).toColoredFont();
                    rightInfo.add(ColorUtils.stringToRGB(SBExtendedConfig.INSTANCE.golemStageColor).toColoredFont() + "Golem Stage: " + color + (golemStage == 5 ? "Golem Spawning soon!" : golemStage + "/5"));
                }
            }*/
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (event.getEntity() == this.mc.player)
            {
                this.recentlyLoadedChunks.clear();
            }
            if (event.getEntity() instanceof EnderDragonEntity)
            {
                this.foundDragon = true;

                if (SBExtendedConfig.INSTANCE.showHitboxWhenDragonSpawned)
                {
                    this.mc.getRenderManager().setDebugBoundingBox(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (event.getEntity() instanceof EnderDragonEntity)
            {
                this.foundDragon = false;

                if (SBExtendedConfig.INSTANCE.showHitboxWhenDragonSpawned)
                {
                    this.mc.getRenderManager().setDebugBoundingBox(false);
                }
            }
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event)
    {
        if (SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION == SBLocation.DRAGON_NEST)
        {
            CoordsPair coords = new CoordsPair(event.getChunk().getPos().x, event.getChunk().getPos().z);
            this.recentlyLoadedChunks.add(coords);
            TimeUtils.schedule(() -> this.recentlyLoadedChunks.remove(coords), 20);
        }
    }

    @SubscribeEvent
    public void onEntityEnteringChunk(EntityEvent.EnteringChunk event)
    {
        Entity entity = event.getEntity();

        if (SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION == SBLocation.DRAGON_NEST)
        {
            if (ZEALOT_SPAWN_AREA.stream().anyMatch(aabb -> aabb.contains(new Vec3d(entity.getPosX(), entity.getPosY(), entity.getPosZ()))))
            {
                if (entity instanceof EndermanEntity)
                {
                    if (!this.recentlyLoadedChunks.contains(new CoordsPair(event.getNewChunkX(), event.getNewChunkZ())) && entity.ticksExisted == 0)
                    {
                        long now = System.currentTimeMillis();

                        if (now - this.lastZealotRespawn > 15000L)
                        {
                            this.lastZealotRespawn = now;
                        }
                    }
                }
            }
        }
        else
        {
            this.lastZealotRespawn = -1;
        }
    }

    private double getItemDelay(int base, long delay)
    {
        long now = System.currentTimeMillis();
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.ENGLISH);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator('.');
        DecimalFormat numberFormat = new DecimalFormat("##.#", symbols);
        double seconds = base / 1000.0D - (now - delay) / 1000.0D;

        if (seconds >= 0.01D)
        {
            return Double.parseDouble(numberFormat.format(seconds));
        }
        return 0.0D;
    }

    static class CrosshairOverlay
    {
        private final String color;
        private final double delay;

        CrosshairOverlay(String color, double delay)
        {
            this.color = color;
            this.delay = delay;
        }

        public String getColor()
        {
            return ColorUtils.stringToRGB(this.color).toColoredFont();
        }

        public String getDelay()
        {
            return String.valueOf(this.delay);
        }
    }
}