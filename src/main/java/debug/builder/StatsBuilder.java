package debug.builder;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import debug.Helper;

public class StatsBuilder
{
    private static final List<String> SEA_CREATURES = Helper.make(Lists.newArrayList(), list ->
    {
        list.add("sea_walker");
        list.add("pond_squid");
        list.add("night_squid");
        list.add("frozen_steve");
        list.add("grinch");
        list.add("yeti");
        list.add("frosty_the_snowman");
        list.add("sea_guardian");
        list.add("sea_archer");
        list.add("sea_witch");
        list.add("chicken_deep");
        list.add("zombie_deep");
        list.add("catfish");
        list.add("sea_leech");
        list.add("deep_sea_protector");
        list.add("water_hydra");
        list.add("skeleton_emperor");
        list.add("guardian_defender");
        list.add("guardian_emperor");
        list.add("carrot_king");
        list.add("nurse_shark");
        list.add("blue_shark");
        list.add("tiger_shark");
        list.add("great_white_shark");
        list.add("nightmare");
        list.add("scarecrow");
        list.add("werewolf");
        list.add("phantom_fisherman");
        list.add("grim_reaper");
        Collections.sort(list);
    });

    private static final Map<String, String> CURRENT_LOCATION_MAP = Helper.make(Maps.newTreeMap(), map ->
    {
        map.put("combat_1", "Spider's Den");
        map.put("combat_2", "Blazing Fortress");
        map.put("combat_3", "The End");
        map.put("dark_auction", "Dark Auction");
        map.put("dungeon", "Dungeon");
        map.put("dungeon_hub", "Dungeon Hub");
        map.put("dynamic", "Private Island");
        map.put("farming_1", "The Barn");
        map.put("farming_2", "Mushroom Desert");
        map.put("foraging_1", "The Park");
        map.put("hub", "Hub");
        map.put("mining_1", "Gold Mine");
        map.put("mining_2", "Deep Caverns");
        map.put("mining_3", "Dwarven Mines");
        map.put("winter", "Jerry's Workshop");
        map.put("crystal_hollows", "Crystal Hollows");
    });

    private static final Map<String, String> RENAMED_STATS_MAP = Helper.make(Maps.newTreeMap(), map ->
    {
        map.put("auctions_bought_common", "common_auctions_bought");
        map.put("auctions_bought_epic", "epic_auctions_bought");
        map.put("auctions_bought_legendary", "legendary_auctions_bought");
        map.put("auctions_bought_rare", "rare_auctions_bought");
        map.put("auctions_bought_special", "special_auctions_bought");
        map.put("auctions_bought_uncommon", "uncommon_auctions_bought");
        map.put("auctions_sold_common", "common_auctions_sold");
        map.put("auctions_sold_epic", "epic_auctions_sold");
        map.put("auctions_sold_legendary", "legendary_auctions_sold");
        map.put("auctions_sold_rare", "rare_auctions_sold");
        map.put("auctions_sold_special", "special_auctions_sold");
        map.put("auctions_sold_uncommon", "uncommon_auctions_sold");
        map.put("items_fished_large_treasure", "large_treasure_items_fished");
        map.put("items_fished_normal", "normal_items_fished");
        map.put("items_fished_treasure", "treasure_items_fished");
        map.put("mythos_burrows_chains_complete_common", "mythos_burrows_common_chains_complete");
        map.put("mythos_burrows_chains_complete_epic", "mythos_burrows_epic_chains_complete");
        map.put("mythos_burrows_chains_complete_legendary", "mythos_burrows_legendary_chains_complete");
        map.put("mythos_burrows_chains_complete_rare", "mythos_burrows_rare_chains_complete");
        map.put("mythos_burrows_dug_combat_common", "mythos_burrows_dug_common_monsters");
        map.put("mythos_burrows_dug_combat_epic", "mythos_burrows_dug_epic_monsters");
        map.put("mythos_burrows_dug_combat_legendary", "mythos_burrows_dug_legendary_monsters");
        map.put("mythos_burrows_dug_combat_rare", "mythos_burrows_dug_rare_monsters");
        map.put("mythos_burrows_dug_next_common", "mythos_burrows_dug_common_arrows");
        map.put("mythos_burrows_dug_next_epic", "mythos_burrows_dug_epic_arrows");
        map.put("mythos_burrows_dug_next_legendary", "mythos_burrows_dug_legendary_arrows");
        map.put("mythos_burrows_dug_next_rare", "mythos_burrows_dug_rare_arrows");
        map.put("mythos_burrows_dug_treasure_common", "mythos_burrows_dug_common_treasure");
        map.put("mythos_burrows_dug_treasure_epic", "mythos_burrows_dug_epic_treasure");
        map.put("mythos_burrows_dug_treasure_legendary", "mythos_burrows_dug_legendary_treasure");
        map.put("mythos_burrows_dug_treasure_rare", "mythos_burrows_dug_rare_treasure");
        map.put("shredder_bait", "bait_used_with_shredder");
    });

    private static final List<String> BLACKLIST_STATS = Helper.make(Lists.newArrayList(), list ->
    {
        list.add("highest_crit_damage");
        list.add("mythos_burrows_dug_combat");
        list.add("mythos_burrows_dug_combat_null");
        list.add("mythos_burrows_dug_treasure");
        list.add("mythos_burrows_dug_next");
        list.add("mythos_burrows_dug_treasure_null");
        list.add("mythos_burrows_chains_complete");
        list.add("mythos_burrows_chains_complete_null");
        list.add("mythos_burrows_dug_next_null");
        Collections.sort(list);
    });

    public static void main(String[] args)
    {
        Map<String, Object> maps = Maps.newTreeMap();

        /*for (Map.Entry<String, String> test : new TreeMap<>(RENAMED_STATS_MAP).entrySet())
        {
            System.out.println("map.put(\"" + test.getKey() + "\", \"" + test.getValue() + "\");");
        }
        for (String test : BLACKLIST_STATS)
        {
            System.out.println("list.add(\"" + test + "\");");
        }*/

        maps.put("blacklist", BLACKLIST_STATS);
        maps.put("current_locations", CURRENT_LOCATION_MAP);
        maps.put("renamed", RENAMED_STATS_MAP);
        maps.put("sea_creatures", SEA_CREATURES);

        File file = new File("M:/Modding/SkyBlockcatia/SkyblockData", "stats.json");
        File file2 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.8.9/src/main/resources/assets/skyblockcatia/api", "stats.json");
        File file3 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.16.5_architectury/common/src/main/resources/assets/skyblockcatia/api", "stats.json");

        Helper.writeFile(maps, file);
        Helper.writeFile(maps, file2);
        Helper.writeFile(maps, file3);
    }
}