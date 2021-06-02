package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.util.Locale;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.skyblock.SBSlayers;

public class SkyblockProfiles
{
    private final Profile[] profiles;

    public SkyblockProfiles(Profile[] profiles)
    {
        this.profiles = profiles;
    }

    public Profile[] getProfiles()
    {
        return this.profiles;
    }

    public static class DirectProfile
    {
        private final Profile profile;

        public DirectProfile(Profile profile)
        {
            this.profile = profile;
        }

        public Profile getProfile()
        {
            return this.profile;
        }
    }

    public static class Profile
    {
        @SerializedName("game_mode")
        private final String gameMode;
        @SerializedName("profile_id")
        private final String profileId;
        @SerializedName("cute_name")
        private final String cuteName;
        private final Map<String, Members> members;
        private final Banking banking;
        @SerializedName("community_upgrades")
        private final CommunityUpgrades communityUpgrades;

        public Profile(String gameMode, String profileId, String cuteName, Map<String, Members> members, Banking banking, CommunityUpgrades communityUpgrades)
        {
            this.gameMode = gameMode;
            this.profileId = profileId;
            this.cuteName = cuteName;
            this.members = members;
            this.banking = banking;
            this.communityUpgrades = communityUpgrades;
        }

        public String getGameMode()
        {
            return this.gameMode;
        }

        public String getProfileId()
        {
            return this.profileId;
        }

        public String getCuteName()
        {
            return this.cuteName;
        }

        public Map<String, Members> getMembers()
        {
            return this.members;
        }

        public Banking getBanking()
        {
            return this.banking;
        }

        public CommunityUpgrades getCommunityUpgrades()
        {
            return this.communityUpgrades;
        }
    }

    public static class Members
    {
        @SerializedName("first_join")
        private final long firstJoin;
        @SerializedName("last_save")
        private final long lastSave;
        @SerializedName("crafted_generators")
        private final String[] craftedGenerators;
        @SerializedName("jacob2")
        private final Jacob jacob;

        @SerializedName("experience_skill_farming")
        private final Double farmingExp;
        @SerializedName("experience_skill_foraging")
        private final Double foragingExp;
        @SerializedName("experience_skill_mining")
        private final Double miningExp;
        @SerializedName("experience_skill_fishing")
        private final Double fishingExp;
        @SerializedName("experience_skill_runecrafting")
        private final Double runecraftingExp;
        @SerializedName("experience_skill_combat")
        private final Double combatExp;
        @SerializedName("experience_skill_enchanting")
        private final Double enchantingExp;
        @SerializedName("experience_skill_alchemy")
        private final Double alchemyExp;
        @SerializedName("experience_skill_taming")
        private final Double tamingExp;
        @SerializedName("experience_skill_carpentry")
        private final Double carpentryExp;

        @SerializedName("fairy_souls_collected")
        private final int fairySoulsCollected;
        @SerializedName("fairy_exchanges")
        private final int fairyExchanges;
        @SerializedName("death_count")
        private final int deathCount;
        @SerializedName("coin_purse")
        private final double purse;

        @SerializedName("inv_armor")
        private final Inventory armorInventory;
        @SerializedName("inv_contents")
        private final Inventory mainInventory;
        @SerializedName("ender_chest_contents")
        private final Inventory enderChestInventory;
        @SerializedName("personal_vault_contents")
        private final Inventory vaultInventory;
        @SerializedName("talisman_bag")
        private final Inventory accessoryInventory;
        @SerializedName("potion_bag")
        private final Inventory potionInventory;
        @SerializedName("fishing_bag")
        private final Inventory fishingInventory;
        @SerializedName("wardrobe_contents")
        private final Inventory wardrobeInventory;
        @SerializedName("quiver")
        private final Inventory quiverInventory;
        @SerializedName("candy_inventory_contents")
        private final Inventory candyInventory;

        private final JsonObject stats;
        private final JsonElement dungeons;
        private final Map<String, Integer> collection;
        @SerializedName("unlocked_coll_tiers")
        private final String[] unlockedCollections;
        @SerializedName("sacks_counts")
        private final Map<String, Integer> sacks;
        private final Pets[] pets;

        @SerializedName("slayer_bosses")
        private final JsonObject slayerBoss;
        @SerializedName("slayer_quest")
        private final SlayerQuest slayerQuest;

