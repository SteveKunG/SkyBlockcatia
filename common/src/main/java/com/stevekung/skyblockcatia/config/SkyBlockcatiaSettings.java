package com.stevekung.skyblockcatia.config;

import java.io.File;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.utils.HitboxRenderMode;
import com.stevekung.skyblockcatia.utils.ToastMode;
import com.stevekung.stevekungslib.utils.GameProfileUtils;
import com.stevekung.stevekungslib.utils.LangUtils;
import com.stevekung.stevekungslib.utils.config.BooleanSettings;
import com.stevekung.stevekungslib.utils.config.IteratableSettings;
import com.stevekung.stevekungslib.utils.config.Settings;
import com.stevekung.stevekungslib.utils.config.SliderPercentageSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

public class SkyBlockcatiaSettings extends Settings
{
    public static SkyBlockcatiaSettings INSTANCE = new SkyBlockcatiaSettings();
    public static final File INDICATIA_DIR = new File(Minecraft.getInstance().gameDirectory, "skyblockcatia");
    public static final File USER_DIR = new File(SkyBlockcatiaSettings.INDICATIA_DIR, GameProfileUtils.getUUID().toString() + ".dat");

    // Hypixel
    public boolean axeCooldown = true;
    public boolean grapplingHookCooldown = true;
    public boolean zealotRespawnCooldown = false;
    public boolean glowingDragonArmor = true;
    public boolean showItemRarity = true;
    public boolean showHitboxWhenDragonSpawned = false;
    public boolean sneakToOpenInventoryWhileFightDragon = false;
    public boolean leavePartyWhenLastEyePlaced = false;
    public boolean lobbyPlayerViewer = true;
    public boolean auctionBidConfirm = false;
    public boolean disableBlockParticles = false;
    public boolean supportersFancyColor = true;
    public boolean bazaarOnItemTooltip = true;
    public boolean sneakToTradeOtherPlayerIsland = true;
    public boolean makeSpecialZealotHeldGold = true;
    public boolean displayItemAbilityMaxUsed = false;
    public boolean shortcutButtonInInventory = true;
    public boolean showObtainedDate = true;
    public boolean fixSkyblockEnchantTag = true;
    public boolean disableNightVision = false;

