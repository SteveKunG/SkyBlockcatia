package com.stevekung.skyblockcatia.config;

import java.io.*;
import java.nio.charset.StandardCharsets;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.gui.config.BooleanConfigOption;
import com.stevekung.skyblockcatia.gui.config.DoubleConfigOption;
import com.stevekung.skyblockcatia.gui.config.StringConfigOption;
import com.stevekung.skyblockcatia.gui.config.TextFieldConfigOption;
import com.stevekung.skyblockcatia.utils.ToastMode;
import com.stevekung.stevekungslib.utils.GameProfileUtils;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.client.ClientUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.common.util.Constants;

public class SBExtendedConfig
{
    public static SBExtendedConfig INSTANCE = new SBExtendedConfig();
    private static final String WHITE = "255,255,255";
    public static final File INDICATIA_DIR = new File(Minecraft.getInstance().gameDir, "skyblockcatia");
    public static final File USER_DIR = new File(SBExtendedConfig.INDICATIA_DIR, GameProfileUtils.getUUID().toString());
    public static final File DEFAULT_CONFIG_FILE = new File(SBExtendedConfig.USER_DIR, "default.dat");
    public static String CURRENT_PROFILE = "";
    private static File PROFILE_FILE;

    // Custom Color
    public String axeCooldownColor = WHITE;
    public String grapplingHookCooldownColor = WHITE;
    public String zealotRespawnCooldownColor = WHITE;
    public String placedSummoningEyeColor = WHITE;

    // Custom Color : Value
    public String placedSummoningEyeValueColor = WHITE;

    // Hypixel
    public boolean axeCooldown = true;
    public boolean grapplingHookCooldown = true;
    public boolean zealotRespawnCooldown = false;
    public boolean glowingDragonArmor = true;
    public boolean placedSummoningEyeTracker = false;
    public boolean showItemRarity = true;
    public boolean showDragonHitboxOnly = false;
    public boolean showHitboxWhenDragonSpawned = false;
    public boolean sneakToOpenInventoryWhileFightDragon = false;
    public boolean leavePartyWhenLastEyePlaced = false;
    public boolean currentServerDay = true;
    public boolean lobbyPlayerViewer = true;
    public boolean auctionBidConfirm = false;
    public boolean disableBlockParticles = false;
    public boolean supportersFancyColor = true;
    public boolean bazaarOnItemTooltip = true;
    public boolean ignoreBushHitbox = false;
    public boolean onlyMineableHitbox = false;
    public boolean ignoreInteractInvisibleArmorStand = true;
    public boolean automaticOpenMaddox = false;
    public boolean sneakToTradeOtherPlayerIsland = true;
    public boolean makeSpecialZealotHeldGold = true;
    public boolean lobbyPlayerCount = true;
    public boolean displayItemAbilityMaxUsed = false;
    public boolean preventScrollHotbarWhileFightDragon = false;
    public boolean preventClickingOnDummyItem = true;
    public boolean shortcutButtonInInventory = true;
    public boolean showObtainedDate = true;

    public ToastMode visitIslandDisplayMode = ToastMode.CHAT_AND_TOAST;
    public ToastMode itemLogDisplayMode = ToastMode.CHAT_AND_TOAST;
    public ToastMode fishCatchDisplayMode = ToastMode.CHAT_AND_TOAST;
    public ToastMode giftDisplayMode = ToastMode.CHAT_AND_TOAST;
    public ToastMode petDisplayMode = ToastMode.CHAT_AND_TOAST;

    public int itemRarityOpacity = 75;
    public int auctionBidConfirmValue = 500000;

    public int visitIslandToastTime = 8;//TODO
    public int rareDropToastTime = 10;
    public int specialDropToastTime = 15;
    public int fishCatchToastTime = 10;
    public int giftToastTime = 5;
    public int petToastTime = 10;

