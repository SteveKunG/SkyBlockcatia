package com.stevekung.skyblockcatia.event.handler;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.ClientBlockBreakEvent;
import com.stevekung.skyblockcatia.event.GrapplingHookEvent;
import com.stevekung.skyblockcatia.integration.sba.SBAMana;
import com.stevekung.skyblockcatia.utils.CompatibilityUtils;
import com.stevekung.skyblockcatia.utils.CoordsPair;
import com.stevekung.skyblockcatia.utils.TimeUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBLocation;
import com.stevekung.skyblockcatia.utils.skyblock.api.PetStats;
import com.stevekung.stevekungslib.utils.ModDecimalFormat;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import me.shedaniel.architectury.event.events.EntityEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("deprecation")
public class HUDRenderEventHandler
{
    private final Minecraft mc;
    private long lastBlockBreak = -1;
    private long lastGrapplingHookUse = -1;
    private long lastZealotRespawn = -1;
    public static boolean foundDragon;
    private final Set<CoordsPair> recentlyLoadedChunks = Sets.newHashSet();
    private static final ImmutableList<AABB> ZEALOT_SPAWN_AREA = ImmutableList.of(new AABB(-609, 9, -303, -631, 5, -320), new AABB(-622, 5, -321, -640, 5, -334), new AABB(-631, 7, -293, -648, 7, -312), new AABB(-658, 8, -308, -672, 7, -320), new AABB(-709, 9, -325, -694, 10, -315), new AABB(-702, 10, -303, -738, 5, -261), new AABB(-705, 5, -257, -678, 5, -296), new AABB(-657, 5, -210, -624, 8, -242), new AABB(-625, 7, -256, -662, 5, -286));
    public static final HUDRenderEventHandler INSTANCE = new HUDRenderEventHandler();

    public HUDRenderEventHandler()
    {
        this.mc = Minecraft.getInstance();
        GrapplingHookEvent.GRAPPLING_HOOK.register(this::onGrapplingHookUse);
        ClientBlockBreakEvent.CLIENT_BLOCK_BREAK.register(this::onClientBlockBreak);
        EntityEvent.LIVING_DEATH.register(this::onLivingDeath);
        EntityEvent.ADD.register(this::onEntityJoinWorld);
        EntityEvent.ENTER_CHUNK.register(this::onEntityEnteringChunk);
    }

    public void onGrapplingHookUse(ItemStack itemStack)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            long now = System.currentTimeMillis();
            boolean isHook = itemStack.getHoverName().getString().equals("Grappling Hook");

