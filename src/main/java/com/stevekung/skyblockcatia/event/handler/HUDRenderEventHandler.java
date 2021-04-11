package com.stevekung.skyblockcatia.event.handler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.ClientBlockBreakEvent;
import com.stevekung.skyblockcatia.event.GrapplingHookEvent;
import com.stevekung.skyblockcatia.gui.toasts.GuiToast;
import com.stevekung.skyblockcatia.hud.InfoUtils;
import com.stevekung.skyblockcatia.integration.sba.SBAMana;
import com.stevekung.skyblockcatia.utils.CompatibilityUtils;
import com.stevekung.skyblockcatia.utils.CoordsPair;
import com.stevekung.skyblockcatia.utils.GuiScreenUtils;
import com.stevekung.skyblockcatia.utils.ModDecimalFormat;
import com.stevekung.skyblockcatia.utils.skyblock.SBBossBar;
import com.stevekung.skyblockcatia.utils.skyblock.SBLocation;
import com.stevekung.skyblockcatia.utils.skyblock.api.PetStats;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
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
    public static boolean foundDragon;
    private Set<CoordsPair> recentlyLoadedChunks = new HashSet<>();
    private static final ImmutableList<AxisAlignedBB> ZEALOT_SPAWN_AREA = ImmutableList.of(new AxisAlignedBB(-609, 9, -303, -631, 5, -320), new AxisAlignedBB(-622, 5, -321, -640, 5, -334), new AxisAlignedBB(-631, 7, -293, -648, 7, -312), new AxisAlignedBB(-658, 8, -308, -672, 7, -320), new AxisAlignedBB(-709, 9, -325, -694, 10, -315), new AxisAlignedBB(-702, 10, -303, -738, 5, -261), new AxisAlignedBB(-705, 5, -257, -678, 5, -296), new AxisAlignedBB(-657, 5, -210, -624, 8, -242), new AxisAlignedBB(-625, 7, -256, -662, 5, -286));
    public static boolean otherPlayerIsland;

    public HUDRenderEventHandler()
    {
        this.mc = Minecraft.getMinecraft();
        this.toastGui = new GuiToast(this.mc);
    }

    @SubscribeEvent
    public void onGrapplingHookUse(GrapplingHookEvent event)
    {
        if (SkyBlockEventHandler.isSkyBlock)
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
        if (!SkyBlockEventHandler.isSkyBlock || this.mc.thePlayer.getCurrentEquippedItem() == null || this.mc.thePlayer.getCurrentEquippedItem() != null && this.mc.thePlayer.getCurrentEquippedItem().hasTagCompound() && !(this.mc.thePlayer.getCurrentEquippedItem().getTagCompound().getCompoundTag("ExtraAttributes").getString("id").equals("JUNGLE_AXE") || this.mc.thePlayer.getCurrentEquippedItem().getTagCompound().getCompoundTag("ExtraAttributes").getString("id").equals("TREECAPITATOR_AXE")))
        {
            return;
        }

        long now = System.currentTimeMillis();
        Block block = event.getBlockState() != null ? event.getBlockState().getBlock() : event.getWorld().getBlockState(event.getPos()).getBlock();

        if (now - this.lastBlockBreak > PetStats.INSTANCE.getAxeCooldown(2000) && block instanceof BlockLog)
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

            if (MainEventHandler.showChat && GuiScreenUtils.isChatable(chest.lowerChestInventory))
            {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPreInfoRender(RenderGameOverlayEvent.Pre event)
    {
        double jungleAxeDelay = 0;
        double grapplingHookDelay = 0;
        double zealotRespawnDelay = 0;

        if (SkyBlockcatiaSettings.INSTANCE.jungleAxeCooldown)
        {
            jungleAxeDelay = this.getItemDelay(PetStats.INSTANCE.getAxeCooldown(2000), this.lastBlockBreak);
        }
        if (SkyBlockcatiaSettings.INSTANCE.grapplingHookCooldown)
        {
            grapplingHookDelay = this.getItemDelay(2000, this.lastGrapplingHookUse);
        }
        if (SkyBlockcatiaSettings.INSTANCE.zealotRespawnCooldown)
        {
            zealotRespawnDelay = this.getItemDelay(11000, this.lastZealotRespawn);
        }

        if (event.type == RenderGameOverlayEvent.ElementType.TEXT)
        {
            if (this.mc.gameSettings.showDebugInfo || this.mc.thePlayer == null && this.mc.theWorld == null)
            {
                return;
            }

            if (CompatibilityUtils.isSkyblockAddonsLoaded && SkyBlockcatiaSettings.INSTANCE.displayItemAbilityMaxUsed && this.mc.thePlayer.getCurrentEquippedItem() != null)
            {
                ItemStack itemStack = this.mc.thePlayer.getCurrentEquippedItem();

                if (itemStack.hasTagCompound())
                {
                    NBTTagCompound compound = itemStack.getTagCompound().getCompoundTag("display");

                    if (compound.getTagId("Lore") == 9)
                    {
                        NBTTagList list = compound.getTagList("Lore", 8);

                        if (list.tagCount() > 0)
                        {
                            for (int j1 = 0; j1 < list.tagCount(); ++j1)
                            {
                                String lore = EnumChatFormatting.getTextWithoutFormattingCodes(list.getStringTagAt(j1));

                                if (lore.startsWith("Mana Cost: "))
                                {
                                    int count = SBAMana.INSTANCE.getMana() / Integer.parseInt(lore.replace("Mana Cost: ", ""));

                                    if (count > 0)
                                    {
                                        String usedCount = EnumChatFormatting.AQUA + String.valueOf(count);
                                        float fontHeight = this.mc.fontRendererObj.FONT_HEIGHT + 1;
                                        float width = event.resolution.getScaledWidth() / 2 + 1.0625F;
                                        float height = event.resolution.getScaledHeight() / 2 - 24 + fontHeight;
                                        this.mc.fontRendererObj.drawString(usedCount, width - this.mc.fontRendererObj.getStringWidth(usedCount) / 2, height, 16777215, true);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            List<CrosshairOverlay> crosshairInfo = new LinkedList<>();
            int center = 0;

            if (SkyBlockcatiaSettings.INSTANCE.jungleAxeCooldown && jungleAxeDelay >= 0.01D)
            {
                crosshairInfo.add(new CrosshairOverlay(jungleAxeDelay));
            }
            if (SkyBlockcatiaSettings.INSTANCE.grapplingHookCooldown && grapplingHookDelay >= 0.01D)
            {
                crosshairInfo.add(new CrosshairOverlay(grapplingHookDelay));
            }
            if (SkyBlockcatiaSettings.INSTANCE.zealotRespawnCooldown && zealotRespawnDelay >= 0.01D && !HUDRenderEventHandler.foundDragon)
            {
                crosshairInfo.add(new CrosshairOverlay(zealotRespawnDelay));
            }

            for (CrosshairOverlay overlay : crosshairInfo)
            {
                float fontHeight = this.mc.fontRendererObj.FONT_HEIGHT + 1;
                float width = event.resolution.getScaledWidth() / 2 + 1.0625F;
                float height = event.resolution.getScaledHeight() / 2 + 6 + fontHeight * center;
                this.mc.fontRendererObj.drawString(overlay.getDelay(), width - this.mc.fontRendererObj.getStringWidth(overlay.getDelay()) / 2, height, 16777215, true);
                center++;
            }
        }
        if (event.type == RenderGameOverlayEvent.ElementType.BOSSHEALTH)
        {
            if (SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION.isTheEnd())
            {
                event.setCanceled(true);
                this.mc.getTextureManager().bindTexture(Gui.icons);
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.enableBlend();

                if (SBBossBar.bossName != null && SBBossBar.renderBossBar)
                {
                    ScaledResolution res = new ScaledResolution(this.mc);
                    int i = res.getScaledWidth();
                    int j = 182;
                    int k = i / 2 - j / 2;
                    int l = (int)(SBBossBar.healthScale * (j + 1));
                    int i1 = 12;

                    this.mc.ingameGUI.drawTexturedModalRect(k, i1, 0, 74, j, 5);

                    if (l > 0)
                    {
                        this.mc.ingameGUI.drawTexturedModalRect(k, i1, 0, 79, l, 5);
                    }

                    String name = SBBossBar.bossName;
                    this.mc.ingameGUI.getFontRenderer().drawStringWithShadow(name, i / 2 - this.mc.ingameGUI.getFontRenderer().getStringWidth(name) / 2, i1 - 10, 16777215);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.mc.getTextureManager().bindTexture(Gui.icons);
                }
                GlStateManager.disableBlend();
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (event.entity == this.mc.thePlayer)
            {
                this.recentlyLoadedChunks.clear();
            }
            if (event.entity instanceof EntityDragon)
            {
                HUDRenderEventHandler.foundDragon = true;

                if (SkyBlockcatiaSettings.INSTANCE.showHitboxWhenDragonSpawned)
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
            if (event.entity instanceof EntityDragon)
            {
                HUDRenderEventHandler.foundDragon = false;

                if (SkyBlockcatiaSettings.INSTANCE.showHitboxWhenDragonSpawned)
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
            CoordsPair coords = new CoordsPair(event.getChunk().xPosition, event.getChunk().zPosition);
            this.recentlyLoadedChunks.add(coords);
            InfoUtils.INSTANCE.schedule(() -> this.recentlyLoadedChunks.remove(coords), 20);
        }
    }

    @SubscribeEvent
    public void onEntityEnteringChunk(EntityEvent.EnteringChunk event)
    {
        Entity entity = event.entity;

        if (SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION == SBLocation.DRAGON_NEST)
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
        private final double delay;

        CrosshairOverlay(double delay)
        {
            this.delay = delay;
        }

        public String getDelay()
        {
            return String.valueOf(this.delay);
        }
    }
}