    public static final DoubleConfigOption ITEM_RARITY_OPACITY = new DoubleConfigOption("item_rarity_opacity", 1.0D, 100.0D, 1.0F, config -> (double)config.itemRarityOpacity, (config, value) -> config.itemRarityOpacity = value.intValue(), (config, doubleOpt) -> doubleOpt.getDisplayPrefix() + (int)doubleOpt.get());
    public static final DoubleConfigOption AUCTION_BID_CONFIRM_VALUE = new DoubleConfigOption("auction_bid_confirm_value", 100000.0D, 20000000.0D, 100000.0F, config -> (double)config.auctionBidConfirmValue, (config, value) -> config.auctionBidConfirmValue = value.intValue(), (config, doubleOpt) -> doubleOpt.getDisplayPrefix() + (int)doubleOpt.get());


    public static final BooleanConfigOption AXE_COOLDOWN = new BooleanConfigOption("axe_cooldown", config -> config.axeCooldown, (config, value) -> config.axeCooldown = value);
    public static final BooleanConfigOption GRAPPLING_HOOK_COOLDOWN = new BooleanConfigOption("grappling_hook_cooldown", config -> config.grapplingHookCooldown, (config, value) -> config.grapplingHookCooldown = value);
    public static final BooleanConfigOption ZEALOT_RESPAWN_COOLDOWN = new BooleanConfigOption("zealot_respawn_cooldown", config -> config.zealotRespawnCooldown, (config, value) -> config.zealotRespawnCooldown = value);
    public static final BooleanConfigOption GLOWING_DRAGON_ARMOR = new BooleanConfigOption("glowing_dragon_armor", config -> config.glowingDragonArmor, (config, value) -> config.glowingDragonArmor = value);
    public static final BooleanConfigOption PLACED_SUMMONING_EYE_TRACKER = new BooleanConfigOption("placed_summoning_eye_tracker", config -> config.placedSummoningEyeTracker, (config, value) -> config.placedSummoningEyeTracker = value);
    public static final BooleanConfigOption ITEM_RARITY = new BooleanConfigOption("item_rarity", config -> config.showItemRarity, (config, value) -> config.showItemRarity = value);
    public static final BooleanConfigOption DRAGON_HITBOX_ONLY = new BooleanConfigOption("dragon_hitbox_only", config -> config.showDragonHitboxOnly, (config, value) -> config.showDragonHitboxOnly = value);
    public static final BooleanConfigOption SHOW_HITBOX_WHEN_DRAGON_SPAWNED = new BooleanConfigOption("show_hitbox_when_dragon_spawned", config -> config.showHitboxWhenDragonSpawned, (config, value) -> config.showHitboxWhenDragonSpawned = value);
    public static final BooleanConfigOption SNEAK_TO_OPEN_INVENTORY = new BooleanConfigOption("sneak_to_open_inventory", config -> config.sneakToOpenInventoryWhileFightDragon, (config, value) -> config.sneakToOpenInventoryWhileFightDragon = value);
    public static final BooleanConfigOption LEAVE_PARTY_WHEN_LAST_EYE_PLACED = new BooleanConfigOption("leave_party_when_last_eye_placed", config -> config.leavePartyWhenLastEyePlaced, (config, value) -> config.leavePartyWhenLastEyePlaced = value);
    public static final BooleanConfigOption CURRENT_SERVER_DAY = new BooleanConfigOption("current_server_day", config -> config.currentServerDay, (config, value) -> config.currentServerDay = value);
    public static final BooleanConfigOption PLAYER_COUNT_LOBBY_VIEWER = new BooleanConfigOption("player_count_lobby_viewer", config -> config.lobbyPlayerViewer, (config, value) -> config.lobbyPlayerViewer = value);
    public static final BooleanConfigOption AUCTION_BID_CONFIRM = new BooleanConfigOption("auction_bid_confirm", config -> config.auctionBidConfirm, (config, value) -> config.auctionBidConfirm = value);
    public static final BooleanConfigOption DISABLE_BLOCK_PARTICLES = new BooleanConfigOption("disable_block_particles", config -> config.disableBlockParticles, (config, value) -> config.disableBlockParticles = value);
    public static final BooleanConfigOption SUPPORTERS_FANCY_COLOR = new BooleanConfigOption("supporters_fancy_color", config -> config.supportersFancyColor, (config, value) -> config.supportersFancyColor = value);
    public static final BooleanConfigOption BAZAAR_ON_ITEM_TOOLTIP = new BooleanConfigOption("bazaar_on_item_tooltip", config -> config.bazaarOnItemTooltip, (config, value) -> config.bazaarOnItemTooltip = value);
    public static final BooleanConfigOption IGNORE_BUSH_HITBOX = new BooleanConfigOption("ignore_bush_hitbox", config -> config.ignoreBushHitbox, (config, value) -> config.ignoreBushHitbox = value);
    public static final BooleanConfigOption ONLY_MINEABLE_HITBOX = new BooleanConfigOption("only_mineable_hitbox", config -> config.onlyMineableHitbox, (config, value) -> config.onlyMineableHitbox = value);
    public static final BooleanConfigOption IGNORE_INTERACT_INVISIBLE_ARMOR_STAND = new BooleanConfigOption("ignore_interact_invisible_armor_stand", config -> config.ignoreInteractInvisibleArmorStand, (config, value) -> config.ignoreInteractInvisibleArmorStand = value);
    public static final BooleanConfigOption AUTOMATIC_OPEN_MADDOX = new BooleanConfigOption("automatic_open_maddox", config -> config.automaticOpenMaddox, (config, value) -> config.automaticOpenMaddox = value);
    public static final BooleanConfigOption SNEAK_TO_TRADE_OTHER_PLAYER_ISLAND = new BooleanConfigOption("sneak_to_trade_other_player_island", config -> config.sneakToTradeOtherPlayerIsland, (config, value) -> config.sneakToTradeOtherPlayerIsland = value);
    public static final BooleanConfigOption MAKE_SPECIAL_ZEALOT_HELD_GOLD = new BooleanConfigOption("make_special_zealot_held_gold", config -> config.makeSpecialZealotHeldGold, (config, value) -> config.makeSpecialZealotHeldGold = value);
    public static final BooleanConfigOption LOBBY_PLAYER_COUNT  = new BooleanConfigOption("lobby_player_count", config -> config.lobbyPlayerCount, (config, value) -> config.lobbyPlayerCount = value);
    public static final BooleanConfigOption DISPLAY_ITEM_ABILITY_MAX_USED  = new BooleanConfigOption("display_item_ability_max_used", config -> config.displayItemAbilityMaxUsed, (config, value) -> config.displayItemAbilityMaxUsed = value);
    public static final BooleanConfigOption PREVENT_SCROLL_HOTBAR_WHILE_FIGHT_DRAGON  = new BooleanConfigOption("prevent_scroll_hotbar_while_fight_dragon", config -> config.preventScrollHotbarWhileFightDragon, (config, value) -> config.preventScrollHotbarWhileFightDragon = value);
    public static final BooleanConfigOption PREVENT_CLICKING_ON_DUMMY_ITEM  = new BooleanConfigOption("prevent_clicking_on_dummy_item", config -> config.preventClickingOnDummyItem, (config, value) -> config.preventClickingOnDummyItem = value);
    public static final BooleanConfigOption SHORTCUT_BUTTON_IN_INVENTORY  = new BooleanConfigOption("shortcut_button_in_inventory", config -> config.shortcutButtonInInventory, (config, value) -> config.shortcutButtonInInventory = value);
    public static final BooleanConfigOption SHOW_OBTAINED_DATE  = new BooleanConfigOption("show_obtained_date", config -> config.showObtainedDate, (config, value) -> config.showObtainedDate = value);