            if (now - this.lastGrapplingHookUse > 2000L && isHook)
            {
                this.lastGrapplingHookUse = now;
            }
        }
    }

    public void onClientBlockBreak(Level level, BlockPos pos, BlockState prevState)
    {
        long now = System.currentTimeMillis();

        if (SkyBlockEventHandler.isSkyBlock && !this.mc.player.getMainHandItem().isEmpty() && this.mc.player.getMainHandItem().hasTag() && (this.mc.player.getMainHandItem().getTag().getCompound("ExtraAttributes").getString("id").equals("JUNGLE_AXE") || this.mc.player.getMainHandItem().getTag().getCompound("ExtraAttributes").getString("id").equals("TREECAPITATOR_AXE")))
        {
            Block block = prevState != null ? prevState.getBlock() : level.getBlockState(pos).getBlock();

            if (now - this.lastBlockBreak > PetStats.INSTANCE.getAxeCooldown(2000) && block.is(BlockTags.LOGS))
            {
                this.lastBlockBreak = now;
            }
        }
    }

    public void onPreInfoRender(PoseStack poseStack, Window window)
    {
        double jungleAxeDelay = 0;
        double grapplingHookDelay = 0;
        double zealotRespawnDelay = 0;

        if (SkyBlockcatiaSettings.INSTANCE.axeCooldown)
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

        if (this.mc.options.renderDebug || this.mc.player == null && this.mc.level == null)
        {
            return;
        }

        if (CompatibilityUtils.isSkyblockAddonsLoaded && SkyBlockcatiaSettings.INSTANCE.displayItemAbilityMaxUsed && !this.mc.player.getMainHandItem().isEmpty())
        {
            ItemStack itemStack = this.mc.player.getMainHandItem();

            if (itemStack.hasTag())
            {
                CompoundTag compound = itemStack.getTag().getCompound("display");

                if (compound.getTagType("Lore") == 9)
                {
                    ListTag list = compound.getList("Lore", 8);

                    for (int j1 = 0; j1 < list.size(); ++j1)
                    {
                        String lore = TextComponentUtils.fromJsonUnformatted(list.getString(j1));

                        if (lore.startsWith("Mana Cost: "))
                        {
                            int count = SBAMana.INSTANCE.getMana() / Integer.parseInt(lore.replace("Mana Cost: ", ""));

                            if (count > 0)
                            {
                                String usedCount = ChatFormatting.AQUA + String.valueOf(count);
                                float fontHeight = this.mc.font.lineHeight + 1;
                                float width = window.getGuiScaledWidth() / 2F + 1.0625F;
                                float height = window.getGuiScaledHeight() / 2F - 24 + fontHeight;
                                this.mc.font.drawShadow(poseStack, usedCount, width - this.mc.font.width(usedCount) / 2F, height, 16777215);
                            }
                            break;
                        }
                    }
                }
            }
        }

        List<CrosshairOverlay> crosshairInfo = Lists.newArrayList();
        int center = 0;

        if (SkyBlockcatiaSettings.INSTANCE.axeCooldown && jungleAxeDelay >= 0.01D)
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
            float fontHeight = this.mc.font.lineHeight + 1;
            float width = window.getGuiScaledWidth() / 2F + 1.0625F;
            float height = window.getGuiScaledHeight() / 2F + 6 + fontHeight * center;
            this.mc.font.drawShadow(poseStack, TextComponentUtils.component(overlay.getDelay()), width - this.mc.font.width(overlay.getDelay()) / 2F, height, 16777215);
            center++;
        }
    }

    public InteractionResult onEntityJoinWorld(Entity entity, Level level)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (entity == this.mc.player)
            {
                this.recentlyLoadedChunks.clear();
                return InteractionResult.SUCCESS;
            }
            if (entity instanceof EnderDragon)
            {
                HUDRenderEventHandler.foundDragon = true;

                if (SkyBlockcatiaSettings.INSTANCE.showHitboxWhenDragonSpawned)
                {
                    this.mc.getEntityRenderDispatcher().setRenderHitBoxes(true);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public InteractionResult onLivingDeath(LivingEntity entity, DamageSource source)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (entity instanceof EnderDragon)
            {
                HUDRenderEventHandler.foundDragon = false;

                if (SkyBlockcatiaSettings.INSTANCE.showHitboxWhenDragonSpawned)
                {
                    this.mc.getEntityRenderDispatcher().setRenderHitBoxes(false);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public void onChunkLoad(int chunkX, int chunkZ)
    {
        if (SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION == SBLocation.DRAGON_NEST)
        {
            CoordsPair coords = new CoordsPair(chunkX, chunkZ);
            this.recentlyLoadedChunks.add(coords);
            TimeUtils.schedule(() -> this.recentlyLoadedChunks.remove(coords), 20);
        }
    }

    public void onEntityEnteringChunk(Entity entity, int chunkX, int chunkZ, int prevX, int prevZ)
    {
        if (SkyBlockEventHandler.isSkyBlock && SkyBlockEventHandler.SKY_BLOCK_LOCATION == SBLocation.DRAGON_NEST)
        {
            if (ZEALOT_SPAWN_AREA.stream().anyMatch(aabb -> aabb.contains(new Vec3(entity.getX(), entity.getY(), entity.getZ()))))
            {
                if (entity instanceof EnderMan)
                {
                    if (!this.recentlyLoadedChunks.contains(new CoordsPair(chunkX, chunkZ)) && entity.tickCount == 0)
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
        final double delay;

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