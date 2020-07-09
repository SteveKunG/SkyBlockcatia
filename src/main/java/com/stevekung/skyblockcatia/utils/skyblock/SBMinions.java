package com.stevekung.skyblockcatia.utils.skyblock;

import java.io.BufferedReader;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.stevekung.skyblockcatia.utils.CurlExecutor;

import net.minecraft.item.ItemStack;

public class SBMinions
{
    private static final Gson GSON = new Gson();
    public static SBMinions.Slot[] MINION_SLOTS;

    public static void getMinionSlotFromRemote() throws Exception
    {
        BufferedReader in = CurlExecutor.execute("api/minion_slots.json");
        MINION_SLOTS = GSON.fromJson(in, SBMinions.Slot[].class);
    }

    public class Slot
    {
        @SerializedName("current_slot")
        private final int currentSlot;
        @SerializedName("minion_slot")
        private final int minionSlot;

        public Slot(int currentSlot, int minionSlot)
        {
            this.currentSlot = currentSlot;
            this.minionSlot = minionSlot;
        }

        public int getCurrentSlot()
        {
            return this.currentSlot;
        }

        public int getMinionSlot()
        {
            return this.minionSlot;
        }
    }

    public enum Type
    {
        COBBLESTONE(SBSkills.Type.MINING, "44f3d931-5b9a-35a1-a84b-669755c7c7ee", "eyJ0aW1lc3RhbXAiOjE1NTc5MTkyNzc1NzMsInByb2ZpbGVJZCI6IjkxZjA0ZmU5MGYzNjQzYjU4ZjIwZTMzNzVmODZkMzllIiwicHJvZmlsZU5hbWUiOiJTdG9ybVN0b3JteSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmY5MzI4OWE4MmJkMmEwNmNiYmU2MWI3MzNjZmRjMWYxYmQ5M2M0MzQwZjdhOTBhYmQ5YmRkYTc3NDEwOTA3MSJ9fX0="),
        OBSIDIAN(SBSkills.Type.MINING, "58c0f4df-db55-3492-be5d-2e72f289aaa9", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk5NDcyMTUsInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzMyMGMyOWFiOTY2NjM3Y2I5YWVjYzM0ZWU3NmQ1YTAxMzA0NjFlMGM0ZmRiMDhjZGFmODA5MzlmYTEyMDkxMDIifX19"),
        GLOWSTONE(SBSkills.Type.MINING, "985fcfc1-6922-3fdf-8247-13ffd7def691", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk2NTk3MTUsInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzIwZjRkN2MyNmIwMzEwOTkwYTdkM2EzYjQ1OTQ4Yjk1ZGQ0YWI0MDdhMTZhNGI2ZDNiN2NiNGZiYTAzMWFlZWQifX19"),
        GRAVEL(SBSkills.Type.MINING, "b499f2a6-291f-3626-a36d-a888e4a2e424", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk3NDgyNjUsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJ4RmFpaUxlUiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQ1ODUwN2VkMzFjZjlhMzg5ODZhYzg3OTUxNzNjNjA5NjM3ZjAzZGE2NTNmMzA0ODNhNzIxZDNmYmU2MDJkIn19fQ=="),
        SAND(SBSkills.Type.MINING, "49a80bce-61b1-3f83-b2b4-5122c16a23fe", "eyJ0aW1lc3RhbXAiOjE1NTc5MjAxNDY2NzAsInByb2ZpbGVJZCI6IjgyYzYwNmM1YzY1MjRiNzk4YjkxYTEyZDNhNjE2OTc3IiwicHJvZmlsZU5hbWUiOiJOb3ROb3RvcmlvdXNOZW1vIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84MWY4ZTJhZDAyMWVlZmQxMjE3ZTY1MGU4NDhiNTc2MjIxNDRkMmJmOGEzOWZiZDUwZGFiOTM3YTdlYWMxMGRlIn19fQ=="),
        CLAY(SBSkills.Type.FISHING, "1f9105c0-7873-3e6d-ae1a-a9a5ca044733", "eyJ0aW1lc3RhbXAiOjE1NTc5MTkyMjM1NzcsInByb2ZpbGVJZCI6IjVkODgxNDRhMzhjYzRkZWVhODA3MzYwODc2YzIxYzU4IiwicHJvZmlsZU5hbWUiOiJSZW1WTiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWY5YjMxMmM4ZjUzZGEyODkwNjBlNjQ1Mjg1NTA3MmUwNzk3MTQ1OGFiYmYzMzhkZGVjMzUxZTE2YzE3MWZmOCJ9fX0="),
        ICE(SBSkills.Type.MINING, "017c5692-85da-306e-8013-a11fd5f74b59", "eyJ0aW1lc3RhbXAiOjE1NTk2NTkzMjk0MjcsInByb2ZpbGVJZCI6ImIwZDczMmZlMDBmNzQwN2U5ZTdmNzQ2MzAxY2Q5OGNhIiwicHJvZmlsZU5hbWUiOiJPUHBscyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTUwMDA2NDMyMWIxMjk3MmY4ZTU3NTA3OTNlYzFjODIzZGE0NjI3NTM1ZTlkMTJmZWFlZTc4Mzk0Yjg2ZGFiZSJ9fX0="),
        SNOW(SBSkills.Type.MINING, "fde00899-094f-3ff8-81b8-253830a8d3e9", "eyJ0aW1lc3RhbXAiOjE1NzY1MTMxOTQ4MDUsInByb2ZpbGVJZCI6ImRlNTcxYTEwMmNiODQ4ODA4ZmU3YzlmNDQ5NmVjZGFkIiwicHJvZmlsZU5hbWUiOiJNSEZfTWluZXNraW4iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y2ZDE4MDY4NGMzNTIxYzlmYzg5NDc4YmE0NDA1YWU5Y2U0OTdkYTgxMjRmYTBkYTVhMDEyNjQzMWM0Yjc4YzMifX19"),
        COAL(SBSkills.Type.MINING, "304f13a1-ca59-3db9-8194-68d1c07352d1", "eyJ0aW1lc3RhbXAiOjE1NTc5MTkyNTA4MjIsInByb2ZpbGVJZCI6IjU2Njc1YjIyMzJmMDRlZTA4OTE3OWU5YzkyMDZjZmU4IiwicHJvZmlsZU5hbWUiOiJUaGVJbmRyYSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDI1YjhkMmVhOTY1Yzc4MDY1MmQyOWMyNmIxNTcyNjg2ZmQ3NGY2ZmU2NDAzYjVhMzgwMDk1OWZlYjJhZDkzNSJ9fX0="),
        IRON(SBSkills.Type.MINING, "785de520-c4f3-371c-88c8-c8f560bc4977", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk3OTUzNTAsInByb2ZpbGVJZCI6IjJjMTA2NGZjZDkxNzQyODI4NGUzYmY3ZmFhN2UzZTFhIiwicHJvZmlsZU5hbWUiOiJOYWVtZSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWY0MzUwMjJjYjM4MDlhNjhkYjBmY2NmYTg5OTNmYzE5NTRkYzY5N2E3MTgxNDk0OTA1YjAzZmRkYTAzNWU0YSJ9fX0="),
        GOLD(SBSkills.Type.MINING, "ef5b80f5-2ddc-3005-b441-784230573c60", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk3MTIzMDIsInByb2ZpbGVJZCI6IjkxZjA0ZmU5MGYzNjQzYjU4ZjIwZTMzNzVmODZkMzllIiwicHJvZmlsZU5hbWUiOiJTdG9ybVN0b3JteSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjZkYTA0ZWQ4YzgxMGJlMjliYmE1M2M2MmU3MTJkNjVjZmIyNTIzODExN2I5NGQ3ZTg1YTQ2MTU3NzViZjE0ZiJ9fX0="),
        DIAMOND(SBSkills.Type.MINING, "50cb7221-fcc7-3450-a8ee-6d7593ebec22", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk0NzI5ODEsInByb2ZpbGVJZCI6IjU3MGIwNWJhMjZmMzRhOGViZmRiODBlY2JjZDdlNjIwIiwicHJvZmlsZU5hbWUiOiJMb3JkU29ubnkiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzIzNTRiYmU2MDRkZmU1OGJmOTJlNzcyOTczMGQwYzhlMzc4NDRlODMxZWUzODE2ZDdlODQyN2MyN2ExODI0YTIifX19"),
        LAPIS(SBSkills.Type.MINING, "7ef29dbe-89c9-3372-80c8-edf8955b285a", "eyJ0aW1lc3RhbXAiOjE1NTc5MTc2MDMwNDMsInByb2ZpbGVJZCI6IjkxZjA0ZmU5MGYzNjQzYjU4ZjIwZTMzNzVmODZkMzllIiwicHJvZmlsZU5hbWUiOiJTdG9ybVN0b3JteSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjRmZDk3YjkzNDZjMTIwOGMxZGIzOTU3NTMwY2RmYzU3ODllM2U2NTk0Mzc4NmIwMDcxY2YyYjI5MDRhNmI1YyJ9fX0="),
        REDSTONE(SBSkills.Type.MINING, "a4fff41c-bae4-393c-8d97-e98075ccb590", "eyJ0aW1lc3RhbXAiOjE1NTc5MjAxMDg2NDUsInByb2ZpbGVJZCI6Ijc1MTQ0NDgxOTFlNjQ1NDY4Yzk3MzlhNmUzOTU3YmViIiwicHJvZmlsZU5hbWUiOiJUaGFua3NNb2phbmciLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFlZGVmY2YxYTg5ZDY4N2EwYTRlY2YxNTg5OTc3YWYxZTUyMGZjNjczYzQ4YTA0MzRiZTQyNjYxMmU4ZmFhNjcifX19"),
        EMERALD(SBSkills.Type.MINING, "8383fe2e-6c76-32cf-8e5f-264b13de62ae", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk1MjU2MTUsInByb2ZpbGVJZCI6ImRkZWQ1NmUxZWY4YjQwZmU4YWQxNjI5MjBmN2FlY2RhIiwicHJvZmlsZU5hbWUiOiJEaXNjb3JkQXBwIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85YmY1N2YzNDAxYjEzMGM2YjUzODA4ZjJiMWUxMTljYzdiOTg0NjIyZGFjNzA3N2JiZDUzNDU0ZTFmNjViYmYwIn19fQ=="),
        QUARTZ(SBSkills.Type.MINING, "26774528-cfaf-398d-9e9c-e68e8761d1ed", "eyJ0aW1lc3RhbXAiOjE1NTc5MjAwNDc1MjYsInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2QyNzAwOTNiZTYyZGZkMzAxOWY5MDgwNDNkYjU3MGI1ZGZkMzY2ZmQ1MzQ1ZmNjZjlkYTM0MGU3NWM3MDFhNjAifX19"),
        ENDER_STONE(SBSkills.Type.MINING, "END_STONE", "13276fea-088f-32f2-bf79-a3201607ec56", "eyJ0aW1lc3RhbXAiOjE1NjM5ODE2MjkxODksInByb2ZpbGVJZCI6IjgyYzYwNmM1YzY1MjRiNzk4YjkxYTEyZDNhNjE2OTc3IiwicHJvZmlsZU5hbWUiOiJOb3ROb3RvcmlvdXNOZW1vIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83OTk0YmUzZGNmYmI0ZWQwYTVhNzQ5NWI3MzM1YWYxYTNjZWQwYjU4ODhiNTAwNzI4NmE3OTA3NjdjM2I1N2U2In19fQ=="),
        WHEAT(SBSkills.Type.FARMING, "81b06ed8-e9ec-34e2-b11f-234eb6e20318", "eyJ0aW1lc3RhbXAiOjE1NTc5MjA1NzU1MzAsInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2JiYzU3MWM1NTI3MzM2MzUyZTJmZWUyYjQwYTllZGZhMmU4MDlmNjQyMzA3NzlhYTAxMjUzYzZhYTUzNTg4MWIifX19"),
        MELON(SBSkills.Type.FARMING, "845dc766-7f65-3750-9807-461185f480ab", "eyJ0aW1lc3RhbXAiOjE1NTc5MjA1Mjg4MzQsInByb2ZpbGVJZCI6ImRkZWQ1NmUxZWY4YjQwZmU4YWQxNjI5MjBmN2FlY2RhIiwicHJvZmlsZU5hbWUiOiJEaXNjb3JkQXBwIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85NWQ1NDUzOWFjOGQzZmJhOTY5NmM5MWY0ZGNjN2YxNWMzMjBhYjg2MDI5ZDVjOTJmMTIzNTlhYmQ0ZGY4MTFlIn19fQ=="),
        PUMPKIN(SBSkills.Type.FARMING, "a25aead0-758a-3ced-8153-84362a361c47", "eyJ0aW1lc3RhbXAiOjE1NTc5MjAwMjQ5NTQsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJ4RmFpaUxlUiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjNmYjY2M2U4NDNhN2RhNzg3ZTI5MGYyM2M4YWYyZjk3ZjdiNmY1NzJmYTU5YTBkNGQwMjE4NmRiNmVhYWJiNyJ9fX0="),
        CARROT(SBSkills.Type.FARMING, "3a394733-b862-3676-ab4f-60fca79bd18c", "eyJ0aW1lc3RhbXAiOjE1NTc5MTkwOTgyNTEsInByb2ZpbGVJZCI6IjVkMjRiYTBiMjg4YzQyOTM4YmExMGVjOTkwNjRkMjU5IiwicHJvZmlsZU5hbWUiOiIxbnYzbnQxdjN0NGwzbnQiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzRiYWVhOTkwYjQ1ZDMzMDk5OGNiMGMxZjg1MTVjMjdiMjRmOTNiZmYxZGYwZGIwNTZlNjQ3ZjgyMDBkMDNiOWQifX19"),
        POTATO(SBSkills.Type.FARMING, "4a8b2791-3dd0-3376-9dbf-5683f3dac782", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk5OTkyNjksInByb2ZpbGVJZCI6IjkxZjA0ZmU5MGYzNjQzYjU4ZjIwZTMzNzVmODZkMzllIiwicHJvZmlsZU5hbWUiOiJTdG9ybVN0b3JteSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2RkYTM1YTA0NGNiMDM3NGI1MTYwMTVkOTkxYTBmNjViZjdkMGZiNjU2NmUzNTA0OTY2NDJjZjIwNTlmZjFkOSJ9fX0="),
        MUSHROOM(SBSkills.Type.FARMING, "4c4116e2-193d-32a4-948b-39ae936ed909", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk4Nzc2MjgsInByb2ZpbGVJZCI6IjVkODgxNDRhMzhjYzRkZWVhODA3MzYwODc2YzIxYzU4IiwicHJvZmlsZU5hbWUiOiJSZW1WTiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGEzYjU4MzQxZDE5NmE5ODQxZWYxNTI2YjM2NzIwOWNiYzlmOTY3NjdjMjRmNWY1ODdjZjQxM2Q0MmI3NGE5MyJ9fX0="),
        CACTUS(SBSkills.Type.FARMING, "78b57eb1-ea68-3072-a861-9c89a2f467cd", "eyJ0aW1lc3RhbXAiOjE1NTc5MTgxNDU1MzMsInByb2ZpbGVJZCI6IjZlZDg3NmUzZjg4MTRmYzhhMTNlMDU0MDU1ODFjNDZiIiwicHJvZmlsZU5hbWUiOiJBenVyZUJsdWUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2VmOTNlYzZlNjdhNmNkMjcyYzlhOTY4NGI2N2RmNjI1ODRjYjA4NGEyNjVlZWUzY2RlMTQxZDIwZTcwZDdkNzIifX19"),
        COCOA(SBSkills.Type.FARMING, "a1448e51-e34a-3a25-a6cc-fdb3f834ca81", "eyJ0aW1lc3RhbXAiOjE1NTc5MTkzMTQ3MDQsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJ4RmFpaUxlUiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWNiNjgwZTk2ZjYxNzdjZDhmZmFmMjdlOTYyNWQ4YjU0NGQ3MjBhZmM1MDczODgwMTgxOGQwZTc0NWMwZTVmNyJ9fX0="),
        SUGAR_CANE(SBSkills.Type.FARMING, "71eee614-7b18-3c47-9bd1-372477730016", "eyJ0aW1lc3RhbXAiOjE1NTc5MjA0OTcwNDksInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJ4RmFpaUxlUiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmZjZWQwZTgwZjBkN2E1ZDFmNDVhMWE3MjE3ZTZhOTllYTk3MjAxNTZjNjNmNmVmYzg0OTE2ZDQ4MzdmYWJkZSJ9fX0="),
        NETHER_WARTS(SBSkills.Type.FARMING, "NETHER_WART", "b82584b7-49ba-32b8-b732-f14f160b7ff2", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk5MDAxMjIsInByb2ZpbGVJZCI6IjkxZjA0ZmU5MGYzNjQzYjU4ZjIwZTMzNzVmODZkMzllIiwicHJvZmlsZU5hbWUiOiJTdG9ybVN0b3JteSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzFhNDYyMGJiMzQ1OWMxYzJmYTc0YjIxMGIxYzA3YjRhMDIyNTQzNTFmNzUxNzNlNjQzYTBlMDA5YTYzZjU1OCJ9fX0="),
        FLOWER(SBSkills.Type.FORAGING, "ce005804-135c-3062-8c81-aae83ccd0da2", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk1ODkyNDUsInByb2ZpbGVJZCI6IjkxZjA0ZmU5MGYzNjQzYjU4ZjIwZTMzNzVmODZkMzllIiwicHJvZmlsZU5hbWUiOiJTdG9ybVN0b3JteSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFhN2M1OWIyZjc5MmQ4ZDA5MWFlY2FjZjQ3YTE5ZjhhYjkzZjNmZDNjNDhmNjkzMGIxYzJiYWViMDllMGY5YiJ9fX0="),
        FISHING(SBSkills.Type.FISHING, "7223a6bf-d320-3e0f-aad9-d3dcecc790ab", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk1Njk3MzYsInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzUzZWEwZmQ4OTUyNGRiM2Q3YTM1NDQ5MDQ5MzM4MzBiNGZjODg5OWVmNjBjMTEzZDk0OGJiM2M0ZmU3YWFiYjEifX19"),
        ZOMBIE(SBSkills.Type.COMBAT, "c62d2e04-ed45-3718-9aac-75e93485d77a", "eyJ0aW1lc3RhbXAiOjE1NTc5MjA1OTkxMzMsInByb2ZpbGVJZCI6IjkxZjA0ZmU5MGYzNjQzYjU4ZjIwZTMzNzVmODZkMzllIiwicHJvZmlsZU5hbWUiOiJTdG9ybVN0b3JteSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk2MDYzYTg4NGQzOTAxYzQxZjM1YjY5YThjOWY0MDFjNjFhYzlmNjMzMGY5NjRmODBjMzUzNTJjM2U4YmZiMCJ9fX0="),
        REVENANT(SBSkills.Type.COMBAT, "b22f77c8-1454-392b-8c35-0e0b3d415499", "eyJ0aW1lc3RhbXAiOjE1Njg1MzgxNzcxMTMsInByb2ZpbGVJZCI6IjQxZDNhYmMyZDc0OTQwMGM5MDkwZDU0MzRkMDM4MzFiIiwicHJvZmlsZU5hbWUiOiJNZWdha2xvb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2EzZGNlODU1NTkyMzU1OGQ4ZDc0YzJhMmIyNjFiMmIyZDYzMDU1OWRiNTRlZjk3ZWQzZjljMzBlOWEyMGFiYSJ9fX0="),
        SKELETON(SBSkills.Type.COMBAT, "67d71d08-9f30-3b31-87e7-5824bc3c9de2", "eyJ0aW1lc3RhbXAiOjE1NTc5MjAzMTgzOTIsInByb2ZpbGVJZCI6ImIwZDczMmZlMDBmNzQwN2U5ZTdmNzQ2MzAxY2Q5OGNhIiwicHJvZmlsZU5hbWUiOiJPUHBscyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmZlMDA5YzVjZmE0NGMwNWM4OGU1ZGYwNzBhZTI1MzNiZDY4MmE3MjhlMGIzM2JmYzkzZmQ5MmE2ZTVmM2Y2NCJ9fX0="),
        CREEPER(SBSkills.Type.COMBAT, "15fade4b-f08c-37f3-a9a4-f0faefb62a33", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk0MDIzMjgsInByb2ZpbGVJZCI6IjkxZmUxOTY4N2M5MDQ2NTZhYTFmYzA1OTg2ZGQzZmU3IiwicHJvZmlsZU5hbWUiOiJoaGphYnJpcyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTRhOTJjMmY4YzFiMzc3NGU4MDQ5MjIwMGQwYjIyMThkN2IwMTkzMTRhNzNjOWNiNWI5ZjA0Y2ZjYWNlYzQ3MSJ9fX0="),
        SPIDER(SBSkills.Type.COMBAT, "ff027ceb-6c80-33cf-a232-27b5c7790201", "eyJ0aW1lc3RhbXAiOjE1NTc5MjAzNzk5MzcsInByb2ZpbGVJZCI6IjU3MGIwNWJhMjZmMzRhOGViZmRiODBlY2JjZDdlNjIwIiwicHJvZmlsZU5hbWUiOiJMb3JkU29ubnkiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2U3N2M0YzI4NGUxMGRlYTAzOGYwMDRkN2ViNDNhYzQ5M2RlNjlmMzQ4ZDQ2YjVjMWY4ZWY4MTU0ZWMyYWZkZDAifX19"),
        TARANTULA(SBSkills.Type.COMBAT, "5715f21b-1a5f-3c8f-a831-23c71e80477f", "eyJ0aW1lc3RhbXAiOjE1NjkyNDU3NDI1ODAsInByb2ZpbGVJZCI6IjQxZDNhYmMyZDc0OTQwMGM5MDkwZDU0MzRkMDM4MzFiIiwicHJvZmlsZU5hbWUiOiJNZWdha2xvb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzk3ZTg2MDA3MDY0YzljZTI2ZWI0YmFkOGFjOWFhMzBhYWMzMDllNzBhOWUwYjYxNTkzNjMxOGRlYTQwYTcyMSJ9fX0="),
        CAVESPIDER(SBSkills.Type.COMBAT, "CAVE_SPIDER", "b5b999b2-ec38-38ae-b1c8-c62ba9cc03bb", "eyJ0aW1lc3RhbXAiOjE1NTc5MTkxMjk4MzgsInByb2ZpbGVJZCI6IjJkYzc3YWU3OTQ2MzQ4MDI5NDI4MGM4NDIyNzRiNTY3IiwicHJvZmlsZU5hbWUiOiJzYWR5MDYxMCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ4MTVkZjk3M2JjZDAxZWU4ZGZkYjNiZDc0ZjBiN2NiOGZlZjJhNzA1NTllNGZhYTU5MDUxMjdiYmI0YTQzNSJ9fX0="),
        BLAZE(SBSkills.Type.COMBAT, "f33a5de3-01a4-3b99-895f-c99c680ba735", "eyJ0aW1lc3RhbXAiOjE1NTc5MTgwOTE5NjUsInByb2ZpbGVJZCI6IjZlZDg3NmUzZjg4MTRmYzhhMTNlMDU0MDU1ODFjNDZiIiwicHJvZmlsZU5hbWUiOiJBenVyZUJsdWUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzMyMDhmYmQ2NGU5N2M2ZTAwODUzZDM2YjNhMjAxZTQ4MDNjYWU0M2RjYmQ2OTM2YTNjZWNlMDUwOTEyZTFmMjAifX19"),
        MAGMA_CUBE(SBSkills.Type.COMBAT, "17531060-11bd-39c5-b2a4-899fb78d34e3", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk4NTQzMTMsInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE4YzlhN2EyNGRhN2UzMTgyZTRmNjJmYTYyNzYyZTIxZTE2ODA5NjIxOTdjNzQyNDE0NGFlMWQyYzQyMTc0ZjcifX19"),
        ENDERMAN(SBSkills.Type.COMBAT, "5a8f2068-aadc-3bd3-bbad-770759233c4e", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk1NDkxODMsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJ4RmFpaUxlUiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTQ2MGQyMGJhMWU5Y2QxZDRjZmQ2ZDVmYjAxNzlmZjQxNTk3YWM2ZDI0NjFiZDdjY2RiNThiMjAyOTFlYzQ2ZSJ9fX0="),
        GHAST(SBSkills.Type.COMBAT, "3d4096a7-cb00-3d44-8844-562317f13b3b", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk2MzU1MzcsInByb2ZpbGVJZCI6ImU3NmYwZDlhZjc4MjQyYzM5NDY2ZDY3MjE3MzBmNDUzIiwicHJvZmlsZU5hbWUiOiJLbGxscmFoIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yNDc4NTQ3ZDEyMmVjODNhODE4YjQ2ZjNiMTNjNTIzMDQyOTU1OWU0MGM3ZDE0NGQ0ZWMyMjVmOTJjMTQ5NGIzIn19fQ=="),
        SLIME(SBSkills.Type.COMBAT, "59e230be-99bd-33f5-a4d1-acc5ca825512", "eyJ0aW1lc3RhbXAiOjE1NTc5MjAzNDQ0NzcsInByb2ZpbGVJZCI6IjkxZmUxOTY4N2M5MDQ2NTZhYTFmYzA1OTg2ZGQzZmU3IiwicHJvZmlsZU5hbWUiOiJoaGphYnJpcyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzk1ZWNlZDg1ZGI2MmM5MjI3MjRlZmNhODA0ZWEwMDYwYzRhODdmY2RlZGYyZmQ1YzRmOWFjMTEzMGE2ZWIyNiJ9fX0="),
        COW(SBSkills.Type.FARMING, "65247dc3-18db-3b4a-a131-ce575839a782", "eyJ0aW1lc3RhbXAiOjE1NTc5MTkzNDg2MTYsInByb2ZpbGVJZCI6IjgyYzYwNmM1YzY1MjRiNzk4YjkxYTEyZDNhNjE2OTc3IiwicHJvZmlsZU5hbWUiOiJOb3ROb3RvcmlvdXNOZW1vIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMmZkODk3NmUxYjY0YWViZmQzOGFmYmU2MmFhMTQyOTkxNDI1M2RmMzQxN2FjZTFmNTg5ZTVjZjQ1ZmJkNzE3In19fQ=="),
        PIG(SBSkills.Type.FARMING, "d30f7d75-d0d0-3857-8c11-0ec0867eb91d", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk5NzI5NzIsInByb2ZpbGVJZCI6ImZkNjBmMzZmNTg2MTRmMTJiM2NkNDdjMmQ4NTUyOTlhIiwicHJvZmlsZU5hbWUiOiJSZWFkIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hOWJiNWYwYzU2NDA4YzczY2ZhNDEyMzQ1YzhmYzUxZjc1YjZjNzMxMWFlNjBlNzA5OWM0NzgxYzQ4NzYwNTYyIn19fQ=="),
        CHICKEN(SBSkills.Type.FARMING, "f01dbc43-b3e0-3766-a03f-c5add6b794d3", "eyJ0aW1lc3RhbXAiOjE1NTc5MTkxNTgwNDEsInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2EwNGI3ZGExM2IwYTk3ODM5ODQ2YWE1NjQ4ZjVhYzY3MzZiYTBjYTlmYmYzOGNkMzY2OTE2ZTQxNzE1M2ZkN2YifX19"),
        SHEEP(SBSkills.Type.FARMING, "dfd57392-0596-366f-82a9-cbf22a077651", "eyJ0aW1lc3RhbXAiOjE1NTc5MjAyNTMwNzQsInByb2ZpbGVJZCI6IjkxZjA0ZmU5MGYzNjQzYjU4ZjIwZTMzNzVmODZkMzllIiwicHJvZmlsZU5hbWUiOiJTdG9ybVN0b3JteSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmQxNWQ0YjhiY2U3MDhmNzdmOTYzZjFiNGU4N2IxYjk2OWZlZjE3NjZhM2U5YjY3YjI0OWM1OWQ1ZTgwZThjNSJ9fX0="),
        RABBIT(SBSkills.Type.FARMING, "fbb8165f-a72b-37b9-bde2-6d9dbd72ae3e", "eyJ0aW1lc3RhbXAiOjE1NTc5MjAwNzAyMTIsInByb2ZpbGVJZCI6IjU2Njc1YjIyMzJmMDRlZTA4OTE3OWU5YzkyMDZjZmU4IiwicHJvZmlsZU5hbWUiOiJUaGVJbmRyYSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWY1OWMwNTJkMzM5YmI2MzA1Y2FkMzcwZmQ4YzUyZjU4MjY5YTk1N2RmYWY0MzNhMjU1NTk3ZDk1ZTY4YTM3MyJ9fX0="),
        OAK(SBSkills.Type.FORAGING, "ab31254d-75fd-3a62-b681-a027d86090c7", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk5MjMyMDcsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJ4RmFpaUxlUiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTdlNGEzMGYzNjEyMDRlYTljZGVkM2ZiZmY4NTAxNjA3MzFhMDA4MWNjNDUyY2ZlMjZhZWQ0OGU5N2Y2MzY0YiJ9fX0="),
        SPRUCE(SBSkills.Type.FORAGING, "9d35ea53-93e3-3d03-a258-be6da1faa800", "eyJ0aW1lc3RhbXAiOjE1NTc5MjA0NDk3MDAsInByb2ZpbGVJZCI6IjkxZjA0ZmU5MGYzNjQzYjU4ZjIwZTMzNzVmODZkMzllIiwicHJvZmlsZU5hbWUiOiJTdG9ybVN0b3JteSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2JhMDRiZmU1MTY5NTVmZDQzOTMyZGNiMzNiZDVlYWMyMGIzOGEyMzFkOWZhODQxNWIzZmIzMDFmNjBmNzM2MyJ9fX0="),
        BIRCH(SBSkills.Type.FORAGING, "efc6ffa9-3a7a-3e77-9eda-c70dcb8c20b9", "eyJ0aW1lc3RhbXAiOjE1NTc5MTg5MDY1NTQsInByb2ZpbGVJZCI6ImU3NTMyNjk3ZTgwZjQ1NmU5ZjNhZjZiODIzNWU5YTgxIiwicHJvZmlsZU5hbWUiOiJNaW5lQWx0c19NU2tpbl8xIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lYjc0MTA5ZGJiODgxNzhhZmI3YTk4NzRhZmM2ODI5MDRjZWRiM2RmNzU5NzhhNTFmN2JlZWIyOGY5MjQyNTEifX19"),
        DARK_OAK(SBSkills.Type.FORAGING, "d1d45cb5-6e2d-3f9a-877d-f9acef04178f", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk0NDEzODMsInByb2ZpbGVJZCI6IjkxZjA0ZmU5MGYzNjQzYjU4ZjIwZTMzNzVmODZkMzllIiwicHJvZmlsZU5hbWUiOiJTdG9ybVN0b3JteSIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWVjZGM4ZDZiMmI3ZTA4MWVkOWMzNjYwOTA1MmM5MTg3OWI4OTczMGI5OTUzYWRiYzk4N2UyNWJmMTZjNTU4MSJ9fX0="),
        ACACIA(SBSkills.Type.FORAGING, "ec02b209-c96d-3c47-9ddf-e5fcb00bb6b4", "eyJ0aW1lc3RhbXAiOjE1NTc5MTcyMjA2NjAsInByb2ZpbGVJZCI6IjZlZDg3NmUzZjg4MTRmYzhhMTNlMDU0MDU1ODFjNDZiIiwicHJvZmlsZU5hbWUiOiJBenVyZUJsdWUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQyMTgzZWFmNWIxMzNiODM4ZGIxM2QxNDUyNDdlMzg5YWI0YjRmMzNjNjc4NDYzNjM3OTJkYzNkODJiNTI0YzAifX19"),
        JUNGLE(SBSkills.Type.FORAGING, "060c1801-f359-3603-ae39-d485ecd64e38", "eyJ0aW1lc3RhbXAiOjE1NTc5MTk4MTgzOTYsInByb2ZpbGVJZCI6IjVkMjRiYTBiMjg4YzQyOTM4YmExMGVjOTkwNjRkMjU5IiwicHJvZmlsZU5hbWUiOiIxbnYzbnQxdjN0NGwzbnQiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzJmZTczZDk4MTY5MGMxYmUzNDZhMTYzMzE4MTljNGU4ODAwODU5ZmNkYzNlNTE1MzcxOGM2YWQ0NTg2MTkyNGMifX19");

        private final SBSkills.Type category;
        private final String altName;
        private final String uuid;
        private final String value;
        public static final SBMinions.Type[] VALUES = SBMinions.Type.values();

        private Type(SBSkills.Type category, String altName, String uuid, String value)
        {
            this.category = category;
            this.altName = altName;
            this.uuid = uuid;
            this.value = value;
        }

        private Type(SBSkills.Type category, String uuid, String value)
        {
            this(category, null, uuid, value);
        }

        public String getAltName()
        {
            return this.altName;
        }

        public SBSkills.Type getMinionCategory()
        {
            return this.category;
        }

        public ItemStack getMinionItem()
        {
            return SBRenderUtils.getSkullItemStack(this.uuid, this.value);
        }
    }

    public static class Info
    {
        private final String minionType;
        private final String displayName;
        private final ItemStack minionItem;
        private final int minionMaxTier;
        private final SBSkills.Type category;

        public Info(String minionType, String displayName, ItemStack minionItem, int minionMaxTier, SBSkills.Type category)
        {
            this.minionType = minionType;
            this.displayName = displayName;
            this.minionItem = minionItem;
            this.minionMaxTier = minionMaxTier;
            this.category = category;
        }

        public String getMinionType()
        {
            return this.minionType;
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public ItemStack getMinionItem()
        {
            return this.minionItem;
        }

        public int getMinionMaxTier()
        {
            return this.minionMaxTier;
        }

        public SBSkills.Type getMinionCategory()
        {
            return this.category;
        }
    }

    public static class Data
    {
        private final String minionType;
        private final String craftedTiers;

        public Data(String minionType, String craftedTiers)
        {
            this.minionType = minionType;
            this.craftedTiers = craftedTiers;
        }

        public String getMinionType()
        {
            return this.minionType;
        }

        public String getCraftedTiers()
        {
            return this.craftedTiers;
        }
    }

    public static class CraftedInfo
    {
        private final String minionName;
        private final String displayName;
        private final int minionMaxTier;
        private final String craftedTiers;
        private final ItemStack minionItem;
        private final SBSkills.Type category;

        public CraftedInfo(String minionName, String displayName, int minionMaxTier, String craftedTiers, ItemStack minionItem, SBSkills.Type category)
        {
            this.minionName = minionName;
            this.displayName = displayName;
            this.minionMaxTier = minionMaxTier;
            this.craftedTiers = craftedTiers;
            this.minionItem = minionItem;
            this.category = category;
        }

        public String getMinionName()
        {
            return this.minionName;
        }

        public String getDisplayName()
        {
            return this.displayName;
        }

        public int getMinionMaxTier()
        {
            return this.minionMaxTier;
        }

        public String getCraftedTiers()
        {
            return this.craftedTiers;
        }

        public ItemStack getMinionItem()
        {
            return this.minionItem;
        }

        public SBSkills.Type getMinionCategory()
        {
            return this.category;
        }
    }
}