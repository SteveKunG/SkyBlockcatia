package com.stevekung.skyblockcatia.config;

import java.io.*;
import java.nio.charset.StandardCharsets;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.utils.PlayerCountMode;
import com.stevekung.skyblockcatia.utils.ToastMode;
import com.stevekung.stevekungslib.utils.GameProfileUtils;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.client.ClientUtils;
import com.stevekung.stevekungslib.utils.config.*;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.common.util.Constants;

public class SkyBlockcatiaSettings extends Settings
{
    public static SkyBlockcatiaSettings INSTANCE = new SkyBlockcatiaSettings();
    private static final String WHITE = "255,255,255";
    public static final File INDICATIA_DIR = new File(Minecraft.getInstance().gameDir, "skyblockcatia");
    public static final File USER_DIR = new File(SkyBlockcatiaSettings.INDICATIA_DIR, GameProfileUtils.getUUID().toString());
    public static final File DEFAULT_CONFIG_FILE = new File(SkyBlockcatiaSettings.USER_DIR, "default.dat");
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
    public boolean fixSkyblockEnchantTag = true;

    public ToastMode visitIslandDisplayMode = ToastMode.CHAT_AND_TOAST;
    public ToastMode itemLogDisplayMode = ToastMode.CHAT_AND_TOAST;
    public ToastMode fishCatchDisplayMode = ToastMode.CHAT_AND_TOAST;
    public ToastMode giftDisplayMode = ToastMode.CHAT_AND_TOAST;
    public ToastMode petDisplayMode = ToastMode.CHAT_AND_TOAST;
    public PlayerCountMode playerCountMode = PlayerCountMode.TAB_LIST;

    public int itemRarityOpacity = 75;
    public int auctionBidConfirmValue = 500000;

    public int visitIslandToastTime = 8;
    public int rareDropToastTime = 10;
    public int specialDropToastTime = 15;
    public int fishCatchToastTime = 10;
    public int giftToastTime = 5;
    public int petToastTime = 10;

    public static final SliderPercentageSettings<SkyBlockcatiaSettings> ITEM_RARITY_OPACITY = new SliderPercentageSettings<>("skyblockcatia_setting.item_rarity_opacity", 1.0D, 100.0D, 1.0F, config -> (double)config.itemRarityOpacity, (config, value) -> config.itemRarityOpacity = value.intValue(), (setting, doubleOpt) -> doubleOpt.getMessageWithValue((int)doubleOpt.get(setting)));
    public static final SliderPercentageSettings<SkyBlockcatiaSettings> AUCTION_BID_CONFIRM_VALUE = new SliderPercentageSettings<>("skyblockcatia_setting.auction_bid_confirm_value", 100000.0D, 20000000.0D, 100000.0F, config -> (double)config.auctionBidConfirmValue, (config, value) -> config.auctionBidConfirmValue = value.intValue(), (setting, doubleOpt) -> doubleOpt.getMessageWithValue((int)doubleOpt.get(setting)));
    public static final SliderPercentageSettings<SkyBlockcatiaSettings> VISIT_ISLAND_TOAST_TIME = new SliderPercentageSettings<>("skyblockcatia_setting.visit_island_toast_time", 5.0D, 20.0D, 1.0F, config -> (double)config.visitIslandToastTime, (config, value) -> config.visitIslandToastTime = value.intValue(), (setting, doubleOpt) -> doubleOpt.getMessageWithValue((int)doubleOpt.get(setting)));
    public static final SliderPercentageSettings<SkyBlockcatiaSettings> RARE_DROP_TOAST_TIME = new SliderPercentageSettings<>("skyblockcatia_setting.rare_drop_toast_time", 5.0D, 20.0D, 1.0F, config -> (double)config.rareDropToastTime, (config, value) -> config.rareDropToastTime = value.intValue(), (setting, doubleOpt) -> doubleOpt.getMessageWithValue((int)doubleOpt.get(setting)));
    public static final SliderPercentageSettings<SkyBlockcatiaSettings> SPECIAL_DROP_TOAST_TIME = new SliderPercentageSettings<>("skyblockcatia_setting.special_drop_toast_time", 5.0D, 20.0D, 1.0F, config -> (double)config.specialDropToastTime, (config, value) -> config.specialDropToastTime = value.intValue(), (setting, doubleOpt) -> doubleOpt.getMessageWithValue((int)doubleOpt.get(setting)));
    public static final SliderPercentageSettings<SkyBlockcatiaSettings> FISH_CATCH_TOAST_TIME = new SliderPercentageSettings<>("skyblockcatia_setting.fish_catch_toast_time", 5.0D, 20.0D, 1.0F, config -> (double)config.fishCatchToastTime, (config, value) -> config.fishCatchToastTime = value.intValue(), (setting, doubleOpt) -> doubleOpt.getMessageWithValue((int)doubleOpt.get(setting)));
    public static final SliderPercentageSettings<SkyBlockcatiaSettings> GIFT_TOAST_TIME = new SliderPercentageSettings<>("skyblockcatia_setting.gift_toast_time", 5.0D, 20.0D, 1.0F, config -> (double)config.giftToastTime, (config, value) -> config.giftToastTime = value.intValue(), (setting, doubleOpt) -> doubleOpt.getMessageWithValue((int)doubleOpt.get(setting)));
    public static final SliderPercentageSettings<SkyBlockcatiaSettings> PET_TOAST_TIME = new SliderPercentageSettings<>("skyblockcatia_setting.pet_toast_time", 5.0D, 20.0D, 1.0F, config -> (double)config.petToastTime, (config, value) -> config.petToastTime = value.intValue(), (setting, doubleOpt) -> doubleOpt.getMessageWithValue((int)doubleOpt.get(setting)));


