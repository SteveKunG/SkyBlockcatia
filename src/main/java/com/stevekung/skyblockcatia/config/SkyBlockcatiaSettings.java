package com.stevekung.skyblockcatia.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import com.stevekung.skyblockcatia.utils.*;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class SkyBlockcatiaSettings
{
    public static SkyBlockcatiaSettings INSTANCE = new SkyBlockcatiaSettings();
    public static final File skyblockcatiaDir = new File(Minecraft.getMinecraft().mcDataDir, "skyblockcatia");
    public static final File userDir = new File(skyblockcatiaDir, GameProfileUtils.getUUID().toString());
    public static final File defaultConfig = new File(userDir, "default.dat");
    public static String currentProfile = "";
    private static final String[] TOAST_MODE_DISABLED = new String[] {"skyblockcatia.chat", "skyblockcatia.toast", "skyblockcatia.chat_and_toast", "skyblockcatia.disabled"};
    private static final String[] TOAST_MODE = new String[] {"skyblockcatia.chat", "skyblockcatia.toast", "skyblockcatia.chat_and_toast"};
    private static final String[] HITBOX_RENDER_MODE = new String[] {"skyblockcatia.default", "skyblockcatia.dragon", "skyblockcatia.crystal", "skyblockcatia.dragon_and_crystal"};
    private static File file;

    // Main
    public int hitboxRenderMode = 0;

    // Hypixel
    public boolean rightClickToAddParty = false;
    public boolean jungleAxeCooldown = true;
    public boolean grapplingHookCooldown = true;
    public boolean zealotRespawnCooldown = false;
    public boolean glowingDragonArmor = true;
    public int selectedHypixelMinigame = 0;
    public int hypixelMinigameScrollPos = 0;
    public int visitIslandToastMode = 2;
    public int visitIslandToastTime = 8;
    public int rareDropToastMode = 2;
    public int rareDropToastTime = 10;
    public int specialDropToastTime = 15;
    public int fishCatchToastMode = 2;
    public int fishCatchToastTime = 10;
    public int giftToastMode = 2;
    public int giftToastTime = 5;
    public int petToastMode = 2;
    public int petToastTime = 10;
    public int chatMode = 0;
    public boolean placedSummoningEyeTracker = false;
    public boolean showItemRarity = true;
    public boolean showHitboxWhenDragonSpawned = false;
    public boolean sneakToOpenInventoryWhileFightDragon = false;
    public boolean leavePartyWhenLastEyePlaced = false;
    public boolean lobbyPlayerViewer = true;
    public boolean auctionBidConfirm = false;
    public boolean disableBlockParticles = false;
    public int itemRarityOpacity = 75;
    public int auctionBidConfirmValue = 500000;
    public boolean supportersFancyColor = true;
    public boolean bazaarOnTooltips = true;
    public boolean sneakToTradeOtherPlayerIsland = true;
    public boolean makeSpecialZealotHeldGold = true;
    public boolean lobbyPlayerCount = true;
    public boolean displayItemAbilityMaxUsed = false;
    public boolean shortcutButtonInInventory = true;
    public boolean showObtainedDate = true;
    public boolean fixSkyblockEnchantTag = true;
    public boolean disableNightVision = false;

    private SkyBlockcatiaSettings() {}

    public void setCurrentProfile(String profileName)
    {
        SkyBlockcatiaSettings.file = new File(userDir, profileName + ".dat");
        currentProfile = profileName;
    }

    public void load()
    {
        try
        {
            NBTTagCompound nbt = CompressedStreamTools.read(SkyBlockcatiaSettings.file);

            if (nbt == null)
            {
                return;
            }

            // Main
            this.hitboxRenderMode = SkyBlockcatiaSettings.getInteger(nbt, "HitboxRenderMode", this.hitboxRenderMode);
            this.visitIslandToastMode = SkyBlockcatiaSettings.getInteger(nbt, "VisitIslandToastMode", this.visitIslandToastMode);
            this.visitIslandToastTime = SkyBlockcatiaSettings.getInteger(nbt, "VisitIslandToastTime", this.visitIslandToastTime);
            this.rareDropToastMode = SkyBlockcatiaSettings.getInteger(nbt, "RareDropToastMode", this.rareDropToastMode);
            this.rareDropToastTime = SkyBlockcatiaSettings.getInteger(nbt, "RareDropToastTime", this.rareDropToastTime);
            this.specialDropToastTime = SkyBlockcatiaSettings.getInteger(nbt, "SpecialDropToastTime", this.specialDropToastTime);
            this.fishCatchToastMode = SkyBlockcatiaSettings.getInteger(nbt, "FishCatchToastMode", this.fishCatchToastMode);
            this.fishCatchToastTime = SkyBlockcatiaSettings.getInteger(nbt, "FishCatchToastTime", this.fishCatchToastTime);
            this.giftToastMode = SkyBlockcatiaSettings.getInteger(nbt, "GiftToastMode", this.giftToastMode);
            this.giftToastTime = SkyBlockcatiaSettings.getInteger(nbt, "GiftToastTime", this.giftToastTime);
            this.petToastMode = SkyBlockcatiaSettings.getInteger(nbt, "PetToastMode", this.petToastMode);
            this.petToastTime = SkyBlockcatiaSettings.getInteger(nbt, "PetToastTime", this.petToastTime);
            this.chatMode = SkyBlockcatiaSettings.getInteger(nbt, "ChatMode", this.chatMode);

            // Hypixel
            this.rightClickToAddParty = SkyBlockcatiaSettings.getBoolean(nbt, "RightClickToAddParty", this.rightClickToAddParty);
            this.selectedHypixelMinigame = SkyBlockcatiaSettings.getInteger(nbt, "SelectedHypixelMinigame", this.selectedHypixelMinigame);
            this.hypixelMinigameScrollPos = SkyBlockcatiaSettings.getInteger(nbt, "HypixelMinigameScrollPos", this.hypixelMinigameScrollPos);
            this.itemRarityOpacity = SkyBlockcatiaSettings.getInteger(nbt, "ItemRarityOpacity", this.itemRarityOpacity);
            this.auctionBidConfirmValue = SkyBlockcatiaSettings.getInteger(nbt, "AuctionBidConfirmValue", this.auctionBidConfirmValue);
            this.jungleAxeCooldown = SkyBlockcatiaSettings.getBoolean(nbt, "JungleAxeCooldown", this.jungleAxeCooldown);
            this.grapplingHookCooldown = SkyBlockcatiaSettings.getBoolean(nbt, "GrapplingHookCooldown", this.grapplingHookCooldown);
            this.zealotRespawnCooldown = SkyBlockcatiaSettings.getBoolean(nbt, "ZealotRespawnCooldown", this.zealotRespawnCooldown);
            this.glowingDragonArmor = SkyBlockcatiaSettings.getBoolean(nbt, "GlowingDragonArmor", this.glowingDragonArmor);
            this.placedSummoningEyeTracker = SkyBlockcatiaSettings.getBoolean(nbt, "PlacedSummoningEyeTracker", this.placedSummoningEyeTracker);
            this.showItemRarity = SkyBlockcatiaSettings.getBoolean(nbt, "ShowItemRarity", this.showItemRarity);
            this.showHitboxWhenDragonSpawned = SkyBlockcatiaSettings.getBoolean(nbt, "ShowHitboxWhenDragonSpawned", this.showHitboxWhenDragonSpawned);
            this.sneakToOpenInventoryWhileFightDragon = SkyBlockcatiaSettings.getBoolean(nbt, "SneakToOpenInventoryWhileFightDragon", this.sneakToOpenInventoryWhileFightDragon);
            this.leavePartyWhenLastEyePlaced = SkyBlockcatiaSettings.getBoolean(nbt, "LeavePartyWhenLastEyePlaced", this.leavePartyWhenLastEyePlaced);
            this.lobbyPlayerViewer = SkyBlockcatiaSettings.getBoolean(nbt, "LobbyPlayerViewer", this.lobbyPlayerViewer);
            this.auctionBidConfirm = SkyBlockcatiaSettings.getBoolean(nbt, "AuctionBidConfirm", this.auctionBidConfirm);
            this.disableBlockParticles = SkyBlockcatiaSettings.getBoolean(nbt, "DisableBlockParticles", this.disableBlockParticles);
            this.supportersFancyColor = SkyBlockcatiaSettings.getBoolean(nbt, "SupportersFancyColor", this.supportersFancyColor);
            this.bazaarOnTooltips = SkyBlockcatiaSettings.getBoolean(nbt, "BazaarOnTooltips", this.bazaarOnTooltips);
            this.sneakToTradeOtherPlayerIsland = SkyBlockcatiaSettings.getBoolean(nbt, "SneakToTradeOtherPlayerIsland", this.sneakToTradeOtherPlayerIsland);
            this.makeSpecialZealotHeldGold = SkyBlockcatiaSettings.getBoolean(nbt, "MakeSpecialZealotHeldGold", this.makeSpecialZealotHeldGold);
            this.lobbyPlayerCount = SkyBlockcatiaSettings.getBoolean(nbt, "LobbyPlayerCount", this.lobbyPlayerCount);
            this.displayItemAbilityMaxUsed = SkyBlockcatiaSettings.getBoolean(nbt, "DisplayItemAbilityMaxUsed", this.displayItemAbilityMaxUsed);
            this.shortcutButtonInInventory = SkyBlockcatiaSettings.getBoolean(nbt, "ShortcutButtonInInventory", this.shortcutButtonInInventory);
            this.showObtainedDate = SkyBlockcatiaSettings.getBoolean(nbt, "ShowObtainedDate", this.showObtainedDate);
            this.fixSkyblockEnchantTag = SkyBlockcatiaSettings.getBoolean(nbt, "FixSkyblockEnchantTag", this.fixSkyblockEnchantTag);
            this.disableNightVision = SkyBlockcatiaSettings.getBoolean(nbt, "DisableNightVision", this.disableNightVision);

            LoggerIN.info("Loading extended config {}", SkyBlockcatiaSettings.file.getPath());
        }
        catch (Exception e) {}
    }

    public void save()
    {
        this.save(!SkyBlockcatiaSettings.currentProfile.isEmpty() ? SkyBlockcatiaSettings.currentProfile : "default");
    }

    public void save(String profileName)
    {
        try
        {
            NBTTagCompound nbt = new NBTTagCompound();

            // Main
            nbt.setInteger("HitboxRenderMode", this.hitboxRenderMode);
            nbt.setInteger("VisitIslandToastMode", this.visitIslandToastMode);
            nbt.setInteger("VisitIslandToastTime", this.visitIslandToastTime);
            nbt.setInteger("RareDropToastMode", this.rareDropToastMode);
            nbt.setInteger("RareDropToastTime", this.rareDropToastTime);
            nbt.setInteger("SpecialDropToastTime", this.specialDropToastTime);
            nbt.setInteger("FishCatchToastMode", this.fishCatchToastMode);
            nbt.setInteger("FishCatchToastTime", this.fishCatchToastTime);
            nbt.setInteger("GiftToastMode", this.giftToastMode);
            nbt.setInteger("GiftToastTime", this.giftToastTime);
            nbt.setInteger("PetToastMode", this.petToastMode);
            nbt.setInteger("PetToastTime", this.petToastTime);
            nbt.setInteger("ChatMode", this.chatMode);

            // Hypixel
            nbt.setBoolean("RightClickToAddParty", this.rightClickToAddParty);
            nbt.setBoolean("JungleAxeCooldown", this.jungleAxeCooldown);
            nbt.setBoolean("GrapplingHookCooldown", this.grapplingHookCooldown);
            nbt.setBoolean("ZealotRespawnCooldown", this.zealotRespawnCooldown);
            nbt.setBoolean("GlowingDragonArmor", this.glowingDragonArmor);
            nbt.setBoolean("PlacedSummoningEyeTracker", this.placedSummoningEyeTracker);
            nbt.setBoolean("ShowItemRarity", this.showItemRarity);
            nbt.setBoolean("ShowHitboxWhenDragonSpawned", this.showHitboxWhenDragonSpawned);
            nbt.setBoolean("SneakToOpenInventoryWhileFightDragon", this.sneakToOpenInventoryWhileFightDragon);
            nbt.setBoolean("LeavePartyWhenLastEyePlaced", this.leavePartyWhenLastEyePlaced);
            nbt.setBoolean("LobbyPlayerViewer", this.lobbyPlayerViewer);
            nbt.setBoolean("AuctionBidConfirm", this.auctionBidConfirm);
            nbt.setBoolean("SupportersFancyColor", this.supportersFancyColor);
            nbt.setBoolean("BazaarOnTooltips", this.bazaarOnTooltips);
            nbt.setBoolean("SneakToTradeOtherPlayerIsland", this.sneakToTradeOtherPlayerIsland);
            nbt.setBoolean("MakeSpecialZealotHeldGold", this.makeSpecialZealotHeldGold);
            nbt.setBoolean("LobbyPlayerCount", this.lobbyPlayerCount);
            nbt.setBoolean("DisableBlockParticles", this.disableBlockParticles);
            nbt.setBoolean("DisplayItemAbilityMaxUsed", this.displayItemAbilityMaxUsed);
            nbt.setBoolean("ShortcutButtonInInventory", this.shortcutButtonInInventory);
            nbt.setBoolean("ShowObtainedDate", this.showObtainedDate);
            nbt.setBoolean("FixSkyblockEnchantTag", this.fixSkyblockEnchantTag);
            nbt.setBoolean("DisableNightVision", this.disableNightVision);
            nbt.setInteger("SelectedHypixelMinigame", this.selectedHypixelMinigame);
            nbt.setInteger("HypixelMinigameScrollPos", this.hypixelMinigameScrollPos);
            nbt.setInteger("ItemRarityOpacity", this.itemRarityOpacity);
            nbt.setInteger("AuctionBidConfirmValue", this.auctionBidConfirmValue);

            CompressedStreamTools.safeWrite(nbt, !profileName.equalsIgnoreCase("default") ? new File(userDir, profileName + ".dat") : SkyBlockcatiaSettings.file);
        }
        catch (Exception e) {}
    }

    public static void saveProfileFile(String profileName)
    {
        File profile = new File(SkyBlockcatiaSettings.userDir, "profile.txt");

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
        SkyBlockcatiaSettings.INSTANCE = new SkyBlockcatiaSettings();
        SkyBlockcatiaSettings.INSTANCE.save(SkyBlockcatiaSettings.currentProfile);
        ClientUtils.printClientMessage(LangUtils.translate("message.reset_config", SkyBlockcatiaSettings.currentProfile));
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

    public String getKeyBinding(SkyBlockcatiaSettings.Options options)
    {
        String name = LangUtils.translate(options.getTranslation()) + ": ";
        ModDecimalFormat format = new ModDecimalFormat("#,###");

        if (options.isFloat())
        {
            if (options == SkyBlockcatiaSettings.Options.VISIT_ISLAND_TOAST_TIME || options == SkyBlockcatiaSettings.Options.RARE_DROP_TOAST_TIME || options == SkyBlockcatiaSettings.Options.SPECIAL_DROP_TOAST_TIME || options == SkyBlockcatiaSettings.Options.FISH_CATCH_TOAST_TIME || options == SkyBlockcatiaSettings.Options.GIFT_TOAST_TIME || options == SkyBlockcatiaSettings.Options.PET_TOAST_TIME)
            {
                float value = this.getOptionFloatValue(options);
                return name + format.format(value) + "s";
            }
            else
            {
                float value = this.getOptionFloatValue(options);
                return name + format.format(value);
            }
        }
        else if (options.isBoolean())
        {
            boolean flag = this.getOptionOrdinalValue(options);
            return flag ? name + EnumChatFormatting.GREEN + "ON" : name + EnumChatFormatting.RED + "OFF";
        }
        else if (options == SkyBlockcatiaSettings.Options.HITBOX_RENDER_MODE)
        {
            return name + this.getTranslation(HITBOX_RENDER_MODE, this.hitboxRenderMode);
        }
        else if (options == SkyBlockcatiaSettings.Options.VISIT_ISLAND_TOAST_MODE)
        {
            return name + this.getTranslation(TOAST_MODE_DISABLED, this.visitIslandToastMode);
        }
        else if (options == SkyBlockcatiaSettings.Options.RARE_DROP_TOAST_MODE)
        {
            return name + this.getTranslation(TOAST_MODE, this.rareDropToastMode);
        }
        else if (options == SkyBlockcatiaSettings.Options.FISH_CATCH_TOAST_MODE)
        {
            return name + this.getTranslation(TOAST_MODE, this.fishCatchToastMode);
        }
        else if (options == SkyBlockcatiaSettings.Options.GIFT_TOAST_MODE)
        {
            return name + this.getTranslation(TOAST_MODE, this.giftToastMode);
        }
        else if (options == SkyBlockcatiaSettings.Options.PET_TOAST_MODE)
        {
            return name + this.getTranslation(TOAST_MODE, this.petToastMode);
        }
        else
        {
            return name;
        }
    }

    public void setOptionValue(SkyBlockcatiaSettings.Options options, int value)
    {
        if (options == SkyBlockcatiaSettings.Options.HITBOX_RENDER_MODE)
        {
            this.hitboxRenderMode = (this.hitboxRenderMode + value) % 4;
        }
        else if (options == SkyBlockcatiaSettings.Options.VISIT_ISLAND_TOAST_MODE)
        {
            this.visitIslandToastMode = (this.visitIslandToastMode + value) % 4;
        }
        else if (options == SkyBlockcatiaSettings.Options.RARE_DROP_TOAST_MODE)
        {
            this.rareDropToastMode = (this.rareDropToastMode + value) % 3;
        }
        else if (options == SkyBlockcatiaSettings.Options.FISH_CATCH_TOAST_MODE)
        {
            this.fishCatchToastMode = (this.fishCatchToastMode + value) % 3;
        }
        else if (options == SkyBlockcatiaSettings.Options.GIFT_TOAST_MODE)
        {
            this.giftToastMode = (this.giftToastMode + value) % 3;
        }
        else if (options == SkyBlockcatiaSettings.Options.PET_TOAST_MODE)
        {
            this.petToastMode = (this.petToastMode + value) % 3;
        }

        else if (options == SkyBlockcatiaSettings.Options.RIGHT_CLICK_ADD_PARTY)
        {
            this.rightClickToAddParty = !this.rightClickToAddParty;
        }
        else if (options == SkyBlockcatiaSettings.Options.JUNGLE_AXE_COOLDOWN)
        {
            this.jungleAxeCooldown = !this.jungleAxeCooldown;
        }
        else if (options == SkyBlockcatiaSettings.Options.GRAPPLING_HOOK_COOLDOWN)
        {
            this.grapplingHookCooldown = !this.grapplingHookCooldown;
        }
        else if (options == SkyBlockcatiaSettings.Options.ZEALOT_RESPAWN_COOLDOWN)
        {
            this.zealotRespawnCooldown = !this.zealotRespawnCooldown;
        }
        else if (options == SkyBlockcatiaSettings.Options.GLOWING_DRAGON_ARMOR)
        {
            this.glowingDragonArmor = !this.glowingDragonArmor;
        }
        else if (options == SkyBlockcatiaSettings.Options.PLACED_SUMMONING_EYE_TRACKER)
        {
            this.placedSummoningEyeTracker = !this.placedSummoningEyeTracker;
        }
        else if (options == SkyBlockcatiaSettings.Options.SHOW_ITEM_RARITY)
        {
            this.showItemRarity = !this.showItemRarity;
        }
        else if (options == SkyBlockcatiaSettings.Options.SHOW_HITBOX_WHEN_DRAGON_SPAWNED)
        {
            this.showHitboxWhenDragonSpawned = !this.showHitboxWhenDragonSpawned;
        }
        else if (options == SkyBlockcatiaSettings.Options.SNEAK_TO_OPEN_INVENTORY_WHILE_FIGHT_DRAGON)
        {
            this.sneakToOpenInventoryWhileFightDragon = !this.sneakToOpenInventoryWhileFightDragon;
        }
        else if (options == SkyBlockcatiaSettings.Options.LEAVE_PARTY_WHEN_LAST_EYE_PLACED)
        {
            this.leavePartyWhenLastEyePlaced = !this.leavePartyWhenLastEyePlaced;
        }
        else if (options == SkyBlockcatiaSettings.Options.LOBBY_PLAYER_VIEWER)
        {
            this.lobbyPlayerViewer = !this.lobbyPlayerViewer;
        }
        else if (options == SkyBlockcatiaSettings.Options.AUCTION_BID_CONFIRM)
        {
            this.auctionBidConfirm = !this.auctionBidConfirm;
        }
        else if (options == SkyBlockcatiaSettings.Options.DISABLE_BLOCK_PARTICLES)
        {
            this.disableBlockParticles = !this.disableBlockParticles;
        }
        else if (options == SkyBlockcatiaSettings.Options.SUPPORTERS_FANCY_COLOR)
        {
            this.supportersFancyColor = !this.supportersFancyColor;
        }
        else if (options == SkyBlockcatiaSettings.Options.BAZAAR_ON_TOOLTIPS)
        {
            this.bazaarOnTooltips = !this.bazaarOnTooltips;
        }
        else if (options == SkyBlockcatiaSettings.Options.SNEAK_TO_TRADE_OTHER_PLAYER_ISLAND)
        {
            this.sneakToTradeOtherPlayerIsland = !this.sneakToTradeOtherPlayerIsland;
        }
        else if (options == SkyBlockcatiaSettings.Options.MAKE_SPECIAL_ZEALOT_HELD_GOLD)
        {
            this.makeSpecialZealotHeldGold = !this.makeSpecialZealotHeldGold;
        }
        else if (options == SkyBlockcatiaSettings.Options.LOBBY_PLAYER_COUNT)
        {
            this.lobbyPlayerCount = !this.lobbyPlayerCount;
        }
        else if (options == SkyBlockcatiaSettings.Options.DISPLAY_ITEM_ABILITY_MAX_USED)
        {
            this.displayItemAbilityMaxUsed = !this.displayItemAbilityMaxUsed;
        }
        else if (options == SkyBlockcatiaSettings.Options.SHORTCUT_BUTTON_IN_INVENTORY)
        {
            this.shortcutButtonInInventory = !this.shortcutButtonInInventory;
        }
        else if (options == SkyBlockcatiaSettings.Options.SHOW_OBTAINED_DATE)
        {
            this.showObtainedDate = !this.showObtainedDate;
        }
        else if (options == SkyBlockcatiaSettings.Options.FIX_SKYBLOCK_ENCHANT_TAG)
        {
            this.fixSkyblockEnchantTag = !this.fixSkyblockEnchantTag;
        }
        else if (options == SkyBlockcatiaSettings.Options.DISABLE_NIGHT_VISION)
        {
            this.disableNightVision = !this.disableNightVision;
        }
    }

    public void setOptionFloatValue(SkyBlockcatiaSettings.Options options, float value)
    {
        if (options == SkyBlockcatiaSettings.Options.ITEM_RARITY_OPACITY)
        {
            this.itemRarityOpacity = (int) value;
        }
        else if (options == SkyBlockcatiaSettings.Options.AUCTION_BID_CONFIRM_VALUE)
        {
            this.auctionBidConfirmValue = (int) value;
        }
        else if (options == SkyBlockcatiaSettings.Options.VISIT_ISLAND_TOAST_TIME)
        {
            this.visitIslandToastTime = (int) value;
        }
        else if (options == SkyBlockcatiaSettings.Options.RARE_DROP_TOAST_TIME)
        {
            this.rareDropToastTime = (int) value;
        }
        else if (options == SkyBlockcatiaSettings.Options.SPECIAL_DROP_TOAST_TIME)
        {
            this.specialDropToastTime = (int) value;
        }
        else if (options == SkyBlockcatiaSettings.Options.FISH_CATCH_TOAST_TIME)
        {
            this.fishCatchToastTime = (int) value;
        }
        else if (options == SkyBlockcatiaSettings.Options.GIFT_TOAST_TIME)
        {
            this.giftToastTime = (int) value;
        }
        else if (options == SkyBlockcatiaSettings.Options.PET_TOAST_TIME)
        {
            this.petToastTime = (int) value;
        }
    }

    public float getOptionFloatValue(SkyBlockcatiaSettings.Options settingOption)
    {
        if (settingOption == SkyBlockcatiaSettings.Options.ITEM_RARITY_OPACITY)
        {
            return this.itemRarityOpacity;
        }
        else if (settingOption == SkyBlockcatiaSettings.Options.AUCTION_BID_CONFIRM_VALUE)
        {
            return this.auctionBidConfirmValue;
        }
        else if (settingOption == SkyBlockcatiaSettings.Options.VISIT_ISLAND_TOAST_TIME)
        {
            return this.visitIslandToastTime;
        }
        else if (settingOption == SkyBlockcatiaSettings.Options.RARE_DROP_TOAST_TIME)
        {
            return this.rareDropToastTime;
        }
        else if (settingOption == SkyBlockcatiaSettings.Options.SPECIAL_DROP_TOAST_TIME)
        {
            return this.specialDropToastTime;
        }
        else if (settingOption == SkyBlockcatiaSettings.Options.FISH_CATCH_TOAST_TIME)
        {
            return this.fishCatchToastTime;
        }
        else if (settingOption == SkyBlockcatiaSettings.Options.GIFT_TOAST_TIME)
        {
            return this.giftToastTime;
        }
        else if (settingOption == SkyBlockcatiaSettings.Options.PET_TOAST_TIME)
        {
            return this.petToastTime;
        }
        return 0.0F;
    }

    public boolean getOptionOrdinalValue(SkyBlockcatiaSettings.Options options)
    {
        switch (options)
        {
        case RIGHT_CLICK_ADD_PARTY:
            return this.rightClickToAddParty;
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
        case SHOW_HITBOX_WHEN_DRAGON_SPAWNED:
            return this.showHitboxWhenDragonSpawned;
        case SNEAK_TO_OPEN_INVENTORY_WHILE_FIGHT_DRAGON:
            return this.sneakToOpenInventoryWhileFightDragon;
        case LEAVE_PARTY_WHEN_LAST_EYE_PLACED:
            return this.leavePartyWhenLastEyePlaced;
        case LOBBY_PLAYER_VIEWER:
            return this.lobbyPlayerViewer;
        case AUCTION_BID_CONFIRM:
            return this.auctionBidConfirm;
        case DISABLE_BLOCK_PARTICLES:
            return this.disableBlockParticles;
        case SUPPORTERS_FANCY_COLOR:
            return this.supportersFancyColor;
        case BAZAAR_ON_TOOLTIPS:
            return this.bazaarOnTooltips;
        case SNEAK_TO_TRADE_OTHER_PLAYER_ISLAND:
            return this.sneakToTradeOtherPlayerIsland;
        case MAKE_SPECIAL_ZEALOT_HELD_GOLD:
            return this.makeSpecialZealotHeldGold;
        case LOBBY_PLAYER_COUNT:
            return this.lobbyPlayerCount;
        case DISPLAY_ITEM_ABILITY_MAX_USED:
            return this.displayItemAbilityMaxUsed;
        case SHORTCUT_BUTTON_IN_INVENTORY:
            return this.shortcutButtonInInventory;
        case SHOW_OBTAINED_DATE:
            return this.showObtainedDate;
        case FIX_SKYBLOCK_ENCHANT_TAG:
            return this.fixSkyblockEnchantTag;
        case DISABLE_NIGHT_VISION:
            return this.disableNightVision;
        default:
            return false;
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

    public enum Options
    {
        HITBOX_RENDER_MODE(false, false),
        VISIT_ISLAND_TOAST_MODE(false, false),
        RARE_DROP_TOAST_MODE(false, false),
        FISH_CATCH_TOAST_MODE(false, false),
        GIFT_TOAST_MODE(false, false),
        PET_TOAST_MODE(false, false),

        RIGHT_CLICK_ADD_PARTY(false, true),
        JUNGLE_AXE_COOLDOWN(false, true),
        GRAPPLING_HOOK_COOLDOWN(false, true),
        ZEALOT_RESPAWN_COOLDOWN(false, true),
        GLOWING_DRAGON_ARMOR(false, true),
        PLACED_SUMMONING_EYE_TRACKER(false, true),
        SHOW_ITEM_RARITY(false, true),
        SHOW_HITBOX_WHEN_DRAGON_SPAWNED(false, true),
        SNEAK_TO_OPEN_INVENTORY_WHILE_FIGHT_DRAGON(false, true),
        LEAVE_PARTY_WHEN_LAST_EYE_PLACED(false, true),
        LOBBY_PLAYER_VIEWER(false, true),
        AUCTION_BID_CONFIRM(false, true),
        DISABLE_BLOCK_PARTICLES(false, true),
        SUPPORTERS_FANCY_COLOR(false, true),
        BAZAAR_ON_TOOLTIPS(false, true),
        SNEAK_TO_TRADE_OTHER_PLAYER_ISLAND(false, true),
        MAKE_SPECIAL_ZEALOT_HELD_GOLD(false, true),
        LOBBY_PLAYER_COUNT(false, true),
        DISPLAY_ITEM_ABILITY_MAX_USED(false, true),
        SHORTCUT_BUTTON_IN_INVENTORY(false, true),
        SHOW_OBTAINED_DATE(false, true),
        FIX_SKYBLOCK_ENCHANT_TAG(false, true),
        DISABLE_NIGHT_VISION(false, true),
        ITEM_RARITY_OPACITY(true, false, 1.0F, 100.0F, 1.0F),
        AUCTION_BID_CONFIRM_VALUE(true, false, 100000.0F, 20000000.0F, 100000.0F),
        VISIT_ISLAND_TOAST_TIME(true, false, 5.0F, 20.0F, 1.0F),
        RARE_DROP_TOAST_TIME(true, false, 5.0F, 20.0F, 1.0F),
        SPECIAL_DROP_TOAST_TIME(true, false, 5.0F, 20.0F, 1.0F),
        FISH_CATCH_TOAST_TIME(true, false, 5.0F, 20.0F, 1.0F),
        GIFT_TOAST_TIME(true, false, 5.0F, 20.0F, 1.0F),
        PET_TOAST_TIME(true, false, 5.0F, 20.0F, 1.0F),
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
            return LangUtils.translate(this.name().toLowerCase(Locale.ROOT) + ".extended_config");
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