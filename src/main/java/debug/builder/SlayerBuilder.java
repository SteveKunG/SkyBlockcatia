package debug.builder;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import debug.Helper;

public class SlayerBuilder
{
    static final String ZOMBIE = "5,15,200,1000,5000,20000,100000,400000,1000000";
    static final String SPIDER = "5,25,200,1000,5000,20000,100000,400000,1000000";
    static final String WOLF = "10,30,250,1500,5000,20000,100000,400000,1000000";
    static final String ENDERMAN = "10,30,250,1500,5000,20000,100000,400000,1000000";

    static final List<SlayerBonus> ZOMBIE_BONUS = Helper.make(Lists.newLinkedList(), list ->
    {
        list.add(new SlayerBonus(1, "health=2"));
        list.add(new SlayerBonus(3, "health=3"));
        list.add(new SlayerBonus(5, "health=4"));
        list.add(new SlayerBonus(7, "health=5"));
        list.add(new SlayerBonus(9, "health=6"));
    });
    static final List<SlayerBonus> SPIDER_BONUS = Helper.make(Lists.newLinkedList(), list ->
    {
        list.add(new SlayerBonus(1, "crit_damage=1"));
        list.add(new SlayerBonus(5, "crit_damage=2"));
        list.add(new SlayerBonus(7, "crit_chance=1"));
        list.add(new SlayerBonus(8, "crit_damage=3"));
    });
    static final List<SlayerBonus> WOLF_BONUS = Helper.make(Lists.newLinkedList(), list ->
    {
        list.add(new SlayerBonus(1, "speed=1"));
        list.add(new SlayerBonus(2, "health=2"));
        list.add(new SlayerBonus(3, "speed=1"));
        list.add(new SlayerBonus(4, "health=2"));
        list.add(new SlayerBonus(5, "crit_damage=1"));
        list.add(new SlayerBonus(6, "health=3"));
        list.add(new SlayerBonus(7, "crit_damage=2"));
        list.add(new SlayerBonus(8, "speed=1"));
    });
    static final List<SlayerBonus> ENDERMAN_BONUS = Helper.make(Lists.newLinkedList(), list ->
    {
        list.add(new SlayerBonus(1, "health=1"));
        list.add(new SlayerBonus(2, "intelligence=2"));
        list.add(new SlayerBonus(3, "health=2"));
        list.add(new SlayerBonus(4, "intelligence=2"));
        list.add(new SlayerBonus(5, "health=3"));
        list.add(new SlayerBonus(6, "intelligence=3"));
        list.add(new SlayerBonus(7, "health=4"));
        list.add(new SlayerBonus(8, "intelligence=4"));
        list.add(new SlayerBonus(9, "health=5"));
    });

    static final Map<Integer, Integer> SLAYER_PRICES = Helper.make(Maps.newLinkedHashMap(), map ->
    {
        map.put(0, 2000);
        map.put(1, 7500);
        map.put(2, 20000);
        map.put(3, 50000);
        map.put(4, 100000);
    });

    public static void main(String[] args)
    {
        Map<String, Object> maps = Maps.newLinkedHashMap();
        Map<String, Object> levelMaps = Maps.newLinkedHashMap();

        addLevel(levelMaps, ZOMBIE, "zombie");
        addLevel(levelMaps, SPIDER, "spider");
        addLevel(levelMaps, WOLF, "wolf");
        addLevel(levelMaps, ENDERMAN, "enderman");

        Map<String, Object> bonusMap = Maps.newLinkedHashMap();

        addBonus(bonusMap, ZOMBIE_BONUS, "zombie");
        addBonus(bonusMap, SPIDER_BONUS, "spider");
        addBonus(bonusMap, WOLF_BONUS, "wolf");
        addBonus(bonusMap, ENDERMAN_BONUS, "enderman");

        maps.put("leveling", levelMaps);
        maps.put("bonus", bonusMap);
        maps.put("price", SLAYER_PRICES);

        Helper.exportJson(maps, "slayers");
    }

    static void addLevel(Map<String, Object> levelMaps, String slayerlevel, String type)
    {
        List<Integer> levels = Lists.newArrayList();

        for (String level : slayerlevel.split(","))
        {
            levels.add(Integer.parseInt(level));
        }

        levelMaps.put(type, levels);
    }

    static void addBonus(Map<String, Object> bonusMap, List<SlayerBonus> bonusListTemp, String type)
    {
        List<Map<String, Object>> bonusList = Lists.newLinkedList();

        for (SlayerBonus bos : bonusListTemp)
        {
            Map<String, Object> bonus = Maps.newLinkedHashMap();
            bonus.put("level", bos.level);
            String[] split = bos.bonus.split("=");
            bonus.put(split[0], Integer.parseInt(split[1]));
            bonusList.add(bonus);
        }
        bonusMap.put(type, bonusList);
    }

    static class SlayerBonus
    {
        int level;
        String bonus;

        SlayerBonus(int level, String bonus)
        {
            this.level = level;
            this.bonus = bonus;
        }
    }
}