package debug.builder;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import debug.Helper;

public class SkillBuilder
{
    static final List<Cap> CAPS = Lists.newLinkedList();
    static final List<Bonus> FARMING = Lists.newLinkedList();
    static final List<Bonus> MINING = Lists.newLinkedList();
    static final List<Bonus> FORAGING = Lists.newLinkedList();
    static final List<Bonus> FISHING = Lists.newLinkedList();
    static final List<Bonus> ENCHANTING = Lists.newLinkedList();
    static final List<Bonus> ALCHEMY = Lists.newLinkedList();
    static final List<Bonus> TAMING = Lists.newLinkedList();

    static final String SKILL = "50,125,200,300,500,750,1000,1500,2000,3500,5000,7500,10000,15000,20000,30000,50000,75000,100000,200000,300000,400000,500000,600000,700000,800000,900000,1000000,1100000,1200000,1300000,1400000,1500000,1600000,1700000,1800000,1900000,2000000,2100000,2200000,2300000,2400000,2500000,2600000,2750000,2900000,3100000,3400000,3700000,4000000,4300000,4600000,4900000,5200000,5500000,5800000,6100000,6400000,6700000,7000000";
    static final String RUNE = "50,100,125,160,200,250,315,400,500,625,785,1000,1250,1600,2000,2465,3125,4000,5000,6200,7800,9800,12200,15300,19050";

    static
    {
        CAPS.add(new Cap("farming", 50));
        CAPS.add(new Cap("mining", 60));
        CAPS.add(new Cap("combat", 60));
        CAPS.add(new Cap("foraging", 50));
        CAPS.add(new Cap("fishing", 50));
        CAPS.add(new Cap("enchanting", 60));
        CAPS.add(new Cap("alchemy", 50));
        CAPS.add(new Cap("taming", 50));
        CAPS.add(new Cap("carpentry", 50));
        CAPS.add(new Cap("runecrafting", 25));

        FARMING.add(new Bonus(1, "health=2,farming_fortune=4"));
        FARMING.add(new Bonus(15, "health=3,farming_fortune=4"));
        FARMING.add(new Bonus(20, "health=4,farming_fortune=4"));
        FARMING.add(new Bonus(26, "health=5,farming_fortune=4"));

        MINING.add(new Bonus(1, "defense=1,mining_fortune=4"));
        MINING.add(new Bonus(15, "defense=2,mining_fortune=4"));

        FORAGING.add(new Bonus(1, "strength=1,foraging_fortune=4"));
        FORAGING.add(new Bonus(15, "strength=2,foraging_fortune=4"));

        FISHING.add(new Bonus(1, "health=2"));
        FISHING.add(new Bonus(15, "health=3"));
        FISHING.add(new Bonus(20, "health=4"));
        FISHING.add(new Bonus(26, "health=5"));

        ENCHANTING.add(new Bonus(1, "intelligence=1,ability_damage=0.5"));
        ENCHANTING.add(new Bonus(15, "intelligence=2,ability_damage=0.5"));

        ALCHEMY.add(new Bonus(1, "intelligence=1"));
        ALCHEMY.add(new Bonus(15, "intelligence=2"));

        TAMING.add(new Bonus(1, "pet_luck=1"));
    }

    public static void main(String[] args)
    {
        Map<String, Object> maps = Maps.newLinkedHashMap();
        Map<String, Object> caps = Maps.newLinkedHashMap();

        for (Cap cap : CAPS)
        {
            caps.put(cap.type, cap.cap);
        }

        maps.put("cap", caps);

        Map<String, Object> levelingMap = Maps.newLinkedHashMap();

        addSkill(levelingMap, "default", SKILL);
        addSkill(levelingMap, "runecrafting", RUNE);

        maps.put("leveling", levelingMap);

        Map<String, Object> bonusMap = Maps.newLinkedHashMap();
        addBonus(bonusMap, FARMING, "farming");
        addBonus(bonusMap, MINING, "mining");
        addBonus(bonusMap, Collections.singletonList(new Bonus(1, "crit_chance=0.5")), "combat");
        addBonus(bonusMap, FORAGING, "foraging");
        addBonus(bonusMap, FISHING, "fishing");
        addBonus(bonusMap, ENCHANTING, "enchanting");
        addBonus(bonusMap, ALCHEMY, "alchemy");
        addBonus(bonusMap, TAMING, "taming");
        maps.put("bonus", bonusMap);

        File file = new File("M:/Modding/SkyBlockcatia/SkyblockData", "skills.json");
        File file2 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.8.9/src/main/resources/assets/skyblockcatia/api", "skills.json");
        File file3 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.16.5_architectury/common/src/main/resources/assets/skyblockcatia/api", "skills.json");

        Helper.writeFile(maps, file);
        Helper.writeFile(maps, file2);
        Helper.writeFile(maps, file3);
    }

    static void addSkill(Map<String, Object> levelingMap, String type, String skill)
    {
        String[] expSplit = skill.split(",");
        List<Integer> levels = Lists.newLinkedList();

        for (String element : expSplit)
        {
            levels.add(Integer.valueOf(element));
        }
        levelingMap.put(type, levels);
    }

    static void addBonus(Map<String, Object> bonusMap, List<Bonus> bonusListTemp, String type)
    {
        List<Map<String, Object>> bonusList = Lists.newLinkedList();

        for (Bonus bos : bonusListTemp)
        {
            Map<String, Object> bonus = Maps.newLinkedHashMap();
            bonus.put("level", bos.level);
            String[] commaSplit = bos.bonus.split(",");

            for (String comma : commaSplit)
            {
                String[] split = comma.split("=");

                try
                {
                    bonus.put(split[0], Integer.parseInt(split[1]));
                }
                catch (NumberFormatException e)
                {
                    bonus.put(split[0], Double.parseDouble(split[1]));
                }
            }
            bonusList.add(bonus);
        }
        bonusMap.put(type, bonusList);
    }

    static class Cap
    {
        String type;
        int cap;

        public Cap(String type, int cap)
        {
            this.type = type;
            this.cap = cap;
        }
    }

    static class Bonus
    {
        int level;
        String bonus;

        public Bonus(int level, String bonus)
        {
            this.level = level;
            this.bonus = bonus;
        }
    }
}