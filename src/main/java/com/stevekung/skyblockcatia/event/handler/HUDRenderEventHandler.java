package com.stevekung.skyblockcatia.event.handler;

import java.util.*;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.event.ClientBlockBreakEvent;
import com.stevekung.skyblockcatia.event.GrapplingHookEvent;
import com.stevekung.skyblockcatia.utils.CoordsPair;
import com.stevekung.skyblockcatia.utils.TimeUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBLocation;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.ModDecimalFormat;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
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
    private static final Ordering<NetworkPlayerInfo> ENTRY_ORDERING = Ordering.from(new PlayerComparator());

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
            zealotRespawnDelay = this.getItemDelay(11000, this.lastZealotRespawn);
        }

        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT)
        {
            if (this.mc.gameSettings.showDebugInfo || this.mc.player == null && this.mc.world == null)
            {
                return;
            }

            /*if (SkyBlockcatiaMod.isSkyblockAddonsLoaded && SBExtendedConfig.INSTANCE.displayItemAbilityMaxUsed && !this.mc.player.getHeldItemMainhand().isEmpty())TODO
            {
                ItemStack itemStack = this.mc.player.getHeldItemMainhand();

                if (itemStack.hasTag())
                {
                    CompoundNBT compound = itemStack.getTag().getCompound("display");

                    if (compound.getTagId("Lore") == Constants.NBT.TAG_LIST)
                    {
                        ListNBT list = compound.getList("Lore", Constants.NBT.TAG_STRING);

                        for (int j1 = 0; j1 < list.size(); ++j1)
                        {
                            String lore = TextFormatting.getTextWithoutFormattingCodes(list.getString(j1));

                            if (lore.startsWith("Mana Cost: "))
                            {
                                int count = SBAMana.INSTANCE.getMana() / Integer.parseInt(lore.replace("Mana Cost: ", ""));

                                if (count > 0)
                                {
                                    String usedCount = TextFormatting.AQUA + String.valueOf(count);
                                    float fontHeight = this.mc.fontRenderer.FONT_HEIGHT + 1;
                                    float width = event.getWindow().getScaledWidth() / 2 + 1.0625F;
                                    float height = event.getWindow().getScaledHeight() / 2 - 24 + fontHeight;
                                    this.mc.fontRenderer.drawStringWithShadow(usedCount, width - this.mc.fontRenderer.getStringWidth(usedCount) / 2, height, 16777215);
                                }
                                break;
                            }
                        }
                    }
                }
            }*/

            List<CrosshairOverlay> crosshairInfo = new ArrayList<>();
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
                            if (SBExtendedConfig.INSTANCE.lobbyPlayerCount && !this.mc.isSingleplayer())
            {
                List<NetworkPlayerInfo> list = ENTRY_ORDERING.sortedCopy(this.mc.player.connection.getPlayerInfoMap());
                list = list.subList(0, Math.min(list.size(), 80));
                rightInfo.add(JsonUtils.create("Lobby Players Count: ").setChatStyle(JsonUtils.gold()).appendSibling(JsonUtils.create(String.valueOf(list.size())).setChatStyle(JsonUtils.green())).getFormattedText());
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

                        if (now - this.lastZealotRespawn > 11000L)
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
        ModDecimalFormat numberFormat = new ModDecimalFormat("##.#");
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

    static class PlayerComparator implements Comparator<NetworkPlayerInfo>
    {
        @Override
        public int compare(NetworkPlayerInfo p_compare_1_, NetworkPlayerInfo p_compare_2_)
        {
            ScorePlayerTeam scoreplayerteam = p_compare_1_.getPlayerTeam();
            ScorePlayerTeam scoreplayerteam1 = p_compare_2_.getPlayerTeam();
            return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != GameType.SPECTATOR, p_compare_2_.getGameType() != GameType.SPECTATOR).compare(scoreplayerteam != null ? scoreplayerteam.getName() : "", scoreplayerteam1 != null ? scoreplayerteam1.getName() : "").compare(p_compare_1_.getGameProfile().getName(), p_compare_2_.getGameProfile().getName(), String::compareToIgnoreCase).result();
        }
    }
}