    public ToastMode visitIslandDisplayMode = ToastMode.CHAT_AND_TOAST;
    public ToastMode itemLogDisplayMode = ToastMode.CHAT_AND_TOAST;
    public ToastMode fishCatchDisplayMode = ToastMode.CHAT_AND_TOAST;
    public ToastMode giftDisplayMode = ToastMode.CHAT_AND_TOAST;
    public ToastMode petDisplayMode = ToastMode.CHAT_AND_TOAST;
    public HitboxRenderMode hitboxRenderMode = HitboxRenderMode.DEFAULT;

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
    public static final BooleanSettings<SkyBlockcatiaSettings> ITEM_RARITY = new BooleanSettings<>("skyblockcatia_setting.item_rarity", config -> config.showItemRarity, (config, value) -> config.showItemRarity = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> SHOW_HITBOX_WHEN_DRAGON_SPAWNED = new BooleanSettings<>("skyblockcatia_setting.show_hitbox_when_dragon_spawned", config -> config.showHitboxWhenDragonSpawned, (config, value) -> config.showHitboxWhenDragonSpawned = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> SNEAK_TO_OPEN_INVENTORY = new BooleanSettings<>("skyblockcatia_setting.sneak_to_open_inventory", config -> config.sneakToOpenInventoryWhileFightDragon, (config, value) -> config.sneakToOpenInventoryWhileFightDragon = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> LEAVE_PARTY_WHEN_LAST_EYE_PLACED = new BooleanSettings<>("skyblockcatia_setting.leave_party_when_last_eye_placed", config -> config.leavePartyWhenLastEyePlaced, (config, value) -> config.leavePartyWhenLastEyePlaced = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> LOBBY_PLAYER_VIEWER = new BooleanSettings<>("skyblockcatia_setting.lobby_player_viewer", config -> config.lobbyPlayerViewer, (config, value) -> config.lobbyPlayerViewer = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> AUCTION_BID_CONFIRM = new BooleanSettings<>("skyblockcatia_setting.auction_bid_confirm", config -> config.auctionBidConfirm, (config, value) -> config.auctionBidConfirm = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> DISABLE_BLOCK_PARTICLES = new BooleanSettings<>("skyblockcatia_setting.disable_block_particles", config -> config.disableBlockParticles, (config, value) -> config.disableBlockParticles = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> SUPPORTERS_FANCY_COLOR = new BooleanSettings<>("skyblockcatia_setting.supporters_fancy_color", config -> config.supportersFancyColor, (config, value) -> config.supportersFancyColor = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> BAZAAR_ON_ITEM_TOOLTIP = new BooleanSettings<>("skyblockcatia_setting.bazaar_on_item_tooltip", config -> config.bazaarOnItemTooltip, (config, value) -> config.bazaarOnItemTooltip = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> SNEAK_TO_TRADE_OTHER_PLAYER_ISLAND = new BooleanSettings<>("skyblockcatia_setting.sneak_to_trade_other_player_island", config -> config.sneakToTradeOtherPlayerIsland, (config, value) -> config.sneakToTradeOtherPlayerIsland = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> MAKE_SPECIAL_ZEALOT_HELD_GOLD = new BooleanSettings<>("skyblockcatia_setting.make_special_zealot_held_gold", config -> config.makeSpecialZealotHeldGold, (config, value) -> config.makeSpecialZealotHeldGold = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> DISPLAY_ITEM_ABILITY_MAX_USED = new BooleanSettings<>("skyblockcatia_setting.display_item_ability_max_used", config -> config.displayItemAbilityMaxUsed, (config, value) -> config.displayItemAbilityMaxUsed = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> SHORTCUT_BUTTON_IN_INVENTORY = new BooleanSettings<>("skyblockcatia_setting.shortcut_button_in_inventory", config -> config.shortcutButtonInInventory, (config, value) -> config.shortcutButtonInInventory = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> SHOW_OBTAINED_DATE = new BooleanSettings<>("skyblockcatia_setting.show_obtained_date", config -> config.showObtainedDate, (config, value) -> config.showObtainedDate = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> FIX_SKYBLOCK_ENCHANT_TAG = new BooleanSettings<>("skyblockcatia_setting.fix_skyblock_enchant_tag", config -> config.fixSkyblockEnchantTag, (config, value) -> config.fixSkyblockEnchantTag = value);
    public static final BooleanSettings<SkyBlockcatiaSettings> DISABLE_NIGHT_VISION = new BooleanSettings<>("skyblockcatia_setting.disable_night_vision", config -> config.disableNightVision, (config, value) -> config.disableNightVision = value);


    public static final IteratableSettings<SkyBlockcatiaSettings> VISIT_ISLAND_DISPLAY_MODE = new IteratableSettings<>("skyblockcatia_setting.visit_island_display_mode", (config, value) -> config.visitIslandDisplayMode = ToastMode.byId(config.visitIslandDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getGenericValueComponent(LangUtils.translate(config.visitIslandDisplayMode.getTranslationKey())));
    public static final IteratableSettings<SkyBlockcatiaSettings> ITEM_LOG_DISPLAY_MODE = new IteratableSettings<>("skyblockcatia_setting.item_log_display_mode", (config, value) -> config.itemLogDisplayMode = ToastMode.byId(config.itemLogDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getGenericValueComponent(LangUtils.translate(config.itemLogDisplayMode.getTranslationKey())));
    public static final IteratableSettings<SkyBlockcatiaSettings> FISH_CATCH_DISPLAY_MODE = new IteratableSettings<>("skyblockcatia_setting.fish_catch_display_mode", (config, value) -> config.fishCatchDisplayMode = ToastMode.byId(config.fishCatchDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getGenericValueComponent(LangUtils.translate(config.fishCatchDisplayMode.getTranslationKey())));
    public static final IteratableSettings<SkyBlockcatiaSettings> GIFT_DISPLAY_MODE = new IteratableSettings<>("skyblockcatia_setting.gift_display_mode", (config, value) -> config.giftDisplayMode = ToastMode.byId(config.giftDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getGenericValueComponent(LangUtils.translate(config.giftDisplayMode.getTranslationKey())));
    public static final IteratableSettings<SkyBlockcatiaSettings> PET_DISPLAY_MODE = new IteratableSettings<>("skyblockcatia_setting.pet_display_mode", (config, value) -> config.petDisplayMode = ToastMode.byId(config.petDisplayMode.getId() + value), (config, stringOpt) -> stringOpt.getGenericValueComponent(LangUtils.translate(config.petDisplayMode.getTranslationKey())));
    public static final IteratableSettings<SkyBlockcatiaSettings> HITBOX_RENDER_MODE = new IteratableSettings<>("skyblockcatia_setting.hitbox_render_mode", (config, value) -> config.hitboxRenderMode = HitboxRenderMode.byId(config.hitboxRenderMode.getId() + value), (config, stringOpt) -> stringOpt.getGenericValueComponent(LangUtils.translate(config.hitboxRenderMode.getTranslationKey())));

    private SkyBlockcatiaSettings() {}

    public void load()
    {
        try
        {
            CompoundTag nbt = NbtIo.read(SkyBlockcatiaSettings.USER_DIR);

            if (nbt == null)
            {
                return;
            }

            // Hypixel
            this.axeCooldown = this.getBoolean(nbt, "AxeCooldown", this.axeCooldown);
            this.grapplingHookCooldown = this.getBoolean(nbt, "GrapplingHookCooldown", this.grapplingHookCooldown);
            this.zealotRespawnCooldown = this.getBoolean(nbt, "ZealotRespawnCooldown", this.zealotRespawnCooldown);
            this.glowingDragonArmor = this.getBoolean(nbt, "GlowingDragonArmor", this.glowingDragonArmor);
            this.showItemRarity = this.getBoolean(nbt, "ShowItemRarity", this.showItemRarity);
            this.showHitboxWhenDragonSpawned = this.getBoolean(nbt, "ShowHitboxWhenDragonSpawned", this.showHitboxWhenDragonSpawned);
            this.sneakToOpenInventoryWhileFightDragon = this.getBoolean(nbt, "SneakToOpenInventoryWhileFightDragon", this.sneakToOpenInventoryWhileFightDragon);
            this.leavePartyWhenLastEyePlaced = this.getBoolean(nbt, "LeavePartyWhenLastEyePlaced", this.leavePartyWhenLastEyePlaced);
            this.lobbyPlayerViewer = this.getBoolean(nbt, "LobbyPlayerViewer", this.lobbyPlayerViewer);
            this.auctionBidConfirm = this.getBoolean(nbt, "AuctionBidConfirm", this.auctionBidConfirm);
            this.disableBlockParticles = this.getBoolean(nbt, "DisableBlockParticles", this.disableBlockParticles);
            this.supportersFancyColor = this.getBoolean(nbt, "SupportersFancyColor", this.supportersFancyColor);
            this.bazaarOnItemTooltip = this.getBoolean(nbt, "BazaarOnItemTooltip", this.bazaarOnItemTooltip);
            this.sneakToTradeOtherPlayerIsland = this.getBoolean(nbt, "SneakToTradeOtherPlayerIsland", this.sneakToTradeOtherPlayerIsland);
            this.makeSpecialZealotHeldGold = this.getBoolean(nbt, "MakeSpecialZealotHeldGold", this.makeSpecialZealotHeldGold);
            this.displayItemAbilityMaxUsed = this.getBoolean(nbt, "DisplayItemAbilityMaxUsed", this.displayItemAbilityMaxUsed);
            this.shortcutButtonInInventory = this.getBoolean(nbt, "ShortcutButtonInInventory", this.shortcutButtonInInventory);
            this.showObtainedDate = this.getBoolean(nbt, "ShowObtainedDate", this.showObtainedDate);
            this.fixSkyblockEnchantTag = this.getBoolean(nbt, "FixSkyblockEnchantTag", this.fixSkyblockEnchantTag);
            this.disableNightVision = this.getBoolean(nbt, "DisableNightVision", this.disableNightVision);

            this.itemRarityOpacity = this.getInteger(nbt, "ItemRarityOpacity", this.itemRarityOpacity);
            this.auctionBidConfirmValue = this.getInteger(nbt, "AuctionBidConfirmValue", this.auctionBidConfirmValue);

            this.visitIslandDisplayMode = ToastMode.byId(this.getInteger(nbt, "VisitIslandDisplayMode", this.visitIslandDisplayMode.getId()));
            this.itemLogDisplayMode = ToastMode.byId(this.getInteger(nbt, "ItemLogDisplayMode", this.itemLogDisplayMode.getId()));
            this.fishCatchDisplayMode = ToastMode.byId(this.getInteger(nbt, "FishCatchDisplayMode", this.fishCatchDisplayMode.getId()));
            this.giftDisplayMode = ToastMode.byId(this.getInteger(nbt, "GiftDisplayMode", this.giftDisplayMode.getId()));
            this.petDisplayMode = ToastMode.byId(this.getInteger(nbt, "PetDisplayMode", this.petDisplayMode.getId()));
            this.hitboxRenderMode = HitboxRenderMode.byId(this.getInteger(nbt, "HitboxRenderMode", this.hitboxRenderMode.getId()));

            SkyBlockcatiaMod.LOGGER.info("Loading extended config {}", SkyBlockcatiaSettings.USER_DIR.getPath());
        }
        catch (Exception ignored) {}
    }

    @Override
    public void save()
    {
        try
        {
            CompoundTag nbt = new CompoundTag();

            // Hypixel
            nbt.putBoolean("AxeCooldown", this.axeCooldown);
            nbt.putBoolean("GrapplingHookCooldown", this.grapplingHookCooldown);
            nbt.putBoolean("ZealotRespawnCooldown", this.zealotRespawnCooldown);
            nbt.putBoolean("GlowingDragonArmor", this.glowingDragonArmor);
            nbt.putBoolean("ShowItemRarity", this.showItemRarity);
            nbt.putBoolean("ShowHitboxWhenDragonSpawned", this.showHitboxWhenDragonSpawned);
            nbt.putBoolean("SneakToOpenInventoryWhileFightDragon", this.sneakToOpenInventoryWhileFightDragon);
            nbt.putBoolean("LeavePartyWhenLastEyePlaced", this.leavePartyWhenLastEyePlaced);
            nbt.putBoolean("LobbyPlayerViewer", this.lobbyPlayerViewer);
            nbt.putBoolean("AuctionBidConfirm", this.auctionBidConfirm);
            nbt.putBoolean("DisableBlockParticles", this.disableBlockParticles);
            nbt.putBoolean("SupportersFancyColor", this.supportersFancyColor);
            nbt.putBoolean("BazaarOnItemTooltip", this.bazaarOnItemTooltip);
            nbt.putBoolean("SneakToTradeOtherPlayerIsland", this.sneakToTradeOtherPlayerIsland);
            nbt.putBoolean("MakeSpecialZealotHeldGold", this.makeSpecialZealotHeldGold);
            nbt.putBoolean("DisplayItemAbilityMaxUsed", this.displayItemAbilityMaxUsed);
            nbt.putBoolean("ShortcutButtonInInventory", this.shortcutButtonInInventory);
            nbt.putBoolean("ShowObtainedDate", this.showObtainedDate);
            nbt.putBoolean("FixSkyblockEnchantTag", this.fixSkyblockEnchantTag);
            nbt.putBoolean("DisableNightVision", this.disableNightVision);

            nbt.putInt("VisitIslandDisplayMode", this.visitIslandDisplayMode.getId());
            nbt.putInt("ItemLogDisplayMode", this.itemLogDisplayMode.getId());
            nbt.putInt("FishCatchDisplayMode", this.fishCatchDisplayMode.getId());
            nbt.putInt("GiftDisplayMode", this.giftDisplayMode.getId());
            nbt.putInt("PetDisplayMode", this.petDisplayMode.getId());
            nbt.putInt("HitboxRenderMode", this.hitboxRenderMode.getId());

            nbt.putInt("ItemRarityOpacity", this.itemRarityOpacity);
            nbt.putInt("AuctionBidConfirmValue", this.auctionBidConfirmValue);

            NbtIo.write(nbt, USER_DIR);
        }
        catch (Exception ignored) {}
    }

    private boolean getBoolean(CompoundTag nbt, String key, boolean defaultValue)
    {
        if (nbt.contains(key, 99))
        {
            return nbt.getBoolean(key);
        }
        else
        {
            return defaultValue;
        }
    }

    private int getInteger(CompoundTag nbt, String key, int defaultValue)
    {
        if (nbt.contains(key, 99))
        {
            return nbt.getInt(key);
        }
        else
        {
            return defaultValue;
        }
    }
}