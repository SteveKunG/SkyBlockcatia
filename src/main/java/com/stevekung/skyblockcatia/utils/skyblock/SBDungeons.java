package com.stevekung.skyblockcatia.utils.skyblock;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.skyblockcatia.utils.skyblock.api.IBonusTemplate;

import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class SBDungeons
{
    private static final Gson GSON = new Gson();
    public static SBDungeons DUNGEONS;

    private final int[] leveling;
    private final Bonus bonus;
    @SerializedName("valid_dungeons")
    private final List<String> validDungeons;

    public SBDungeons(int[] leveling, Bonus bonus, List<String> validDungeons)
    {
        this.leveling = leveling;
        this.bonus = bonus;
        this.validDungeons = validDungeons;
    }

    public int[] getLeveling()
    {
        return this.leveling;
    }

    public Bonus getBonus()
    {
        return this.bonus;
    }

    public List<String> getValidDungeons()
    {
        return this.validDungeons;
    }

    public static void getDungeons() throws IOException
    {
        DUNGEONS = GSON.fromJson(DataUtils.getData("dungeons.json"), SBDungeons.class);
    }

    public class Bonus
    {
        private final Catacombs[] catacombs;

        public Bonus(Catacombs[] catacombs)
        {
            this.catacombs = catacombs;
        }

        public Catacombs[] getCatacombs()
        {
            return this.catacombs;
        }
    }

    public class Catacombs implements IBonusTemplate
    {
        private final int level;
        private final double health;

        public Catacombs(int level, double health)
        {
            this.level = level;
            this.health = health;
        }

        @Override
        public int getLevel()
        {
            return this.level;
        }

        @Override
        public double getHealth()
        {
            return this.health;
        }
    }

    public class Dungeons
    {
        @SerializedName("dungeon_types")
        private final Map<String, TypeData> dungeonTypes;
        @SerializedName("selected_dungeon_class")
        private final String selectedClass;
        @SerializedName("player_classes")
        private final Map<String, Exp> playerClasses;

        public Dungeons(Map<String, TypeData> dungeonTypes, String selectedClass, Map<String, Exp> playerClasses)
        {
            this.dungeonTypes = dungeonTypes;
            this.selectedClass = selectedClass;
            this.playerClasses = playerClasses;
        }

        public Map<String, TypeData> getDungeonTypes()
        {
            return this.dungeonTypes;
        }

        public String getSelectedClass()
        {
            return this.selectedClass;
        }

        public Map<String, Exp> getPlayerClasses()
        {
            return this.playerClasses;
        }
    }

    public class TypeData
    {
        private final double experience;
        @SerializedName("highest_tier_completed")
        private final int highestFloorCompleted;
        @SerializedName("times_played")
        private final Map<Integer, Integer> timesPlayed;
        @SerializedName("tier_completions")
        private final Map<Integer, Integer> floorCompletions;
        @SerializedName("fastest_time")
        private final Map<Integer, Integer> fastestTime;
        @SerializedName("best_runs")
        private final Map<Integer, List<Runs>> bestRuns;
        @SerializedName("best_score")
        private final Map<Integer, Integer> bestScores;
        @SerializedName("mobs_killed")
        private final Map<Integer, Integer> mobsKilled;
        @SerializedName("most_mobs_killed")
        private final Map<Integer, Integer> mostMobsKilled;
        @SerializedName("most_damage_berserk")
        private final Map<Integer, Float> mostBerserkDamage;
        @SerializedName("most_damage_mage")
        private final Map<Integer, Float> mostMageDamage;
        @SerializedName("most_healing")
        private final Map<Integer, Float> mostHealing;
        @SerializedName("watcher_kills")
        private final Map<Integer, Integer> watcherKills;
        @SerializedName("fastest_time_s")
        private final Map<Integer, Integer> sFastestTime;
        @SerializedName("fastest_time_s_plus")
        private final Map<Integer, Integer> sPlusFastestTime;
        @SerializedName("milestone_completions")
        private final Map<Integer, Integer> milestoneCompletions;

        public TypeData(double experience, int highestFloorCompleted, Map<Integer, Integer> timesPlayed, Map<Integer, Integer> floorCompletions,
                Map<Integer, Integer> fastestTime, Map<Integer, List<Runs>> bestRuns, Map<Integer, Integer> bestScores, Map<Integer, Integer> mobsKilled,
                Map<Integer, Integer> mostMobsKilled, Map<Integer, Float> mostBerserkDamage, Map<Integer, Float> mostMageDamage,
                Map<Integer, Float> mostHealing, Map<Integer, Integer> watcherKills, Map<Integer, Integer> sFastestTime, Map<Integer, Integer> sPlusFastestTime,
                Map<Integer, Integer> milestoneCompletions)
        {
            this.experience = experience;
            this.highestFloorCompleted = highestFloorCompleted;
            this.timesPlayed = timesPlayed;
            this.floorCompletions = floorCompletions;
            this.fastestTime = fastestTime;
            this.bestRuns = bestRuns;
            this.bestScores = bestScores;
            this.mobsKilled = mobsKilled;
            this.mostMobsKilled = mostMobsKilled;
            this.mostBerserkDamage = mostBerserkDamage;
            this.mostMageDamage = mostMageDamage;
            this.mostHealing = mostHealing;
            this.watcherKills = watcherKills;
            this.sFastestTime = sFastestTime;
            this.sPlusFastestTime = sPlusFastestTime;
            this.milestoneCompletions = milestoneCompletions;
        }

        public double getExperience()
        {
            return this.experience;
        }

        public int getHighestFloorCompleted()
        {
            return this.highestFloorCompleted;
        }

        public Map<Integer, Integer> getTimesPlayed()
        {
            return this.timesPlayed;
        }

        public Map<Integer, Integer> getFloorCompletions()
        {
            return this.floorCompletions;
        }

        public Map<Integer, Integer> getFastestTime()
        {
            return this.fastestTime;
        }

        public Map<Integer, List<Runs>> getBestRuns()
        {
            return this.bestRuns;
        }

        public Map<Integer, Integer> getBestScores()
        {
            return this.bestScores;
        }

        public Map<Integer, Integer> getMobsKilled()
        {
            return this.mobsKilled;
        }

        public Map<Integer, Integer> getMostMobsKilled()
        {
            return this.mostMobsKilled;
        }

        public Map<Integer, Float> getMostBerserkDamage()
        {
            return this.mostBerserkDamage;
        }

        public Map<Integer, Float> getMostMageDamage()
        {
            return this.mostMageDamage;
        }

        public Map<Integer, Float> getMostHealing()
        {
            return this.mostHealing;
        }

        public Map<Integer, Integer> getWatcherKills()
        {
            return this.watcherKills;
        }

        public Map<Integer, Integer> getsFastestTime()
        {
            return this.sFastestTime;
        }

        public Map<Integer, Integer> getsPlusFastestTime()
        {
            return this.sPlusFastestTime;
        }

        public Map<Integer, Integer> getMilestoneCompletions()
        {
            return this.milestoneCompletions;
        }
    }

    public class Runs
    {
        private final long timestamp;
        @SerializedName("dungeon_class")
        private final String dungeonClass;
        @SerializedName("score_skill")
        private final int skillScore;
        @SerializedName("score_speed")
        private final int speedScore;
        @SerializedName("score_exploration")
        private final int explorationScore;
        @SerializedName("score_bonus")
        private final int bonusScore;
        private final List<String> teammates;
        @SerializedName("elapsed_time")
        private final int elapsedTime;
        @SerializedName("damage_dealt")
        private final double damageDealt;
        @SerializedName("damage_mitigated")
        private final double damageMitigated;
        private final int deaths;
        @SerializedName("mobs_killed")
        private final int mobsKilled;
        @SerializedName("secrets_found")
        private final int secretsFound;

        public Runs(long timestamp, String dungeonClass, int skillScore, int speedScore, int explorationScore, int bonusScore, List<String> teammates,
                int elapsedTime, double damageDealt, double damageMitigated, int deaths, int mobsKilled, int secretsFound)
        {
            this.timestamp = timestamp;
            this.dungeonClass = dungeonClass;
            this.skillScore = skillScore;
            this.speedScore = speedScore;
            this.explorationScore = explorationScore;
            this.bonusScore = bonusScore;
            this.teammates = teammates;
            this.elapsedTime = elapsedTime;
            this.damageDealt = damageDealt;
            this.damageMitigated = damageMitigated;
            this.deaths = deaths;
            this.mobsKilled = mobsKilled;
            this.secretsFound = secretsFound;
        }

        public long getTimestamp()
        {
            return this.timestamp;
        }

        public String getDungeonClass()
        {
            return this.dungeonClass;
        }

        public int getSkillScore()
        {
            return this.skillScore;
        }

        public int getSpeedScore()
        {
            return this.speedScore;
        }

        public int getExplorationScore()
        {
            return this.explorationScore;
        }

        public int getBonusScore()
        {
            return this.bonusScore;
        }

        public List<String> getTeammates()
        {
            return this.teammates;
        }

        public int getElapsedTime()
        {
            return this.elapsedTime;
        }

        public double getDamageDealt()
        {
            return this.damageDealt;
        }

        public double getDamageMitigated()
        {
            return this.damageMitigated;
        }

        public int getDeaths()
        {
            return this.deaths;
        }

        public int getMobsKilled()
        {
            return this.mobsKilled;
        }

        public int getSecretsFound()
        {
            return this.secretsFound;
        }
    }

    public class PlayerClasses
    {
        private final Exp healer;
        private final Exp mage;
        private final Exp berserk;
        private final Exp archer;
        private final Exp tank;

        public PlayerClasses(Exp healer, Exp mage, Exp berserk, Exp archer, Exp tank)
        {
            this.healer = healer;
            this.mage = mage;
            this.berserk = berserk;
            this.archer = archer;
            this.tank = tank;
        }

        public Exp getHealer()
        {
            return this.healer;
        }

        public Exp getMage()
        {
            return this.mage;
        }

        public Exp getBerserk()
        {
            return this.berserk;
        }

        public Exp getArcher()
        {
            return this.archer;
        }

        public Exp getTank()
        {
            return this.tank;
        }
    }

    public class Exp
    {
        private final double experience;

        public Exp(double experience)
        {
            this.experience = experience;
        }

        public double getExperience()
        {
            return this.experience;
        }
    }

    public enum Class
    {
        HEALER("Healer"),
        MAGE("Mage"),
        BERSERK("Berserk"),
        ARCHER("Archer"),
        TANK("Tank");

        private final String name;

        Class(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return this.name;
        }
    }

    public enum Drops
    {
        SPIRIT_LEAP(EnumChatFormatting.RESET.toString() + EnumChatFormatting.BLUE + "Spirit Leap", new ItemStack(Items.ender_pearl), true),
        DUNGEON_DECOY(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Decoy", new ItemStack(Items.spawn_egg), false),
        INFLATABLE_JERRY(EnumChatFormatting.RESET.toString() + EnumChatFormatting.WHITE + "Inflatable Jerry", new ItemStack(Items.spawn_egg, 0, EntityList.getIDFromString("Villager")), false),
        DUNGEON_TRAP(EnumChatFormatting.RESET.toString() + EnumChatFormatting.GREEN + "Dungeon Trap", new ItemStack(Blocks.heavy_weighted_pressure_plate), false);

        private final String displayName;
        private final ItemStack baseItem;
        private final boolean enchanted;

        Drops(String displayName, ItemStack baseItem, boolean enchanted)
        {
            this.displayName = displayName;
            this.baseItem = baseItem;
            this.enchanted = enchanted;
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public ItemStack getBaseItem()
        {
            return this.baseItem;
        }

        public boolean isEnchanted()
        {
            return this.enchanted;
        }
    }
}