package com.stevekung.skyblockcatia.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

import com.stevekung.skyblockcatia.utils.ClientUtils;
import com.stevekung.skyblockcatia.utils.GameProfileUtils;
import com.stevekung.skyblockcatia.utils.LangUtils;
import com.stevekung.skyblockcatia.utils.LoggerIN;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class ExtendedConfig
{
    public static ExtendedConfig instance = new ExtendedConfig();
    private static final String defaultWhite = "255,255,255";
    public static final File skyblockcatiaDir = new File(Minecraft.getMinecraft().mcDataDir, "skyblockcatia");
    public static final File userDir = new File(skyblockcatiaDir, GameProfileUtils.getUUID().toString());
    public static final File defaultConfig = new File(userDir, "default.dat");
    public static String currentProfile = "";
    private static final String[] EQUIPMENT_DIRECTION = new String[] {"equipment.vertical", "equipment.horizontal"};
    private static final String[] EQUIPMENT_STATUS = new String[] {"equipment.damage_and_max_damage", "equipment.percent", "equipment.only_damage", "skyblockcatia.none", "equipment.count", "equipment.count_and_stack"};
    private static final String[] EQUIPMENT_POSITION = new String[] {"skyblockcatia.left", "skyblockcatia.right", "skyblockcatia.hotbar"};
    private static final String[] POTION_STATUS_HUD_STYLE = new String[] {"skyblockcatia.default", "potion_hud.icon_and_time"};
    private static final String[] POTION_STATUS_HUD_POSITION = new String[] {"skyblockcatia.left", "skyblockcatia.right", "skyblockcatia.hotbar_left", "skyblockcatia.hotbar_right"};
    private static final String[] PING_MODE = new String[] {"skyblockcatia.only_ping", "skyblockcatia.ping_and_delay"};
    private static final String[] TOAST_MODE_DISABLED = new String[] {"skyblockcatia.chat", "skyblockcatia.toast", "skyblockcatia.chat_and_toast", "skyblockcatia.disabled"};
    private static final String[] TOAST_MODE = new String[] {"skyblockcatia.chat", "skyblockcatia.toast", "skyblockcatia.chat_and_toast"};
    private static File file;

    // Render Info
    public boolean fps = true;
    public boolean xyz = true;
    public boolean direction = true;
    public boolean biome = true;
    public boolean ping = true;
    public boolean pingToSecond = false;
    public boolean serverIP = false;
    public boolean serverIPMCVersion = false;
    public boolean equipmentHUD = false;
    public boolean equipmentArmorItems = true;
    public boolean equipmentHandItems = true;
    public boolean potionHUD = false;
    public boolean realTime = true;
    public boolean gameTime = true;
    public boolean gameWeather = true;
    public boolean moonPhase = true;
    public boolean potionHUDIcon = false;
    public boolean alternatePotionHUDTextColor = false;
    public boolean toggleSprint = false;
    public boolean toggleSneak = false;

    // Main
    public boolean swapRenderInfo = false;
    public int equipmentDirection = 0;
    public int equipmentStatus = 0;
    public int equipmentPosition = 2;
    public int potionHUDStyle = 0;
    public int potionHUDPosition = 0;
    public int pingMode = 0;

    // Offset
    public int armorHUDYOffset = 0;
    public int potionHUDYOffset = 0;
    public int maximumPotionDisplay = 2;
    public int potionLengthYOffset = 23;
    public int potionLengthYOffsetOverlap = 45;

    // Custom Color
    public String fpsColor = defaultWhite;
    public String xyzColor = defaultWhite;
    public String biomeColor = defaultWhite;
    public String directionColor = defaultWhite;
    public String pingColor = defaultWhite;
    public String pingToSecondColor = defaultWhite;
    public String serverIPColor = defaultWhite;
    public String equipmentStatusColor = defaultWhite;
    public String arrowCountColor = defaultWhite;
    public String baitCountColor = defaultWhite;
    public String realTimeColor = defaultWhite;
    public String gameTimeColor = defaultWhite;
    public String gameWeatherColor = defaultWhite;
    public String moonPhaseColor = defaultWhite;
    public String jungleAxeCooldownColor = defaultWhite;
    public String grapplingHookCooldownColor = defaultWhite;
    public String zealotRespawnCooldownColor = defaultWhite;
    public String placedSummoningEyeColor = defaultWhite;
    public String golemStageColor = defaultWhite;

    // Custom Color : Value
    public String fpsValueColor = "85,255,85";
    public String fps26And49Color = "255,255,85";
    public String fpsLow25Color = "255,85,85";
    public String xyzValueColor = defaultWhite;
    public String directionValueColor = defaultWhite;
    public String biomeValueColor = defaultWhite;
    public String pingValueColor = "85,255,85";
    public String ping200And300Color = "255,255,85";
    public String ping300And500Color = "255,85,85";
    public String pingMax500Color = "170,0,0";
    public String serverIPValueColor = defaultWhite;
    public String realTimeHHMMSSValueColor = defaultWhite;
    public String realTimeDDMMYYValueColor = defaultWhite;
    public String gameTimeValueColor = defaultWhite;
    public String gameWeatherValueColor = defaultWhite;
    public String moonPhaseValueColor = defaultWhite;
    public String placedSummoningEyeValueColor = defaultWhite;
    public String golemStageValueColor = defaultWhite;

    // Misc
    public String toggleSprintUseMode = "command";
    public String toggleSneakUseMode = "command";

    // Hypixel
    public boolean rightClickToAddParty = false;
    public boolean addPartyVisitIsland = false;
    public boolean jungleAxeCooldown = true;
    public boolean grapplingHookCooldown = true;
    public boolean zealotRespawnCooldown = false;
    public boolean glowingDragonArmor = false;
    public String hypixelNickName = "";
    public int selectedHypixelMinigame = 0;
    public int hypixelMinigameScrollPos = 0;
    public int visitIslandToastMode = 2;
    public int itemDropToastMode = 2;
    public int fishCatchToastMode = 2;
    public int giftToastMode = 2;
    public int petToastMode = 2;
    public int chatMode = 0;
    public boolean placedSummoningEyeTracker = false;
    public boolean showItemRarity = true;
    public boolean showDragonHitboxOnly = false;
    public boolean showHitboxWhenDragonSpawned = false;
    public boolean sneakToOpenInventoryWhileFightDragon = false;
    public boolean leavePartyWhenLastEyePlaced = false;
    public boolean currentServerDay = true;
    public boolean lobbyPlayerViewer = false;
    public boolean auctionBidConfirm = false;
    public boolean disableBlockParticles = false;
    public boolean golemStageTracker = false;
    public int itemRarityOpacity = 75;
    public int auctionBidConfirmValue = 500000;
    public boolean supportersFancyColor = false;
    public boolean bazaarOnTooltips = false;
    public boolean ignoreBushHitbox = false;
    public boolean onlyMineableHitbox = false;
    public boolean ignoreInteractInvisibleArmorStand = false;
    public boolean automaticOpenMaddox = false;
    public boolean sneakToTradeOtherPlayerIsland = true;
    public boolean makeSpecialZealotHeldGold = true;

    private ExtendedConfig() {}

    public void setCurrentProfile(String profileName)
    {
        ExtendedConfig.file = new File(userDir, profileName + ".dat");
        currentProfile = profileName;
    }

    public void load()
    {
        try
        {
            NBTTagCompound nbt = CompressedStreamTools.read(ExtendedConfig.file);

            if (nbt == null)
            {
                return;
            }

            // Render Info
            this.fps = ExtendedConfig.getBoolean(nbt, "FPS", this.fps);
            this.xyz = ExtendedConfig.getBoolean(nbt, "XYZ", this.xyz);
            this.direction = ExtendedConfig.getBoolean(nbt, "Direction", this.direction);
            this.biome = ExtendedConfig.getBoolean(nbt, "Biome", this.biome);
            this.ping = ExtendedConfig.getBoolean(nbt, "Ping", this.ping);
            this.pingToSecond = ExtendedConfig.getBoolean(nbt, "PingToSecond", this.pingToSecond);
            this.serverIP = ExtendedConfig.getBoolean(nbt, "ServerIP", this.serverIP);
            this.serverIPMCVersion = ExtendedConfig.getBoolean(nbt, "ServerIPMCVersion", this.serverIPMCVersion);
            this.equipmentHUD = ExtendedConfig.getBoolean(nbt, "EquipmentHUD", this.equipmentHUD);
            this.equipmentArmorItems = ExtendedConfig.getBoolean(nbt, "EquipmentArmorItems", this.equipmentArmorItems);
            this.equipmentHandItems = ExtendedConfig.getBoolean(nbt, "EquipmentHandItems", this.equipmentHandItems);
            this.potionHUD = ExtendedConfig.getBoolean(nbt, "PotionHUD", this.potionHUD);
            this.realTime = ExtendedConfig.getBoolean(nbt, "RealTime", this.realTime);
            this.gameTime = ExtendedConfig.getBoolean(nbt, "GameTime", this.gameTime);
            this.gameWeather = ExtendedConfig.getBoolean(nbt, "GameWeather", this.gameWeather);
            this.moonPhase = ExtendedConfig.getBoolean(nbt, "MoonPhase", this.moonPhase);
            this.potionHUDIcon = ExtendedConfig.getBoolean(nbt, "PotionHUDIcon", this.potionHUDIcon);
            this.alternatePotionHUDTextColor = ExtendedConfig.getBoolean(nbt, "AlternatePotionHUDTextColor", this.alternatePotionHUDTextColor);

            // Main
            this.swapRenderInfo = ExtendedConfig.getBoolean(nbt, "SwapRenderInfo", this.swapRenderInfo);
            this.equipmentDirection = ExtendedConfig.getInteger(nbt, "EquipmentDirection", this.equipmentDirection);
            this.equipmentStatus = ExtendedConfig.getInteger(nbt, "EquipmentStatus", this.equipmentStatus);
            this.equipmentPosition = ExtendedConfig.getInteger(nbt, "EquipmentPosition", this.equipmentPosition);
            this.potionHUDStyle = ExtendedConfig.getInteger(nbt, "PotionHUDStyle", this.potionHUDStyle);
            this.potionHUDPosition = ExtendedConfig.getInteger(nbt, "PotionHUDPosition", this.potionHUDPosition);
            this.pingMode = ExtendedConfig.getInteger(nbt, "PingMode", this.pingMode);
            this.visitIslandToastMode = ExtendedConfig.getInteger(nbt, "VisitIslandToastMode", this.visitIslandToastMode);
            this.itemDropToastMode = ExtendedConfig.getInteger(nbt, "ItemDropToastMode", this.itemDropToastMode);
            this.fishCatchToastMode = ExtendedConfig.getInteger(nbt, "FishCatchToastMode", this.fishCatchToastMode);
            this.giftToastMode = ExtendedConfig.getInteger(nbt, "GiftToastMode", this.giftToastMode);
            this.petToastMode = ExtendedConfig.getInteger(nbt, "PetToastMode", this.petToastMode);
            this.chatMode = ExtendedConfig.getInteger(nbt, "ChatMode", this.chatMode);

            // Movement
            this.toggleSprint = ExtendedConfig.getBoolean(nbt, "ToggleSprint", this.toggleSprint);
            this.toggleSneak = ExtendedConfig.getBoolean(nbt, "ToggleSneak", this.toggleSneak);

            // Offset
            this.armorHUDYOffset = ExtendedConfig.getInteger(nbt, "ArmorHUDYOffset", this.armorHUDYOffset);
            this.potionHUDYOffset = ExtendedConfig.getInteger(nbt, "PotionHUDYOffset", this.potionHUDYOffset);
            this.maximumPotionDisplay = ExtendedConfig.getInteger(nbt, "MaximumPotionDisplay", this.maximumPotionDisplay);
            this.potionLengthYOffset = ExtendedConfig.getInteger(nbt, "PotionLengthYOffset", this.potionLengthYOffset);
            this.potionLengthYOffsetOverlap = ExtendedConfig.getInteger(nbt, "PotionLengthYOffsetOverlap", this.potionLengthYOffsetOverlap);

            // Custom Color
            this.fpsColor = ExtendedConfig.getString(nbt, "FPSColor", this.fpsColor);
            this.xyzColor = ExtendedConfig.getString(nbt, "XYZColor", this.xyzColor);
            this.biomeColor = ExtendedConfig.getString(nbt, "BiomeColor", this.biomeColor);
            this.directionColor = ExtendedConfig.getString(nbt, "DirectionColor", this.directionColor);
            this.pingColor = ExtendedConfig.getString(nbt, "PingColor", this.pingColor);
            this.pingToSecondColor = ExtendedConfig.getString(nbt, "PingToSecondColor", this.pingToSecondColor);
            this.serverIPColor = ExtendedConfig.getString(nbt, "ServerIPColor", this.serverIPColor);
            this.equipmentStatusColor = ExtendedConfig.getString(nbt, "EquipmentStatusColor", this.equipmentStatusColor);
            this.arrowCountColor = ExtendedConfig.getString(nbt, "ArrowCountColor", this.arrowCountColor);
            this.baitCountColor = ExtendedConfig.getString(nbt, "BaitCountColor", this.baitCountColor);
            this.realTimeColor = ExtendedConfig.getString(nbt, "RealTimeColor", this.realTimeColor);
            this.gameTimeColor = ExtendedConfig.getString(nbt, "GameTimeColor", this.gameTimeColor);
            this.gameWeatherColor = ExtendedConfig.getString(nbt, "GameWeatherColor", this.gameWeatherColor);
            this.moonPhaseColor = ExtendedConfig.getString(nbt, "MoonPhaseColor", this.moonPhaseColor);
            this.jungleAxeCooldownColor = ExtendedConfig.getString(nbt, "JungleAxeCooldownColor", this.jungleAxeCooldownColor);
            this.grapplingHookCooldownColor = ExtendedConfig.getString(nbt, "GrapplingHookCooldownColor", this.grapplingHookCooldownColor);
            this.zealotRespawnCooldownColor = ExtendedConfig.getString(nbt, "ZealotRespawnCooldownColor", this.zealotRespawnCooldownColor);
            this.placedSummoningEyeColor = ExtendedConfig.getString(nbt, "PlacedSummoningEyeColor", this.placedSummoningEyeColor);
            this.golemStageColor = ExtendedConfig.getString(nbt, "GolemStageColor", this.golemStageColor);

            // Custom Color : Value
            this.fpsValueColor = ExtendedConfig.getString(nbt, "FPSValueColor", this.fpsValueColor);
            this.fps26And49Color = ExtendedConfig.getString(nbt, "FPS26And49Color", this.fps26And49Color);
            this.fpsLow25Color = ExtendedConfig.getString(nbt, "FPSLow25Color", this.fpsLow25Color);
            this.xyzValueColor = ExtendedConfig.getString(nbt, "XYZValueColor", this.xyzValueColor);
            this.biomeValueColor = ExtendedConfig.getString(nbt, "BiomeValueColor", this.biomeValueColor);
            this.directionValueColor = ExtendedConfig.getString(nbt, "DirectionValueColor", this.directionValueColor);
            this.pingValueColor = ExtendedConfig.getString(nbt, "PingValueColor", this.pingValueColor);
            this.ping200And300Color = ExtendedConfig.getString(nbt, "Ping200And300Color", this.ping200And300Color);
            this.ping300And500Color = ExtendedConfig.getString(nbt, "Ping300And500Color", this.ping300And500Color);
            this.pingMax500Color = ExtendedConfig.getString(nbt, "PingMax500Color", this.pingMax500Color);
            this.serverIPValueColor = ExtendedConfig.getString(nbt, "ServerIPValueColor", this.serverIPValueColor);
            this.realTimeHHMMSSValueColor = ExtendedConfig.getString(nbt, "RealTimeHHMMSSValueColor", this.realTimeHHMMSSValueColor);
            this.realTimeDDMMYYValueColor = ExtendedConfig.getString(nbt, "RealTimeDDMMYYValueColor", this.realTimeDDMMYYValueColor);
            this.gameTimeValueColor = ExtendedConfig.getString(nbt, "GameTimeValueColor", this.gameTimeValueColor);
            this.gameWeatherValueColor = ExtendedConfig.getString(nbt, "GameWeatherValueColor", this.gameWeatherValueColor);
            this.moonPhaseValueColor = ExtendedConfig.getString(nbt, "MoonPhaseValueColor", this.moonPhaseValueColor);
            this.placedSummoningEyeValueColor = ExtendedConfig.getString(nbt, "PlacedSummoningEyeValueColor", this.placedSummoningEyeValueColor);
            this.golemStageValueColor = ExtendedConfig.getString(nbt, "GolemStageValueColor", this.golemStageValueColor);

            // Misc
            this.toggleSprintUseMode = ExtendedConfig.getString(nbt, "ToggleSprintUseMode", this.toggleSprintUseMode);
            this.toggleSneakUseMode = ExtendedConfig.getString(nbt, "ToggleSneakUseMode", this.toggleSneakUseMode);

            // Hypixel
            this.rightClickToAddParty = ExtendedConfig.getBoolean(nbt, "RightClickToAddParty", this.rightClickToAddParty);
            this.addPartyVisitIsland = ExtendedConfig.getBoolean(nbt, "AddPartyVisitIsland", this.addPartyVisitIsland);
            this.hypixelNickName = ExtendedConfig.getString(nbt, "HypixelNickName", this.hypixelNickName);
            this.selectedHypixelMinigame = ExtendedConfig.getInteger(nbt, "SelectedHypixelMinigame", this.selectedHypixelMinigame);
            this.hypixelMinigameScrollPos = ExtendedConfig.getInteger(nbt, "HypixelMinigameScrollPos", this.hypixelMinigameScrollPos);
            this.itemRarityOpacity = ExtendedConfig.getInteger(nbt, "ItemRarityOpacity", this.itemRarityOpacity);
            this.auctionBidConfirmValue = ExtendedConfig.getInteger(nbt, "AuctionBidConfirmValue", this.auctionBidConfirmValue);
            this.jungleAxeCooldown = ExtendedConfig.getBoolean(nbt, "JungleAxeCooldown", this.jungleAxeCooldown);
            this.grapplingHookCooldown = ExtendedConfig.getBoolean(nbt, "GrapplingHookCooldown", this.grapplingHookCooldown);
            this.zealotRespawnCooldown = ExtendedConfig.getBoolean(nbt, "ZealotRespawnCooldown", this.zealotRespawnCooldown);
            this.glowingDragonArmor = ExtendedConfig.getBoolean(nbt, "GlowingDragonArmor", this.glowingDragonArmor);
            this.placedSummoningEyeTracker = ExtendedConfig.getBoolean(nbt, "PlacedSummoningEyeTracker", this.placedSummoningEyeTracker);
            this.showItemRarity = ExtendedConfig.getBoolean(nbt, "ShowItemRarity", this.showItemRarity);
            this.showDragonHitboxOnly = ExtendedConfig.getBoolean(nbt, "ShowDragonHitboxOnly", this.showDragonHitboxOnly);
            this.showHitboxWhenDragonSpawned = ExtendedConfig.getBoolean(nbt, "ShowHitboxWhenDragonSpawned", this.showHitboxWhenDragonSpawned);
            this.sneakToOpenInventoryWhileFightDragon = ExtendedConfig.getBoolean(nbt, "SneakToOpenInventoryWhileFightDragon", this.sneakToOpenInventoryWhileFightDragon);
            this.leavePartyWhenLastEyePlaced = ExtendedConfig.getBoolean(nbt, "LeavePartyWhenLastEyePlaced", this.leavePartyWhenLastEyePlaced);
            this.currentServerDay = ExtendedConfig.getBoolean(nbt, "CurrentServerDay", this.currentServerDay);
            this.lobbyPlayerViewer = ExtendedConfig.getBoolean(nbt, "LobbyPlayerViewer", this.lobbyPlayerViewer);
            this.auctionBidConfirm = ExtendedConfig.getBoolean(nbt, "AuctionBidConfirm", this.auctionBidConfirm);
            this.disableBlockParticles = ExtendedConfig.getBoolean(nbt, "DisableBlockParticles", this.disableBlockParticles);
            this.supportersFancyColor = ExtendedConfig.getBoolean(nbt, "SupportersFancyColor", this.supportersFancyColor);
            this.golemStageTracker = ExtendedConfig.getBoolean(nbt, "GolemStageTracker", this.golemStageTracker);
            this.bazaarOnTooltips = ExtendedConfig.getBoolean(nbt, "BazaarOnTooltips", this.bazaarOnTooltips);
            this.ignoreBushHitbox = ExtendedConfig.getBoolean(nbt, "IgnoreBushHitbox", this.ignoreBushHitbox);
            this.onlyMineableHitbox = ExtendedConfig.getBoolean(nbt, "OnlyMineableHitbox", this.onlyMineableHitbox);
            this.ignoreInteractInvisibleArmorStand = ExtendedConfig.getBoolean(nbt, "IgnoreInteractInvisibleArmorStand", this.ignoreInteractInvisibleArmorStand);
            this.automaticOpenMaddox = ExtendedConfig.getBoolean(nbt, "AutomaticOpenMaddox", this.automaticOpenMaddox);
            this.sneakToTradeOtherPlayerIsland = ExtendedConfig.getBoolean(nbt, "SneakToTradeOtherPlayerIsland", this.sneakToTradeOtherPlayerIsland);
            this.makeSpecialZealotHeldGold = ExtendedConfig.getBoolean(nbt, "MakeSpecialZealotHeldGold", this.makeSpecialZealotHeldGold);

            LoggerIN.info("Loading extended config {}", ExtendedConfig.file.getPath());
        }
        catch (Exception e) {}
    }

    public void save()
    {
        this.save(!ExtendedConfig.currentProfile.isEmpty() ? ExtendedConfig.currentProfile : "default");
    }

    public void save(String profileName)
    {
        try
        {
            NBTTagCompound nbt = new NBTTagCompound();

            // Render Info
            nbt.setBoolean("FPS", this.fps);
            nbt.setBoolean("XYZ", this.xyz);
            nbt.setBoolean("Direction", this.direction);
            nbt.setBoolean("Biome", this.biome);
            nbt.setBoolean("Ping", this.ping);
            nbt.setBoolean("PingToSecond", this.pingToSecond);
            nbt.setBoolean("ServerIP", this.serverIP);
            nbt.setBoolean("ServerIPMCVersion", this.serverIPMCVersion);
            nbt.setBoolean("EquipmentHUD", this.equipmentHUD);
            nbt.setBoolean("EquipmentArmorItems", this.equipmentArmorItems);
            nbt.setBoolean("EquipmentHandItems", this.equipmentHandItems);
            nbt.setBoolean("PotionHUD", this.potionHUD);
            nbt.setBoolean("RealTime", this.realTime);
            nbt.setBoolean("GameTime", this.gameTime);
            nbt.setBoolean("GameWeather", this.gameWeather);
            nbt.setBoolean("MoonPhase", this.moonPhase);
            nbt.setBoolean("PotionHUDIcon", this.potionHUDIcon);
            nbt.setBoolean("AlternatePotionHUDTextColor", this.alternatePotionHUDTextColor);

            // Main
            nbt.setBoolean("SwapRenderInfo", this.swapRenderInfo);
            nbt.setInteger("EquipmentDirection", this.equipmentDirection);
            nbt.setInteger("EquipmentStatus", this.equipmentStatus);
            nbt.setInteger("EquipmentPosition", this.equipmentPosition);
            nbt.setInteger("PotionHUDStyle", this.potionHUDStyle);
            nbt.setInteger("PotionHUDPosition", this.potionHUDPosition);
            nbt.setInteger("PingMode", this.pingMode);
            nbt.setInteger("VisitIslandToastMode", this.visitIslandToastMode);
            nbt.setInteger("ItemDropToastMode", this.itemDropToastMode);
            nbt.setInteger("FishCatchToastMode", this.fishCatchToastMode);
            nbt.setInteger("GiftToastMode", this.giftToastMode);
            nbt.setInteger("PetToastMode", this.petToastMode);
            nbt.setInteger("ChatMode", this.chatMode);

            // Movement
            nbt.setBoolean("ToggleSprint", this.toggleSprint);
            nbt.setBoolean("ToggleSneak", this.toggleSneak);

            // Offset
            nbt.setInteger("ArmorHUDYOffset", this.armorHUDYOffset);
            nbt.setInteger("PotionHUDYOffset", this.potionHUDYOffset);
            nbt.setInteger("MaximumPotionDisplay", this.maximumPotionDisplay);
            nbt.setInteger("PotionLengthYOffset", this.potionLengthYOffset);
            nbt.setInteger("PotionLengthYOffsetOverlap", this.potionLengthYOffsetOverlap);

            // Custom Color
            nbt.setString("FPSColor", this.fpsColor);
            nbt.setString("XYZColor", this.xyzColor);
            nbt.setString("BiomeColor", this.biomeColor);
            nbt.setString("DirectionColor", this.directionColor);
            nbt.setString("PingColor", this.pingColor);
            nbt.setString("PingToSecondColor", this.pingToSecondColor);
            nbt.setString("ServerIPColor", this.serverIPColor);
            nbt.setString("EquipmentStatusColor", this.equipmentStatusColor);
            nbt.setString("ArrowCountColor", this.arrowCountColor);
            nbt.setString("BaitCountColor", this.baitCountColor);
            nbt.setString("RealTimeColor", this.realTimeColor);
            nbt.setString("GameTimeColor", this.gameTimeColor);
            nbt.setString("GameWeatherColor", this.gameWeatherColor);
            nbt.setString("MoonPhaseColor", this.moonPhaseColor);
            nbt.setString("JungleAxeCooldownColor", this.jungleAxeCooldownColor);
            nbt.setString("GrapplingHookCooldownColor", this.grapplingHookCooldownColor);
            nbt.setString("ZealotRespawnCooldownColor", this.zealotRespawnCooldownColor);
            nbt.setString("PlacedSummoningEyeColor", this.placedSummoningEyeColor);
            nbt.setString("GolemStageColor", this.golemStageColor);

            // Custom Color : Value
            nbt.setString("FPSValueColor", this.fpsValueColor);
            nbt.setString("FPS26And49Color", this.fps26And49Color);
            nbt.setString("FPSLow25Color", this.fpsLow25Color);
            nbt.setString("XYZValueColor", this.xyzValueColor);
            nbt.setString("BiomeValueColor", this.biomeValueColor);
            nbt.setString("DirectionValueColor", this.directionValueColor);
            nbt.setString("PingValueColor", this.pingValueColor);
            nbt.setString("Ping200And300Color", this.ping200And300Color);
            nbt.setString("Ping300And500Color", this.ping300And500Color);
            nbt.setString("PingMax500Color", this.pingMax500Color);
            nbt.setString("ServerIPValueColor", this.serverIPValueColor);
            nbt.setString("RealTimeHHMMSSValueColor", this.realTimeHHMMSSValueColor);
            nbt.setString("RealTimeDDMMYYValueColor", this.realTimeDDMMYYValueColor);
            nbt.setString("GameTimeValueColor", this.gameTimeValueColor);
            nbt.setString("GameWeatherValueColor", this.gameWeatherValueColor);
            nbt.setString("MoonPhaseValueColor", this.moonPhaseValueColor);
            nbt.setString("PlacedSummoningEyeValueColor", this.placedSummoningEyeValueColor);
            nbt.setString("GolemStageValueColor", this.golemStageValueColor);

            // Misc
            nbt.setString("ToggleSprintUseMode", this.toggleSprintUseMode);
            nbt.setString("ToggleSneakUseMode", this.toggleSneakUseMode);

            // Hypixel
            nbt.setBoolean("RightClickToAddParty", this.rightClickToAddParty);
            nbt.setBoolean("AddPartyVisitIsland", this.addPartyVisitIsland);
            nbt.setBoolean("JungleAxeCooldown", this.jungleAxeCooldown);
            nbt.setBoolean("GrapplingHookCooldown", this.grapplingHookCooldown);
            nbt.setBoolean("ZealotRespawnCooldown", this.zealotRespawnCooldown);
            nbt.setBoolean("GlowingDragonArmor", this.glowingDragonArmor);
            nbt.setBoolean("PlacedSummoningEyeTracker", this.placedSummoningEyeTracker);
            nbt.setBoolean("ShowItemRarity", this.showItemRarity);
            nbt.setBoolean("ShowDragonHitboxOnly", this.showDragonHitboxOnly);
            nbt.setBoolean("ShowHitboxWhenDragonSpawned", this.showHitboxWhenDragonSpawned);
            nbt.setBoolean("SneakToOpenInventoryWhileFightDragon", this.sneakToOpenInventoryWhileFightDragon);
            nbt.setBoolean("LeavePartyWhenLastEyePlaced", this.leavePartyWhenLastEyePlaced);
            nbt.setBoolean("CurrentServerDay", this.currentServerDay);
            nbt.setBoolean("LobbyPlayerViewer", this.lobbyPlayerViewer);
            nbt.setBoolean("AuctionBidConfirm", this.auctionBidConfirm);
            nbt.setBoolean("SupportersFancyColor", this.supportersFancyColor);
            nbt.setBoolean("GolemStageTracker", this.golemStageTracker);
            nbt.setBoolean("BazaarOnTooltips", this.bazaarOnTooltips);
            nbt.setBoolean("IgnoreBushHitbox", this.ignoreBushHitbox);
            nbt.setBoolean("OnlyMineableHitbox", this.onlyMineableHitbox);
            nbt.setBoolean("IgnoreInteractInvisibleArmorStand", this.ignoreInteractInvisibleArmorStand);
            nbt.setBoolean("AutomaticOpenMaddox", this.automaticOpenMaddox);
            nbt.setBoolean("SneakToTradeOtherPlayerIsland", this.sneakToTradeOtherPlayerIsland);
            nbt.setBoolean("MakeSpecialZealotHeldGold", this.makeSpecialZealotHeldGold);
            nbt.setBoolean("DisableBlockParticles", this.disableBlockParticles);
            nbt.setString("HypixelNickName", this.hypixelNickName);
            nbt.setInteger("SelectedHypixelMinigame", this.selectedHypixelMinigame);
            nbt.setInteger("HypixelMinigameScrollPos", this.hypixelMinigameScrollPos);
            nbt.setInteger("ItemRarityOpacity", this.itemRarityOpacity);
            nbt.setInteger("AuctionBidConfirmValue", this.auctionBidConfirmValue);

            CompressedStreamTools.safeWrite(nbt, !profileName.equalsIgnoreCase("default") ? new File(userDir, profileName + ".dat") : ExtendedConfig.file);
        }
        catch (Exception e) {}
    }

    public static void saveProfileFile(String profileName)
    {
        File profile = new File(ExtendedConfig.userDir, "profile.txt");

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(profile), StandardCharsets.UTF_8)))
        {
            writer.println("profile:" + profileName);
            LoggerIN.info("Saving profile name!");
        }
        catch (IOException e)
        {
            LoggerIN.error("Failed to save profiles", (Throwable)e);
        }
    }

    public static void resetConfig()
    {
        ExtendedConfig.instance = new ExtendedConfig();
        ExtendedConfig.instance.save(ExtendedConfig.currentProfile);
        ClientUtils.printClientMessage(LangUtils.translate("message.reset_config", ExtendedConfig.currentProfile));
    }

    private static boolean getBoolean(NBTTagCompound nbt, String key, boolean defaultValue)
    {
        if (nbt.hasKey(key, 99))
        {
            return nbt.getBoolean(key);
        }
        else
        {
            return defaultValue;
        }
    }

    private static int getInteger(NBTTagCompound nbt, String key, int defaultValue)
    {
        if (nbt.hasKey(key, 99))
        {
            return nbt.getInteger(key);
        }
        else
        {
            return defaultValue;
        }
    }

    private static String getString(NBTTagCompound nbt, String key, String defaultValue)
    {
        if (nbt.hasKey(key, 8))
        {
            return nbt.getString(key);
        }
        else
        {
            return defaultValue;
        }
    }

    public String getKeyBinding(ExtendedConfig.Options options)
    {
        String name = LangUtils.translate(options.getTranslation()) + ": ";
        DecimalFormat format = new DecimalFormat("#,###");

        if (options.isFloat())
        {
            float value = this.getOptionFloatValue(options);
            return name + format.format(value);
        }
        else if (options.isBoolean())
        {
            boolean flag = this.getOptionOrdinalValue(options);
            return flag ? name + EnumChatFormatting.GREEN + (options == ExtendedConfig.Options.SWAP_INFO_POS ? LangUtils.translate("gui.yes") : "ON") : name + EnumChatFormatting.RED + (options == ExtendedConfig.Options.SWAP_INFO_POS ? LangUtils.translate("gui.no") : "OFF");
        }
        else if (options.isTextbox())
        {
            return this.getOptionStringValue(options);
        }
        else if (options == ExtendedConfig.Options.EQUIPMENT_DIRECTION)
        {
            return name + this.getTranslation(EQUIPMENT_DIRECTION, this.equipmentDirection);
        }
        else if (options == ExtendedConfig.Options.EQUIPMENT_STATUS)
        {
            return name + this.getTranslation(EQUIPMENT_STATUS, this.equipmentStatus);
        }
        else if (options == ExtendedConfig.Options.EQUIPMENT_POSITION)
        {
            return name + this.getTranslation(EQUIPMENT_POSITION, this.equipmentPosition);
        }
        else if (options == ExtendedConfig.Options.POTION_HUD_STYLE)
        {
            return name + this.getTranslation(POTION_STATUS_HUD_STYLE, this.potionHUDStyle);
        }
        else if (options == ExtendedConfig.Options.POTION_HUD_POSITION)
        {
            return name + this.getTranslation(POTION_STATUS_HUD_POSITION, this.potionHUDPosition);
        }
        else if (options == ExtendedConfig.Options.PING_MODE)
        {
            return name + this.getTranslation(PING_MODE, this.pingMode);
        }
        else if (options == ExtendedConfig.Options.VISIT_ISLAND_TOAST_MODE)
        {
            return name + this.getTranslation(TOAST_MODE_DISABLED, this.visitIslandToastMode);
        }
        else if (options == ExtendedConfig.Options.ITEM_DROP_TOAST_MODE)
        {
            return name + this.getTranslation(TOAST_MODE, this.itemDropToastMode);
        }
        else if (options == ExtendedConfig.Options.FISH_CATCH_TOAST_MODE)
        {
            return name + this.getTranslation(TOAST_MODE, this.fishCatchToastMode);
        }
        else if (options == ExtendedConfig.Options.GIFT_TOAST_MODE)
        {
            return name + this.getTranslation(TOAST_MODE, this.giftToastMode);
        }
        else if (options == ExtendedConfig.Options.PET_TOAST_MODE)
        {
            return name + this.getTranslation(TOAST_MODE, this.petToastMode);
        }
        else
        {
            return name;
        }
    }

    public void setOptionValue(ExtendedConfig.Options options, int value)
    {
        if (options == ExtendedConfig.Options.SWAP_INFO_POS)
        {
            this.swapRenderInfo = !this.swapRenderInfo;
        }
        else if (options == ExtendedConfig.Options.EQUIPMENT_DIRECTION)
        {
            this.equipmentDirection = (this.equipmentDirection + value) % 2;
        }
        else if (options == ExtendedConfig.Options.EQUIPMENT_STATUS)
        {
            this.equipmentStatus = (this.equipmentStatus + value) % 6;
        }
        else if (options == ExtendedConfig.Options.EQUIPMENT_POSITION)
        {
            this.equipmentPosition = (this.equipmentPosition + value) % 3;
        }
        else if (options == ExtendedConfig.Options.POTION_HUD_STYLE)
        {
            this.potionHUDStyle = (this.potionHUDStyle + value) % 2;
        }
        else if (options == ExtendedConfig.Options.POTION_HUD_POSITION)
        {
            this.potionHUDPosition = (this.potionHUDPosition + value) % 4;
        }
        else if (options == ExtendedConfig.Options.PING_MODE)
        {
            this.pingMode = (this.pingMode + value) % 2;
        }
        else if (options == ExtendedConfig.Options.VISIT_ISLAND_TOAST_MODE)
        {
            this.visitIslandToastMode = (this.visitIslandToastMode + value) % 4;
        }
        else if (options == ExtendedConfig.Options.ITEM_DROP_TOAST_MODE)
        {
            this.itemDropToastMode = (this.itemDropToastMode + value) % 3;
        }
        else if (options == ExtendedConfig.Options.FISH_CATCH_TOAST_MODE)
        {
            this.fishCatchToastMode = (this.fishCatchToastMode + value) % 3;
        }
        else if (options == ExtendedConfig.Options.GIFT_TOAST_MODE)
        {
            this.giftToastMode = (this.giftToastMode + value) % 3;
        }
        else if (options == ExtendedConfig.Options.PET_TOAST_MODE)
        {
            this.petToastMode = (this.petToastMode + value) % 3;
        }

        else if (options == ExtendedConfig.Options.FPS)
        {
            this.fps = !this.fps;
        }
        else if (options == ExtendedConfig.Options.XYZ)
        {
            this.xyz = !this.xyz;
        }
        else if (options == ExtendedConfig.Options.DIRECTION)
        {
            this.direction = !this.direction;
        }
        else if (options == ExtendedConfig.Options.BIOME)
        {
            this.biome = !this.biome;
        }
        else if (options == ExtendedConfig.Options.PING)
        {
            this.ping = !this.ping;
        }
        else if (options == ExtendedConfig.Options.PING_TO_SECOND)
        {
            this.pingToSecond = !this.pingToSecond;
        }
        else if (options == ExtendedConfig.Options.SERVER_IP)
        {
            this.serverIP = !this.serverIP;
        }
        else if (options == ExtendedConfig.Options.SERVER_IP_MC)
        {
            this.serverIPMCVersion = !this.serverIPMCVersion;
        }
        else if (options == ExtendedConfig.Options.EQUIPMENT_HUD)
        {
            this.equipmentHUD = !this.equipmentHUD;
        }
        else if (options == ExtendedConfig.Options.EQUIPMENT_ARMOR_ITEMS)
        {
            this.equipmentArmorItems = !this.equipmentArmorItems;
        }
        else if (options == ExtendedConfig.Options.EQUIPMENT_HAND_ITEMS)
        {
            this.equipmentHandItems = !this.equipmentHandItems;
        }
        else if (options == ExtendedConfig.Options.POTION_HUD)
        {
            this.potionHUD = !this.potionHUD;
        }
        else if (options == ExtendedConfig.Options.REAL_TIME)
        {
            this.realTime = !this.realTime;
        }
        else if (options == ExtendedConfig.Options.GAME_TIME)
        {
            this.gameTime = !this.gameTime;
        }
        else if (options == ExtendedConfig.Options.GAME_WEATHER)
        {
            this.gameWeather = !this.gameWeather;
        }
        else if (options == ExtendedConfig.Options.MOON_PHASE)
        {
            this.moonPhase = !this.moonPhase;
        }
        else if (options == ExtendedConfig.Options.POTION_ICON)
        {
            this.potionHUDIcon = !this.potionHUDIcon;
        }
        else if (options == ExtendedConfig.Options.ALTERNATE_POTION_COLOR)
        {
            this.alternatePotionHUDTextColor = !this.alternatePotionHUDTextColor;
        }

        else if (options == ExtendedConfig.Options.RIGHT_CLICK_ADD_PARTY)
        {
            this.rightClickToAddParty = !this.rightClickToAddParty;
        }
        else if (options == ExtendedConfig.Options.ADD_PARTY_VISIT_ISLAND)
        {
            this.addPartyVisitIsland = !this.addPartyVisitIsland;
        }
        else if (options == ExtendedConfig.Options.JUNGLE_AXE_COOLDOWN)
        {
            this.jungleAxeCooldown = !this.jungleAxeCooldown;
        }
        else if (options == ExtendedConfig.Options.GRAPPLING_HOOK_COOLDOWN)
        {
            this.grapplingHookCooldown = !this.grapplingHookCooldown;
        }
        else if (options == ExtendedConfig.Options.ZEALOT_RESPAWN_COOLDOWN)
        {
            this.zealotRespawnCooldown = !this.zealotRespawnCooldown;
        }
        else if (options == ExtendedConfig.Options.GLOWING_DRAGON_ARMOR)
        {
            this.glowingDragonArmor = !this.glowingDragonArmor;
        }
        else if (options == ExtendedConfig.Options.PLACED_SUMMONING_EYE_TRACKER)
        {
            this.placedSummoningEyeTracker = !this.placedSummoningEyeTracker;
        }
        else if (options == ExtendedConfig.Options.SHOW_ITEM_RARITY)
        {
            this.showItemRarity = !this.showItemRarity;
        }
        else if (options == ExtendedConfig.Options.SHOW_DRAGON_HITBOX_ONLY)
        {
            this.showDragonHitboxOnly = !this.showDragonHitboxOnly;
        }
        else if (options == ExtendedConfig.Options.SHOW_HITBOX_WHEN_DRAGON_SPAWNED)
        {
            this.showHitboxWhenDragonSpawned = !this.showHitboxWhenDragonSpawned;
        }
        else if (options == ExtendedConfig.Options.SNEAK_TO_OPEN_INVENTORY_WHILE_FIGHT_DRAGON)
        {
            this.sneakToOpenInventoryWhileFightDragon = !this.sneakToOpenInventoryWhileFightDragon;
        }
        else if (options == ExtendedConfig.Options.LEAVE_PARTY_WHEN_LAST_EYE_PLACED)
        {
            this.leavePartyWhenLastEyePlaced = !this.leavePartyWhenLastEyePlaced;
        }
        else if (options == ExtendedConfig.Options.CURRENT_SERVER_DAY)
        {
            this.currentServerDay = !this.currentServerDay;
        }
        else if (options == ExtendedConfig.Options.LOBBY_PLAYER_VIEWER)
        {
            this.lobbyPlayerViewer = !this.lobbyPlayerViewer;
        }
        else if (options == ExtendedConfig.Options.AUCTION_BID_CONFIRM)
        {
            this.auctionBidConfirm = !this.auctionBidConfirm;
        }
        else if (options == ExtendedConfig.Options.DISABLE_BLOCK_PARTICLES)
        {
            this.disableBlockParticles = !this.disableBlockParticles;
        }
        else if (options == ExtendedConfig.Options.SUPPORTERS_FANCY_COLOR)
        {
            this.supportersFancyColor = !this.supportersFancyColor;
        }
        else if (options == ExtendedConfig.Options.GOLEM_STAGE_TRACKER)
        {
            this.golemStageTracker = !this.golemStageTracker;
        }
        else if (options == ExtendedConfig.Options.BAZAAR_ON_TOOLTIPS)
        {
            this.bazaarOnTooltips = !this.bazaarOnTooltips;
        }
        else if (options == ExtendedConfig.Options.IGNORE_BUSH_HITBOX)
        {
            this.ignoreBushHitbox = !this.ignoreBushHitbox;
        }
        else if (options == ExtendedConfig.Options.ONLY_MINEABLE_HITBOX)
        {
            this.onlyMineableHitbox = !this.onlyMineableHitbox;
        }
        else if (options == ExtendedConfig.Options.IGNORE_INTERACT_INVISIBLE_ARMOR_STAND)
        {
            this.ignoreInteractInvisibleArmorStand = !this.ignoreInteractInvisibleArmorStand;
        }
        else if (options == ExtendedConfig.Options.AUTOMATIC_OPEN_MADDOX)
        {
            this.automaticOpenMaddox = !this.automaticOpenMaddox;
        }
        else if (options == ExtendedConfig.Options.SNEAK_TO_TRADE_OTHER_PLAYER_ISLAND)
        {
            this.sneakToTradeOtherPlayerIsland = !this.sneakToTradeOtherPlayerIsland;
        }
        else if (options == ExtendedConfig.Options.MAKE_SPECIAL_ZEALOT_HELD_GOLD)
        {
            this.makeSpecialZealotHeldGold = !this.makeSpecialZealotHeldGold;
        }
    }

    public void setOptionFloatValue(ExtendedConfig.Options options, float value)
    {
        if (options == ExtendedConfig.Options.ARMOR_HUD_Y)
        {
            this.armorHUDYOffset = (int) value;
        }
        else if (options == ExtendedConfig.Options.POTION_HUD_Y)
        {
            this.potionHUDYOffset = (int) value;
        }
        else if (options == ExtendedConfig.Options.MAXIMUM_POTION_DISPLAY)
        {
            this.maximumPotionDisplay = (int) value;
        }
        else if (options == ExtendedConfig.Options.POTION_LENGTH_Y_OFFSET)
        {
            this.potionLengthYOffset = (int) value;
        }
        else if (options == ExtendedConfig.Options.POTION_LENGTH_Y_OFFSET_OVERLAP)
        {
            this.potionLengthYOffsetOverlap = (int) value;
        }

        else if (options == ExtendedConfig.Options.ITEM_RARITY_OPACITY)
        {
            this.itemRarityOpacity = (int) value;
        }
        else if (options == ExtendedConfig.Options.AUCTION_BID_CONFIRM_VALUE)
        {
            this.auctionBidConfirmValue = (int) value;
        }
    }

    public void setOptionStringValue(ExtendedConfig.Options options, String value)
    {
        if (options == ExtendedConfig.Options.FPS_COLOR)
        {
            this.fpsColor = value;
        }
        else if (options == ExtendedConfig.Options.XYZ_COLOR)
        {
            this.xyzColor = value;
        }
        else if (options == ExtendedConfig.Options.BIOME_COLOR)
        {
            this.biomeColor = value;
        }
        else if (options == ExtendedConfig.Options.DIRECTION_COLOR)
        {
            this.directionColor = value;
        }
        else if (options == ExtendedConfig.Options.PING_COLOR)
        {
            this.pingColor = value;
        }
        else if (options == ExtendedConfig.Options.PING_TO_SECOND_COLOR)
        {
            this.pingToSecondColor = value;
        }
        else if (options == ExtendedConfig.Options.SERVER_IP_COLOR)
        {
            this.serverIPColor = value;
        }
        else if (options == ExtendedConfig.Options.EQUIPMENT_STATUS_COLOR)
        {
            this.equipmentStatusColor = value;
        }
        else if (options == ExtendedConfig.Options.ARROW_COUNT_COLOR)
        {
            this.arrowCountColor = value;
        }
        else if (options == ExtendedConfig.Options.BAIT_COUNT_COLOR)
        {
            this.baitCountColor = value;
        }
        else if (options == ExtendedConfig.Options.REAL_TIME_COLOR)
        {
            this.realTimeColor = value;
        }
        else if (options == ExtendedConfig.Options.GAME_TIME_COLOR)
        {
            this.gameTimeColor = value;
        }
        else if (options == ExtendedConfig.Options.GAME_WEATHER_COLOR)
        {
            this.gameWeatherColor = value;
        }
        else if (options == ExtendedConfig.Options.MOON_PHASE_COLOR)
        {
            this.moonPhaseColor = value;
        }
        else if (options == ExtendedConfig.Options.JUNGLE_AXE_COOLDOWN_COLOR)
        {
            this.jungleAxeCooldownColor = value;
        }
        else if (options == ExtendedConfig.Options.GRAPPLING_HOOK_COOLDOWN_COLOR)
        {
            this.grapplingHookCooldownColor = value;
        }
        else if (options == ExtendedConfig.Options.ZEALOT_RESPAWN_COOLDOWN_COLOR)
        {
            this.zealotRespawnCooldownColor = value;
        }
        else if (options == ExtendedConfig.Options.PLACED_SUMMONING_EYE_COLOR)
        {
            this.placedSummoningEyeColor = value;
        }
        else if (options == ExtendedConfig.Options.GOLEM_STAGE_COLOR)
        {
            this.golemStageColor = value;
        }

        else if (options == ExtendedConfig.Options.FPS_VALUE_COLOR)
        {
            this.fpsValueColor = value;
        }
        else if (options == ExtendedConfig.Options.FPS_26_AND_40_COLOR)
        {
            this.fps26And49Color = value;
        }
        else if (options == ExtendedConfig.Options.FPS_LOW_25_COLOR)
        {
            this.fpsLow25Color = value;
        }
        else if (options == ExtendedConfig.Options.XYZ_VALUE_COLOR)
        {
            this.xyzValueColor = value;
        }
        else if (options == ExtendedConfig.Options.DIRECTION_VALUE_COLOR)
        {
            this.directionValueColor = value;
        }
        else if (options == ExtendedConfig.Options.BIOME_VALUE_COLOR)
        {
            this.biomeValueColor = value;
        }
        else if (options == ExtendedConfig.Options.PING_VALUE_COLOR)
        {
            this.pingValueColor = value;
        }
        else if (options == ExtendedConfig.Options.PING_200_AND_300_COLOR)
        {
            this.ping200And300Color = value;
        }
        else if (options == ExtendedConfig.Options.PING_300_AND_500_COLOR)
        {
            this.ping300And500Color = value;
        }
        else if (options == ExtendedConfig.Options.PING_MAX_500_COLOR)
        {
            this.pingMax500Color = value;
        }
        else if (options == ExtendedConfig.Options.SERVER_IP_VALUE_COLOR)
        {
            this.serverIPValueColor = value;
        }
        else if (options == ExtendedConfig.Options.REAL_TIME_HHMMSS_VALUE_COLOR)
        {
            this.realTimeHHMMSSValueColor = value;
        }
        else if (options == ExtendedConfig.Options.REAL_TIME_DDMMYY_VALUE_COLOR)
        {
            this.realTimeDDMMYYValueColor = value;
        }
        else if (options == ExtendedConfig.Options.GAME_TIME_VALUE_COLOR)
        {
            this.gameTimeValueColor = value;
        }
        else if (options == ExtendedConfig.Options.GAME_WEATHER_VALUE_COLOR)
        {
            this.gameWeatherValueColor = value;
        }
        else if (options == ExtendedConfig.Options.MOON_PHASE_VALUE_COLOR)
        {
            this.moonPhaseValueColor = value;
        }
        else if (options == ExtendedConfig.Options.PLACED_SUMMONING_EYE_VALUE_COLOR)
        {
            this.placedSummoningEyeValueColor = value;
        }
        else if (options == ExtendedConfig.Options.GOLEM_STAGE_VALUE_COLOR)
        {
            this.golemStageValueColor = value;
        }
    }

    public float getOptionFloatValue(ExtendedConfig.Options settingOption)
    {
        if (settingOption == ExtendedConfig.Options.ARMOR_HUD_Y)
        {
            return this.armorHUDYOffset;
        }
        else if (settingOption == ExtendedConfig.Options.POTION_HUD_Y)
        {
            return this.potionHUDYOffset;
        }
        else if (settingOption == ExtendedConfig.Options.MAXIMUM_POTION_DISPLAY)
        {
            return this.maximumPotionDisplay;
        }
        else if (settingOption == ExtendedConfig.Options.POTION_LENGTH_Y_OFFSET)
        {
            return this.potionLengthYOffset;
        }
        else if (settingOption == ExtendedConfig.Options.POTION_LENGTH_Y_OFFSET_OVERLAP)
        {
            return this.potionLengthYOffsetOverlap;
        }

        else if (settingOption == ExtendedConfig.Options.ITEM_RARITY_OPACITY)
        {
            return this.itemRarityOpacity;
        }
        else if (settingOption == ExtendedConfig.Options.AUCTION_BID_CONFIRM_VALUE)
        {
            return this.auctionBidConfirmValue;
        }
        return 0.0F;
    }

    public boolean getOptionOrdinalValue(ExtendedConfig.Options options)
    {
        switch (options)
        {
        case SWAP_INFO_POS:
            return this.swapRenderInfo;
        case FPS:
            return this.fps;
        case XYZ:
            return this.xyz;
        case DIRECTION:
            return this.direction;
        case BIOME:
            return this.biome;
        case PING:
            return this.ping;
        case PING_TO_SECOND:
            return this.pingToSecond;
        case SERVER_IP:
            return this.serverIP;
        case SERVER_IP_MC:
            return this.serverIPMCVersion;
        case EQUIPMENT_HUD:
            return this.equipmentHUD;
        case EQUIPMENT_ARMOR_ITEMS:
            return this.equipmentArmorItems;
        case EQUIPMENT_HAND_ITEMS:
            return this.equipmentHandItems;
        case POTION_HUD:
            return this.potionHUD;
        case REAL_TIME:
            return this.realTime;
        case GAME_TIME:
            return this.gameTime;
        case GAME_WEATHER:
            return this.gameWeather;
        case MOON_PHASE:
            return this.moonPhase;
        case POTION_ICON:
            return this.potionHUDIcon;
        case ALTERNATE_POTION_COLOR:
            return this.alternatePotionHUDTextColor;

        case RIGHT_CLICK_ADD_PARTY:
            return this.rightClickToAddParty;
        case ADD_PARTY_VISIT_ISLAND:
            return this.addPartyVisitIsland;
        case JUNGLE_AXE_COOLDOWN:
            return this.jungleAxeCooldown;
        case GRAPPLING_HOOK_COOLDOWN:
            return this.grapplingHookCooldown;
        case ZEALOT_RESPAWN_COOLDOWN:
            return this.zealotRespawnCooldown;
        case GLOWING_DRAGON_ARMOR:
            return this.glowingDragonArmor;
        case PLACED_SUMMONING_EYE_TRACKER:
            return this.placedSummoningEyeTracker;
        case SHOW_ITEM_RARITY:
            return this.showItemRarity;
        case SHOW_DRAGON_HITBOX_ONLY:
            return this.showDragonHitboxOnly;
        case SHOW_HITBOX_WHEN_DRAGON_SPAWNED:
            return this.showHitboxWhenDragonSpawned;
        case SNEAK_TO_OPEN_INVENTORY_WHILE_FIGHT_DRAGON:
            return this.sneakToOpenInventoryWhileFightDragon;
        case LEAVE_PARTY_WHEN_LAST_EYE_PLACED:
            return this.leavePartyWhenLastEyePlaced;
        case CURRENT_SERVER_DAY:
            return this.currentServerDay;
        case LOBBY_PLAYER_VIEWER:
            return this.lobbyPlayerViewer;
        case AUCTION_BID_CONFIRM:
            return this.auctionBidConfirm;
        case DISABLE_BLOCK_PARTICLES:
            return this.disableBlockParticles;
        case SUPPORTERS_FANCY_COLOR:
            return this.supportersFancyColor;
        case GOLEM_STAGE_TRACKER:
            return this.golemStageTracker;
        case BAZAAR_ON_TOOLTIPS:
            return this.bazaarOnTooltips;
        case IGNORE_BUSH_HITBOX:
            return this.ignoreBushHitbox;
        case ONLY_MINEABLE_HITBOX:
            return this.onlyMineableHitbox;
        case IGNORE_INTERACT_INVISIBLE_ARMOR_STAND:
            return this.ignoreInteractInvisibleArmorStand;
        case AUTOMATIC_OPEN_MADDOX:
            return this.automaticOpenMaddox;
        case SNEAK_TO_TRADE_OTHER_PLAYER_ISLAND:
            return this.sneakToTradeOtherPlayerIsland;
        case MAKE_SPECIAL_ZEALOT_HELD_GOLD:
            return this.makeSpecialZealotHeldGold;
        default:
            return false;
        }
    }

    public String getOptionStringValue(ExtendedConfig.Options options)
    {
        switch (options)
        {
        case FPS_COLOR:
            return this.fpsColor;
        case XYZ_COLOR:
            return this.xyzColor;
        case BIOME_COLOR:
            return this.biomeColor;
        case DIRECTION_COLOR:
            return this.directionColor;
        case PING_COLOR:
            return this.pingColor;
        case PING_TO_SECOND_COLOR:
            return this.pingToSecondColor;
        case SERVER_IP_COLOR:
            return this.serverIPColor;
        case EQUIPMENT_STATUS_COLOR:
            return this.equipmentStatusColor;
        case ARROW_COUNT_COLOR:
            return this.arrowCountColor;
        case BAIT_COUNT_COLOR:
            return this.baitCountColor;
        case REAL_TIME_COLOR:
            return this.realTimeColor;
        case GAME_TIME_COLOR:
            return this.gameTimeColor;
        case GAME_WEATHER_COLOR:
            return this.gameWeatherColor;
        case MOON_PHASE_COLOR:
            return this.moonPhaseColor;
        case JUNGLE_AXE_COOLDOWN_COLOR:
            return this.jungleAxeCooldownColor;
        case GRAPPLING_HOOK_COOLDOWN_COLOR:
            return this.grapplingHookCooldownColor;
        case ZEALOT_RESPAWN_COOLDOWN_COLOR:
            return this.zealotRespawnCooldownColor;
        case PLACED_SUMMONING_EYE_COLOR:
            return this.placedSummoningEyeColor;
        case GOLEM_STAGE_COLOR:
            return this.golemStageColor;

        case FPS_VALUE_COLOR:
            return this.fpsValueColor;
        case FPS_26_AND_40_COLOR:
            return this.fps26And49Color;
        case FPS_LOW_25_COLOR:
            return this.fpsLow25Color;
        case XYZ_VALUE_COLOR:
            return this.xyzValueColor;
        case DIRECTION_VALUE_COLOR:
            return this.directionValueColor;
        case BIOME_VALUE_COLOR:
            return this.biomeValueColor;
        case PING_VALUE_COLOR:
            return this.pingValueColor;
        case PING_200_AND_300_COLOR:
            return this.ping200And300Color;
        case PING_300_AND_500_COLOR:
            return this.ping300And500Color;
        case PING_MAX_500_COLOR:
            return this.pingMax500Color;
        case SERVER_IP_VALUE_COLOR:
            return this.serverIPValueColor;
        case REAL_TIME_HHMMSS_VALUE_COLOR:
            return this.realTimeHHMMSSValueColor;
        case REAL_TIME_DDMMYY_VALUE_COLOR:
            return this.realTimeDDMMYYValueColor;
        case GAME_TIME_VALUE_COLOR:
            return this.gameTimeValueColor;
        case GAME_WEATHER_VALUE_COLOR:
            return this.gameWeatherValueColor;
        case MOON_PHASE_VALUE_COLOR:
            return this.moonPhaseValueColor;
        case PLACED_SUMMONING_EYE_VALUE_COLOR:
            return this.placedSummoningEyeValueColor;
        case GOLEM_STAGE_VALUE_COLOR:
            return this.golemStageValueColor;
        default:
            return "";
        }
    }

    private String getTranslation(String[] strArray, int index)
    {
        if (index < 0 || index >= strArray.length)
        {
            index = 0;
        }
        return LangUtils.translate(strArray[index]);
    }

    public static enum Options
    {
        SWAP_INFO_POS(false, true),
        EQUIPMENT_DIRECTION(false, false),
        EQUIPMENT_STATUS(false, false),
        EQUIPMENT_POSITION(false, false),
        POTION_HUD_STYLE(false, false),
        POTION_HUD_POSITION(false, false),
        PING_MODE(false, false),
        VISIT_ISLAND_TOAST_MODE(false, false),
        ITEM_DROP_TOAST_MODE(false, false),
        FISH_CATCH_TOAST_MODE(false, false),
        GIFT_TOAST_MODE(false, false),
        PET_TOAST_MODE(false, false),

        FPS(false, true),
        XYZ(false, true),
        DIRECTION(false, true),
        BIOME(false, true),
        PING(false, true),
        PING_TO_SECOND(false, true),
        SERVER_IP(false, true),
        SERVER_IP_MC(false, true),
        EQUIPMENT_HUD(false, true),
        EQUIPMENT_ARMOR_ITEMS(false, true),
        EQUIPMENT_HAND_ITEMS(false, true),
        POTION_HUD(false, true),
        REAL_TIME(false, true),
        GAME_TIME(false, true),
        GAME_WEATHER(false, true),
        MOON_PHASE(false, true),
        POTION_ICON(false, true),
        ALTERNATE_POTION_COLOR(false, true),

        ARMOR_HUD_Y(true, false, -512.0F, 512.0F, 1.0F),
        POTION_HUD_Y(true, false, -512.0F, 512.0F, 1.0F),
        MAXIMUM_POTION_DISPLAY(true, false, 2.0F, 8.0F, 1.0F),
        POTION_LENGTH_Y_OFFSET(true, false, 1.0F, 256.0F, 1.0F),
        POTION_LENGTH_Y_OFFSET_OVERLAP(true, false, 1.0F, 256.0F, 1.0F),

        FPS_COLOR(false, false, true),
        XYZ_COLOR(false, false, true),
        BIOME_COLOR(false, false, true),
        DIRECTION_COLOR(false, false, true),
        PING_COLOR(false, false, true),
        PING_TO_SECOND_COLOR(false, false, true),
        SERVER_IP_COLOR(false, false, true),
        EQUIPMENT_STATUS_COLOR(false, false, true),
        ARROW_COUNT_COLOR(false, false, true),
        BAIT_COUNT_COLOR(false, false, true),
        REAL_TIME_COLOR(false, false, true),
        GAME_TIME_COLOR(false, false, true),
        GAME_WEATHER_COLOR(false, false, true),
        MOON_PHASE_COLOR(false, false, true),
        JUNGLE_AXE_COOLDOWN_COLOR(false, false, true),
        GRAPPLING_HOOK_COOLDOWN_COLOR(false, false, true),
        ZEALOT_RESPAWN_COOLDOWN_COLOR(false, false, true),
        PLACED_SUMMONING_EYE_COLOR(false, false, true),
        GOLEM_STAGE_COLOR(false, false, true),

        FPS_VALUE_COLOR(false, false, true),
        FPS_26_AND_40_COLOR(false, false, true),
        FPS_LOW_25_COLOR(false, false, true),
        XYZ_VALUE_COLOR(false, false, true),
        DIRECTION_VALUE_COLOR(false, false, true),
        BIOME_VALUE_COLOR(false, false, true),
        PING_VALUE_COLOR(false, false, true),
        PING_200_AND_300_COLOR(false, false, true),
        PING_300_AND_500_COLOR(false, false, true),
        PING_MAX_500_COLOR(false, false, true),
        SERVER_IP_VALUE_COLOR(false, false, true),
        REAL_TIME_HHMMSS_VALUE_COLOR(false, false, true),
        REAL_TIME_DDMMYY_VALUE_COLOR(false, false, true),
        GAME_TIME_VALUE_COLOR(false, false, true),
        GAME_WEATHER_VALUE_COLOR(false, false, true),
        MOON_PHASE_VALUE_COLOR(false, false, true),
        PLACED_SUMMONING_EYE_VALUE_COLOR(false, false, true),
        GOLEM_STAGE_VALUE_COLOR(false, false, true),

        RIGHT_CLICK_ADD_PARTY(false, true),
        ADD_PARTY_VISIT_ISLAND(false, true),
        JUNGLE_AXE_COOLDOWN(false, true),
        GRAPPLING_HOOK_COOLDOWN(false, true),
        ZEALOT_RESPAWN_COOLDOWN(false, true),
        GLOWING_DRAGON_ARMOR(false, true),
        PLACED_SUMMONING_EYE_TRACKER(false, true),
        SHOW_ITEM_RARITY(false, true),
        SHOW_DRAGON_HITBOX_ONLY(false, true),
        SHOW_HITBOX_WHEN_DRAGON_SPAWNED(false, true),
        SNEAK_TO_OPEN_INVENTORY_WHILE_FIGHT_DRAGON(false, true),
        LEAVE_PARTY_WHEN_LAST_EYE_PLACED(false, true),
        CURRENT_SERVER_DAY(false, true),
        LOBBY_PLAYER_VIEWER(false, true),
        AUCTION_BID_CONFIRM(false, true),
        DISABLE_BLOCK_PARTICLES(false, true),
        SUPPORTERS_FANCY_COLOR(false, true),
        GOLEM_STAGE_TRACKER(false, true),
        BAZAAR_ON_TOOLTIPS(false, true),
        IGNORE_BUSH_HITBOX(false, true),
        ONLY_MINEABLE_HITBOX(false, true),
        IGNORE_INTERACT_INVISIBLE_ARMOR_STAND(false, true),
        AUTOMATIC_OPEN_MADDOX(false, true),
        SNEAK_TO_TRADE_OTHER_PLAYER_ISLAND(false, true),
        MAKE_SPECIAL_ZEALOT_HELD_GOLD(false, true),
        ITEM_RARITY_OPACITY(true, false, 1.0F, 100.0F, 1.0F),
        AUCTION_BID_CONFIRM_VALUE(true, false, 100000.0F, 20000000.0F, 100000.0F),
        ;

        private final boolean isFloat;
        private final boolean isBoolean;
        private final float valueStep;
        private boolean isTextbox;
        private float valueMin;
        private float valueMax;
        private static final Options[] values = Options.values();

        public static Options byOrdinal(int ordinal)
        {
            for (Options options : values)
            {
                if (options.getOrdinal() == ordinal)
                {
                    return options;
                }
            }
            return null;
        }

        private Options(boolean isFloat, boolean isBoolean)
        {
            this(isFloat, isBoolean, false, 0.0F, 1.0F, 0.0F);
        }

        private Options(boolean isFloat, boolean isBoolean, float valMin, float valMax, float valStep)
        {
            this(isFloat, isBoolean, false, valMin, valMax, valStep);
        }

        private Options(boolean isFloat, boolean isBoolean, boolean isTextbox)
        {
            this(isFloat, isBoolean, isTextbox, 0.0F, 1.0F, 0.0F);
        }

        private Options(boolean isFloat, boolean isBoolean, boolean isTextbox, float valMin, float valMax, float valStep)
        {
            this.isFloat = isFloat;
            this.isBoolean = isBoolean;
            this.isTextbox = isTextbox;
            this.valueMin = valMin;
            this.valueMax = valMax;
            this.valueStep = valStep;
        }

        public boolean isFloat()
        {
            return this.isFloat;
        }

        public boolean isBoolean()
        {
            return this.isBoolean;
        }

        public boolean isTextbox()
        {
            return this.isTextbox;
        }

        public int getOrdinal()
        {
            return this.ordinal();
        }

        public String getTranslation()
        {
            return LangUtils.translate(this.name().toLowerCase() + ".extended_config");
        }

        public float getValueMin()
        {
            return this.valueMin;
        }

        public float getValueMax()
        {
            return this.valueMax;
        }

        public void setValueMax(float value)
        {
            this.valueMax = value;
        }

        public float normalizeValue(float value)
        {
            return MathHelper.clamp_float((this.snapToStepClamp(value) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
        }

        public float denormalizeValue(float value)
        {
            return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(value, 0.0F, 1.0F));
        }

        public float snapToStepClamp(float value)
        {
            value = this.snapToStep(value);
            return MathHelper.clamp_float(value, this.valueMin, this.valueMax);
        }

        private float snapToStep(float value)
        {
            if (this.valueStep > 0.0F)
            {
                value = this.valueStep * Math.round(value / this.valueStep);
            }
            return value;
        }
    }
}