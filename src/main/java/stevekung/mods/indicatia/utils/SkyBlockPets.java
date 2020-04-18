package stevekung.mods.indicatia.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import stevekung.mods.indicatia.gui.api.ExpProgress;
import stevekung.mods.indicatia.gui.api.GuiSkyBlockData.SkillType;

public class SkyBlockPets
{
    public enum Tier
    {
        COMMON(ExpProgress.PET_COMMON, EnumChatFormatting.WHITE),
        UNCOMMON(ExpProgress.PET_UNCOMMON, EnumChatFormatting.GREEN),
        RARE(ExpProgress.PET_RARE, EnumChatFormatting.BLUE),
        EPIC(ExpProgress.PET_EPIC, EnumChatFormatting.DARK_PURPLE),
        LEGENDARY(ExpProgress.PET_LEGENDARY, EnumChatFormatting.GOLD);

        private final ExpProgress[] progression;
        private final EnumChatFormatting color;

        private Tier(ExpProgress[] progression, EnumChatFormatting color)
        {
            this.progression = progression;
            this.color = color;
        }

        public ExpProgress[] getProgression()
        {
            return this.progression;
        }

        public EnumChatFormatting getTierColor()
        {
            return this.color;
        }
    }

    public enum Type
    {
        BAT(SkillType.MINING, "82224cdb-5c6b-47cd-98a6-f97003db2ed3", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzgyZmMzZjcxYjQxNzY5Mzc2YTllOTJmZTNhZGJhYWMzNzcyYjk5OWIyMTljOWQ2YjQ2ODBiYTk5ODNlNTI3In19fQ=="),
        BEE(SkillType.FARMING, "af894c68-45d0-3ae2-952c-b3cf925199ad", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2U5NDE5ODdlODI1YTI0ZWE3YmFhZmFiOTgxOTM0NGI2YzI0N2M3NWM1NGE2OTE5ODdjZDI5NmJjMTYzYzI2MyJ9fX0="),
        BLAZE(SkillType.COMBAT, "7ceb88b2-7f5f-4399-abb9-7068251baa9d", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc4ZWYyZTRjZjJjNDFhMmQxNGJmZGU5Y2FmZjEwMjE5ZjViMWJmNWIzNWE0OWViNTFjNjQ2Nzg4MmNiNWYwIn19fQ=="),
        BLUE_WHALE(SkillType.FISHING, "47c8ba46-82ac-3c09-b511-5502860eb012", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGFiNzc5YmJjY2M4NDlmODgyNzNkODQ0ZThjYTJmM2E2N2ExNjk5Y2IyMTZjMGExMWI0NDMyNmNlMmNjMjAifX19"),
        CHICKEN(SkillType.FARMING, "32e97556-5cb3-4b69-badf-ae4004863579", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2YzN2Q1MjRjM2VlZDE3MWNlMTQ5ODg3ZWExZGVlNGVkMzk5OTA0NzI3ZDUyMTg2NTY4OGVjZTNiYWM3NWUifX19"),
        ENDER_DRAGON(SkillType.COMBAT, "3f9632a1-0ce2-311a-97e7-b144dfcb74f3", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWVjM2ZmNTYzMjkwYjEzZmYzYmNjMzY4OThhZjdlYWE5ODhiNmNjMThkYzI1NDE0N2Y1ODM3NGFmZTliMjFiOSJ9fX0="),
        ENDERMAN(SkillType.COMBAT, "476cd413-4182-331d-845f-451608355831", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVhYjc1ZWFhNWM5ZjJjNDNhMGQyM2NmZGNlMzVmNGRmNjMyZTk4MTUwMDE4NTAzNzczODVmN2IyZjAzOWNlMSJ9fX0="),
        FLYING_FISH(SkillType.FISHING, "c7b1a789-f7b4-394f-84a5-9ba19cb347d0", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDBjZDcxZmJiYmJiNjZjN2JhZjc4ODFmNDE1YzY0ZmE4NGY2NTA0OTU4YTU3Y2NkYjg1ODkyNTI2NDdlYSJ9fX0="),
        GIRAFFE(SkillType.FORAGING, "11216f12-2843-31c8-bf8a-b8535e6c6dce", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc2YjRlMzkwZjJlY2RiOGE3OGRjNjExNzg5Y2EwYWYxZTdlMDkyMjkzMTljM2E3YWE4MjA5YjYzYjkifX19"),
        GUARDIAN(SkillType.COMBAT, "26508276-c01a-32a9-9201-7dae1724954e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjIxMDI1NDM0MDQ1YmRhNzAyNWIzZTUxNGIzMTZhNGI3NzBjNmZhYTRiYTlhZGI0YmUzODA5NTI2ZGI3N2Y5ZCJ9fX0="),
        HORSE(SkillType.COMBAT, "6d310633-c175-4b47-92ab-778287bb7a5e", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzZmY2QzZWMzYmM4NGJhZmI0MTIzZWE0Nzk0NzFmOWQyZjQyZDhmYjljNWYxMWNmNWY0ZTBkOTMyMjYifX19"),
        JERRY(SkillType.COMBAT, "0a9e8efb-9191-4c81-80f5-e27ca5433156", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODIyZDhlNzUxYzhmMmZkNGM4OTQyYzQ0YmRiMmY1Y2E0ZDhhZThlNTc1ZWQzZWIzNGMxOGE4NmU5M2IifX19"),
        LION(SkillType.FORAGING, "7e3ed445-3545-3c76-993b-8f292ea576c6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzhmZjQ3M2JkNTJiNGRiMmMwNmYxYWM4N2ZlMTM2N2JjZTc1NzRmYWMzMzBmZmFjNzk1NjIyOWY4MmVmYmExIn19fQ=="),
        MAGMA_CUBE(SkillType.COMBAT, "35f02923-7bec-3869-9ef5-b42a4794cac8", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzg5NTdkNTAyM2M5MzdjNGM0MWFhMjQxMmQ0MzQxMGJkYTIzY2Y3OWE5ZjZhYjM2Yjc2ZmVmMmQ3YzQyOSJ9fX0="),
        OCELOT(SkillType.FORAGING, "664dd492-3fcd-443b-9e61-4c7ebd9e4e10", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY1N2NkNWMyOTg5ZmY5NzU3MGZlYzRkZGNkYzY5MjZhNjhhMzM5MzI1MGMxYmUxZjBiMTE0YTFkYjEifX19"),
        PARROT(SkillType.ALCHEMY, "cbd58638-f78c-3f85-9b08-6b02f0614215", "eyJ0aW1lc3RhbXAiOjE1ODIwODk4NjgyMDksInByb2ZpbGVJZCI6IjQxZDNhYmMyZDc0OTQwMGM5MDkwZDU0MzRkMDM4MzFiIiwicHJvZmlsZU5hbWUiOiJNZWdha2xvb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVkZjRiMzQwMWE0ZDA2YWQ2NmFjOGI1YzRkMTg5NjE4YWU2MTdmOWMxNDMwNzFjOGFjMzlhNTYzY2Y0ZTQyMDgifX19"),
        PHOENIX(SkillType.COMBAT, "4173bc61-9e2f-3c84-8d31-4517e64062ab", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjNhYWY3YjFhNzc4OTQ5Njk2Y2I5OWQ0ZjA0YWQxYWE1MThjZWVlMjU2YzcyZTVlZDY1YmZhNWMyZDg4ZDllIn19fQ=="),
        PIGMAN(SkillType.COMBAT, "e3410337-d22b-4427-beab-d9ceae561d2c", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNkOWNiNjUxM2YyMDcyZTVkNGU0MjZkNzBhNTU1N2JjMzk4NTU0Yzg4MGQ0ZTdiN2VjOGVmNDk0NWViMDJmMiJ9fX0="),
        RABBIT(SkillType.FARMING, "a9a344e6-192c-335f-8d51-5bc1321cb350", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTE3YmZmYzE5NzJhY2Q3ZjNiNGE4ZjQzYjViNmM3NTM0Njk1YjhmZDYyNjc3ZTAzMDZiMjgzMTU3NGIifX19"),
        SHEEP(SkillType.ALCHEMY, "37bacd66-7fe6-39e3-81cf-82911daf648b", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjRlMjJhNDYwNDdkMjcyZTg5YTFjZmExM2U5NzM0YjdlMTI4MjdlMjM1YzIwMTJjMWE5NTk2Mjg3NGRhMCJ9fX0="),
        SILVERFISH(SkillType.MINING, "79e570d8-f66e-375c-9e70-97224ccd5692", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE5MWRhYjgzOTFhZjVmZGE1NGFjZDJjMGIxOGZiZDgxOWI4NjVlMWE4ZjFkNjIzODEzZmE3NjFlOTI0NTQwIn19fQ=="),
        SKELETON_HORSE(SkillType.COMBAT, "bd84da18-cd6c-3191-9bc5-0fa4c2071372", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDdlZmZjZTM1MTMyYzg2ZmY3MmJjYWU3N2RmYmIxZDIyNTg3ZTk0ZGYzY2JjMjU3MGVkMTdjZjg5NzNhIn19fQ=="),
        SNOWMAN(SkillType.COMBAT, "b2b19dcd-dc67-31df-a790-e6cf07ae12ac", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTExMzY2MTZkOGM0YTg3YTU0Y2U3OGE5N2I1NTE2MTBjMmIyYzhmNmQ0MTBiYzM4Yjg1OGY5NzRiMTEzYjIwOCJ9fX0="),
        SQUID(SkillType.FISHING, "7b5da593-80d3-39f4-8220-2cef27c5b9d9", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDE0MzNiZTI0MjM2NmFmMTI2ZGE0MzRiODczNWRmMWViNWIzY2IyY2VkZTM5MTQ1OTc0ZTljNDgzNjA3YmFjIn19fQ=="),
        TIGER(SkillType.COMBAT, "33a69ead-44ac-3791-9425-52109aacdaa6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmM0MjYzODc0NDkyMmI1ZmNmNjJjZDliZjI3ZWVhYjkxYjJlNzJkNmM3MGU4NmNjNWFhMzg4Mzk5M2U5ZDg0In19fQ=="),
        TURTLE(SkillType.COMBAT, "f10d652b-906b-3065-adf5-9817983201ca", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjEyYjU4Yzg0MWIzOTQ4NjNkYmNjNTRkZTFjMmFkMjY0OGFmOGYwM2U2NDg5ODhjMWY5Y2VmMGJjMjBlZTIzYyJ9fX0="),
        WITHER_SKELETON(SkillType.MINING, "d928ce5e-e75e-3cdc-aaf1-0c93d49b5c31", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVlYzk2NDY0NWE4ZWZhYzc2YmUyZjE2MGQ3Yzk5NTYzNjJmMzJiNjUxNzM5MGM1OWMzMDg1MDM0ZjA1MGNmZiJ9fX0="),
        WOLF(SkillType.COMBAT, "9d2b3b08-495a-3a45-a49a-0869cf7e8ffa", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGMzZGQ5ODRiYjY1OTg0OWJkNTI5OTQwNDY5NjRjMjI3MjVmNzE3ZTk4NmIxMmQ1NDhmZDE2OTM2N2Q0OTQifX19");

        private final SkillType type;
        private final String uuid;
        private final String value;

        private Type(SkillType type, String uuid, String value)
        {
            this.type = type;
            this.uuid = uuid;
            this.value = value;
        }

        public SkillType getSkillType()
        {
            return this.type;
        }

        public ItemStack getPetItem()
        {
            return RenderUtils.getSkullItemStack(this.uuid, this.value);
        }
    }
}