        public Members(long firstJoin, long lastSave, String[] craftedGenerators, Jacob jacob, double farmingExp, double foragingExp, double miningExp, double fishingExp, double runecraftingExp, double combatExp, double enchantingExp, double alchemyExp, double tamingExp, double carpentryExp, int fairySoulsCollected, int fairyExchanges, int deathCount, double purse, Inventory armorInventory, Inventory mainInventory, Inventory enderChestInventory, Inventory vaultInventory, Inventory accessoryInventory, Inventory potionInventory, Inventory fishingInventory, Inventory wardrobeInventory, Inventory quiverInventory, Inventory candyInventory, JsonObject stats, JsonElement dungeons, Map<String, Integer> collection, String[] unlockedCollections, Map<String, Integer> sacks, Pets[] pets, JsonObject slayerBoss, SlayerQuest slayerQuest)
        {
            this.firstJoin = firstJoin;
            this.lastSave = lastSave;
            this.craftedGenerators = craftedGenerators;
            this.jacob = jacob;
            this.farmingExp = farmingExp;
            this.foragingExp = foragingExp;
            this.miningExp = miningExp;
            this.fishingExp = fishingExp;
            this.runecraftingExp = runecraftingExp;
            this.combatExp = combatExp;
            this.enchantingExp = enchantingExp;
            this.alchemyExp = alchemyExp;
            this.tamingExp = tamingExp;
            this.carpentryExp = carpentryExp;
            this.fairySoulsCollected = fairySoulsCollected;
            this.fairyExchanges = fairyExchanges;
            this.deathCount = deathCount;
            this.purse = purse;
            this.armorInventory = armorInventory;
            this.mainInventory = mainInventory;
            this.enderChestInventory = enderChestInventory;
            this.vaultInventory = vaultInventory;
            this.accessoryInventory = accessoryInventory;
            this.potionInventory = potionInventory;
            this.fishingInventory = fishingInventory;
            this.wardrobeInventory = wardrobeInventory;
            this.quiverInventory = quiverInventory;
            this.candyInventory = candyInventory;
            this.stats = stats;
            this.dungeons = dungeons;
            this.collection = collection;
            this.unlockedCollections = unlockedCollections;
            this.sacks = sacks;
            this.pets = pets;
            this.slayerBoss = slayerBoss;
            this.slayerQuest = slayerQuest;
        }

        public long getFirstJoin()
        {
            return this.firstJoin > 0 ? this.firstJoin : -1;
        }

        public long getLastSave()
        {
            return this.lastSave > 0 ? this.lastSave : -1;
        }

        public String[] getCraftedGenerators()
        {
            return this.craftedGenerators;
        }

        public Jacob getJacob()
        {
            return this.jacob;
        }

        public Double getFarmingExp()
        {
            return this.farmingExp;
        }

        public Double getForagingExp()
        {
            return this.foragingExp;
        }

        public Double getMiningExp()
        {
            return this.miningExp;
        }

        public Double getFishingExp()
        {
            return this.fishingExp;
        }

        public Double getRunecraftingExp()
        {
            return this.runecraftingExp;
        }

        public Double getCombatExp()
        {
            return this.combatExp;
        }

        public Double getEnchantingExp()
        {
            return this.enchantingExp;
        }

        public Double getAlchemyExp()
        {
            return this.alchemyExp;
        }

        public Double getTamingExp()
        {
            return this.tamingExp;
        }

        public Double getCarpentryExp()
        {
            return this.carpentryExp;
        }

        public int getFairySoulsCollected()
        {
            return this.fairySoulsCollected;
        }

        public int getFairyExchanges()
        {
            return this.fairyExchanges;
        }

        public int getDeathCount()
        {
            return this.deathCount;
        }

        public double getPurse()
        {
            return this.purse;
        }

        public Inventory getArmorInventory()
        {
            return this.armorInventory;
        }

        public Inventory getMainInventory()
        {
            return this.mainInventory;
        }

        public Inventory getEnderChestInventory()
        {
            return this.enderChestInventory;
        }

        public Inventory getVaultInventory()
        {
            return this.vaultInventory;
        }

        public Inventory getAccessoryInventory()
        {
            return this.accessoryInventory;
        }