    public static final BooleanSettings<SkyBlockcatiaSettings> AXE_COOLDOWN = new BooleanSettings<>("skyblockcatia_setting.axe_cooldown", config -> config.axeCooldown, (config, value) -> config.axeCooldown = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> GRAPPLING_HOOK_COOLDOWN = new BooleanSettings<>("skyblockcatia_setting.grappling_hook_cooldown", config -> config.grapplingHookCooldown, (config, value) -> config.grapplingHookCooldown = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> ZEALOT_RESPAWN_COOLDOWN = new BooleanSettings<>("skyblockcatia_setting.zealot_respawn_cooldown", config -> config.zealotRespawnCooldown, (config, value) -> config.zealotRespawnCooldown = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> GLOWING_DRAGON_ARMOR = new BooleanSettings<>("skyblockcatia_setting.glowing_dragon_armor", config -> config.glowingDragonArmor, (config, value) -> config.glowingDragonArmor = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> PLACED_SUMMONING_EYE_TRACKER = new BooleanSettings<>("skyblockcatia_setting.placed_summoning_eye_tracker", config -> config.placedSummoningEyeTracker, (config, value) -> config.placedSummoningEyeTracker = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> ITEM_RARITY = new BooleanSettings<>("skyblockcatia_setting.item_rarity", config -> config.showItemRarity, (config, value) -> config.showItemRarity = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> DRAGON_HITBOX_ONLY = new BooleanSettings<>("skyblockcatia_setting.dragon_hitbox_only", config -> config.showDragonHitboxOnly, (config, value) -> config.showDragonHitboxOnly = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> SHOW_HITBOX_WHEN_DRAGON_SPAWNED = new BooleanSettings<>("skyblockcatia_setting.show_hitbox_when_dragon_spawned", config -> config.showHitboxWhenDragonSpawned, (config, value) -> config.showHitboxWhenDragonSpawned = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> SNEAK_TO_OPEN_INVENTORY = new BooleanSettings<>("skyblockcatia_setting.sneak_to_open_inventory", config -> config.sneakToOpenInventoryWhileFightDragon, (config, value) -> config.sneakToOpenInventoryWhileFightDragon = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> LEAVE_PARTY_WHEN_LAST_EYE_PLACED = new BooleanSettings<>("skyblockcatia_setting.leave_party_when_last_eye_placed", config -> config.leavePartyWhenLastEyePlaced, (config, value) -> config.leavePartyWhenLastEyePlaced = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> PLAYER_COUNT_LOBBY_VIEWER = new BooleanSettings<>("skyblockcatia_setting.player_count_lobby_viewer", config -> config.lobbyPlayerViewer, (config, value) -> config.lobbyPlayerViewer = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> AUCTION_BID_CONFIRM = new BooleanSettings<>("skyblockcatia_setting.auction_bid_confirm", config -> config.auctionBidConfirm, (config, value) -> config.auctionBidConfirm = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> DISABLE_BLOCK_PARTICLES = new BooleanSettings<>("skyblockcatia_setting.disable_block_particles", config -> config.disableBlockParticles, (config, value) -> config.disableBlockParticles = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> SUPPORTERS_FANCY_COLOR = new BooleanSettings<>("skyblockcatia_setting.supporters_fancy_color", config -> config.supportersFancyColor, (config, value) -> config.supportersFancyColor = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> BAZAAR_ON_ITEM_TOOLTIP = new BooleanSettings<>("skyblockcatia_setting.bazaar_on_item_tooltip", config -> config.bazaarOnItemTooltip, (config, value) -> config.bazaarOnItemTooltip = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> IGNORE_BUSH_HITBOX = new BooleanSettings<>("skyblockcatia_setting.ignore_bush_hitbox", config -> config.ignoreBushHitbox, (config, value) -> config.ignoreBushHitbox = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> ONLY_MINEABLE_HITBOX = new BooleanSettings<>("skyblockcatia_setting.only_mineable_hitbox", config -> config.onlyMineableHitbox, (config, value) -> config.onlyMineableHitbox = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> IGNORE_INTERACT_INVISIBLE_ARMOR_STAND = new BooleanSettings<>("skyblockcatia_setting.ignore_interact_invisible_armor_stand", config -> config.ignoreInteractInvisibleArmorStand, (config, value) -> config.ignoreInteractInvisibleArmorStand = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> AUTOMATIC_OPEN_MADDOX = new BooleanSettings<>("skyblockcatia_setting.automatic_open_maddox", config -> config.automaticOpenMaddox, (config, value) -> config.automaticOpenMaddox = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> SNEAK_TO_TRADE_OTHER_PLAYER_ISLAND = new BooleanSettings<>("skyblockcatia_setting.sneak_to_trade_other_player_island", config -> config.sneakToTradeOtherPlayerIsland, (config, value) -> config.sneakToTradeOtherPlayerIsland = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> MAKE_SPECIAL_ZEALOT_HELD_GOLD = new BooleanSettings<>("skyblockcatia_setting.make_special_zealot_held_gold", config -> config.makeSpecialZealotHeldGold, (config, value) -> config.makeSpecialZealotHeldGold = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> LOBBY_PLAYER_COUNT  = new BooleanSettings<>("skyblockcatia_setting.lobby_player_count", config -> config.lobbyPlayerCount, (config, value) -> config.lobbyPlayerCount = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> DISPLAY_ITEM_ABILITY_MAX_USED  = new BooleanSettings<>("skyblockcatia_setting.display_item_ability_max_used", config -> config.displayItemAbilityMaxUsed, (config, value) -> config.displayItemAbilityMaxUsed = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> PREVENT_SCROLL_HOTBAR_WHILE_FIGHT_DRAGON  = new BooleanSettings<>("skyblockcatia_setting.prevent_scroll_hotbar_while_fight_dragon", config -> config.preventScrollHotbarWhileFightDragon, (config, value) -> config.preventScrollHotbarWhileFightDragon = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> PREVENT_CLICKING_ON_DUMMY_ITEM  = new BooleanSettings<>("skyblockcatia_setting.prevent_clicking_on_dummy_item", config -> config.preventClickingOnDummyItem, (config, value) -> config.preventClickingOnDummyItem = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> SHORTCUT_BUTTON_IN_INVENTORY  = new BooleanSettings<>("skyblockcatia_setting.shortcut_button_in_inventory", config -> config.shortcutButtonInInventory, (config, value) -> config.shortcutButtonInInventory = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> SHOW_OBTAINED_DATE  = new BooleanSettings<>("skyblockcatia_setting.show_obtained_date", config -> config.showObtainedDate, (config, value) -> config.showObtainedDate = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> FIX_SKYBLOCK_ENCHANT_TAG  = new BooleanSettings<>("skyblockcatia_setting.fix_skyblock_enchant_tag", config -> config.fixSkyblockEnchantTag, (config, value) -> config.fixSkyblockEnchantTag = value);


    public static final IteratableSettings<SkyBlockcatiaSettings> VISIT_ISLAND_DISPLAY_MODE = new IteratableSettings<>("skyblockcatia_setting.visit_island_display_mode", (config, value) -> config.visitIslandDisplayMode = ToastMode.byId(config.visitIslandDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getGenericValueComponent(LangUtils.translate(config.visitIslandDisplayMode.getTranslationKey())));
    public static final IteratableSettings<SkyBlockcatiaSettings> ITEM_LOG_DISPLAY_MODE = new IteratableSettings<>("skyblockcatia_setting.item_log_display_mode", (config, value) -> config.itemLogDisplayMode = ToastMode.byId(config.itemLogDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getGenericValueComponent(LangUtils.translate(config.itemLogDisplayMode.getTranslationKey())));
    public static final IteratableSettings<SkyBlockcatiaSettings> FISH_CATCH_DISPLAY_MODE = new IteratableSettings<>("skyblockcatia_setting.fish_catch_display_mode", (config, value) -> config.fishCatchDisplayMode = ToastMode.byId(config.fishCatchDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getGenericValueComponent(LangUtils.translate(config.fishCatchDisplayMode.getTranslationKey())));
    public static final IteratableSettings<SkyBlockcatiaSettings> GIFT_DISPLAY_MODE = new IteratableSettings<>("skyblockcatia_setting.gift_display_mode", (config, value) -> config.giftDisplayMode = ToastMode.byId(config.giftDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getGenericValueComponent(LangUtils.translate(config.giftDisplayMode.getTranslationKey())));
    public static final IteratableSettings<SkyBlockcatiaSettings> PET_DISPLAY_MODE = new IteratableSettings<>("skyblockcatia_setting.pet_display_mode", (config, value) -> config.petDisplayMode = ToastMode.byId(config.petDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getGenericValueComponent(LangUtils.translate(config.petDisplayMode.getTranslationKey())));
    public static final IteratableSettings<SkyBlockcatiaSettings> PLAYER_COUNT_MODE = new IteratableSettings<>("skyblockcatia_setting.player_count_mode", (config, value) -> config.playerCountMode = PlayerCountMode.byId(config.playerCountMode.getId() + value), (config, stringOpt) -> stringOpt.getGenericValueComponent(LangUtils.translate(config.playerCountMode.getTranslationKey())));


    public static final TextFieldSettings<SkyBlockcatiaSettings> AXE_COOLDOWN_COLOR = new TextFieldSettings<>("skyblockcatia_setting.axe_cooldown_color", config -> config.axeCooldownColor, (config, value) -> config.axeCooldownColor = value);
    public static final TextFieldSettings<SkyBlockcatiaSettings> GRAPPLING_HOOK_COOLDOWN_COLOR = new TextFieldSettings<>("skyblockcatia_setting.grappling_hook_cooldown_color", config -> config.grapplingHookCooldownColor, (config, value) -> config.grapplingHookCooldownColor = value);
    public static final TextFieldSettings<SkyBlockcatiaSettings> ZEALOT_RESPAWN_COOLDOWN_COLOR = new TextFieldSettings<>("skyblockcatia_setting.zealot_respawn_cooldown_color", config -> config.zealotRespawnCooldownColor, (config, value) -> config.zealotRespawnCooldownColor = value);
    public static final TextFieldSettings<SkyBlockcatiaSettings> PLACED_SUMMONING_EYE_COLOR = new TextFieldSettings<>("skyblockcatia_setting.placed_summoning_eye_color", config -> config.placedSummoningEyeColor, (config, value) -> config.placedSummoningEyeColor = value);
    public static final TextFieldSettings<SkyBlockcatiaSettings> PLACED_SUMMONING_EYE_VALUE_COLOR = new TextFieldSettings<>("skyblockcatia_setting.placed_summoning_eye_value_color", config -> config.placedSummoningEyeValueColor, (config, value) -> config.placedSummoningEyeValueColor = value);

    private SkyBlockcatiaSettings() {}

    public static void setCurrentProfile(String profileName)
    {
        SkyBlockcatiaSettings.PROFILE_FILE = new File(USER_DIR, profileName + ".dat");
        SkyBlockcatiaSettings.CURRENT_PROFILE = profileName;
    }

    public void load()
    {
        try
        {
            CompoundNBT nbt = CompressedStreamTools.read(SkyBlockcatiaSettings.PROFILE_FILE);

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
            this.fixSkyblockEnchantTag = this.getBoolean(nbt, "FixSkyblockEnchantTag", this.fixSkyblockEnchantTag);

            this.itemRarityOpacity = this.getInteger(nbt, "ItemRarityOpacity", this.itemRarityOpacity);
            this.auctionBidConfirmValue = this.getInteger(nbt, "AuctionBidConfirmValue", this.auctionBidConfirmValue);

            this.visitIslandDisplayMode = ToastMode.byId(this.getInteger(nbt, "VisitIslandDisplayMode", this.visitIslandDisplayMode.getId()));
            this.itemLogDisplayMode = ToastMode.byId(this.getInteger(nbt, "ItemLogDisplayMode", this.itemLogDisplayMode.getId()));
            this.fishCatchDisplayMode = ToastMode.byId(this.getInteger(nbt, "FishCatchDisplayMode", this.fishCatchDisplayMode.getId()));
            this.giftDisplayMode = ToastMode.byId(this.getInteger(nbt, "GiftDisplayMode", this.giftDisplayMode.getId()));
            this.petDisplayMode = ToastMode.byId(this.getInteger(nbt, "PetDisplayMode", this.petDisplayMode.getId()));
            this.playerCountMode = PlayerCountMode.byId(this.getInteger(nbt, "PlayerCountMode", this.playerCountMode.getId()));

            // Custom Color
            this.axeCooldownColor = this.getString(nbt, "AxeCooldownColor", this.axeCooldownColor);
            this.grapplingHookCooldownColor = this.getString(nbt, "GrapplingHookCooldownColor", this.grapplingHookCooldownColor);
            this.zealotRespawnCooldownColor = this.getString(nbt, "ZealotRespawnCooldownColor", this.zealotRespawnCooldownColor);
            this.placedSummoningEyeColor = this.getString(nbt, "PlacedSummoningEyeColor", this.placedSummoningEyeColor);

            // Custom Color : Value
            this.placedSummoningEyeValueColor = this.getString(nbt, "PlacedSummoningEyeValueColor", this.placedSummoningEyeValueColor);

            SkyBlockcatiaMod.LOGGER.info("Loading extended config {}", SkyBlockcatiaSettings.PROFILE_FILE.getPath());
        }
        catch (Exception e) {}
    }

    @Override
    public void save()
    {
        this.save(!SkyBlockcatiaSettings.CURRENT_PROFILE.isEmpty() ? SkyBlockcatiaSettings.CURRENT_PROFILE : "default");
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
            nbt.putBoolean("FixSkyblockEnchantTag", this.fixSkyblockEnchantTag);

            nbt.putInt("VisitIslandDisplayMode", this.visitIslandDisplayMode.getId());
            nbt.putInt("ItemLogDisplayMode", this.itemLogDisplayMode.getId());
            nbt.putInt("FishCatchDisplayMode", this.fishCatchDisplayMode.getId());
            nbt.putInt("GiftDisplayMode", this.giftDisplayMode.getId());
            nbt.putInt("PetDisplayMode", this.petDisplayMode.getId());
            nbt.putInt("PlayerCountMode", this.playerCountMode.getId());

            nbt.putInt("ItemRarityOpacity", this.itemRarityOpacity);
            nbt.putInt("AuctionBidConfirmValue", this.auctionBidConfirmValue);

            // Custom Color
            nbt.putString("AxeCooldownColor", this.axeCooldownColor);
            nbt.putString("GrapplingHookCooldownColor", this.grapplingHookCooldownColor);
            nbt.putString("ZealotRespawnCooldownColor", this.zealotRespawnCooldownColor);
            nbt.putString("PlacedSummoningEyeColor", this.placedSummoningEyeColor);

            // Custom Color : Value
            nbt.putString("PlacedSummoningEyeValueColor", this.placedSummoningEyeValueColor);

            CompressedStreamTools.write(nbt, !profileName.equalsIgnoreCase("default") ? new File(USER_DIR, profileName + ".dat") : SkyBlockcatiaSettings.PROFILE_FILE);
        }
        catch (Exception e) {}
    }

    public static void saveProfileFile(String profileName)
    {
        File profile = new File(SkyBlockcatiaSettings.USER_DIR, "profile.txt");

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
        SkyBlockcatiaSettings.INSTANCE = new SkyBlockcatiaSettings();
        SkyBlockcatiaSettings.INSTANCE.save(SkyBlockcatiaSettings.CURRENT_PROFILE);
        ClientUtils.printClientMessage(LangUtils.translate("misc.extended_config.reset_config", SkyBlockcatiaSettings.CURRENT_PROFILE));
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