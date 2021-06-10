package debug.builder;

import java.io.File;
import java.util.*;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.text.WordUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import debug.Helper;

public class MinionsBuilder
{
    static final String CRAFTED_MINIONS = "0,5,15,30,50,75,100,125,150,175,200,225,250,275,300,350,400,450,500,550,600";
    static final String MINION_SLOT = "5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25";

    public static void main(String[] args)
    {
        Map<String, Object> maps = Maps.newLinkedHashMap();
        List<Map<String, Object>> minionTypeList = Lists.newArrayList();

        List<MinionType> minionTypeSort = Lists.newArrayList();
        minionTypeSort.addAll(EnumSet.allOf(MinionType.class));
        Collections.sort(minionTypeSort, (o1, o2) -> new CompareToBuilder().append(o1.name(), o2.name()).build());

        for (MinionType type : minionTypeSort)
        {
            Map<String, Object> prop = Maps.newLinkedHashMap();
            String displayName = type.altName != null ? WordUtils.capitalize(type.altName.toLowerCase(Locale.ROOT).replace("_", " ")) : WordUtils.capitalize(type.name().toLowerCase(Locale.ROOT).replace("_", " "));

            prop.put("type", type.name());
            prop.put("category", type.category);
            prop.put("displayName", displayName + " Minion");
            prop.put("uuid", type.uuid);
            prop.put("texture", type.value);

            if (type.hasTier12)
            {
                prop.put("has_tier_12", type.hasTier12);
            }

            minionTypeList.add(prop);
        }

        Map<String, Object> slots = Maps.newLinkedHashMap();

        for (int i = 0; i < CRAFTED_MINIONS.split(",").length; i++)
        {
            slots.put(CRAFTED_MINIONS.split(",")[i], Integer.parseInt(MINION_SLOT.split(",")[i]));
        }

        maps.put("unique_minions", 620);
        maps.put("crafted_minions", slots);
        maps.put("type", minionTypeList);

        File file = new File("M:/Modding/SkyBlockcatia/SkyblockData", "minions.json");
        File file2 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.8.9/src/main/resources/assets/skyblockcatia/api", "minions.json");
        File file3 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.16.5_architectury/common/src/main/resources/assets/skyblockcatia/api", "minions.json");

        Helper.writeFile(maps, file);
        Helper.writeFile(maps, file2);
        Helper.writeFile(maps, file3);
    }