        public Inventory getPotionInventory()
        {
            return this.potionInventory;
        }

        public Inventory getFishingInventory()
        {
            return this.fishingInventory;
        }

        public Inventory getWardrobeInventory()
        {
            return this.wardrobeInventory;
        }

        public Inventory getQuiverInventory()
        {
            return this.quiverInventory;
        }

        public Inventory getCandyInventory()
        {
            return this.candyInventory;
        }

        public JsonObject getStats()
        {
            return this.stats;
        }

        @Deprecated
        public JsonElement getDungeons()
        {
            return this.dungeons;
        }

        public Map<String, Integer> getCollection()
        {
            return this.collection;
        }

        public String[] getUnlockedCollections()
        {
            return this.unlockedCollections;
        }

        public Map<String, Integer> getSacks()
        {
            return this.sacks;
        }

        public Pets[] getPets()
        {
            return this.pets;
        }

        public SlayerQuest getSlayerQuest()
        {
            return this.slayerQuest;
        }

        public JsonObject getSlayerBoss()
        {
            return this.slayerBoss;
        }
    }

    public static class Banking
    {
        private final BankHistory[] transactions;
        private final double balance;

        public Banking(BankHistory[] transactions, double balance)
        {
            this.transactions = transactions;
            this.balance = balance;
        }

        public BankHistory[] getTransactions()
        {
            return this.transactions;
        }

        public double getBalance()
        {
            return this.balance;
        }
    }

    public static class Jacob
    {
        @SerializedName("medals_inv")
        private final MedalInventory medals;
        private final FarmingPerks perks;

        public Jacob(MedalInventory medals, FarmingPerks perks)
        {
            this.medals = medals;
            this.perks = perks;
        }

        public MedalInventory getMedals()
        {
            return this.medals;
        }

        public FarmingPerks getPerks()
        {
            return this.perks;
        }
    }

    public static class MedalInventory
    {
        private final int gold;
        private final int silver;
        private final int bronze;

        public MedalInventory(int gold, int silver, int bronze)
        {
            this.gold = gold;
            this.silver = silver;
            this.bronze = bronze;
        }

        public int getGold()
        {
            return this.gold;
        }

        public int getSilver()
        {
            return this.silver;
        }

        public int getBronze()
        {
            return this.bronze;
        }
    }

    public static class FarmingPerks
    {
        @SerializedName("double_drops")
        private final int doubleDrops;
        @SerializedName("farming_level_cap")
        private final int levelCap;

        public FarmingPerks(int doubleDrops, int levelCap)
        {
            this.doubleDrops = doubleDrops;
            this.levelCap = levelCap;
        }

        public int getDoubleDrops()
        {
            return this.doubleDrops;
        }

        public int getLevelCap()
        {
            return this.levelCap;
        }
    }

    public static class Pets
    {
        private final double exp;
        private final String tier;
        private final String type;
        private final String heldItem;
        private final String skin;
        private final boolean active;
        private final int candyUsed;

        public Pets(double exp, String tier, String type, String heldItem, String skin, boolean active, int candyUsed)
        {
            this.exp = exp;
            this.tier = tier;
            this.type = type;
            this.heldItem = heldItem;
            this.skin = skin;
            this.active = active;
            this.candyUsed = candyUsed;
        }

        public double getExp()
        {
            return this.exp;
        }

        public String getTier()
        {
            return this.tier;
        }

        public String getType()
        {
            return this.type;
        }

        public String getHeldItem()
        {
            return this.heldItem;
        }

        public String getSkin()
        {
            return this.skin;
        }

        public boolean isActive()
        {
            return this.active;
        }

        public int getCandyUsed()
        {
            return this.candyUsed;
        }
    }

    public static class Inventory
    {
        private final String data;

        public Inventory(String data)
        {
            this.data = data;
        }

        public String getData()
        {
            return this.data.replace("\\u003d", "=");
        }
    }

    public static class SlayerQuest
    {
        private final String type;
        private final int tier;

        public SlayerQuest(String type, int tier)
        {
            this.type = type;
            this.tier = tier;
        }

        public SBSlayers.Type getType()
        {
            return SBSlayers.Type.getSlayerByName(this.type.toUpperCase(Locale.ROOT));
        }

        public int getTier()
        {
            return this.tier;
        }
    }
}