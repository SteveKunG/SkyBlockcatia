package com.stevekung.skyblockcatia.utils.skyblock;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.skyblockcatia.utils.skyblock.api.IBonusTemplate;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public record SBDungeons(int[] leveling, com.stevekung.skyblockcatia.utils.skyblock.SBDungeons.Bonus bonus, @SerializedName("valid_dungeons") List<String> validDungeons)
{
    public static SBDungeons DUNGEONS;

    public static void getDungeons()
    {
        DUNGEONS = TextComponentUtils.GSON.fromJson(DataUtils.getData("dungeons.json"), SBDungeons.class);
    }

    public record Bonus(Catacombs[] catacombs) {}

    public record Catacombs(int level, double health) implements IBonusTemplate
    {
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

    public record Dungeons(@SerializedName("dungeon_types") Map<String, TypeData> dungeonTypes, @SerializedName("selected_dungeon_class") String selectedClass, @SerializedName("player_classes") Map<String, Exp> playerClasses) {}
    public record TypeData(double experience, @SerializedName("highest_tier_completed") int highestFloorCompleted, @SerializedName("times_played") Map<Integer, Integer> timesPlayed, @SerializedName("tier_completions") Map<Integer, Integer> floorCompletions, @SerializedName("fastest_time") Map<Integer, Integer> fastestTime, @SerializedName("best_runs") Map<Integer, List<Runs>> bestRuns, @SerializedName("best_score") Map<Integer, Integer> bestScores, @SerializedName("mobs_killed") Map<Integer, Integer> mobsKilled, @SerializedName("most_mobs_killed") Map<Integer, Integer> mostMobsKilled, @SerializedName("most_damage_berserk") Map<Integer, Float> mostBerserkDamage, @SerializedName("most_damage_mage") Map<Integer, Float> mostMageDamage, @SerializedName("most_healing") Map<Integer, Float> mostHealing, @SerializedName("watcher_kills") Map<Integer, Integer> watcherKills, @SerializedName("fastest_time_s") Map<Integer, Integer> sFastestTime, @SerializedName("fastest_time_s_plus") Map<Integer, Integer> sPlusFastestTime, @SerializedName("milestone_completions") Map<Integer, Integer> milestoneCompletions) {}
    public record Runs(long timestamp, @SerializedName("dungeon_class") String dungeonClass, @SerializedName("score_skill") int skillScore, @SerializedName("score_speed") int speedScore, @SerializedName("score_exploration") int explorationScore, @SerializedName("score_bonus") int bonusScore, List<String> teammates, @SerializedName("elapsed_time") int elapsedTime, @SerializedName("damage_dealt") double damageDealt, @SerializedName("damage_mitigated") double damageMitigated, int deaths, @SerializedName("mobs_killed") int mobsKilled, @SerializedName("secrets_found") int secretsFound) {}
    public record PlayerClasses(Exp healer, Exp mage, Exp berserk, Exp archer, Exp tank) {}
    public record Exp(double experience) {}

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
        SPIRIT_LEAP(TextComponentUtils.formatted("Spirit Leap", ChatFormatting.BLUE), Items.ENDER_PEARL, true),
        DUNGEON_DECOY(TextComponentUtils.formatted("Decoy", ChatFormatting.GREEN), Items.POLAR_BEAR_SPAWN_EGG, false),
        INFLATABLE_JERRY(TextComponentUtils.formatted("Inflatable Jerry", ChatFormatting.WHITE), Items.VILLAGER_SPAWN_EGG, false),
        DUNGEON_TRAP(TextComponentUtils.formatted("Dungeon Trap", ChatFormatting.GREEN), Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, false);

        private final Component displayName;
        private final ItemLike baseItem;
        private final boolean enchanted;

        Drops(Component displayName, ItemLike baseItem, boolean enchanted)
        {
            this.displayName = displayName;
            this.baseItem = baseItem;
            this.enchanted = enchanted;
        }

        public Component getDisplayName()
        {
            return this.displayName;
        }

        public ItemLike getBaseItem()
        {
            return this.baseItem;
        }

        public boolean isEnchanted()
        {
            return this.enchanted;
        }
    }
}