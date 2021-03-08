package com.stevekung.skyblockcatia.hud;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.stevekung.skyblockcatia.config.Equipments;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.config.StatusEffects;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.utils.ColorUtils;
import com.stevekung.skyblockcatia.utils.CompatibilityUtils;
import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeVersion;

public class InfoOverlays
{
    private static final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");

    public static String getFPS()
    {
        int fps = Minecraft.getDebugFPS();
        String color = ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.fpsValueColor).toColoredFont();

        if (fps >= 26 && fps <= 49)
        {
            color = ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.fps26And49Color).toColoredFont();
        }
        else if (fps <= 25)
        {
            color = ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.fpsLow25Color).toColoredFont();
        }
        return ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.fpsColor).toColoredFont() + "FPS: " + color + fps;
    }

    public static String getXYZ(Minecraft mc)
    {
        BlockPos pos = new BlockPos(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().getEntityBoundingBox().minY, mc.getRenderViewEntity().posZ);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        String nether = mc.thePlayer.dimension == -1 ? "Nether " : "";
        return ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.xyzColor).toColoredFont() + nether + "XYZ: " + ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.xyzValueColor).toColoredFont() + x + " " + y + " " + z;
    }

    public static String getOverworldXYZFromNether(Minecraft mc)
    {
        BlockPos pos = new BlockPos(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().getEntityBoundingBox().minY, mc.getRenderViewEntity().posZ);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        return ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.xyzColor).toColoredFont() + "Overworld XYZ: " + ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.xyzValueColor).toColoredFont() + x * 8 + " " + y + " " + z * 8;
    }

    public static String getBiome(Minecraft mc)
    {
        BlockPos pos = new BlockPos(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().getEntityBoundingBox().minY, mc.getRenderViewEntity().posZ);
        Chunk chunk = mc.theWorld.getChunkFromBlockCoords(pos);

        if (mc.theWorld.isBlockLoaded(pos) && pos.getY() >= 0 && pos.getY() < 256)
        {
            if (!chunk.isEmpty())
            {
                String biomeName = chunk.getBiome(pos, mc.theWorld.getWorldChunkManager()).biomeName.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
                return ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.biomeColor).toColoredFont() + "Biome: " + ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.biomeValueColor).toColoredFont() + biomeName;
            }
            else
            {
                return "Waiting for chunk...";
            }
        }
        else
        {
            return "Outside of world...";
        }
    }

    public static String getPing()
    {
        int responseTime = InfoUtils.INSTANCE.getPing();
        return ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.pingColor).toColoredFont() + "Ping: " + InfoOverlays.getResponseTimeColor(responseTime) + responseTime + "ms";
    }

    public static String getPingToSecond()
    {
        double responseTime = InfoUtils.INSTANCE.getPing() / 1000D;
        return ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.pingToSecondColor).toColoredFont() + "Delay: " + InfoOverlays.getResponseTimeColor((int) (responseTime * 1000D)) + responseTime + "s";
    }

    public static String getServerIP(Minecraft mc)
    {
        String ip = ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.serverIPColor).toColoredFont() + "IP: " + "" + ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.serverIPValueColor).toColoredFont() + mc.getCurrentServerData().serverIP;

        if (SkyBlockcatiaSettings.INSTANCE.serverIPMCVersion)
        {
            ip = ip + "/" + ForgeVersion.mcVersion;
        }
        return ip;
    }

    public static String renderDirection(Minecraft mc)
    {
        Entity entity = mc.getRenderViewEntity();
        int yaw = (int)entity.rotationYaw + 22;
        String direction;

        yaw %= 360;

        if (yaw < 0)
        {
            yaw += 360;
        }

        int facing = yaw / 45;

        if (facing < 0)
        {
            facing = 7;
        }

        EnumFacing coordFacing = entity.getHorizontalFacing();
        String coord = "";

        switch (coordFacing)
        {
        default:
        case NORTH:
            coord = "-Z";
            break;
        case SOUTH:
            coord = "+Z";
            break;
        case WEST:
            coord = "-X";
            break;
        case EAST:
            coord = "+X";
            break;
        }

        switch (facing)
        {
        case 0:
            direction = "South";
            break;
        case 1:
            direction = "South West";
            break;
        case 2:
            direction = "West";
            break;
        case 3:
            direction = "North West";
            break;
        case 4:
            direction = "North";
            break;
        case 5:
            direction = "North East";
            break;
        case 6:
            direction = "East";
            break;
        case 7:
            direction = "South East";
            break;
        default:
            direction = "Unknown";
            break;
        }
        direction += " (" + coord + ")";
        return ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.directionColor).toColoredFont() + "Direction: " + ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.directionValueColor).toColoredFont() + direction;
    }

    public static String getCurrentTime()
    {
        Date date = new Date();
        boolean isThai = Calendar.getInstance().getTimeZone().getID().equals("Asia/Bangkok");
        String dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, isThai ? new Locale("th", "TH") : Locale.ROOT).format(date);
        String timeFormat = SkyBlockcatiaSettings.INSTANCE.twentyFourTime ? new SimpleDateFormat("HH:mm:ss").format(date) : DateFormat.getTimeInstance(DateFormat.MEDIUM, isThai ? new Locale("th", "TH") : Locale.ROOT).format(date);
        String currentTime = ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.realTimeDDMMYYValueColor).toColoredFont() + dateFormat + " " + ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.realTimeHHMMSSValueColor).toColoredFont() + timeFormat;
        return ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.realTimeColor).toColoredFont() + "Time: " + currentTime;
    }

    public static String getCurrentGameTime(Minecraft mc)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (CompatibilityUtils.isSkyblockAddonsLoaded)
            {
                try
                {
                    StringBuilder builder = new StringBuilder();
                    Class<?> skyblockAddons = Class.forName("codes.biscuit.skyblockaddons.SkyblockAddons");
                    Object getInstance = skyblockAddons.getDeclaredMethod("getInstance").invoke(skyblockAddons);
                    Object getUtils = getInstance.getClass().getDeclaredMethod("getUtils").invoke(getInstance);
                    Object getCurrentDate = getUtils.getClass().getDeclaredMethod("getCurrentDate").invoke(getUtils);

                    if (getCurrentDate != null)
                    {
                        Class<?> date = getCurrentDate.getClass(); // SkyblockDate
                        Field hourField = date.getDeclaredField("hour");
                        Field minuteField = date.getDeclaredField("minute");
                        Field periodField = date.getDeclaredField("period");

                        hourField.setAccessible(true);
                        minuteField.setAccessible(true);
                        periodField.setAccessible(true);

                        builder.append(hourField.get(getCurrentDate));
                        builder.append(":");
                        int minute = (int)minuteField.get(getCurrentDate);
                        builder.append(minute == 0 ? "0" + minute : minute);
                        builder.append(" " + periodField.get(getCurrentDate).toString().toUpperCase(Locale.ROOT));
                        String currentTime = ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.realTimeDDMMYYValueColor).toColoredFont() + builder.toString();
                        return ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.realTimeColor).toColoredFont() + "Skyblock Time: " + currentTime + "/Day: " + mc.theWorld.getWorldTime() / 24000L;
                    }
                    else
                    {
                        return InfoUtils.INSTANCE.getCurrentGameTime(mc.theWorld.getWorldTime() % 24000) + "/Day: " + mc.theWorld.getWorldTime() / 24000L;
                    }
                }
                catch (Exception e) {}
            }
            else
            {
                return InfoUtils.INSTANCE.getCurrentGameTime(mc.theWorld.getWorldTime() % 24000) + "/Day: " + mc.theWorld.getWorldTime() / 24000L;
            }
        }
        return InfoUtils.INSTANCE.getCurrentGameTime(mc.theWorld.getWorldTime() % 24000);
    }

    public static String getGameWeather(Minecraft mc)
    {
        String weather = mc.theWorld.isRaining() && !mc.theWorld.isThundering() ? "Raining" : mc.theWorld.isRaining() && mc.theWorld.isThundering() ? "Thunder" : "";
        return ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.gameWeatherColor).toColoredFont() + "Weather: " + ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.gameWeatherValueColor).toColoredFont() + weather;
    }

    public static void renderHorizontalEquippedItems(Minecraft mc)
    {
        ScaledResolution res = new ScaledResolution(mc);
        boolean right = Equipments.Position.byId(SkyBlockcatiaSettings.INSTANCE.equipmentPosition) == Equipments.Position.RIGHT;
        int baseYOffset = SkyBlockcatiaSettings.INSTANCE.armorHUDYOffset;
        ItemStack mainhandStack = mc.thePlayer.getHeldItem();
        List<HorizontalEquipmentOverlay> equippedLists = new ArrayList<>();
        int prevX = 0;

        if (SkyBlockcatiaSettings.INSTANCE.equipmentArmorItems)
        {
            for (int i = 3; i >= 0; i--)
            {
                ItemStack armor = mc.thePlayer.inventory.armorInventory[i];

                if (armor != null)
                {
                    equippedLists.add(new HorizontalEquipmentOverlay(armor));
                }
            }
        }

        if (SkyBlockcatiaSettings.INSTANCE.equipmentHandItems)
        {
            if (mainhandStack != null)
            {
                equippedLists.add(new HorizontalEquipmentOverlay(mainhandStack));
            }
        }

        for (HorizontalEquipmentOverlay equipment : equippedLists)
        {
            int totalWidth = getTotalWidth(equippedLists);
            ItemStack itemStack = equipment.getItemStack();

            if (itemStack == null)
            {
                continue;
            }
            int xBaseRight = res.getScaledWidth() - totalWidth - 2;
            equipment.render(right ? xBaseRight + prevX + equipment.getWidth() : 2 + prevX, baseYOffset);
            prevX += equipment.getWidth();
        }
    }

    public static void renderVerticalEquippedItems(Minecraft mc)
    {
        int i = 0;
        ScaledResolution res = new ScaledResolution(mc);
        List<EquipmentOverlay> equippedLists = new ArrayList<>();
        ItemStack mainhandStack = mc.thePlayer.getHeldItem();
        boolean right = Equipments.Position.byId(SkyBlockcatiaSettings.INSTANCE.equipmentPosition) == Equipments.Position.RIGHT;
        int baseXOffset = right ? res.getScaledWidth() - 18 : 2;
        int baseYOffset = SkyBlockcatiaSettings.INSTANCE.armorHUDYOffset;

        if (SkyBlockcatiaSettings.INSTANCE.equipmentArmorItems)
        {
            for (int armorSlot = 3; armorSlot >= 0; armorSlot--)
            {
                ItemStack armor = mc.thePlayer.inventory.armorInventory[armorSlot];

                if (armor != null)
                {
                    equippedLists.add(new EquipmentOverlay(mc.thePlayer.inventory.armorInventory[armorSlot]));
                }
            }
        }

        if (SkyBlockcatiaSettings.INSTANCE.equipmentHandItems)
        {
            if (mainhandStack != null)
            {
                equippedLists.add(new EquipmentOverlay(mainhandStack));
            }
        }

        for (EquipmentOverlay equipment : equippedLists)
        {
            ItemStack itemStack = equipment.getItemStack();

            if (itemStack == null)
            {
                continue;
            }
            int equipmentYOffset = baseYOffset + 16 * i;
            String info = equipment.renderInfo();
            String arrowInfo = equipment.renderArrowInfo();
            float fontHeight = (mc.fontRendererObj.FONT_HEIGHT + 7) * i;
            float infoXOffset = right ? res.getScaledWidth() - mc.fontRendererObj.getStringWidth(info) - 20.0625F : baseXOffset + 18.0625F;
            float infoYOffset = baseYOffset + 4 + fontHeight;
            float arrowXOffset = right ? res.getScaledWidth() - ColorUtils.unicodeFontRenderer.getStringWidth(arrowInfo) - 2.0625F : baseXOffset + 8.0625F;
            float arrowYOffset = baseYOffset + 8 + fontHeight;

            EquipmentOverlay.renderItem(itemStack, baseXOffset, equipmentYOffset);

            if (!StringUtils.isNullOrEmpty(info))
            {
                mc.fontRendererObj.drawStringWithShadow(ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.equipmentStatusColor).toColoredFont() + info, infoXOffset, infoYOffset, 16777215);
            }
            else if (!StringUtils.isNullOrEmpty(arrowInfo))
            {
                GlStateManager.disableDepth();
                ColorUtils.unicodeFontRenderer.drawStringWithShadow(ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.arrowCountColor).toColoredFont() + arrowInfo, arrowXOffset, arrowYOffset, 16777215);
                GlStateManager.enableDepth();
            }
            ++i;
        }
    }

    public static void renderHotbarEquippedItems(Minecraft mc)
    {
        ScaledResolution res = new ScaledResolution(mc);
        List<HotbarEquipmentOverlay> equippedLists = new ArrayList<>();
        ItemStack mainhandStack = mc.thePlayer.getHeldItem();
        int iLeft = 0;
        int iRight = 0;

        if (SkyBlockcatiaSettings.INSTANCE.equipmentArmorItems)
        {
            for (int i = 2; i <= 3; i++)
            {
                ItemStack armor = mc.thePlayer.inventory.armorInventory[i];

                if (armor != null)
                {
                    equippedLists.add(new HotbarEquipmentOverlay(armor, HotbarEquipmentOverlay.Side.LEFT));
                }
            }
            for (int i = 0; i <= 1; i++)
            {
                ItemStack armor = mc.thePlayer.inventory.armorInventory[i];

                if (armor != null)
                {
                    equippedLists.add(new HotbarEquipmentOverlay(armor, HotbarEquipmentOverlay.Side.RIGHT));
                }
            }
        }

        if (SkyBlockcatiaSettings.INSTANCE.equipmentHandItems)
        {
            if (mainhandStack != null)
            {
                equippedLists.add(new HotbarEquipmentOverlay(mainhandStack, HotbarEquipmentOverlay.Side.LEFT));
            }
        }

        for (HotbarEquipmentOverlay equipment : equippedLists)
        {
            ItemStack itemStack = equipment.getItemStack();

            if (itemStack == null)
            {
                continue;
            }

            String info = equipment.renderInfo();
            String arrowInfo = equipment.renderArrowInfo();

            if (equipment.getSide() == HotbarEquipmentOverlay.Side.LEFT)
            {
                int baseXOffset = res.getScaledWidth() / 2 - 111;
                int armorYOffset = res.getScaledHeight() - 16 * iLeft - 18;
                float infoXOffset = res.getScaledWidth() / 2 - 114 - mc.fontRendererObj.getStringWidth(info);
                int infoYOffset = res.getScaledHeight() - 16 * iLeft - 14;
                float arrowXOffset = res.getScaledWidth() / 2 - 104;
                int arrowYOffset = res.getScaledHeight() - 16 * iLeft - 10;

                EquipmentOverlay.renderItem(itemStack, baseXOffset, armorYOffset);

                if (!StringUtils.isNullOrEmpty(info))
                {
                    mc.fontRendererObj.drawStringWithShadow(ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.equipmentStatusColor).toColoredFont() + info, infoXOffset, infoYOffset, 16777215);
                }
                else if (!StringUtils.isNullOrEmpty(arrowInfo))
                {
                    GlStateManager.disableDepth();
                    ColorUtils.unicodeFontRenderer.drawStringWithShadow(ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.arrowCountColor).toColoredFont() + arrowInfo, arrowXOffset, arrowYOffset, 16777215);
                    GlStateManager.enableDepth();
                }
                ++iLeft;
            }
            else
            {
                int baseXOffset = res.getScaledWidth() / 2 + 95;
                int armorYOffset = res.getScaledHeight() - 16 * iRight - 18;
                float infoXOffset = res.getScaledWidth() / 2 + 114;
                int infoYOffset = res.getScaledHeight() - 16 * iRight - 14;

                EquipmentOverlay.renderItem(itemStack, baseXOffset, armorYOffset);

                if (!StringUtils.isNullOrEmpty(info))
                {
                    mc.fontRendererObj.drawStringWithShadow(ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.equipmentStatusColor).toColoredFont() + info, infoXOffset, infoYOffset, 16777215);
                }
                else if (!StringUtils.isNullOrEmpty(arrowInfo))
                {
                    float arrowXOffset = res.getScaledWidth() / 2 + 112 - ColorUtils.unicodeFontRenderer.getStringWidth(arrowInfo);
                    int arrowYOffset = res.getScaledHeight() - 16 * iRight - 32;

                    GlStateManager.disableDepth();
                    ColorUtils.unicodeFontRenderer.drawStringWithShadow(ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.arrowCountColor).toColoredFont() + arrowInfo, arrowXOffset, arrowYOffset, 16777215);
                    GlStateManager.enableDepth();
                }
                ++iRight;
            }
        }
    }

    public static void renderPotionHUD(Minecraft mc)
    {
        boolean iconAndTime = StatusEffects.Style.byId(SkyBlockcatiaSettings.INSTANCE.potionHUDStyle) == StatusEffects.Style.ICON_AND_TIME;
        boolean right = StatusEffects.Position.byId(SkyBlockcatiaSettings.INSTANCE.potionHUDPosition) == StatusEffects.Position.RIGHT;
        boolean showIcon = SkyBlockcatiaSettings.INSTANCE.potionHUDIcon;
        StatusEffects.Position potionPos = StatusEffects.Position.byId(SkyBlockcatiaSettings.INSTANCE.potionHUDPosition);
        ScaledResolution scaledRes = new ScaledResolution(mc);
        int size = SkyBlockcatiaSettings.INSTANCE.maximumPotionDisplay;
        int length = SkyBlockcatiaSettings.INSTANCE.potionLengthYOffset;
        int lengthOverlap = SkyBlockcatiaSettings.INSTANCE.potionLengthYOffsetOverlap;
        Collection<PotionEffect> collection = mc.thePlayer.getActivePotionEffects();
        int xPotion = 0;
        int yPotion = 0;

        if (potionPos == StatusEffects.Position.HOTBAR_LEFT)
        {
            xPotion = scaledRes.getScaledWidth() / 2 - 91 - 35;
            yPotion = scaledRes.getScaledHeight() - 46;
        }
        else if (potionPos == StatusEffects.Position.HOTBAR_RIGHT)
        {
            xPotion = scaledRes.getScaledWidth() / 2 + 91 - 20;
            yPotion = scaledRes.getScaledHeight() - 42;
        }
        else
        {
            xPotion = right ? scaledRes.getScaledWidth() - 32 : -24;
            yPotion = scaledRes.getScaledHeight() - 220 + SkyBlockcatiaSettings.INSTANCE.potionHUDYOffset + 90;
        }

        if (!collection.isEmpty())
        {
            if (collection.size() > size)
            {
                length = lengthOverlap / (collection.size() - 1);
            }

            for (PotionEffect potioneffect : mc.thePlayer.getActivePotionEffects())
            {
                float alpha = 1.0F;
                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                String s = Potion.getDurationString(potioneffect);
                String s1 = LangUtils.translate(potion.getName());

                if (!potioneffect.getIsAmbient() && potioneffect.getDuration() <= 200)
                {
                    int j1 = 10 - potioneffect.getDuration() / 20;
                    alpha = MathHelper.clamp_float(potioneffect.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + MathHelper.cos(potioneffect.getDuration() * (float)Math.PI / 5.0F) * MathHelper.clamp_float(j1 / 10.0F * 0.25F, 0.0F, 0.25F);
                }

                GlStateManager.enableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

                if (showIcon)
                {
                    mc.getTextureManager().bindTexture(InfoOverlays.inventoryBackground);
                    int index = potion.getStatusIconIndex();

                    if (potionPos == StatusEffects.Position.HOTBAR_LEFT)
                    {
                        mc.ingameGUI.drawTexturedModalRect(xPotion + 12, yPotion + 6, index % 8 * 18, 198 + index / 8 * 18, 18, 18);
                    }
                    else if (potionPos == StatusEffects.Position.HOTBAR_RIGHT)
                    {
                        mc.ingameGUI.drawTexturedModalRect(xPotion + 24, yPotion + 6, index % 8 * 18, 198 + index / 8 * 18, 18, 18);
                    }
                    else
                    {
                        mc.ingameGUI.drawTexturedModalRect(right ? xPotion + 12 : xPotion + 28, yPotion + 6, index % 8 * 18, 198 + index / 8 * 18, 18, 18);
                    }
                }

                if (potioneffect.getAmplifier() == 1)
                {
                    s1 = s1 + " " + LangUtils.translate("enchantment.level.2");
                }
                else if (potioneffect.getAmplifier() == 2)
                {
                    s1 = s1 + " " + LangUtils.translate("enchantment.level.3");
                }
                else if (potioneffect.getAmplifier() == 3)
                {
                    s1 = s1 + " " + LangUtils.translate("enchantment.level.4");
                }

                int stringwidth1 = mc.fontRendererObj.getStringWidth(s);
                int stringwidth2 = mc.fontRendererObj.getStringWidth(s1);

                if (potionPos == StatusEffects.Position.HOTBAR_LEFT)
                {
                    if (!iconAndTime)
                    {
                        mc.fontRendererObj.drawString(s1, showIcon ? xPotion + 8 - stringwidth2 : xPotion + 28 - stringwidth2, yPotion + 6, SkyBlockcatiaSettings.INSTANCE.alternatePotionHUDTextColor ? potion.getLiquidColor() : 16777215, true);
                    }
                    mc.fontRendererObj.drawString(s, showIcon ? xPotion + 8 - stringwidth1 : xPotion + 28 - stringwidth1, iconAndTime ? yPotion + 11 : yPotion + 16, SkyBlockcatiaSettings.INSTANCE.alternatePotionHUDTextColor ? potion.getLiquidColor() : 16777215, true);
                }
                else if (potionPos == StatusEffects.Position.HOTBAR_RIGHT)
                {
                    if (!iconAndTime)
                    {
                        mc.fontRendererObj.drawString(s1, showIcon ? xPotion + 46 : xPotion + 28, yPotion + 6, SkyBlockcatiaSettings.INSTANCE.alternatePotionHUDTextColor ? potion.getLiquidColor() : 16777215, true);
                    }
                    mc.fontRendererObj.drawString(s, showIcon ? xPotion + 46 : xPotion + 28, iconAndTime ? yPotion + 11 : yPotion + 16, SkyBlockcatiaSettings.INSTANCE.alternatePotionHUDTextColor ? potion.getLiquidColor() : 16777215, true);
                }
                else
                {
                    if (!iconAndTime)
                    {
                        mc.fontRendererObj.drawString(s1, right ? showIcon ? xPotion + 8 - stringwidth2 : xPotion + 28 - stringwidth2 : showIcon ? xPotion + 50 : xPotion + 28, yPotion + 6, SkyBlockcatiaSettings.INSTANCE.alternatePotionHUDTextColor ? potion.getLiquidColor() : 16777215, true);
                    }
                    mc.fontRendererObj.drawString(s, right ? showIcon ? xPotion + 8 - stringwidth1 : xPotion + 28 - stringwidth1 : showIcon ? xPotion + 50 : xPotion + 28, iconAndTime ? yPotion + 11 : yPotion + 16, SkyBlockcatiaSettings.INSTANCE.alternatePotionHUDTextColor ? potion.getLiquidColor() : 16777215, true);
                }
                yPotion -= length;
            }
        }
    }

    private static String getResponseTimeColor(int responseTime)
    {
        if (responseTime >= 200 && responseTime < 300)
        {
            return ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.ping200And300Color).toColoredFont();
        }
        else if (responseTime >= 300 && responseTime < 500)
        {
            return ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.ping300And500Color).toColoredFont();
        }
        else if (responseTime >= 500)
        {
            return ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.pingMax500Color).toColoredFont();
        }
        else
        {
            return ColorUtils.stringToRGB(SkyBlockcatiaSettings.INSTANCE.pingValueColor).toColoredFont();
        }
    }

    private static int getTotalWidth(List<HorizontalEquipmentOverlay> equippedLists)
    {
        int width = 0;

        for (HorizontalEquipmentOverlay equipment : equippedLists)
        {
            ItemStack itemStack = equipment.getItemStack();

            if (itemStack == null)
            {
                continue;
            }
            width += equipment.getWidth();
        }
        return width;
    }
}