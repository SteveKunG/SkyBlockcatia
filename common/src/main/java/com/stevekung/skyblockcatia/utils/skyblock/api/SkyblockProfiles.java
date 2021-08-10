package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.util.Locale;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.skyblock.SBDungeons;
import com.stevekung.skyblockcatia.utils.skyblock.SBSlayers;

public record SkyblockProfiles(Profile[] profiles)
{
    public record DirectProfile(Profile profile) {}
    public record Profile(@SerializedName("game_mode") String gameMode, @SerializedName("profile_id") String profileId, @SerializedName("cute_name") String cuteName, Map<String, Members> members, Banking banking, @SerializedName("community_upgrades") CommunityUpgrades communityUpgrades) {}

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
        @SerializedName("backpack_contents")
        private final Map<Integer, Inventory> backpackInventory;

        private final JsonObject stats;
        private final SBDungeons.Dungeons dungeons;
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

        public Members(long firstJoin, long lastSave, String[] craftedGenerators, Jacob jacob, double farmingExp, double foragingExp, double miningExp, double fishingExp, double runecraftingExp, double combatExp, double enchantingExp, double alchemyExp, double tamingExp, double carpentryExp, int fairySoulsCollected, int fairyExchanges, int deathCount, double purse, Inventory armorInventory, Inventory mainInventory, Inventory enderChestInventory, Inventory vaultInventory, Inventory accessoryInventory, Inventory potionInventory, Inventory fishingInventory, Inventory wardrobeInventory, Inventory quiverInventory, Inventory candyInventory, Map<Integer, Inventory> backpackInventory, JsonObject stats, SBDungeons.Dungeons dungeons, Map<String, Integer> collection, String[] unlockedCollections, Map<String, Integer> sacks, Pets[] pets, JsonObject slayerBoss, SlayerQuest slayerQuest)
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
            this.backpackInventory = backpackInventory;
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

        public Map<Integer, Inventory> getBackpackInventory()
        {
            return this.backpackInventory;
        }

        public JsonObject getStats()
        {
            return this.stats;
        }

        public SBDungeons.Dungeons getDungeons()
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

    public record Banking(BankHistory[] transactions, double balance) {}
    public record Jacob(@SerializedName("medals_inv") MedalInventory medals, FarmingPerks perks) {}
    public record MedalInventory(int gold, int silver, int bronze) {}
    public record FarmingPerks(@SerializedName("double_drops") int doubleDrops, @SerializedName("farming_level_cap") int levelCap) {}
    public record Pets(double exp, String tier, String type, String heldItem, String skin, boolean active, int candyUsed) {}

    public record Inventory(String data)
    {
        public String getData()
        {
            return this.data.replace("\\u003d", "=");
        }
    }

    public record SlayerQuest(String type, int tier)
    {
        public SBSlayers.Type getType()
        {
            return SBSlayers.Type.getSlayerByName(this.type.toUpperCase(Locale.ROOT));
        }
    }
}