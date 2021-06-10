package debug.builder;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.stevekung.skyblockcatia.utils.SupportedPack;

import debug.Helper;

public class MiscBuilder
{
    static final List<SupportedPack> PACKS = Lists.newLinkedList();

    static
    {
        List<String> hqpack16 = Lists.newArrayList("vXF16x_Skyblock_Pack_1.8.9", "vXF16x_Skyblock_Pack_1.12.2", "vXF16x_Skyblock_Pack_1.13", "vXO16x_Skyblock_Pack_1.8.9", "vXO16x_Skyblock_Pack_1.12.2", "vXO16x_Skyblock_Pack_1.13", "v11F16x_Skyblock_Pack_1.8.9", "v11F16x_Skyblock_Pack_1.12.2", "v11F16x_Skyblock_Pack_1.13", "v11O16x_Skyblock_Pack_1.8.9", "v11O16x_Skyblock_Pack_1.12.2", "v11O16x_Skyblock_Pack_1.13", "v12O16x_Skyblock_Pack_1.8.9", "v12F16x_Skyblock_Pack_1.8.9", "v13_16x_Hypixel_Skyblock_Pack");
        List<String> hqpack32 = Lists.newArrayList("vXF32x_Skyblock_Pack_1.8.9", "vXF32x_Skyblock_Pack_1.12.2", "vXF32x_Skyblock_Pack_1.13", "vXO32x_Skyblock_Pack_1.8.9", "vXO32x_Skyblock_Pack_1.12.2", "vXO32x_Skyblock_Pack_1.13", "v11F32x_Skyblock_Pack_1.8.9", "v11F32x_Skyblock_Pack_1.12.2", "v11F32x_Skyblock_Pack_1.13", "v11O32x_Skyblock_Pack_1.8.9", "v11O32x_Skyblock_Pack_1.12.2", "v11O32x_Skyblock_Pack_1.13", "v12O32x_Skyblock_Pack_1.8.9", "v12F32x_Skyblock_Pack_1.8.9", "v13_32x_Hypixel_Skyblock_Pack");
        PACKS.add(new SupportedPack("PACKS_HQ", "Hypixel Skyblock Pack|Skyblock_Pack", "by Hypixel Packs HQ|by Packs HQ", hqpack16, hqpack32));
    }

    public static void main(String[] args)
    {
        Map<String, Object> maps = Maps.newLinkedHashMap();

        maps.put("max_fairy_souls", 227);

        List<Map<String, Object>> packs = Lists.newArrayList();

        for (SupportedPack pack : PACKS)
        {
            Map<String, Object> packmaps = Maps.newLinkedHashMap();
            packmaps.put("type", pack.getType());
            packmaps.put("name", pack.getName());
            packmaps.put("description", pack.getDescription());
            packmaps.put("x16", pack.getPack16());
            packmaps.put("x32", pack.getPack32());
            packs.add(packmaps);
        }

        maps.put("supported_pack", packs);

        File file = new File("M:/Modding/SkyBlockcatia/SkyblockData", "misc.json");
        File file2 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.8.9/src/main/resources/assets/skyblockcatia/api", "misc.json");
        File file3 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.16.5_architectury/common/src/main/resources/assets/skyblockcatia/api", "misc.json");

        Helper.writeFile(maps, file);
        Helper.writeFile(maps, file2);
        Helper.writeFile(maps, file3);
    }
}