    public enum MinionType
    {
        COBBLESTONE("MINING", "44f3d931-5b9a-35a1-a84b-669755c7c7ee", "2f93289a82bd2a06cbbe61b733cfdc1f1bd93c4340f7a90abd9bdda774109071", true),
        OBSIDIAN("MINING", "58c0f4df-db55-3492-be5d-2e72f289aaa9", "320c29ab966637cb9aecc34ee76d5a0130461e0c4fdb08cdaf80939fa1209102", true),
        GLOWSTONE("MINING", "985fcfc1-6922-3fdf-8247-13ffd7def691", "20f4d7c26b0310990a7d3a3b45948b95dd4ab407a16a4b6d3b7cb4fba031aeed"),
        GRAVEL("MINING", "b499f2a6-291f-3626-a36d-a888e4a2e424", "7458507ed31cf9a38986ac8795173c609637f03da653f30483a721d3fbe602d"),
        SAND("MINING", "49a80bce-61b1-3f83-b2b4-5122c16a23fe", "81f8e2ad021eefd1217e650e848b57622144d2bf8a39fbd50dab937a7eac10de"),
        CLAY("FISHING", "1f9105c0-7873-3e6d-ae1a-a9a5ca044733", "af9b312c8f53da289060e6452855072e07971458abbf338ddec351e16c171ff8"),
        ICE("MINING", "017c5692-85da-306e-8013-a11fd5f74b59", "e500064321b12972f8e5750793ec1c823da4627535e9d12feaee78394b86dabe"),
        SNOW("MINING", "fde00899-094f-3ff8-81b8-253830a8d3e9", "f6d180684c3521c9fc89478ba4405ae9ce497da8124fa0da5a0126431c4b78c3"),
        COAL("MINING", "304f13a1-ca59-3db9-8194-68d1c07352d1", "425b8d2ea965c780652d29c26b1572686fd74f6fe6403b5a3800959feb2ad935", true),
        IRON("MINING", "785de520-c4f3-371c-88c8-c8f560bc4977", "af435022cb3809a68db0fccfa8993fc1954dc697a7181494905b03fdda035e4a", true),
        GOLD("MINING", "ef5b80f5-2ddc-3005-b441-784230573c60", "f6da04ed8c810be29bba53c62e712d65cfb25238117b94d7e85a4615775bf14f", true),
        DIAMOND("MINING", "50cb7221-fcc7-3450-a8ee-6d7593ebec22", "2354bbe604dfe58bf92e7729730d0c8e37844e831ee3816d7e8427c27a1824a2", true),
        LAPIS("MINING", "7ef29dbe-89c9-3372-80c8-edf8955b285a", "64fd97b9346c1208c1db3957530cdfc5789e3e65943786b0071cf2b2904a6b5c", true),
        REDSTONE("MINING", "a4fff41c-bae4-393c-8d97-e98075ccb590", "1edefcf1a89d687a0a4ecf1589977af1e520fc673c48a0434be426612e8faa67", true),
        EMERALD("MINING", "8383fe2e-6c76-32cf-8e5f-264b13de62ae", "9bf57f3401b130c6b53808f2b1e119cc7b984622dac7077bbd53454e1f65bbf0", true),
        QUARTZ("MINING", "26774528-cfaf-398d-9e9c-e68e8761d1ed", "d270093be62dfd3019f908043db570b5dfd366fd5345fccf9da340e75c701a60"),
        ENDER_STONE("MINING", "END_STONE", "13276fea-088f-32f2-bf79-a3201607ec56", "7994be3dcfbb4ed0a5a7495b7335af1a3ced0b5888b5007286a790767c3b57e6"),
        MITHRIL("MINING", "671ce3e7-e3d7-3add-86fb-2c7854f651ad", "c62fa670ff8599b32ab344195ba15f3ef64c3a8aa8a37821c08375950cb74cd0", true),
        WHEAT("FARMING", "81b06ed8-e9ec-34e2-b11f-234eb6e20318", "bbc571c5527336352e2fee2b40a9edfa2e809f64230779aa01253c6aa535881b", true),
        MELON("FARMING", "845dc766-7f65-3750-9807-461185f480ab", "95d54539ac8d3fba9696c91f4dcc7f15c320ab86029d5c92f12359abd4df811e", true),
        PUMPKIN("FARMING", "a25aead0-758a-3ced-8153-84362a361c47", "f3fb663e843a7da787e290f23c8af2f97f7b6f572fa59a0d4d02186db6eaabb7", true),
        CARROT("FARMING", "3a394733-b862-3676-ab4f-60fca79bd18c", "4baea990b45d330998cb0c1f8515c27b24f93bff1df0db056e647f8200d03b9d", true),
        POTATO("FARMING", "4a8b2791-3dd0-3376-9dbf-5683f3dac782", "7dda35a044cb0374b516015d991a0f65bf7d0fb6566e350496642cf2059ff1d9", true),
        MUSHROOM("FARMING", "4c4116e2-193d-32a4-948b-39ae936ed909", "4a3b58341d196a9841ef1526b367209cbc9f96767c24f5f587cf413d42b74a93", true),
        CACTUS("FARMING", "78b57eb1-ea68-3072-a861-9c89a2f467cd", "ef93ec6e67a6cd272c9a9684b67df62584cb084a265eee3cde141d20e70d7d72", true),
        COCOA("FARMING", "a1448e51-e34a-3a25-a6cc-fdb3f834ca81", "acb680e96f6177cd8ffaf27e9625d8b544d720afc50738801818d0e745c0e5f7", true),
        SUGAR_CANE("FARMING", "71eee614-7b18-3c47-9bd1-372477730016", "2fced0e80f0d7a5d1f45a1a7217e6a99ea9720156c63f6efc84916d4837fabde", true),
        NETHER_WARTS("FARMING", "NETHER_WART", "b82584b7-49ba-32b8-b732-f14f160b7ff2", "71a4620bb3459c1c2fa74b210b1c07b4a02254351f75173e643a0e009a63f558", true),
        FLOWER("FORAGING", "ce005804-135c-3062-8c81-aae83ccd0da2", "baa7c59b2f792d8d091aecacf47a19f8ab93f3fd3c48f6930b1c2baeb09e0f9b"),
        FISHING("FISHING", "7223a6bf-d320-3e0f-aad9-d3dcecc790ab", "53ea0fd89524db3d7a3544904933830b4fc8899ef60c113d948bb3c4fe7aabb1"),
        ZOMBIE("COMBAT", "c62d2e04-ed45-3718-9aac-75e93485d77a", "196063a884d3901c41f35b69a8c9f401c61ac9f6330f964f80c35352c3e8bfb0"),
        REVENANT("COMBAT", "b22f77c8-1454-392b-8c35-0e0b3d415499", "a3dce8555923558d8d74c2a2b261b2b2d630559db54ef97ed3f9c30e9a20aba", true),
        SKELETON("COMBAT", "67d71d08-9f30-3b31-87e7-5824bc3c9de2", "2fe009c5cfa44c05c88e5df070ae2533bd682a728e0b33bfc93fd92a6e5f3f64"),
        CREEPER("COMBAT", "15fade4b-f08c-37f3-a9a4-f0faefb62a33", "54a92c2f8c1b3774e80492200d0b2218d7b019314a73c9cb5b9f04cfcacec471"),
        SPIDER("COMBAT", "ff027ceb-6c80-33cf-a232-27b5c7790201", "e77c4c284e10dea038f004d7eb43ac493de69f348d46b5c1f8ef8154ec2afdd0"),
        TARANTULA("COMBAT", "5715f21b-1a5f-3c8f-a831-23c71e80477f", "97e86007064c9ce26eb4bad8ac9aa30aac309e70a9e0b615936318dea40a721"),
        CAVESPIDER("COMBAT", "CAVE_SPIDER", "b5b999b2-ec38-38ae-b1c8-c62ba9cc03bb", "5d815df973bcd01ee8dfdb3bd74f0b7cb8fef2a70559e4faa5905127bbb4a435"),
        BLAZE("COMBAT", "f33a5de3-01a4-3b99-895f-c99c680ba735", "3208fbd64e97c6e00853d36b3a201e4803cae43dcbd6936a3cece050912e1f20"),
        MAGMA_CUBE("COMBAT", "17531060-11bd-39c5-b2a4-899fb78d34e3", "18c9a7a24da7e3182e4f62fa62762e21e1680962197c7424144ae1d2c42174f7"),
        ENDERMAN("COMBAT", "5a8f2068-aadc-3bd3-bbad-770759233c4e", "e460d20ba1e9cd1d4cfd6d5fb0179ff41597ac6d2461bd7ccdb58b20291ec46e"),
        GHAST("COMBAT", "3d4096a7-cb00-3d44-8844-562317f13b3b", "2478547d122ec83a818b46f3b13c5230429559e40c7d144d4ec225f92c1494b3"),
        SLIME("COMBAT", "59e230be-99bd-33f5-a4d1-acc5ca825512", "c95eced85db62c922724efca804ea0060c4a87fcdedf2fd5c4f9ac1130a6eb26"),
        VOIDLING("COMBAT", "08d8787d-1bc6-41a6-ace7-7c39b75f86b8", "3a851ed2ce5c2c0523af772d206d9555e2e1383ec87946e6ff4c51186e29ef7f"),
        COW("FARMING", "65247dc3-18db-3b4a-a131-ce575839a782", "c2fd8976e1b64aebfd38afbe62aa1429914253df3417ace1f589e5cf45fbd717", true),
        PIG("FARMING", "d30f7d75-d0d0-3857-8c11-0ec0867eb91d", "a9bb5f0c56408c73cfa412345c8fc51f75b6c7311ae60e7099c4781c48760562", true),
        CHICKEN("FARMING", "f01dbc43-b3e0-3766-a03f-c5add6b794d3", "a04b7da13b0a97839846aa5648f5ac6736ba0ca9fbf38cd366916e417153fd7f", true),
        SHEEP("FARMING", "dfd57392-0596-366f-82a9-cbf22a077651", "fd15d4b8bce708f77f963f1b4e87b1b969fef1766a3e9b67b249c59d5e80e8c5", true),
        RABBIT("FARMING", "fbb8165f-a72b-37b9-bde2-6d9dbd72ae3e", "ef59c052d339bb6305cad370fd8c52f58269a957dfaf433a255597d95e68a373", true),
        OAK("FORAGING", "ab31254d-75fd-3a62-b681-a027d86090c7", "57e4a30f361204ea9cded3fbff850160731a0081cc452cfe26aed48e97f6364b"),
        SPRUCE("FORAGING", "9d35ea53-93e3-3d03-a258-be6da1faa800", "7ba04bfe516955fd43932dcb33bd5eac20b38a231d9fa8415b3fb301f60f7363"),
        BIRCH("FORAGING", "efc6ffa9-3a7a-3e77-9eda-c70dcb8c20b9", "eb74109dbb88178afb7a9874afc682904cedb3df75978a51f7beeb28f924251"),
        DARK_OAK("FORAGING", "d1d45cb5-6e2d-3f9a-877d-f9acef04178f", "5ecdc8d6b2b7e081ed9c36609052c91879b89730b9953adbc987e25bf16c5581"),
        ACACIA("FORAGING", "ec02b209-c96d-3c47-9ddf-e5fcb00bb6b4", "42183eaf5b133b838db13d145247e389ab4b4f33c67846363792dc3d82b524c0"),
        JUNGLE("FORAGING", "060c1801-f359-3603-ae39-d485ecd64e38", "2fe73d981690c1be346a16331819c4e8800859fcdc3e5153718c6ad45861924c");

        private final String category;
        private final String altName;
        private final String uuid;
        private final String value;
        private final boolean hasTier12;

        MinionType(String category, String altName, String uuid, String value, boolean hasTier12)
        {
            this.category = category;
            this.altName = altName;
            this.uuid = uuid;
            this.value = value;
            this.hasTier12 = hasTier12;
        }

        MinionType(String category, String altName, String uuid, String value)
        {
            this(category, altName, uuid, value, false);
        }

        MinionType(String category, String uuid, String value, boolean hasTier12)
        {
            this(category, null, uuid, value, hasTier12);
        }

        MinionType(String category, String uuid, String value)
        {
            this(category, null, uuid, value, false);
        }
    }
}