    public static final StringConfigOption VISIT_ISLAND_DISPLAY_MODE = new StringConfigOption("visit_island_display_mode", (config, value) -> config.visitIslandDisplayMode = ToastMode.byId(config.visitIslandDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getDisplayPrefix() + LangUtils.translate(config.visitIslandDisplayMode.getTranslationKey()));
    public static final StringConfigOption ITEM_LOG_DISPLAY_MODE = new StringConfigOption("item_log_display_mode", (config, value) -> config.itemLogDisplayMode = ToastMode.byId(config.itemLogDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getDisplayPrefix() + LangUtils.translate(config.itemLogDisplayMode.getTranslationKey()));
    public static final StringConfigOption FISH_CATCH_DISPLAY_MODE = new StringConfigOption("fish_catch_display_mode", (config, value) -> config.fishCatchDisplayMode = ToastMode.byId(config.fishCatchDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getDisplayPrefix() + LangUtils.translate(config.fishCatchDisplayMode.getTranslationKey()));
    public static final StringConfigOption GIFT_DISPLAY_MODE = new StringConfigOption("gift_display_mode", (config, value) -> config.giftDisplayMode = ToastMode.byId(config.giftDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getDisplayPrefix() + LangUtils.translate(config.giftDisplayMode.getTranslationKey()));
    public static final StringConfigOption PET_DISPLAY_MODE = new StringConfigOption("pet_display_mode", (config, value) -> config.petDisplayMode = ToastMode.byId(config.petDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getDisplayPrefix() + LangUtils.translate(config.petDisplayMode.getTranslationKey()));


    public static final TextFieldConfigOption AXE_COOLDOWN_COLOR = new TextFieldConfigOption("axe_cooldown_color", config -> config.axeCooldownColor, (config, value) -> config.axeCooldownColor = value);
    public static final TextFieldConfigOption GRAPPLING_HOOK_COOLDOWN_COLOR = new TextFieldConfigOption("grappling_hook_cooldown_color", config -> config.grapplingHookCooldownColor, (config, value) -> config.grapplingHookCooldownColor = value);
    public static final TextFieldConfigOption ZEALOT_RESPAWN_COOLDOWN_COLOR = new TextFieldConfigOption("zealot_respawn_cooldown_color", config -> config.zealotRespawnCooldownColor, (config, value) -> config.zealotRespawnCooldownColor = value);
    public static final TextFieldConfigOption PLACED_SUMMONING_EYE_COLOR = new TextFieldConfigOption("placed_summoning_eye_color", config -> config.placedSummoningEyeColor, (config, value) -> config.placedSummoningEyeColor = value);


    public static final TextFieldConfigOption PLACED_SUMMONING_EYE_VALUE_COLOR = new TextFieldConfigOption("placed_summoning_eye_value_color", config -> config.placedSummoningEyeValueColor, (config, value) -> config.placedSummoningEyeValueColor = value);

    private SBExtendedConfig() {}

    public static void setCurrentProfile(String profileName)
    {
        SBExtendedConfig.PROFILE_FILE = new File(USER_DIR, profileName + ".dat");
        SBExtendedConfig.CURRENT_PROFILE = profileName;
    }

    public void load()
    {
        try
        {
            CompoundNBT nbt = CompressedStreamTools.read(SBExtendedConfig.PROFILE_FILE);

            if (nbt == null)
            {
                return;
            }

            // Hypixel
            this.axeCooldown = this.getBoolean(nbt, "AxeCooldown", this.axeCooldown);
            this.grapplingHookCooldown = this.getBoolean(nbt, "GrapplingHookCooldown", this.grapplingHookCooldown);
            this.zealotRespawnCooldown = this.getBoolean(nbt, "ZealotRespawnCooldown", this.zealotRespawnCooldown);
            this.glowingDragonArmor = this.getBoolean(nbt, "GlowingDragonArmor", this.glowingDragonArmor);
            this.placedSummoningEyeTracker = this.getBoolean(nbt, "PlacedSummoningEyeTracker", this.placedSummoningEyeTracker);
            this.showItemRarity = this.getBoolean(nbt, "ShowItemRarity", this.showItemRarity);
            this.showDragonHitboxOnly = this.getBoolean(nbt, "ShowDragonHitboxOnly", this.showDragonHitboxOnly);
            this.showHitboxWhenDragonSpawned = this.getBoolean(nbt, "ShowHitboxWhenDragonSpawned", this.showHitboxWhenDragonSpawned);
            this.sneakToOpenInventoryWhileFightDragon = this.getBoolean(nbt, "SneakToOpenInventoryWhileFightDragon", this.sneakToOpenInventoryWhileFightDragon);
            this.leavePartyWhenLastEyePlaced = this.getBoolean(nbt, "LeavePartyWhenLastEyePlaced", this.leavePartyWhenLastEyePlaced);
            this.currentServerDay = this.getBoolean(nbt, "CurrentServerDay", this.currentServerDay);
            this.lobbyPlayerViewer = this.getBoolean(nbt, "LobbyPlayerViewer", this.lobbyPlayerViewer);
            this.auctionBidConfirm = this.getBoolean(nbt, "AuctionBidConfirm", this.auctionBidConfirm);
            this.disableBlockParticles = this.getBoolean(nbt, "DisableBlockParticles", this.disableBlockParticles);
            this.supportersFancyColor = this.getBoolean(nbt, "SupportersFancyColor", this.supportersFancyColor);
            this.bazaarOnItemTooltip = this.getBoolean(nbt, "BazaarOnItemTooltip", this.bazaarOnItemTooltip);
            this.ignoreBushHitbox = this.getBoolean(nbt, "IgnoreBushHitbox", this.ignoreBushHitbox);
            this.onlyMineableHitbox = this.getBoolean(nbt, "OnlyMineableHitbox", this.onlyMineableHitbox);
            this.ignoreInteractInvisibleArmorStand = this.getBoolean(nbt, "IgnoreInteractInvisibleArmorStand", this.ignoreInteractInvisibleArmorStand);
            this.automaticOpenMaddox = this.getBoolean(nbt, "AutomaticOpenMaddox", this.automaticOpenMaddox);
            this.sneakToTradeOtherPlayerIsland = this.getBoolean(nbt, "SneakToTradeOtherPlayerIsland", this.sneakToTradeOtherPlayerIsland);
            this.makeSpecialZealotHeldGold = this.getBoolean(nbt, "MakeSpecialZealotHeldGold", this.makeSpecialZealotHeldGold);
            this.lobbyPlayerCount = this.getBoolean(nbt, "LobbyPlayerCount", this.lobbyPlayerCount);
            this.displayItemAbilityMaxUsed = this.getBoolean(nbt, "DisplayItemAbilityMaxUsed", this.displayItemAbilityMaxUsed);
            this.preventScrollHotbarWhileFightDragon = this.getBoolean(nbt, "PreventScrollHotbarWhileFightDragon", this.preventScrollHotbarWhileFightDragon);
            this.preventClickingOnDummyItem = this.getBoolean(nbt, "PreventClickingOnDummyItem", this.preventClickingOnDummyItem);
            this.shortcutButtonInInventory = this.getBoolean(nbt, "ShortcutButtonInInventory", this.shortcutButtonInInventory);
            this.showObtainedDate = this.getBoolean(nbt, "ShowObtainedDate", this.showObtainedDate);

            this.itemRarityOpacity = this.getInteger(nbt, "ItemRarityOpacity", this.itemRarityOpacity);
            this.auctionBidConfirmValue = this.getInteger(nbt, "AuctionBidConfirmValue", this.auctionBidConfirmValue);

            this.visitIslandDisplayMode = ToastMode.byId(this.getInteger(nbt, "VisitIslandDisplayMode", this.visitIslandDisplayMode.getId()));
            this.itemLogDisplayMode = ToastMode.byId(this.getInteger(nbt, "ItemLogDisplayMode", this.itemLogDisplayMode.getId()));
            this.fishCatchDisplayMode = ToastMode.byId(this.getInteger(nbt, "FishCatchDisplayMode", this.fishCatchDisplayMode.getId()));
            this.giftDisplayMode = ToastMode.byId(this.getInteger(nbt, "GiftDisplayMode", this.giftDisplayMode.getId()));
            this.petDisplayMode = ToastMode.byId(this.getInteger(nbt, "PetDisplayMode", this.petDisplayMode.getId()));

            // Custom Color
            this.axeCooldownColor = this.getString(nbt, "AxeCooldownColor", this.axeCooldownColor);
            this.grapplingHookCooldownColor = this.getString(nbt, "GrapplingHookCooldownColor", this.grapplingHookCooldownColor);
            this.zealotRespawnCooldownColor = this.getString(nbt, "ZealotRespawnCooldownColor", this.zealotRespawnCooldownColor);
            this.placedSummoningEyeColor = this.getString(nbt, "PlacedSummoningEyeColor", this.placedSummoningEyeColor);

            // Custom Color : Value
            this.placedSummoningEyeValueColor = this.getString(nbt, "PlacedSummoningEyeValueColor", this.placedSummoningEyeValueColor);

            SkyBlockcatiaMod.LOGGER.info("Loading extended config {}", SBExtendedConfig.PROFILE_FILE.getPath());
        }
        catch (Exception e) {}
    }

    public void save()
    {
        this.save(!SBExtendedConfig.CURRENT_PROFILE.isEmpty() ? SBExtendedConfig.CURRENT_PROFILE : "default");
    }

    public void save(String profileName)
    {
        try
        {
            CompoundNBT nbt = new CompoundNBT();

            // Hypixel
            nbt.putBoolean("AxeCooldown", this.axeCooldown);
            nbt.putBoolean("GrapplingHookCooldown", this.grapplingHookCooldown);
            nbt.putBoolean("ZealotRespawnCooldown", this.zealotRespawnCooldown);
            nbt.putBoolean("GlowingDragonArmor", this.glowingDragonArmor);
            nbt.putBoolean("PlacedSummoningEyeTracker", this.placedSummoningEyeTracker);
            nbt.putBoolean("ShowItemRarity", this.showItemRarity);
            nbt.putBoolean("ShowDragonHitboxOnly", this.showDragonHitboxOnly);
            nbt.putBoolean("ShowHitboxWhenDragonSpawned", this.showHitboxWhenDragonSpawned);
            nbt.putBoolean("SneakToOpenInventoryWhileFightDragon", this.sneakToOpenInventoryWhileFightDragon);
            nbt.putBoolean("LeavePartyWhenLastEyePlaced", this.leavePartyWhenLastEyePlaced);
            nbt.putBoolean("CurrentServerDay", this.currentServerDay);
            nbt.putBoolean("LobbyPlayerViewer", this.lobbyPlayerViewer);
            nbt.putBoolean("AuctionBidConfirm", this.auctionBidConfirm);
            nbt.putBoolean("DisableBlockParticles", this.disableBlockParticles);
            nbt.putBoolean("SupportersFancyColor", this.supportersFancyColor);
            nbt.putBoolean("BazaarOnItemTooltip", this.bazaarOnItemTooltip);
            nbt.putBoolean("IgnoreBushHitbox", this.ignoreBushHitbox);
            nbt.putBoolean("OnlyMineableHitbox", this.onlyMineableHitbox);
            nbt.putBoolean("IgnoreInteractInvisibleArmorStand", this.ignoreInteractInvisibleArmorStand);
            nbt.putBoolean("AutomaticOpenMaddox", this.automaticOpenMaddox);
            nbt.putBoolean("SneakToTradeOtherPlayerIsland", this.sneakToTradeOtherPlayerIsland);
            nbt.putBoolean("MakeSpecialZealotHeldGold", this.makeSpecialZealotHeldGold);
            nbt.putBoolean("LobbyPlayerCount", this.lobbyPlayerCount);
            nbt.putBoolean("DisplayItemAbilityMaxUsed", this.displayItemAbilityMaxUsed);
            nbt.putBoolean("PreventScrollHotbarWhileFightDragon", this.preventScrollHotbarWhileFightDragon);
            nbt.putBoolean("PreventClickingOnDummyItem", this.preventClickingOnDummyItem);
            nbt.putBoolean("ShortcutButtonInInventory", this.shortcutButtonInInventory);
            nbt.putBoolean("ShowObtainedDate", this.showObtainedDate);

            nbt.putInt("VisitIslandDisplayMode", this.visitIslandDisplayMode.getId());
            nbt.putInt("ItemLogDisplayMode", this.itemLogDisplayMode.getId());
            nbt.putInt("FishCatchDisplayMode", this.fishCatchDisplayMode.getId());
            nbt.putInt("GiftDisplayMode", this.giftDisplayMode.getId());
            nbt.putInt("PetDisplayMode", this.petDisplayMode.getId());

            nbt.putInt("ItemRarityOpacity", this.itemRarityOpacity);
            nbt.putInt("AuctionBidConfirmValue", this.auctionBidConfirmValue);

            // Custom Color
            nbt.putString("AxeCooldownColor", this.axeCooldownColor);
            nbt.putString("GrapplingHookCooldownColor", this.grapplingHookCooldownColor);
            nbt.putString("ZealotRespawnCooldownColor", this.zealotRespawnCooldownColor);
            nbt.putString("PlacedSummoningEyeColor", this.placedSummoningEyeColor);

            // Custom Color : Value
            nbt.putString("PlacedSummoningEyeValueColor", this.placedSummoningEyeValueColor);

            CompressedStreamTools.write(nbt, !profileName.equalsIgnoreCase("default") ? new File(USER_DIR, profileName + ".dat") : SBExtendedConfig.PROFILE_FILE);
        }
        catch (Exception e) {}
    }

    public static void saveProfileFile(String profileName)
    {
        File profile = new File(SBExtendedConfig.USER_DIR, "profile.txt");

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(profile), StandardCharsets.UTF_8)))
        {
            writer.println("profile:" + profileName);
            SkyBlockcatiaMod.LOGGER.info("Saving profile name!");
        }
        catch (IOException e)
        {
            SkyBlockcatiaMod.LOGGER.error("Failed to save profile", (Throwable)e);
        }
    }

    public static void resetConfig()
    {
        SBExtendedConfig.INSTANCE = new SBExtendedConfig();
        SBExtendedConfig.INSTANCE.save(SBExtendedConfig.CURRENT_PROFILE);
        ClientUtils.printClientMessage(LangUtils.translate("misc.extended_config.reset_config", SBExtendedConfig.CURRENT_PROFILE));
    }

    private boolean getBoolean(CompoundNBT nbt, String key, boolean defaultValue)
    {
        if (nbt.contains(key, Constants.NBT.TAG_ANY_NUMERIC))
        {
            return nbt.getBoolean(key);
        }
        else
        {
            return defaultValue;
        }
    }

    private int getInteger(CompoundNBT nbt, String key, int defaultValue)
    {
        if (nbt.contains(key, Constants.NBT.TAG_ANY_NUMERIC))
        {
            return nbt.getInt(key);
        }
        else
        {
            return defaultValue;
        }
    }

    private String getString(CompoundNBT nbt, String key, String defaultValue)
    {
        if (nbt.contains(key, Constants.NBT.TAG_STRING))
        {
            return nbt.getString(key);
        }
        else
        {
            return defaultValue;
        }
    }
}