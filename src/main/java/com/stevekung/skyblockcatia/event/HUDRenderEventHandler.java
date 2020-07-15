package com.stevekung.skyblockcatia.event;

import java.util.*;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.stevekung.skyblockcatia.config.ConfigManagerIN;
import com.stevekung.skyblockcatia.config.EnumEquipment;
import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.gui.config.GuiRenderPreview;
import com.stevekung.skyblockcatia.gui.toasts.GuiToast;
import com.stevekung.skyblockcatia.handler.ClientBlockBreakEvent;
import com.stevekung.skyblockcatia.handler.GrapplingHookEvent;
import com.stevekung.skyblockcatia.renderer.HUDInfo;
import com.stevekung.skyblockcatia.utils.*;
import com.stevekung.skyblockcatia.utils.JsonUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.*;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUDRenderEventHandler
{
    public static final HUDRenderEventHandler INSTANCE = new HUDRenderEventHandler();
    private final GuiToast toastGui;
    private final Minecraft mc;
    private long lastBlockBreak = -1;
    private long lastGrapplingHookUse = -1;
    private long lastZealotRespawn = -1;
    private boolean foundDragon;
    private Set<CoordsPair> recentlyLoadedChunks = new HashSet<>();
    private static final ImmutableList<AxisAlignedBB> ZEALOT_SPAWN_AREA = ImmutableList.of(new AxisAlignedBB(-609, 9, -303, -631, 5, -320), new AxisAlignedBB(-622, 5, -321, -640, 5, -334), new AxisAlignedBB(-631, 7, -293, -648, 7, -312), new AxisAlignedBB(-658, 8, -308, -672, 7, -320), new AxisAlignedBB(-709, 9, -325, -694, 10, -315), new AxisAlignedBB(-702, 10, -303, -738, 5, -261), new AxisAlignedBB(-705, 5, -257, -678, 5, -296), new AxisAlignedBB(-657, 5, -210, -624, 8, -242), new AxisAlignedBB(-625, 7, -256, -662, 5, -286));
    private static final ImmutableList<BlockPos> END_PORTAL_FRAMES = ImmutableList.of(new BlockPos(-669, 9, -277), new BlockPos(-669, 9, -275), new BlockPos(-670, 9, -278), new BlockPos(-672, 9, -278), new BlockPos(-673, 9, -277), new BlockPos(-673, 9, -275), new BlockPos(-672, 9, -274), new BlockPos(-670, 9, -274));
    private static final Ordering<NetworkPlayerInfo> field_175252_a = Ordering.from(new PlayerComparator());

    public HUDRenderEventHandler()
    {
        this.mc = Minecraft.getMinecraft();
        this.toastGui = new GuiToast(this.mc);
    }

    @SubscribeEvent
    public void onGrapplingHookUse(GrapplingHookEvent event)
    {
        if (HypixelEventHandler.isSkyBlock)
        {
            long now = System.currentTimeMillis();
            boolean isHook = EnumChatFormatting.getTextWithoutFormattingCodes(event.getItemStack().getDisplayName()).equals("Grappling Hook");

            if (now - this.lastGrapplingHookUse > 2000L && isHook)
            {
                this.lastGrapplingHookUse = now;
            }
        }
    }

    @SubscribeEvent
    public void onClientBlockBreak(ClientBlockBreakEvent event)
    {
        if (!HypixelEventHandler.isSkyBlock || this.mc.thePlayer.getCurrentEquippedItem() == null ||
                this.mc.thePlayer.getCurrentEquippedItem() != null && this.mc.thePlayer.getCurrentEquippedItem().hasTagCompound() && !(this.mc.thePlayer.getCurrentEquippedItem().getTagCompound().getCompoundTag("ExtraAttributes").getString("id").equals("JUNGLE_AXE") || this.mc.thePlayer.getCurrentEquippedItem().getTagCompound().getCompoundTag("ExtraAttributes").getString("id").equals("TREECAPITATOR_AXE")))
        {
            return;
        }

        long now = System.currentTimeMillis();
        Block block = event.getWorld().getBlockState(event.getPos()).getBlock();

        if (now - this.lastBlockBreak > 2000L && block instanceof BlockLog)
        {
            this.lastBlockBreak = now;
        }
    }

    @SubscribeEvent
    public void onRenderChat(RenderGameOverlayEvent.Chat event)
    {
        if (this.mc.currentScreen != null && this.mc.currentScreen instanceof GuiChest)
        {
            GuiChest chest = (GuiChest)this.mc.currentScreen;

            if (MainEventHandler.showChat && MainEventHandler.CHATABLE_LIST.stream().anyMatch(invName -> chest.lowerChestInventory.getDisplayName().getUnformattedText().contains(invName)))
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPreInfoRender(RenderGameOverlayEvent.Pre event)
    {
        GuiIngameForge.renderObjective = !this.mc.gameSettings.showDebugInfo;
        double jungleAxeDelay = 0;
        double grapplingHookDelay = 0;
        double zealotRespawnDelay = 0;

        if (ExtendedConfig.instance.jungleAxeCooldown)
        {
            jungleAxeDelay = this.getItemDelay(2000, this.lastBlockBreak);
        }
        if (ExtendedConfig.instance.grapplingHookCooldown)
        {
            grapplingHookDelay = this.getItemDelay(2000, this.lastGrapplingHookUse);
        }
        if (ExtendedConfig.instance.zealotRespawnCooldown)
        {
            zealotRespawnDelay = this.getItemDelay(11000, this.lastZealotRespawn);
        }

        if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR || event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS)
        {
            if (this.mc.currentScreen instanceof GuiRenderPreview)
            {
                event.setCanceled(true);
            }
        }
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT)
        {
            if (this.mc.gameSettings.showDebugInfo || this.mc.thePlayer == null && this.mc.theWorld == null || this.mc.currentScreen instanceof GuiRenderPreview)
            {
                return;
            }

            List<CrosshairOverlay> crosshairInfo = new LinkedList<>();
            int center = 0;

            if (ExtendedConfig.instance.jungleAxeCooldown && jungleAxeDelay >= 0.01D)
            {
                crosshairInfo.add(new CrosshairOverlay(ExtendedConfig.instance.jungleAxeCooldownColor, jungleAxeDelay));
            }
            if (ExtendedConfig.instance.grapplingHookCooldown && grapplingHookDelay >= 0.01D)
            {
                crosshairInfo.add(new CrosshairOverlay(ExtendedConfig.instance.grapplingHookCooldownColor, grapplingHookDelay));
            }
            if (ExtendedConfig.instance.zealotRespawnCooldown && zealotRespawnDelay >= 0.01D && !this.foundDragon)
            {
                crosshairInfo.add(new CrosshairOverlay(ExtendedConfig.instance.zealotRespawnCooldownColor, zealotRespawnDelay));
            }

            for (CrosshairOverlay overlay : crosshairInfo)
            {
                float fontHeight = this.mc.fontRendererObj.FONT_HEIGHT + 1;
                float width = event.resolution.getScaledWidth() / 2 + 1.0625F;
                float height = event.resolution.getScaledHeight() / 2 + 6 + fontHeight * center;
                this.mc.fontRendererObj.drawString(overlay.getColor() + overlay.getDelay(), width - this.mc.fontRendererObj.getStringWidth(overlay.getDelay()) / 2, height, 16777215, true);
                center++;
            }

            if (ConfigManagerIN.enableRenderInfo)
            {
                List<String> leftInfo = new LinkedList<>();
                List<String> rightInfo = new LinkedList<>();

                // left info
                if (ExtendedConfig.instance.ping && !this.mc.isSingleplayer())
                {
                    leftInfo.add(HUDInfo.getPing());

                    if (ExtendedConfig.instance.pingToSecond)
                    {
                        leftInfo.add(HUDInfo.getPingToSecond());
                    }
                }
                if (ExtendedConfig.instance.serverIP && !this.mc.isSingleplayer())
                {
                    if (this.mc.getCurrentServerData() != null)
                    {
                        leftInfo.add(HUDInfo.getServerIP(this.mc));
                    }
                }
                if (ExtendedConfig.instance.fps)
                {
                    leftInfo.add(HUDInfo.getFPS());
                }
                if (ExtendedConfig.instance.xyz)
                {
                    leftInfo.add(HUDInfo.getXYZ(this.mc));

                    if (this.mc.thePlayer.dimension == -1)
                    {
                        leftInfo.add(HUDInfo.getOverworldXYZFromNether(this.mc));
                    }
                }
                if (ExtendedConfig.instance.direction)
                {
                    leftInfo.add(HUDInfo.renderDirection(this.mc));
                }
                if (ExtendedConfig.instance.biome)
                {
                    leftInfo.add(HUDInfo.getBiome(this.mc));
                }

                // right info
                if (ExtendedConfig.instance.realTime)
                {
                    rightInfo.add(HUDInfo.getCurrentTime());
                }
                if (ExtendedConfig.instance.gameTime)
                {
                    rightInfo.add(HUDInfo.getCurrentGameTime(this.mc));
                }
                if (ExtendedConfig.instance.gameWeather && this.mc.theWorld.isRaining())
                {
                    rightInfo.add(HUDInfo.getGameWeather(this.mc));
                }
                if (ExtendedConfig.instance.moonPhase)
                {
                    rightInfo.add(InfoUtils.INSTANCE.getMoonPhase(this.mc));
                }

                if (ExtendedConfig.instance.placedSummoningEyeTracker && HypixelEventHandler.SKY_BLOCK_LOCATION.isTheEnd())
                {
                    int summoningEyeCount = 0;

                    for (BlockPos pos : END_PORTAL_FRAMES)
                    {
                        if (this.mc.theWorld.getBlockState(pos).getBlock() == Blocks.end_portal_frame && this.mc.theWorld.getBlockState(pos).getValue(BlockEndPortalFrame.EYE))
                        {
                            ++summoningEyeCount;
                        }
                    }
                    String color = ColorUtils.stringToRGB(ExtendedConfig.instance.placedSummoningEyeValueColor).toColoredFont();
                    rightInfo.add(ColorUtils.stringToRGB(ExtendedConfig.instance.placedSummoningEyeColor).toColoredFont() + "Placed Eye: " + color + summoningEyeCount + "/8");
                }

                if (ExtendedConfig.instance.golemStageTracker && HypixelEventHandler.SKY_BLOCK_LOCATION.isTheEnd())
                {
                    int golemStage = 0;

                    for (int headPos = 0; headPos < 5; headPos++)
                    {
                        if (this.mc.theWorld.getBlockState(new BlockPos(-689, 5 + headPos, -273)).getBlock() == Blocks.skull)
                        {
                            golemStage = headPos + 1;
                            break;
                        }
                        if (this.mc.theWorld.getBlockState(new BlockPos(-644, 5 + headPos, -269)).getBlock() == Blocks.skull)
                        {
                            golemStage = headPos + 1;
                            break;
                        }
                        if (this.mc.theWorld.getBlockState(new BlockPos(-678, 5 + headPos, -332)).getBlock() == Blocks.skull)
                        {
                            golemStage = headPos + 1;
                            break;
                        }
                        if (this.mc.theWorld.getBlockState(new BlockPos(-639, 5 + headPos, -328)).getBlock() == Blocks.skull)
                        {
                            golemStage = headPos + 1;
                            break;
                        }
                        if (this.mc.theWorld.getBlockState(new BlockPos(-649, 5 + headPos, -219)).getBlock() == Blocks.skull)
                        {
                            golemStage = headPos + 1;
                            break;
                        }
                        if (this.mc.theWorld.getBlockState(new BlockPos(-727, 5 + headPos, -284)).getBlock() == Blocks.skull)
                        {
                            golemStage = headPos + 1;
                            break;
                        }
                    }

                    String color = ColorUtils.stringToRGB(ExtendedConfig.instance.golemStageValueColor).toColoredFont();
                    rightInfo.add(ColorUtils.stringToRGB(ExtendedConfig.instance.golemStageColor).toColoredFont() + "Golem Stage: " + color + (golemStage == 5 ? "Golem Spawning soon!" : golemStage + "/5"));
                }

                if (ExtendedConfig.instance.lobbyPlayerCount && !this.mc.isSingleplayer())
                {
                    List<NetworkPlayerInfo> list = field_175252_a.sortedCopy(this.mc.thePlayer.sendQueue.getPlayerInfoMap());
                    list = list.subList(0, Math.min(list.size(), 80));
                    rightInfo.add(JsonUtils.create("Lobby Players Count: ").setChatStyle(JsonUtils.gold()).appendSibling(JsonUtils.create(String.valueOf(list.size())).setChatStyle(JsonUtils.green())).getFormattedText());
                }

                // equipments
                if (!this.mc.thePlayer.isSpectator() && ExtendedConfig.instance.equipmentHUD)
                {
                    if (EnumEquipment.Position.getById(ExtendedConfig.instance.equipmentPosition).equalsIgnoreCase("hotbar"))
                    {
                        HUDInfo.renderHotbarEquippedItems(this.mc);
                    }
                    else
                    {
                        if (EnumEquipment.Direction.getById(ExtendedConfig.instance.equipmentDirection).equalsIgnoreCase("vertical"))
                        {
                            HUDInfo.renderVerticalEquippedItems(this.mc);
                        }
                        else
                        {
                            HUDInfo.renderHorizontalEquippedItems(this.mc);
                        }
                    }
                }

                if (ExtendedConfig.instance.potionHUD)
                {
                    HUDInfo.renderPotionHUD(this.mc);
                }

                // left info
                for (int i = 0; i < leftInfo.size(); ++i)
                {
                    ScaledResolution res = new ScaledResolution(this.mc);
                    String string = leftInfo.get(i);
                    float fontHeight = this.mc.fontRendererObj.FONT_HEIGHT + 1;
                    float yOffset = 3 + fontHeight * i;
                    float xOffset = res.getScaledWidth() - 2 - this.mc.fontRendererObj.getStringWidth(string);

                    if (!StringUtils.isNullOrEmpty(string))
                    {
                        this.mc.fontRendererObj.drawString(string, ExtendedConfig.instance.swapRenderInfo ? xOffset : 3.0625F, yOffset, 16777215, true);
                    }
                }

                // right info
                for (int i = 0; i < rightInfo.size(); ++i)
                {
                    ScaledResolution res = new ScaledResolution(this.mc);
                    String string = rightInfo.get(i);
                    float fontHeight = this.mc.fontRendererObj.FONT_HEIGHT + 1;
                    float yOffset = 3 + fontHeight * i;
                    float xOffset = res.getScaledWidth() - 2 - this.mc.fontRendererObj.getStringWidth(string);

                    if (!StringUtils.isNullOrEmpty(string))
                    {
                        this.mc.fontRendererObj.drawString(string, ExtendedConfig.instance.swapRenderInfo ? 3.0625F : xOffset, yOffset, 16777215, true);
                    }
                }
            }
        }
        if (event.type == RenderGameOverlayEvent.ElementType.CHAT)
        {
            if (this.mc.currentScreen instanceof GuiRenderPreview)
            {
                event.setCanceled(true);
                return;
            }
        }
        if (event.type == RenderGameOverlayEvent.ElementType.BOSSHEALTH)
        {
            event.setCanceled(true);

            if (HypixelEventHandler.isSkyBlock && HypixelEventHandler.SKY_BLOCK_LOCATION.isTheEnd())
            {
                this.mc.getTextureManager().bindTexture(Gui.icons);
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.enableBlend();

                if (SkyBlockBossStatus.bossName != null && SkyBlockBossStatus.renderBossBar)
                {
                    ScaledResolution res = new ScaledResolution(this.mc);
                    int i = res.getScaledWidth();
                    int j = 182;
                    int k = i / 2 - j / 2;
                    int l = (int)(SkyBlockBossStatus.healthScale * (j + 1));
                    int i1 = 12;

                    if (ConfigManagerIN.enableRenderBossHealthBar)
                    {
                        this.mc.ingameGUI.drawTexturedModalRect(k, i1, 0, 74, j, 5);

                        if (l > 0)
                        {
                            this.mc.ingameGUI.drawTexturedModalRect(k, i1, 0, 79, l, 5);
                        }
                    }
                    String name = SkyBlockBossStatus.bossName;
                    this.mc.ingameGUI.getFontRenderer().drawStringWithShadow(name, i / 2 - this.mc.ingameGUI.getFontRenderer().getStringWidth(name) / 2, i1 - 10, 16777215);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.mc.getTextureManager().bindTexture(Gui.icons);
                }
                GlStateManager.disableBlend();
            }
            else
            {
                if (ConfigManagerIN.enableRenderBossHealthStatus)
                {
                    this.mc.getTextureManager().bindTexture(Gui.icons);
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    GlStateManager.enableBlend();

                    if (BossStatus.bossName != null && BossStatus.statusBarTime > 0)
                    {
                        --BossStatus.statusBarTime;
                        ScaledResolution res = new ScaledResolution(this.mc);
                        int i = res.getScaledWidth();
                        int j = 182;
                        int k = i / 2 - j / 2;
                        int l = (int)(BossStatus.healthScale * (j + 1));
                        int i1 = 12;

                        if (ConfigManagerIN.enableRenderBossHealthBar)
                        {
                            this.mc.ingameGUI.drawTexturedModalRect(k, i1, 0, 74, j, 5);

                            if (l > 0)
                            {
                                this.mc.ingameGUI.drawTexturedModalRect(k, i1, 0, 79, l, 5);
                            }
                        }
                        String name = BossStatus.bossName;
                        this.mc.ingameGUI.getFontRenderer().drawStringWithShadow(name, i / 2 - this.mc.ingameGUI.getFontRenderer().getStringWidth(name) / 2, i1 - 10, 16777215);
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        this.mc.getTextureManager().bindTexture(Gui.icons);
                    }
                    GlStateManager.disableBlend();
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (HypixelEventHandler.isSkyBlock)
        {
            if (event.entity == this.mc.thePlayer)
            {
                this.recentlyLoadedChunks.clear();
            }
            if (event.entity instanceof EntityDragon)
            {
                this.foundDragon = true;

                if (ExtendedConfig.instance.showHitboxWhenDragonSpawned)
                {
                    this.mc.getRenderManager().setDebugBoundingBox(true);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if (HypixelEventHandler.isSkyBlock)
        {
            if (event.entity instanceof EntityDragon)
            {
                this.foundDragon = false;

                if (ExtendedConfig.instance.showHitboxWhenDragonSpawned)
                {
                    this.mc.getRenderManager().setDebugBoundingBox(false);
                }
            }
        }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event)
    {
        if (HypixelEventHandler.isSkyBlock && HypixelEventHandler.SKY_BLOCK_LOCATION == SkyBlockLocation.DRAGON_NEST)
        {
            CoordsPair coords = new CoordsPair(event.getChunk().xPosition, event.getChunk().zPosition);
            this.recentlyLoadedChunks.add(coords);
            InfoUtils.INSTANCE.schedule(() -> this.recentlyLoadedChunks.remove(coords), 20);
        }
    }

    @SubscribeEvent
    public void onEntityEnteringChunk(EntityEvent.EnteringChunk event)
    {
        Entity entity = event.entity;

        if (HypixelEventHandler.isSkyBlock && HypixelEventHandler.SKY_BLOCK_LOCATION == SkyBlockLocation.DRAGON_NEST)
        {
            if (ZEALOT_SPAWN_AREA.stream().anyMatch(aabb -> aabb.isVecInside(new Vec3(entity.posX, entity.posY, entity.posZ))))
            {
                if (entity instanceof EntityEnderman)
                {
                    if (!this.recentlyLoadedChunks.contains(new CoordsPair(event.newChunkX, event.newChunkZ)) && entity.ticksExisted == 0)
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

    public GuiToast getToastGui()
    {
        return this.toastGui;
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
            return ComparisonChain.start().compareTrueFirst(p_compare_1_.getGameType() != WorldSettings.GameType.SPECTATOR, p_compare_2_.getGameType() != WorldSettings.GameType.SPECTATOR).compare(scoreplayerteam != null ? scoreplayerteam.getRegisteredName() : "", scoreplayerteam1 != null ? scoreplayerteam1.getRegisteredName() : "").compare(p_compare_1_.getGameProfile().getName(), p_compare_2_.getGameProfile().getName()).result();
        }
    }
}