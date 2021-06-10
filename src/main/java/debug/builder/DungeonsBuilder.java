package debug.builder;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import debug.Helper;

public class DungeonsBuilder
{
    static final List<Bonus> CATACOMBS = Lists.newLinkedList();
    static final List<String> VALID_DUNGEONS = Lists.newLinkedList();
    static final String CATACOMBS_LEVELING = "50,75,110,160,230,330,470,670,950,1340,1890,2665,3760,5260,7380,10300,14400,20000,27600,38000,52500,71500,97000,132000,180000,243000,328000,445000,600000,800000,1065000,1410000,1900000,2500000,3300000,4300000,5600000,7200000,9200000,12000000,15000000,19000000,24000000,30000000,38000000,48000000,60000000,75000000,93000000,116250000";

    static
    {
        CATACOMBS.add(new Bonus(1, "health=1"));
        VALID_DUNGEONS.add("catacombs");
    }

    public static void main(String[] args)
    {
        Map<String, Object> maps = Maps.newLinkedHashMap();
        
        String[] expSplit = CATACOMBS_LEVELING.split(",");
        List<Integer> levels = Lists.newLinkedList();

        for (String element : expSplit)
        {
            levels.add(Integer.valueOf(element));
        }

        maps.put("leveling", levels);

        Map<String, Object> bonusMap = Maps.newLinkedHashMap();
        addBonus(bonusMap, CATACOMBS, "catacombs");

        maps.put("bonus", bonusMap);
        maps.put("valid_dungeons", VALID_DUNGEONS);

        File file = new File("M:/Modding/SkyBlockcatia/SkyblockData", "dungeons.json");
        File file2 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.8.9/src/main/resources/assets/skyblockcatia/api", "dungeons.json");
        File file3 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.16.5_architectury/common/src/main/resources/assets/skyblockcatia/api", "dungeons.json");

        Helper.writeFile(maps, file);
        Helper.writeFile(maps, file2);
        Helper.writeFile(maps, file3);
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