package debug.builder;

import static debug.Helper.make;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stevekung.skyblockcatia.utils.RenderUtils;

import debug.Helper;
import net.minecraft.util.EnumChatFormatting;

public class PetsBuilder
{
    static final List<PetScore> SCORES = Lists.newLinkedList();
    static final List<PetSkin> SKINS = Lists.newLinkedList();
    static final List<RarityIndex> INDEXS = Lists.newLinkedList();
    static final String PET_EXP = "100,110,120,130,145,160,175,190,210,230,250,275,300,330,360,400,440,490,540,600,660,730,800,880,960,1050,1150,1260,1380,1510,1650,1800,1960,2130,2310,2500,2700,2920,3160,3420,3700,4000,4350,4750,5200,5700,6300,7000,7800,8700,9700,10800,12000,13300,14700,16200,17800,19500,21300,23200,25200,27400,29800,32400,35200,38200,41400,44800,48400,52200,56200,60400,64800,69400,74200,79200,84700,90700,97200,104200,111700,119700,128200,137200,146700,156700,167700,179700,192700,206700,221700,237700,254700,272700,291700,311700,333700,357700,383700,411700,441700,476700,516700,561700,611700,666700,726700,791700,861700,936700,1016700,1101700,1191700,1286700,1386700,1496700,1616700,1746700,1886700";

    static
    {
        SCORES.add(new PetScore(0, 0));
        SCORES.add(new PetScore(10, 1));
        SCORES.add(new PetScore(25, 2));
        SCORES.add(new PetScore(50, 3));
        SCORES.add(new PetScore(75, 4));
        SCORES.add(new PetScore(100, 5));
        SCORES.add(new PetScore(130, 6));
        SCORES.add(new PetScore(175, 7));

        SKINS.add(new PetSkin("ENDERMAN", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE4NGNjODgxOGMyOTM0ODRmZGFhZmM4ZmEyZjBiZjM5ZTU1NzMzYTI0N2Q2ODAyM2RmMmM2YzZiOWI2NzFkMCJ9fX0K"), EnumChatFormatting.DARK_PURPLE, "Spooky Enderman", "62d9011f-9a1c-3673-b60e-d38c5ce1e4af"));
        SKINS.add(new PetSkin("RABBIT", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTM0NjMxZDk0MGZkZGI2ODlkZGVmNmEzYjM1MmM1MDIyMGM0NjBkYmEwNWNkMThkYzgzMTkyYjU5ZGM2NDdmOCJ9fX0K"), EnumChatFormatting.DARK_PURPLE, "Pretty Rabbit", "6e4a93dc-c051-33f0-ae3d-17c29da03219"));
        SKINS.add(new PetSkin("ROCK_COOL", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmVmY2RiYjdkOTU1MDJhY2MxYWUzNWEzMmE0MGNlNGRlYzhmNGM5ZjBkYTI2YzlkOWZlN2MyYzNlYjc0OGY2In19fQo"), EnumChatFormatting.BLUE, "Cool Rock", "0cee5ea7-7af9-30bc-8c91-222ccfc4bae0"));
        SKINS.add(new PetSkin("ROCK_SMILE", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzEzYzhiMjkxNmEyNzVkYjRjMTc2MmNmNWYxM2Q3Yjk1YjkxZDYwYmFmNTE2NGE0NDdkNmVmYTc3MDRjZjExYiJ9fX0K"), EnumChatFormatting.BLUE, "Smiling Rock", "67981030-6f1c-3348-81c5-a3d572617693"));
        SKINS.add(new PetSkin("ROCK_THINKING", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGQyZjc4MWYwM2MzNjViYmM1ZGQxZTcxODZhYjM4ZGM2OTQ2NWU4MzZjOWZlMDY2YTlhODQ0ZjM0YTRkYTkyIn19fQo"), EnumChatFormatting.BLUE, "Thinking Rock", "9562f758-0d5d-393c-abc1-482a2067b664"));
        SKINS.add(new PetSkin("ROCK_EMBARRASSED", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjdmZjM0OTkyZTY2NTk5ZTg1MjkwMDhiZTNmYjU3N2NiMGFiNTQ1Mjk0MjUzZTI1YTBjYzk4OGU0MTZjODQ5In19fQo"), EnumChatFormatting.BLUE, "Embarrassed Rock", "45ff4431-e837-38be-ac08-8cfc9ad09753"));
        SKINS.add(new PetSkin("ROCK_LAUGH", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGNjMWVmNTEzZDVmNjE2Njc1MjQyMTc0YWNkZTdiOWQ2MjU5YTQ3YzRmZThmNmU0YjZlMjA5MjAzMTlkNzA3MyJ9fX0K"), EnumChatFormatting.BLUE, "Laughing Rock", "bec85db9-fd91-396d-861e-8ec67ac1eda9"));
        SKINS.add(new PetSkin("ROCK_DERP", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzRmODlmYmQxMmMyMDlmN2YyNmMxZjM0YTFiZDdmNDc2MzU4MTQ3NTljMDk2ODhkZDIxMmIyMDVjNzNhOGMwMiJ9fX0K"), EnumChatFormatting.BLUE, "Derpy Rock", "7882fc9d-c633-3e5a-8105-8ea45cb6d9ce"));
        SKINS.add(new PetSkin("GUARDIAN", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdjYzc2ZTdhZjI5ZjVmM2ZiZmQ2ZWNlNzk0MTYwODExZWZmOTZmNzUzNDU5ZmE2MWQ3YWQxNzZhMDY0ZTNjNSJ9fX0K"), EnumChatFormatting.DARK_PURPLE, "Watcher Guardian", "36e3a30b-fa57-3aa6-ab40-76565a7ec96f"));
        SKINS.add(new PetSkin("TIGER_TWILIGHT", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODk2MjExZGM1OTkzNjhkYmQ5MDU2YzAxMTZhYjYxMDYzOTkxZGI3OTNiZTkzMDY2YTg1OGViNGU5Y2U1NjQzOCJ9fX0K"), EnumChatFormatting.LIGHT_PURPLE, "Twilight Tiger", "76dba2c8-4555-317e-b3f5-550d29427354"));
        SKINS.add(new PetSkin("SHEEP_PINK", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWZhNzc0NzY4NGRjYjk2MTkyZDkwMzQyY2VhNjI3NDJlYzM2M2RhMDdjYjVlNmUyNWVlY2VjODg4Y2QyMDc2In19fQo"), EnumChatFormatting.GREEN, "Pink Sheep", "8899dc3a-c587-3d3e-8c1a-aa0fba9c38d5"));
        SKINS.add(new PetSkin("SHEEP_PURPLE", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTlhODhjZjdkZDMzMDYzNTg3YzZiNTQwZTYxMzBhYmM1ZDA3ZjFhNjVjNDc1NzNhYjNjMWFkM2NjZWM4ODU3ZiJ9fX0K"), EnumChatFormatting.GREEN, "Purple Sheep", "98d9a319-e671-34a0-b1da-a912ef421fc9"));
        SKINS.add(new PetSkin("SHEEP_WHITE", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjkyYTFhNWMzMjVmMjVmNzQzOGEwYWJiNGY4NmJhNmNmNzU1NTJkMDJjNzM0OWE3MjkyOTgxNDU5YjMxZDJmNyJ9fX0K"), EnumChatFormatting.GREEN, "White Sheep", "354676d5-1529-327c-b06e-899b92132403"));
        SKINS.add(new PetSkin("SHEEP_LIGHT_BLUE", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzIyMjIwZGUxYTg2M2JjNWQ5YjllN2E2YTNiMDMyMTRjOWYzZDY5OGVkM2ZlMGQyODIyMGYzYjkzYjc2ODVjNSJ9fX0K"), EnumChatFormatting.GREEN, "Light Blue Sheep", "5ee3925d-b768-331c-af39-d00e1363aa1f"));
        SKINS.add(new PetSkin("SHEEP_LIGHT_GREEN", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2YxODNlYzJmZTU4ZmFhNDNlNTY4NDE5YjdhMGRjNDQ2ZWNlNGVhMGJlNTJlYzc4NGM5NGUxZDc0Yjc1OTM5ZCJ9fX0K"), EnumChatFormatting.GREEN, "Lime Sheep", "235159c0-4c38-356f-8a2b-519b15d03730"));
        SKINS.add(new PetSkin("SHEEP_BLACK", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWE5ZGNkYTY0MmE4MDdjZDJkYWE0YWE2YmU4N2NlZjk2ZTA4YThjOGY1Y2VjMjY1N2RkYTQyNjZjNmE4ODRjMiJ9fX0K"), EnumChatFormatting.GREEN, "Black Sheep", "a44786f8-b0f4-3e43-ad1b-64956113adec"));
        SKINS.add(new PetSkin("WITHER", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjI0YzJkMTRhMDIxOWFmNWNjZmNhYTM2ZThhMzMzZTI3MTcyNGVkNjEyNzY2MTFmOTUyOWUxNmMxMDI3M2EwZCJ9fX0K"), EnumChatFormatting.DARK_PURPLE, "Dark Wither Skeleton", "f336e789-b720-36ff-9326-71c8bf135db4"));
        SKINS.add(new PetSkin("SILVERFISH", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDg1NTJmZjU5MTA0MmM0YTM4ZjhiYTA2MjY3ODRhZTI4YzQ1NDVhOTdkNDIzZmQ5MDM3YzM0MTAzNTU5MzI3MyJ9fX0K"), EnumChatFormatting.DARK_PURPLE, "Fortified Silverfish", "ee81d609-06c4-38e7-97e5-dd3ef196a4ba"));
        SKINS.add(new PetSkin("ELEPHANT_PINK", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwNTQwMTEyOTc5NSwKICAicHJvZmlsZUlkIiA6ICJiYzRlZGZiNWYzNmM0OGE3YWM5ZjFhMzlkYzIzZjRmOCIsCiAgInByb2ZpbGVOYW1lIiA6ICI4YWNhNjgwYjIyNDYxMzQwIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzU3MGVlZjQ3NGVjMGU1NmNjMzRjMjMwN2VhYTM5ZjAyNDYxMmY4Y2Q3MjQ4ZTdkNWIxNDE2OWViZDMwN2M3NDIiCiAgICB9CiAgfQp9"), EnumChatFormatting.BLUE, "Pink Elephant", "800b4068-e32d-3528-b3d1-a7b8157eb790"));
        SKINS.add(new PetSkin("ELEPHANT_BLUE", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwNTQwMTQzNjQxNiwKICAicHJvZmlsZUlkIiA6ICI3MmNiMDYyMWU1MTA0MDdjOWRlMDA1OTRmNjAxNTIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNb3M5OTAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGI2Mjk2OWMwMDU4MTVkMDQwOTEzNjM4MGZlYmM1YWM0NjhhYWJhOWJkYTRkYjgwOTU0ZmE1NDI2ZWUwYTMyMyIKICAgIH0KICB9Cn0="), EnumChatFormatting.BLUE, "Blue Elephant", "2d6175b3-b23f-3a26-bbb5-a5149b63d0df"));
        SKINS.add(new PetSkin("ELEPHANT_ORANGE", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwNTQwMTI4Nzc2MiwKICAicHJvZmlsZUlkIiA6ICIyMWUzNjdkNzI1Y2Y0ZTNiYjI2OTJjNGEzMDBhNGRlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJHZXlzZXJNQyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81NTRhMzRhODBjNDc0MjA2ZDM3MDBiOGZjZWQ2YjQ0ZmFiMGIwZWQwYjA1YzEyOTNmZjBjNWQ4NmVkYTI1MWQxIgogICAgfQogIH0KfQ"), EnumChatFormatting.BLUE, "Orange Elephant", "69a9731d-621a-36db-bb64-e63dd541f7ea"));
        SKINS.add(new PetSkin("ELEPHANT_RED", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwNTQwMTI2NjY1NCwKICAicHJvZmlsZUlkIiA6ICI5ZDQyNWFiOGFmZjg0MGU1OWM3NzUzZjc5Mjg5YjMyZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJUb21wa2luNDIiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmE1YzY2ZWM2NmNiNmI0YjU1NTAwODVmNTgzYjRlNWMxY2VlNTI0N2JlYzVmYmNjNWMzMThjMzBjNjZjYWI0MiIKICAgIH0KICB9Cn0"), EnumChatFormatting.BLUE, "Red Elephant", "abfffe75-364e-37c4-ae74-e95316a2daa1"));
        SKINS.add(new PetSkin("ELEPHANT_GREEN", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwNTQwMTMxNDcxMCwKICAicHJvZmlsZUlkIiA6ICI5NGMzZGM3YTdiMmQ0NzQ1YmVlYjQzZDc2ZjRjNDVkYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJEb25fVml0b0Nvcmxlb25lIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzM2MGMxMjJhZGU1YjJmZWRjYTE0YWE3OGM4MzRhN2IwYWM5Y2I1ZGEyYTBjOTMxMTIxNjMwODZmOTBjMTNiNjgiCiAgICB9CiAgfQp9"), EnumChatFormatting.BLUE, "Green Elephant", "8a96cf53-45b1-35e2-9f96-cc13d463e6e0"));
        SKINS.add(new PetSkin("ELEPHANT_PURPLE", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwNTQwMTM0MDY1NSwKICAicHJvZmlsZUlkIiA6ICIwNjEzY2I1Y2QxYjg0M2JjYjI4OTk1NWU4N2QzMGEyYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJicmVhZGxvYWZzcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81ZmY5ZGYyOTBiNmM1YTQ5ODRmYzZlNTE2NjA1Zjk4MTZiOTg4MmY3YmYwNGRiMDhkM2Y3ZWUzMmQxOTY5YTQ0IgogICAgfQogIH0KfQ"), EnumChatFormatting.BLUE, "Purple Elephant", "de6b68c9-5d12-3279-b058-cbcf8636b585"));
        SKINS.add(new PetSkin("YETI_GROWN_UP", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwNjQyODA5NTg3NywKICAicHJvZmlsZUlkIiA6ICIzZmM3ZmRmOTM5NjM0YzQxOTExOTliYTNmN2NjM2ZlZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJZZWxlaGEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVmMjlhOTc1NTI5Mjc2ZDkxNmZjNjc5OTg4MzNjMTFlZTE3OGZmMjFlNTk0MWFmZGZiMGZhNzAxMGY4Mzc0ZSIKICAgIH0KICB9Cn0"), EnumChatFormatting.DARK_PURPLE, "Grown-up Baby Yeti", "3a80504c-0993-3be5-92cd-2200f94a72b6"));
        SKINS.add(new PetSkin("JERRY_RED_ELF", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwODUwODM4MTEwMCwKICAicHJvZmlsZUlkIiA6ICI4MmM2MDZjNWM2NTI0Yjc5OGI5MWExMmQzYTYxNjk3NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3ROb3RvcmlvdXNOZW1vIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzFkODJmOWMzNmU4MjRjMWUzNzk2M2E4NDliZjVhYmQ3NmQzYjM0OTEyNTAyMzUwNGFmNTgzNjkwODYwODllZTkiCiAgICB9CiAgfQp9"), EnumChatFormatting.GREEN, "Red Elf Jerry", "cf24edde-fca6-3a29-b9e1-51ef7b4f2662"));
        SKINS.add(new PetSkin("JERRY_GREEN_ELF", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwODUwODQzNDQ1MSwKICAicHJvZmlsZUlkIiA6ICIyM2YxYTU5ZjQ2OWI0M2RkYmRiNTM3YmZlYzEwNDcxZiIsCiAgInByb2ZpbGVOYW1lIiA6ICIyODA3IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzRlYzU0NTVmNDM0MjZjYTE4NzRiNWM3YjRhNDkyZWMzNzIyYTUwMmY4Yjk1OTllNzU4ZTEzM2ZlZDhiM2MxZTQiCiAgICB9CiAgfQp9"), EnumChatFormatting.GREEN, "Green Elf Jerry", "71523c97-40b1-3c07-8e82-a249ebe9339c"));
        SKINS.add(new PetSkin("SHEEP_NEON_YELLOW", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTQyNjM0MjhjMjNkYTkxNjViMjYzOWE4ZjI0MjhmZjQ4MzUyMjc5NDVjOWUxMDM4NDYxY2Y2NDRkNjdjYzgyYSJ9fX0K"), EnumChatFormatting.BLUE, "Neon Yellow Sheep", "f6751ef8-70a9-3e15-9581-ad4a0ca99327"));
        SKINS.add(new PetSkin("SHEEP_NEON_RED", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDkxOGJlMTQyYTIwYjJiMzliYzU4MmY0MjFmNmFlODdiMzE4NGI1Yzk1MjNkMTZmYmU2ZDY5NTMwMTA3ODg2YSJ9fX0K"), EnumChatFormatting.BLUE, "Neon Red Sheep", "7714c9a8-c3c0-3a7d-b759-cebb4042136b"));
        SKINS.add(new PetSkin("SHEEP_NEON_BLUE", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTU1YjNmZTkzMTFjOTkzNDJlYTU2NTQ4M2NiZjllOTY5YTI1OGZhZjdhZmEzMDI3MGZiOWEwOTI5Mzc3YWNmZCJ9fX0K"), EnumChatFormatting.BLUE, "Neon Blue Sheep", "c7ad0ae4-f4bb-3f44-ae71-03dc976ba1f3"));
        SKINS.add(new PetSkin("SHEEP_NEON_GREEN", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmMxNGQ2NjkxMTU1NGJkMDg4MjMzOTA3NGJmNmI4MTEwYzJkMzUwOWI2OWU3YTYxNDRlNGQ1YTcxNjRiYWNjOCJ9fX0K"), EnumChatFormatting.BLUE, "Neon Green Sheep", "1144114b-5624-323e-bb06-66448d2f0f96"));
        SKINS.add(new PetSkin("MONKEY_GOLDEN", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwNTk5Mzk0OTI2NCwKICAicHJvZmlsZUlkIiA6ICJjZGM5MzQ0NDAzODM0ZDdkYmRmOWUyMmVjZmM5MzBiZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJSYXdMb2JzdGVycyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lOTI4MWM0ZDg3ZDY4NTI2YjA3NDlkNDM2MWU2ZWY3ODZjOGEzNTcxN2FhMDUzZGE3MDRiMWQ1MzQxMGQzN2E2IgogICAgfQogIH0KfQ=="), EnumChatFormatting.GOLD, "Golden Monkey", "b78e9901-460c-3b18-af8c-8cb9c9739e23"));
        SKINS.add(new PetSkin("SILVERFISH_FOSSILIZED", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYxMTEwMjQ5MTY3OCwKICAicHJvZmlsZUlkIiA6ICI3ZGEyYWIzYTkzY2E0OGVlODMwNDhhZmMzYjgwZTY4ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJHb2xkYXBmZWwiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2EzYTM2MzM2OGVkMWUwNmNlZTM5MDA3MTdmMDYyZTAyZWMzOWFlZTE3NDc2NzUzOTIyNTViNDhmN2Y4MzYwMCIKICAgIH0KICB9Cn0"), EnumChatFormatting.DARK_PURPLE, "Fossilized Silverfish", "9383ffbc-3aa1-3c38-a957-97d4968ee2b7"));
        SKINS.add(new PetSkin("HORSE_ZOMBIE", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwNTQwMjg1NDk2NCwKICAicHJvZmlsZUlkIiA6ICJmMzA1ZjA5NDI0NTg0ZjU4YmEyYjY0ZjAyZDcyNDYyYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJqcm9ja2EzMyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS81NzgyMTFlMWI0ZDk5ZDFjN2JmZGE0ODM4ZTQ4ZmM4ODRjM2VhZTM3NmY1OGQ5MzJiYzJmNzhiMGE5MTlmOGU3IgogICAgfQogIH0KfQ=="), EnumChatFormatting.DARK_PURPLE, "Zombie Skeleton Horse", "76e06cd3-376f-3cfa-9431-1c2b67de42e9"));
        SKINS.add(new PetSkin("DRAGON_NEON_BLUE", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYxMTEwMjM4NzYxNiwKICAicHJvZmlsZUlkIiA6ICI2ZmU4OTUxZDVhY2M0NDc3OWI2ZmYxMmU3YzFlOTQ2MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJlcGhlbXJhIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzk2YTRiOWZiY2Y4YzNlN2UxMjMyZTU3ZDZhMjg3MGJhM2VhMzBmNzY0MDdhZTExOTdmZDUyZTlmNzZjYTQ2YWMiCiAgICB9CiAgfQp9"), EnumChatFormatting.GOLD, "Blue Ender Dragon", "d034068b-714d-3264-825e-eb4b89fb27af"));
        SKINS.add(new PetSkin("DRAGON_NEON_PURPLE", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYxMTEwMjQ2NjQwNCwKICAicHJvZmlsZUlkIiA6ICIxNzhmMTJkYWMzNTQ0ZjRhYjExNzkyZDc1MDkzY2JmYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzaWxlbnRkZXRydWN0aW9uIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzU0YmRmNWJhNjI4OWIyOWUyN2M1N2RiMWVjN2Y3NjE1MWMzOTQ5MmQ0MDkyNjhlMDBhOTgzOGU4Yzk2MzE1OSIKICAgIH0KICB9Cn0"), EnumChatFormatting.GOLD, "Purple Ender Dragon", "9e485884-34c2-388b-b798-ef899b618c33"));
        SKINS.add(new PetSkin("DRAGON_NEON_RED", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYxMTEwMjQ0MjI1NCwKICAicHJvZmlsZUlkIiA6ICJhNzdkNmQ2YmFjOWE0NzY3YTFhNzU1NjYxOTllYmY5MiIsCiAgInByb2ZpbGVOYW1lIiA6ICIwOEJFRDUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTA1YzliNGY0MjE4Njc3YzViNGJjYzljN2Q5ZTI5ZTE4ZDE2ODRhNTM2NzgxZmVkZTEyODBmYzVlNjk2MTUzOCIKICAgIH0KICB9Cn0"), EnumChatFormatting.GOLD, "Red Ender Dragon", "8e4b87b0-fd00-3eaa-a732-788e791b42bc"));
        SKINS.add(new PetSkin("WHALE_ORCA", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwNTk5NDkwMzQ1MCwKICAicHJvZmlsZUlkIiA6ICJmMjc0YzRkNjI1MDQ0ZTQxOGVmYmYwNmM3NWIyMDIxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJIeXBpZ3NlbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iMDA4Y2E5YzAwY2VjZjQ5OTY4NTAzMGU4ZWYwYzIzMGEzMjkwODYxOWNlOWRjMTA2OTBiNjkxMTE1OTFmYWExIgogICAgfQogIH0KfQ=="), EnumChatFormatting.DARK_PURPLE, "Orca Blue Whale", "b22decd6-faf9-3872-9f85-cc76c011f37b"));
        SKINS.add(new PetSkin("ELEPHANT_MONOCHROME", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwNTQwMTIyMDM3NywKICAicHJvZmlsZUlkIiA6ICI0ZTMwZjUwZTdiYWU0M2YzYWZkMmE3NDUyY2ViZTI5YyIsCiAgInByb2ZpbGVOYW1lIiA6ICJfdG9tYXRvel8iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGJkZjBmNjI4YzA1ZTg2Y2FiZGVlMmY1ODU4ZGQ1ZGVmN2Y4YjhkOTQwY2JmMjVmOTkzN2UyZmZiNTM0MzJmNCIKICAgIH0KICB9Cn0"), EnumChatFormatting.DARK_PURPLE, "Monochrome Elephant", "b22decd6-faf9-3872-9f85-cc76c011f37b"));
        SKINS.add(new PetSkin("CHICKEN_BABY_CHICK", RenderUtils.decodeTextureURL("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWJkZTU1ZWQ1NGNiNWM4NzY2MWI4NmMzNDkxODZhOWQ1YmFmZmIzY2I5MzRiNDQ5YTJkMzI5ZTM5OWQzNGJmIn19fQ=="), EnumChatFormatting.DARK_PURPLE, "Baby Chick Chicken", "83abf023-37cc-3058-a9ff-c8d46cb57a2b"));
        SKINS.add(new PetSkin("RABBIT_AQUAMARINE", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYxNjk2MDMyODc3MCwKICAicHJvZmlsZUlkIiA6ICI5ZDIyZGRhOTVmZGI0MjFmOGZhNjAzNTI1YThkZmE4ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTYWZlRHJpZnQ0OCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zNWEyMTE5ZDEyMjk2MTg1MmMwMTBjMTAwN2FiMmFmZjk1YjRiYmViNzQ0MDc0NjNmNmQyZTFmZjA3OTJjODEyIgogICAgfQogIH0KfQ=="), EnumChatFormatting.BLUE, "Aquamarine Rabbit", "9217de73-e2db-335f-acbd-c572693c71d2"));
        SKINS.add(new PetSkin("RABBIT_ROSE", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYxNjk2MDExNDY4NiwKICAicHJvZmlsZUlkIiA6ICJiMGQ3MzJmZTAwZjc0MDdlOWU3Zjc0NjMwMWNkOThjYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJPUHBscyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kN2NkZGY1YjIwY2I1MGQ2NjAwZTUzMzNjNmJiM2ZiMTViNDc0MWYxN2UzNjc1ZmMyYmZjMDljMmNkMDllNjE5IgogICAgfQogIH0KfQ=="), EnumChatFormatting.BLUE, "Rose Rabbit", "a0482a4d-dc98-3654-855c-21a66508cb72"));
        SKINS.add(new PetSkin("BLACK_CAT_IVORY", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYxMDIyNTAxMTY3MiwKICAicHJvZmlsZUlkIiA6ICIxNzhmMTJkYWMzNTQ0ZjRhYjExNzkyZDc1MDkzY2JmYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzaWxlbnRkZXRydWN0aW9uIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y1MWIxN2Q3ZGVkNmM3ZThmM2IyZGFjMTIzNzhhNmZjNGU5MjI4YjkxMTk4NmY2NGM4YWY0NTgzN2FlNmQ5ZTEiCiAgICB9CiAgfQp9"), EnumChatFormatting.DARK_PURPLE, "Ivory Black Cat", "226082cc-4eae-30ba-b3df-f73b2f898e29"));
        SKINS.add(new PetSkin("BLACK_CAT_ONYX", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYxMDIyNTA0NDA4MiwKICAicHJvZmlsZUlkIiA6ICJiMGQ3MzJmZTAwZjc0MDdlOWU3Zjc0NjMwMWNkOThjYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJPUHBscyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iZTkyNDExNWQzYThiYmFjZmQ0ZmFmYjZjYzcwZjk5YTJmNzU4MGU0NTgzYTUwZmE5YjljMjg1YTk4YWMwYzU2IgogICAgfQogIH0KfQ"), EnumChatFormatting.DARK_PURPLE, "Onyx Black Cat", "9c1f2945-99bf-3ab2-bd0f-7f223a58c7db"));
        SKINS.add(new PetSkin("MONKEY_GORILLA", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwNTM5MjY2NDk2MCwKICAicHJvZmlsZUlkIiA6ICIxOTI1MjFiNGVmZGI0MjVjODkzMWYwMmE4NDk2ZTExYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJTZXJpYWxpemFibGUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNlYjNlMzdlOTg3M2JmYzE3NmI5ZWQ4ZWY0ZmJlZjgzM2RlMTQ0NTQ2YmZhZWZkZjI0ODYzYzNlYjg3YmI4NiIKICAgIH0KICB9Cn0"), EnumChatFormatting.LIGHT_PURPLE, "Gorilla Monkey", "3dcb342c-23bb-3ba8-8abe-1b6ac6c03022"));
        SKINS.add(new PetSkin("ENDERMITE_RADIANT", RenderUtils.decodeTextureURL("ewogICJ0aW1lc3RhbXAiIDogMTYwNTQwMTk0MDQ4NiwKICAicHJvZmlsZUlkIiA6ICI3MmNiMDYyMWU1MTA0MDdjOWRlMDA1OTRmNjAxNTIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNb3M5OTAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmZjNGE3NTQyYjc1NDQyMGIxYjE5ZjlhMjhlYTAwMDQwNTU1YTllODc2MDUyYjk3ZjY1ODQwMzA4YTkzMzQ4ZCIKICAgIH0KICB9Cn0"), EnumChatFormatting.LIGHT_PURPLE, "Radiant Endermite", "f09dfb7d-81f5-32d3-b491-9dd1626cf611"));

        INDEXS.add(new RarityIndex("COMMON", 0));
        INDEXS.add(new RarityIndex("UNCOMMON", 6));
        INDEXS.add(new RarityIndex("RARE", 11));
        INDEXS.add(new RarityIndex("EPIC", 16));
        INDEXS.add(new RarityIndex("LEGENDARY", 20));
        INDEXS.add(new RarityIndex("MYTHIC", 20));
    }

    public static void main(String[] args) throws IOException
    {
        Map<String, Object> maps = Maps.newTreeMap();
        List<Map<String, Object>> petTypeList = Lists.newLinkedList();
        List<Map<String, Object>> heldItemList = Lists.newLinkedList();
        List<Map<String, Object>> scoreList = Lists.newLinkedList();
        List<Map<String, Object>> skinList = Lists.newLinkedList();

        List<PetType> petTypeSort = Lists.newArrayList();
        petTypeSort.addAll(EnumSet.allOf(PetType.class));
        Collections.sort(petTypeSort, (o1, o2) -> new CompareToBuilder().append(o1.name(), o2.name()).build());

        for (PetType type : petTypeSort)
        {
            Map<String, Object> prop = Maps.newLinkedHashMap();
            Map<String, Object> statsLore = Maps.newLinkedHashMap();
            Map<String, Object> petLore = Maps.newLinkedHashMap();

            prop.put("type", type.name());
            prop.put("skill", type.type);
            prop.put("uuid", type.uuid);
            prop.put("texture", type.value);

            if (type.desc != null)
            {
                prop.put("description", type.desc);
            }
            if (type.stats != null)
            {
                Map<String, Object> innerStats = Maps.newLinkedHashMap();

                for (Stats stats : type.stats)
                {
                    String statType = stats.type;
                    Map<String, Object> realstats = Maps.newLinkedHashMap();
                    realstats.put("base", stats.base);
                    realstats.put("multiply", stats.multiply);

                    if (stats.percent)
                    {
                        realstats.put("percent", stats.percent);
                    }
                    innerStats.put(statType, realstats);
                }
                prop.put("stats", innerStats);
            }
            if (type.descStats != null)
            {
                for (Entry<String, Object> lore : type.descStats.entrySet())
                {
                    if (lore.getValue() instanceof Pair)
                    {
                        Property leftProp = (Property)((Pair)lore.getValue()).getLeft();
                        Property rightProp = (Property)((Pair)lore.getValue()).getRight();
                        List<Double> baseList;
                        List<Double> multiplyList = new Gson().fromJson(rightProp.doubleList, new TypeToken<List<Double>>() {}.getType());

                        if (leftProp == null)
                        {
                            List<Double> temp = Lists.newArrayList();

                            for (int i = 0; i < multiplyList.size(); i++)
                            {
                                temp.add((double)0);
                            }
                            baseList = temp;
                        }
                        else
                        {
                            baseList = new Gson().fromJson(leftProp.doubleList, new TypeToken<List<Double>>() {}.getType());
                        }

                        Map<String, Object> realstats = Maps.newLinkedHashMap();

                        if (baseList.size() != multiplyList.size())
                        {
                            throw new RuntimeException("Pet " + type + " doesn't have the same lore length \n Key: " + lore.getKey() + " Base: " + baseList.size() + " Multiply: " + multiplyList.size());
                        }

                        realstats.put("base", baseList);
                        realstats.put("multiply", multiplyList);

                        if (rightProp.roundingMode != null)
                        {
                            realstats.put("rounding_mode", rightProp.roundingMode.name());
                        }
                        if (rightProp.displayMode != null)
                        {
                            realstats.put("display_mode", rightProp.displayMode.name());
                        }
                        statsLore.put(lore.getKey(), realstats);
                    }
                    else
                    {
                        Property leftProp = (Property)((Triple)lore.getValue()).getLeft();
                        Property middleProp = (Property)((Triple)lore.getValue()).getMiddle();
                        Property rightProp = (Property)((Triple)lore.getValue()).getRight();
                        Map<String, Object> realstats = Maps.newLinkedHashMap();
                        List<Double> baseList;
                        List<Double> additionalList = new Gson().fromJson(rightProp.doubleList, new TypeToken<List<Double>>() {}.getType());

                        if (middleProp != null)
                        {
                            List<Double> multiplyList = new Gson().fromJson(middleProp.doubleList, new TypeToken<List<Double>>() {}.getType());

                            if (leftProp == null)
                            {
                                List<Double> temp = Lists.newArrayList();

                                for (int i = 0; i < multiplyList.size(); i++)
                                {
                                    temp.add((double)0);
                                }
                                baseList = temp;
                            }
                            else
                            {
                                baseList = new Gson().fromJson(leftProp.doubleList, new TypeToken<List<Double>>() {}.getType());
                            }

                            if (baseList.size() != multiplyList.size())
                            {
                                throw new RuntimeException("Pet " + type + " doesn't have the same lore length \n Key: " + lore.getKey() + " Base: " + baseList.size() + " Multiply: " + multiplyList.size());
                            }

                            realstats.put("base", baseList);
                            realstats.put("multiply", multiplyList);
                        }
                        realstats.put("additional", additionalList);

                        if (middleProp != null)
                        {
                            if (middleProp.roundingMode != null)
                            {
                                realstats.put("rounding_mode", middleProp.roundingMode.name());
                            }
                            if (middleProp.displayMode != null)
                            {
                                realstats.put("display_mode", middleProp.displayMode.name());
                            }
                        }
                        statsLore.put(lore.getKey(), realstats);
                    }
                }
                prop.put("statsLore", statsLore);
            }
            if (type.petLore != null)
            {
                for (Entry<String, List<String>> statEntry : type.petLore.entrySet())
                {
                    List<String> innerStats = Lists.newLinkedList();
                    innerStats.addAll(statEntry.getValue());
                    petLore.put(statEntry.getKey(), innerStats);
                }
                prop.put("lore", petLore);
            }
            if (type.loreMode != null)
            {
                prop.put("lore_mode", type.loreMode);
            }
            petTypeList.add(prop);
        }

        List<HeldItem> heldItemSort = Lists.newArrayList();
        heldItemSort.addAll(EnumSet.allOf(HeldItem.class));
        Collections.sort(heldItemSort, (o1, o2) -> new CompareToBuilder().append(o1.name(), o2.name()).build());

        for (HeldItem item : heldItemSort)
        {
            Map<String, Object> prop = Maps.newLinkedHashMap();
            Map<String, Object> innerStats = Maps.newLinkedHashMap();
            String heldItemName = WordUtils.capitalize(item.toString().toLowerCase(Locale.ROOT).replace("pet_item_", "").replace("_", " "));

            if (item.altName != null)
            {
                heldItemName = item.altName;
            }

            prop.put("type", item.name());
            prop.put("name", heldItemName);
            prop.put("color", item.color.getFriendlyName());

            if (item.isUpgradeToNextRarity())
            {
                prop.put("isUpgrade", true);
            }
            if (item.lore != null)
            {
                prop.put("lore", item.lore);
            }
            if (item.stats != null)
            {
                for (Stats stats : item.stats)
                {
                    String statType = stats.type;
                    Map<String, Object> realstats = Maps.newLinkedHashMap();
                    realstats.put("base", stats.base);

                    if (stats.percent)
                    {
                        realstats.put("percent", stats.percent);
                    }
                    innerStats.put(statType, realstats);
                }
                prop.put("stats", innerStats);
            }
            heldItemList.add(prop);
        }

        for (PetScore score : SCORES)
        {
            Map<String, Object> prop = Maps.newLinkedHashMap();

            prop.put("value", score.score);
            prop.put("magic_find", score.magic_find);

            scoreList.add(prop);
        }

        SKINS.sort((o1, o2) -> new CompareToBuilder().append(o1.skin, o2.skin).build());

        for (PetSkin skin : SKINS)
        {
            Map<String, Object> prop = Maps.newLinkedHashMap();

            prop.put("type", skin.skin);
            prop.put("displayName", skin.name);
            prop.put("color", skin.color.getFriendlyName());
            prop.put("uuid", skin.uuid);
            prop.put("texture", skin.texture);
            skinList.add(prop);
        }

        Map<String, Object> rarityIndex = Maps.newLinkedHashMap();

        for (RarityIndex index : INDEXS)
        {
            rarityIndex.put(index.type, index.index);
        }

        String[] expSplit = PET_EXP.split(",");
        List<Integer> levels = Lists.newLinkedList();

        for (String element : expSplit)
        {
            levels.add(Integer.valueOf(element));
        }

        maps.put("type", petTypeList);
        maps.put("held_item", heldItemList);
        maps.put("score", scoreList);
        maps.put("skin", skinList);
        maps.put("index", rarityIndex);
        maps.put("leveling", levels);

        File file = new File("M:/Modding/SkyBlockcatia/SkyblockData", "pets.json");
        File file2 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.8.9/src/main/resources/assets/skyblockcatia/api", "pets.json");
        File file3 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.16.5_architectury/common/src/main/resources/assets/skyblockcatia/api", "pets.json");

        Helper.writeFile(maps, file);
        Helper.writeFile(maps, file2);
        Helper.writeFile(maps, file3);
    }

    static class Stats
    {
        String type;
        double base;
        double multiply;
        boolean percent;

        Stats(String type, double base, double multiply, boolean percent)
        {
            this.type = type;
            this.base = base;
            this.multiply = multiply;
            this.percent = percent;
        }

        static Stats build(String type, double base, double multiply)
        {
            return new Stats(type, base, multiply, false);
        }

        static Stats build(String type, double base, double multiply, boolean percent)
        {
            return new Stats(type, base, multiply, percent);
        }
    }

    static class Property
    {
        String doubleList;
        DisplayMode displayMode;
        RoundingMode roundingMode;

        Property(String doubleList)
        {
            this.doubleList = doubleList;
        }

        Property(DisplayMode displayMode, String doubleList)
        {
            this.doubleList = doubleList;
            this.displayMode = displayMode;
        }

        Property(RoundingMode roundingMode, String doubleList)
        {
            this.doubleList = doubleList;
            this.roundingMode = roundingMode;
        }

        static Property build(String doubleList)
        {
            return new Property(doubleList);
        }

        static Property build(DisplayMode displayMode, String doubleList)
        {
            return new Property(displayMode, doubleList);
        }

        static Property build(RoundingMode roundingMode, String doubleList)
        {
            return new Property(roundingMode, doubleList);
        }
    }

    enum DisplayMode
    {
        DISPLAY_AT_LEVEL_1,
        COMBINE_WITH_BASE;
    }

    enum PetType
    {
        BABY_YETI("FISHING", "7895e21a-8f3b-3e30-bea6-06108f64d5dc", "ab126814fc3fa846dad934c349628a7a1de5b415021a03ef4211d62514d5", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("strength", 0, 0.4));
            list.add(Stats.build("intelligence", 0, 0.75));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", Pair.of(null, Property.build("[0.5, 1]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.5, 1, 1]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Cold Breeze");
                list.add("§7Gives §a{0} §c❁ Strength §7and");
                list.add("§9☠ Crit Damage §7when near snow");
                list.add("");
                list.add("§6Ice Shields");
                list.add("§7Gain §a{1}% §7of your strength");
                list.add("§7as §a❈ Defense");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Yeti Fury");
                list.add("§7Buff the Yeti sword by §a{2}");
                list.add("§c❁ Damage §7and §b✎");
                list.add("§bIntelligence");
            }));
        })),

        BAT("MINING", "1911c3bb-c0af-3474-98e2-486478c5b9ea", "382fc3f71b41769376a9e92fe3adbaac3772b999b219c9d6b4680ba9983e527", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("speed", 0, 0.05));
            list.add(Stats.build("intelligence", 0, 1));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.1]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.1]")));
            map.put("RARE", Pair.of(null, Property.build("[0.2, 0.3, 0.5]")));
            map.put("EPIC", Pair.of(null, Property.build("[0.2, 0.3, 0.5]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.2, 0.3, 0.5, 0.5]")));
            map.put("MYTHIC", Pair.of(null, Property.build("[0.2, 0.3, 0.5, 0.5, 0.25]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Candy Lover");
                list.add("§7Increases drop chance of");
                list.add("§7candies from mobs by §a{0}%");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Nightmare");
                list.add("§7During night, gain §a{1} §b✎");
                list.add("§bIntelligence§7, §a{2} §f✦");
                list.add("§fSpeed§7, and night vision");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Wings of Steel");
                list.add("§7Deals §a+{3}% §7damage to");
                list.add("§6Spooky §7enemies during the");
                list.add("§6Spooky Festival");
            }));
            map.put("MYTHIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Sonar");
                list.add("§7+§a{4}% §7chance to fish up");
                list.add("§7spooky sea creatures");
            }));
        })),

        BEE("FARMING", "af894c68-45d0-3ae2-952c-b3cf925199ad", "7e941987e825a24ea7baafab9819344b6c247c75c54a691987cd296bc163c263", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("intelligence", 0, 0.5));
            list.add(Stats.build("speed", 0, 0.1));
            list.add(Stats.build("strength", 5, 0.25));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(Property.build("[1, 1]"), Property.build(RoundingMode.DOWN, "[0.02, 0.02]")));
            map.put("UNCOMMON", Pair.of(Property.build("[1, 1]"), Property.build(RoundingMode.DOWN, "[0.04, 0.04]")));
            map.put("RARE", Pair.of(Property.build("[1.1, 1.1, 0.5]"), Property.build(RoundingMode.DOWN, "[0.089, 0.069, 0.995]")));
            map.put("EPIC", Pair.of(Property.build("[1.1, 1.1, 0]"), Property.build(RoundingMode.DOWN, "[0.139, 0.109, 1]")));
            map.put("LEGENDARY", Pair.of(Property.build("[1.2, 1.1, 0, 5.2]"), Property.build(RoundingMode.DOWN, "[0.188, 0.139, 1, 0.198]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Hive");
                list.add("§7Gain §b+{0}✎ Intelligence");
                list.add("§7and §c+{1}❁ Strength §7for");
                list.add("§7each nearby bee.");
                list.add("§8Max 15 bees");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Busy Buzz Buzz");
                list.add("§7Has §a{2}% §7chance for flowers");
                list.add("§7to drop an extra one");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Weaponized Honey");
                list.add("§7Gain §a{3}% §7of received");
                list.add("§7damage as §6❤ Absorption");
            }));
        })),

        BLACK_CAT("COMBAT", "5992f40a-6406-48a3-867b-232e414232f3", "e4b45cbaa19fe3d68c856cd3846c03b5f59de81a480eec921ab4fa3cd81317", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("speed", 0, 0.25));
            list.add(Stats.build("intelligence", 0, 1));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("LEGENDARY", Pair.of(null, Property.build("[1, 0.15, 0.15]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Hunter");
                list.add("§7Increases your speed and speed");
                list.add("§7cap by +§a{0}");
                list.add("");
                list.add("§6Omen");
                list.add("§7Grants §a{1} §d♣ Pet Luck");
                list.add("");
                list.add("§6Supernatural");
                list.add("§7Grants §a{2} §b✯ Magic Find");
            }));
        })),

        BLAZE("COMBAT", "118fe834-28aa-3b0d-afe6-f0c52d01afe8", "b78ef2e4cf2c41a2d14bfde9caff10219f5b1bf5b35a49eb51c6467882cb5f0", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("defense", 10, 0.2));
            list.add(Stats.build("intelligence", 0, 1));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("ALL", Pair.of(null, Property.build("[0.2, 0.4]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Nether Embodiment");
                list.add("§7Increases all stats by §a{0}%");
                list.add("§7while on the Blazing Fortress");
                list.add("");
                list.add("§6Bling Armor");
                list.add("§7Upgrades §cBlaze Armor §7stats");
                list.add("§7and ability by §a{1}%");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Fusion-Style Potato");
                list.add("§7Double effects of hot potato");
                list.add("§7books");
            }));
        })),

        BLUE_WHALE("FISHING", "47c8ba46-82ac-3c09-b511-5502860eb012", "dab779bbccc849f88273d844e8ca2f3a67a1699cb216c0a11b44326ce2cc20", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("health", 0, 2));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.5]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[1]")));
            map.put("RARE", Pair.of(null, Property.build("[1.5, 0.03]")));
            map.put("EPIC", Pair.of(null, Property.build("[2, 0.03]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[2.5, 0.03, 0.2]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Ingest");
                list.add("§7All potions heal §c+{0}❤");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Bulk");
                list.add("§7Gain §a{1}§a❈ Defense §7per");
                list.add("§c20 Max §c❤ Health");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Archimedes");
                list.add("§7Gain §c+{2}% Max §c❤ Health");
            }));
        })),

        CHICKEN("FARMING", "635fdfb8-3c52-433e-87dc-70a9406c5ff0", "7f37d524c3eed171ce149887ea1dee4ed399904727d521865688ece3bac75e", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("health", 0, 2));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.3]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.4]")));
            map.put("RARE", Pair.of(null, Property.build("[0.4, 0.8]")));
            map.put("EPIC", Pair.of(null, Property.build("[0.5, 1]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.5, 1, 0.3]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Light Feet");
                list.add("§7Reduces fall damage by");
                list.add("§a{0}%");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Eggstra");
                list.add("§7Killing chickens has a §a{1}%");
                list.add("§7chance to drop an egg");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Mighty Chickens");
                list.add("§7Chicken minions work §a{2}%");
                list.add("§7faster while on your island");
            }));
        })),

        DOLPHIN("FISHING", "48f53ffe-a3f0-3280-aac0-11cc0d6121f4", "cefe7d803a45aa2af1993df2544a28df849a762663719bfefc58bf389ab7f5", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("sea_creature_chance", 0, 0.05, true));
            list.add(Stats.build("intelligence", 0, 1));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Triple.of(Property.build("[0]"), Property.build("[0.03]"), Property.build("[15]")));
            map.put("UNCOMMON", Triple.of(Property.build("[0]"), Property.build("[0.04]"), Property.build("[20]")));
            map.put("RARE", Triple.of(Property.build("[0, 0.1]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.04, 0.07]"), Property.build("[20]")));
            map.put("EPIC", Triple.of(Property.build("[0.1, 0.1]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.1, 0.1]"), Property.build("[25]")));
            map.put("LEGENDARY", Triple.of(Property.build("[0.1, 0.1]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.05, 0.1]"), Property.build("[25]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Pod Tactics");
                list.add("§7Increases your fishing speed");
                list.add("§7by §a{0}% §7for each player within");
                list.add("§710 blocks up to §a{A}%");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Echolocation");
                list.add("§7Increases sea creatures catch");
                list.add("§7chance by §a{1}%");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Splash Surprise");
                list.add("§7Stun sea creatures for §a5s");
                list.add("§7after fishing them up");
            }));
        })),

        ELEPHANT("FARMING", "9a58e25a-cf47-447d-b13c-3ea36eccfa31", "7071a76f669db5ed6d32b48bb2dba55d5317d7f45225cb3267ec435cfa514", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("intelligence", 0, 0.75));
            list.add(Stats.build("health", 0, 1));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.1]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.15]")));
            map.put("RARE", Pair.of(null, Property.build("[0.15, 0.01]")));
            map.put("EPIC", Pair.of(null, Property.build("[0.2, 0.01]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.2, 0.01, 1.8]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Stomp");
                list.add("§7Gain §a{0}❈ Defense §7for");
                list.add("§7every 100 §f✦ Speed");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Walking Fortress");
                list.add("§7Gain §c{1}❤ Health §7for every");
                list.add("§710 §a❈ Defense");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Trunk Efficiency");
                list.add("§7Grants §a+{2} §6☘ Farming");
                list.add("§6Fortune, §7which increases your");
                list.add("§7chance for multiple drops.");
            }));
        })),

        //TODO Check stats, because i don't have it D:
        ENDER_DRAGON("COMBAT", "3f9632a1-0ce2-311a-97e7-b144dfcb74f3", "aec3ff563290b13ff3bcc36898af7eaa988b6cc18dc254147f58374afe9b21b9", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("crit_damage", 0, 0.5));
            list.add(Stats.build("crit_chance", 0, 0.1));
            list.add(Stats.build("strength", 0, 0.5));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", Pair.of(null, Property.build("[0.2, 0.5, 0.3]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.2, 0.5, 0.3, 0.1]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6End Strike");
                list.add("§7Deal §a{0}% §7more damage to");
                list.add("§7end mobs");
                list.add("");
                list.add("§6One With The Dragon");
                list.add("§7Buffs the Aspect of the");
                list.add("§7Dragons sword by §a{1} §c❁ Damage");
                list.add("§7and §a{2} §c❁ Strength");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Superior");
                list.add("§7Increases most stats by §a{3}%");
            }));
        })),

        ENDERMAN("COMBAT", "fb3c5e13-61e9-4584-99db-9f9ef9fb834d", "6eab75eaa5c9f2c43a0d23cfdce35f4df632e9815001850377385f7b2f039ce1", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("crit_damage", 0, 0.75, true));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.1]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.2]")));
            map.put("RARE", Pair.of(null, Property.build("[0.2, 0.4]")));
            map.put("EPIC", Pair.of(null, Property.build("[0.3, 0.5]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.3, 0.5, 0.25]")));
            map.put("MYTHIC", Pair.of(null, Property.build("[0.3, 0.5, 0.25, 0.4]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Enderian");
                list.add("§7Take §a{0}% §7less damage");
                list.add("§7from end monsters");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Teleport Savvy");
                list.add("§7Buffs the Aspect of the End");
                list.add("§7ability granting §a{1} §7weapon");
                list.add("§7damage for 5s on use");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Zealot Madness");
                list.add("§7Increases your odds to find a");
                list.add("§7special Zealot by §a{2}%");
            }));
            map.put("MYTHIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Enderman Slayer");
                list.add("§7Gain +§a{3}% §7more combat xp");
                list.add("§7from endermen");
            }));
        })),

        ENDERMITE("MINING", "3302cdfe-6879-4659-ab0b-587b2cdb98e6", "5a1a0831aa03afb4212adcbb24e5dfaa7f476a1173fce259ef75a85855", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("intelligence", 0, 1));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.4]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.4]")));
            map.put("RARE", Pair.of(Property.build("[0.5, 5]"), Property.build("[0.495, 0.05]")));
            map.put("EPIC", Pair.of(Property.build("[0.5, 5]"), Property.build("[0.495, 0.05]")));
            map.put("LEGENDARY", Pair.of(Property.build("[0.5, 5, 10.4]"), Property.build("[0.495, 0.05, 0.396]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6More Stonks");
                list.add("§7Gain more exp orbs for");
                list.add("§7breaking end stone and gain a");
                list.add("§7+§a{0}% §7chance to get an extra");
                list.add("§7block dropped");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Pearl Muncher");
                list.add("§7Upon picking up an ender");
                list.add("§7pearl, consume it and gain §a{1}");
                list.add("§6coins");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Pearl Powered");
                list.add("§7Upon consuming an ender pearl,");
                list.add("§7gain +§a{2} §7speed for 10");
                list.add("§7seconds");
            }));
        })),

        FLYING_FISH("FISHING", "fd4a969b-c84c-4b59-979d-55eca6ec5f0e", "40cd71fbbbbb66c7baf7881f415c64fa84f6504958a57ccdb8589252647ea", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("strength", 0, 0.5));
            list.add(Stats.build("defense", 0, 0.5));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("RARE", Pair.of(null, Property.build("[0.3, 0.4]")));
            map.put("EPIC", Pair.of(null, Property.build("[0.4, 0.5]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.4, 0.5, 0.3]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Quick Reel");
                list.add("§7Increases fishing speed by");
                list.add("§a{0}%");
                list.add("");
                list.add("§6Water Bender");
                list.add("§7Gives §a{1} §c❁ Strength §7and");
                list.add("§a❈ Defense §7when near water");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Deep Sea Diver");
                list.add("§7Increases the stats of Diver");
                list.add("§7Armor by §a{2}%");
            }));
        })),

        GHOUL("COMBAT", "3fbb2c84-3693-4dcd-bc49-3b54ca6fa8cc", "87934565bf522f6f4726cdfe127137be11d37c310db34d8c70253392b5ff5b", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("ferocity", 0, 0.05));
            list.add(Stats.build("health", 0, 1));
            list.add(Stats.build("intelligence", 0, 0.75));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", Pair.of(null, Property.build("[0.25, 0.5]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.25, 0.5, 1]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Amplified Healing");
                list.add("§7Increases all healing by §a{0}%");
                list.add("");
                list.add("§6Zombie Arm");
                list.add("§7Increases the health and range");
                list.add("§7of the Zombie sword by §a{1}%");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Reaper Soul");
                list.add("§7Increases the health and");
                list.add("§7lifespan of the Reaper Scythe");
                list.add("§7zombies by §a{2}%");
            }));
        })),

        //TODO Stats
        GIRAFFE("FORAGING", "11216f12-2843-31c8-bf8a-b8535e6c6dce", "176b4e390f2ecdb8a78dc611789ca0af1e7e09229319c3a7aa8209b63b9", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("health", 0, 1));
            list.add(Stats.build("crit_chance", 0, 0.05, true));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(Property.build("[0.1]"), Property.build("[0.049]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.1]")));
            map.put("RARE", Pair.of(Property.build("[0.1, 0, 20.1]"), Property.build("[0.149, 0.4, 0.099]")));
            map.put("EPIC", Pair.of(Property.build("[0, 0, 20.2]"), Property.build("[0.2, 0.5, 0.248]")));
            map.put("LEGENDARY", Pair.of(Property.build("[0, 0, 20.4, 0]"), Property.build("[0.25, 0.5, 0.396, 0.25]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Good Heart");
                list.add("§7Regen §c{0} ❤ §7per second");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Higher Ground");
                list.add("§7Grants §c+{1}❁ Strength §7and");
                list.add("§9+{2}☠ Crit Damage §7when mid");
                list.add("§7air or jumping");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Long Neck");
                list.add("§7See enemies from afar and gain");
                list.add("§a{3}% §7dodge chance");
            }));
        })),

        //TODO Stats
        GOLEM("COMBAT", "623fa763-a8d1-36c6-8dcf-09f100723d04", "89091d79ea0f59ef7ef94d7bba6e5f17f2f7d4572c44f90f76c4819a714", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("strength", 0, 0.5));
            list.add(Stats.build("health", 0, 1.5));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", Pair.of(null, Property.build("[0.3, 0.25]")));
            map.put("LEGENDARY", Pair.of(Property.build("[0, 0, 203]"), Property.build("[0.3, 0.25, 2.97]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Last Stand");
                list.add("§7While less than 15% HP, deal");
                list.add("§a{0}% §7more damage");
                list.add("");
                list.add("§6Ricochet");
                list.add("§7Your iron plating causes");
                list.add("§a{1}% §7of attacks to ricochet");
                list.add("§7and hit the attacker");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Toss");
                list.add("§7Every 5 hits, throw the enemy");
                list.add("§7up into the air and deal §a{2}%");
                list.add("§7damage (10s cooldown)");
            }));
        })),

        GRANDMA_WOLF("COMBAT", "7f1b261f-0595-4160-9b6a-396436f9cc5d", "4e794274c1bb197ad306540286a7aa952974f5661bccf2b725424f6ed79c7884", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("strength", 0, 0.25));
            list.add(Stats.build("health", 0, 1));
        }), null, make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Kill Combo");
                list.add("§7Gain buffs for combo kills.");
                list.add("§7Effects stack as you increase");
                list.add("§7your combo.");
                list.add("");
                list.add("§a5 Combo §8(lasts §a9s§8)");
                list.add("  §8+§b1% §b✯ Magic Find");
                list.add("§a10 Combo §8(lasts §a7s§8)");
                list.add("  §8+§62 §7coins per kill");
                list.add("§a15 Combo §8(lasts §a5s§8)");
                list.add("  §8+§b1% §b✯ Magic Find");
                list.add("§a20 Combo §8(lasts §a4s§8)");
                list.add("  §8+§35% §7Combat Exp");
                list.add("§a25 Combo §8(lasts §a3.5s§8)");
                list.add("  §8+§b1% §b✯ Magic Find");
                list.add("§a30 Combo §8(lasts §a2.5s§8)");
                list.add("  §8+§62 §7coins per kill");
                list.add("");
                list.add("§8This pet's perks are active");
                list.add("§8even when the pet is not");
                list.add("§8summoned!");
            }));
            map.put("UNCOMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Kill Combo");
                list.add("§7Gain buffs for combo kills.");
                list.add("§7Effects stack as you increase");
                list.add("§7your combo.");
                list.add("");
                list.add("§a5 Combo §8(lasts §a9s§8)");
                list.add("  §8+§b1% §b✯ Magic Find");
                list.add("§a10 Combo §8(lasts §a7s§8)");
                list.add("  §8+§64 §7coins per kill");
                list.add("§a15 Combo §8(lasts §a5s§8)");
                list.add("  §8+§b2% §b✯ Magic Find");
                list.add("§a20 Combo §8(lasts §a4s§8)");
                list.add("  §8+§37% §7Combat Exp");
                list.add("§a25 Combo §8(lasts §a3.5s§8)");
                list.add("  §8+§b1% §b✯ Magic Find");
                list.add("§a30 Combo §8(lasts §a2.5s§8)");
                list.add("  §8+§64 §7coins per kill");
                list.add("");
                list.add("§8This pet's perks are active");
                list.add("§8even when the pet is not");
                list.add("§8summoned!");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Kill Combo");
                list.add("§7Gain buffs for combo kills.");
                list.add("§7Effects stack as you increase");
                list.add("§7your combo.");
                list.add("");
                list.add("§a5 Combo §8(lasts §a9s§8)");
                list.add("  §8+§b2% §b✯ Magic Find");
                list.add("§a10 Combo §8(lasts §a7s§8)");
                list.add("  §8+§66 §7coins per kill");
                list.add("§a15 Combo §8(lasts §a5s§8)");
                list.add("  §8+§b2% §b✯ Magic Find");
                list.add("§a20 Combo §8(lasts §a4s§8)");
                list.add("  §8+§39% §7Combat Exp");
                list.add("§a25 Combo §8(lasts §a3.5s§8)");
                list.add("  §8+§b2% §b✯ Magic Find");
                list.add("§a30 Combo §8(lasts §a2.5s§8)");
                list.add("  §8+§66 §7coins per kill");
                list.add("");
                list.add("§8This pet's perks are active");
                list.add("§8even when the pet is not");
                list.add("§8summoned!");
            }));
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Kill Combo");
                list.add("§7Gain buffs for combo kills.");
                list.add("§7Effects stack as you increase");
                list.add("§7your combo.");
                list.add("");
                list.add("§a5 Combo §8(lasts §a9s§8)");
                list.add("  §8+§b2% §b✯ Magic Find");
                list.add("§a10 Combo §8(lasts §a7s§8)");
                list.add("  §8+§68 §7coins per kill");
                list.add("§a15 Combo §8(lasts §a5s§8)");
                list.add("  §8+§b3% §b✯ Magic Find");
                list.add("§a20 Combo §8(lasts §a4s§8)");
                list.add("  §8+§312% §7Combat Exp");
                list.add("§a25 Combo §8(lasts §a3.5s§8)");
                list.add("  §8+§b2% §b✯ Magic Find");
                list.add("§a30 Combo §8(lasts §a2.5s§8)");
                list.add("  §8+§68 §7coins per kill");
                list.add("");
                list.add("§8This pet's perks are active");
                list.add("§8even when the pet is not");
                list.add("§8summoned!");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Kill Combo");
                list.add("§7Gain buffs for combo kills.");
                list.add("§7Effects stack as you increase");
                list.add("§7your combo.");
                list.add("");
                list.add("§a5 Combo §8(lasts §a9s§8)");
                list.add("  §8+§b3% §b✯ Magic Find");
                list.add("§a10 Combo §8(lasts §a7s§8)");
                list.add("  §8+§610 §7coins per kill");
                list.add("§a15 Combo §8(lasts §a5s§8)");
                list.add("  §8+§b3% §b✯ Magic Find");
                list.add("§a20 Combo §8(lasts §a4s§8)");
                list.add("  §8+§315% §7Combat Exp");
                list.add("§a25 Combo §8(lasts §a3.5s§8)");
                list.add("  §8+§b3% §b✯ Magic Find");
                list.add("§a30 Combo §8(lasts §a2.5s§8)");
                list.add("  §8+§610 §7coins per kill");
                list.add("");
                list.add("§8This pet's perks are active");
                list.add("§8even when the pet is not");
                list.add("§8summoned!");
            }));
        }), "REPLACE"),

        GRIFFIN("COMBAT", "11e506b9-cb3d-43e6-89d2-9e1575944498", "4c27e3cb52a64968e60c861ef1ab84e0a0cb5f07be103ac78da67761731f00c8", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("magic_find", 0, 0.1));
            list.add(Stats.build("intelligence", 0, 0.1));
            list.add(Stats.build("crit_damage", 0, 0.5, true));
            list.add(Stats.build("strength", 0, 0.25));
            list.add(Stats.build("crit_chance", 0, 0.1, true));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("UNCOMMON", Triple.of(null, null, Property.build("[5, 7]")));
            map.put("RARE", Triple.of(null, null, Property.build("[6, 7]")));
            map.put("EPIC", Triple.of(Property.build("[1]"), Property.build("[0.15]"), Property.build("[6, 8]")));
            map.put("LEGENDARY", Triple.of(Property.build("[1, 1]"), Property.build("[0.19, 0.14]"), Property.build("[7, 8]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Odyssey");
                list.add("§2Mythological creatures §7you");
                list.add("§7find and burrows you dig scale");
                list.add("§7in §cdifficulty §7and §6rewards");
                list.add("§7based on your equipped");
                list.add("§7Griffin's rarity.");
            }));
            map.put("UNCOMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Legendary Constitution");
                list.add("§7Permanent §cRegeneration {A}");
                list.add("§7and §4Strength {B}§7.");
            }));
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Perpetual Empathy");
                list.add("§7Heal nearby players for");
                list.add("§a{0}% §7of the final damage");
                list.add("§7you receive.");
                list.add("§8Excludes other griffins.");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6King of Kings");
                list.add("§7Gain §c+{1}% §c❁ Strength");
                list.add("§7when above §c85% §7health.");
            }));
        })),

        GUARDIAN("COMBAT", "26508276-c01a-32a9-9201-7dae1724954e", "221025434045bda7025b3e514b316a4b770c6faa4ba9adb4be3809526db77f9d", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("intelligence", 0, 1));
            list.add(Stats.build("defense", 0, 0.5));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(Property.build("[0.2]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.02]")));
            map.put("UNCOMMON", Pair.of(Property.build("[0.2]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.06]")));
            map.put("RARE", Pair.of(Property.build("[0.2, 0.2]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.1, 0.25]")));
            map.put("EPIC", Pair.of(Property.build("[0.2, 0.3]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.2, 0.3]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.2, 0.3, 0.3]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Lazerbeam");
                list.add("§7Zaps your enemies for §b{0}x");
                list.add("§7your §b✎ Intelligence §7every");
                list.add("§a3s");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Enchanting Exp Boost");
                list.add("§7Boosts your Enchanting exp");
                list.add("§7by §a{1}%");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Mana Pool");
                list.add("§7Regenerate §b{2}% §7extra mana,");
                list.add("§7doubled when near or in water");
            }));
        })),

        HORSE("COMBAT", "6d310633-c175-4b47-92ab-778287bb7a5e", "36fcd3ec3bc84bafb4123ea479471f9d2f42d8fb9c5f11cf5f4e0d93226", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("intelligence", 0, 0.5));
            list.add(Stats.build("speed", 0, 0.25));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("RARE", Pair.of(null, Property.build("[1.1]")));
            map.put("EPIC", Pair.of(null, Property.build("[1.2]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[1.2, 0.25]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Ridable");
                list.add("§7Right-click your summoned pet");
                list.add("§7to ride it!");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Run");
                list.add("§7Increases the speed of your");
                list.add("§7mount by §a{0}%");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Ride Into Battle");
                list.add("§7While riding your horse, gain");
                list.add("§7+§a{1}% §7 bow damage");
            }));
        })),

        HOUND("COMBAT", "802a167c-cbcd-3a1f-becd-5b1a25a4cf15", "b7c8bef6beb77e29af8627ecdc38d86aa2fea7ccd163dc73c00f9f258f9a1457", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("ferocity", 0, 0.05));
            list.add(Stats.build("strength", 0, 0.4));
            list.add(Stats.build("attack_speed", 0, 0.15, true));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", Pair.of(Property.build("[0.1, 0.1]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.05, 0.1]")));
            map.put("LEGENDARY", Pair.of(Property.build("[0.1, 0.1, 0.1]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.05, 0.1, 0.1]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Scavenger");
                list.add("§7Gain +§a{0} §7coins per");
                list.add("§7monster kill");
                list.add("");
                list.add("§6Finder");
                list.add("§7Increases the chance for");
                list.add("§7monsters to drop their armor by");
                list.add("§a{1}%");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Fury Claws");
                list.add("§7Grants §a{2}% §e⚔ Bonus Attack");
                list.add("§eSpeed");
            }));
        })),

        JELLYFISH("ALCHEMY", "a7be2bb4-70a1-32e4-a981-8f26c5864371", "913f086ccb56323f238ba3489ff2a1a34c0fdceeafc483acff0e5488cfd6c2f1", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("health", 0, 2));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", Pair.of(null, Property.build("[1]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[1, 0.5]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Radiant Regeneration");
                list.add("§7While in dungeons, increases");
                list.add("§7your base health regen by §a{0}%");
                list.add("§7and heals players within 8");
                list.add("§7blocks by up to 10hp/s");
                list.add("");
                list.add("§6Hungry Healer");
                list.add("§7While in dungeons, for every");
                list.add("§71000 you heal teammates apply");
                list.add("§7the §aenchanted golden apple");
                list.add("§7effect to all players within");
                list.add("§710 blocks (10s cooldown)");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Powerful Potions");
                list.add("§7While in dungeons, increase");
                list.add("§7the effectiveness of Instant");
                list.add("§7Health and Mana splash potions");
                list.add("§7by §a{1}%");
            }));
        })),

        JERRY("COMBAT", "0a9e8efb-9191-4c81-80f5-e27ca5433156", "822d8e751c8f2fd4c8942c44bdb2f5ca4d8ae8e575ed3eb34c18a86e93b", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("intelligence", 0, -1));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.1]")));
            map.put("MYTHIC", Pair.of(null, Property.build("[0.5]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Jerry");
                list.add("§7Gain §a50% §7chance to deal");
                list.add("§7your regular damage");
                list.add("");
                list.add("§6Jerry");
                list.add("§7Gain §a100% §7chance to");
                list.add("§7receive a normal amount of drops");
                list.add("§7from mobs");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Jerry");
                list.add("§7Actually adds §c5 damage §7to");
                list.add("§7the Aspect of the Jerry");
            }));
            map.put("MYTHIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Jerry");
                list.add("§7Tiny chance to find Jerry");
                list.add("§7Candies when killing mobs");
            }));
        })),

        LION("FORAGING", "7e3ed445-3545-3c76-993b-8f292ea576c6", "38ff473bd52b4db2c06f1ac87fe1367bce7574fac330ffac7956229f82efba1", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("ferocity", 0, 0.05));
            list.add(Stats.build("strength", 0, 0.5));
            list.add(Stats.build("speed", 0, 0.25));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.03]")));
            map.put("UNCOMMON", Pair.of(Property.build("[0.1]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.05]")));
            map.put("RARE", Pair.of(Property.build("[0.1, 0.8]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.1, 0.75]")));
            map.put("EPIC", Pair.of(Property.build("[0.1, 1]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.15, 1]")));
            map.put("LEGENDARY", Pair.of(Property.build("[0.2, 1, 0.1]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.2, 1, 0.15]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Primal Force");
                list.add("§7Adds §c+{0} §c❁ Damage §7and");
                list.add("§c+{0} §c❁ Strength §7to your");
                list.add("§7weapons.");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6First Pounce");
                list.add("§7First Strike,");
                list.add("§7Triple-Strike, and §d§lCombo");
                list.add("§7are §a{1}% §7more effective.");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6King of the Jungle");
                list.add("§7Deal §c+{2}% §c❁ Damage");
                list.add("§7against mobs that have");
                list.add("§7attacked you.");
            }));
        })),

        MAGMA_CUBE("COMBAT", "35f02923-7bec-3869-9ef5-b42a4794cac8", "38957d5023c937c4c41aa2412d43410bda23cf79a9f6ab36b76fef2d7c429", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("health", 0, 0.5));
            list.add(Stats.build("defense", 0, 0.33));
            list.add(Stats.build("strength", 0, 0.2));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.2]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.2]")));
            map.put("RARE", Pair.of(null, Property.build("[0.25, 0.2]")));
            map.put("EPIC", Pair.of(null, Property.build("[0.3, 0.25]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.3, 0.25, 1]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Slimy Minions");
                list.add("§7Slime minions work §a{0}%");
                list.add("§7faster while on your island");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Salt Blade");
                list.add("§7Deal §a{1}% §7more damage to");
                list.add("§7slimes");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Hot Ember");
                list.add("§7Buffs the stats of Ember Armor");
                list.add("§7by §a{2}%");
            }));
        })),

        MEGALODON("FISHING", "82fc79b9-fded-3c05-b8dc-00e562803862", "a94ae433b301c7fb7c68cba625b0bd36b0b14190f20e34a7c8ee0d9de06d53b9", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("magic_find", 0, 0.1));
            list.add(Stats.build("ferocity", 0, 0.05));
            list.add(Stats.build("strength", 0, 0.5));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", Pair.of(null, Property.build("[0.25, 0.2]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.25, 0.2, 0.5]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Blood Scent");
                list.add("§7Deal up to §c+{0}% §c❁ Damage");
                list.add("§7based on the enemy's missing");
                list.add("§7health");
                list.add("");
                list.add("§6Enhanced Scales");
                list.add("§7Increases the stats of Shark");
                list.add("§7Armor by §a{1}%");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Feeding Frenzy");
                list.add("§7On kill gain §c{2}❁ Damage");
                list.add("§7and §f✦ Speed §7for 5 seconds");
            }));
        })),

        MITHRIL_GOLEM("MINING", "39fb84b5-72d3-3221-b373-aa315e956e83", "c1b2dfe8ed5dffc5b1687bc1c249c39de2d8a6c3d90305c95f6d1a1a330a0b1", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("true_defense", 0, 0.5));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.5]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.8]")));
            map.put("RARE", Pair.of(null, Property.build("[0.8, 0.1]")));
            map.put("EPIC", Pair.of(null, Property.build("[1, 0.2]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[1, 0.2, 0.2]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Mithril Affinity");
                list.add("§7Gain §a+{0} §6⸕ Mining Speed");
                list.add("§7when mining §eMithril");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6The Smell Of Powder");
                list.add("§7Gain §a+{1}% §7chance to gain");
                list.add("§7extra §2Mithril Powder §7while");
                list.add("§7mining.");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Danger Averse");
                list.add("§7Increases your combat stats by");
                list.add("§a+{2}% §7on mining islands");
            }));
        })),

        MONKEY("FORAGING", "e410c089-bb3a-40a3-add6-188d6187ac87", "13cf8db84807c471d7c6922302261ac1b5a179f96d1191156ecf3e1b1d3ca", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("intelligence", 0, 0.5));
            list.add(Stats.build("speed", 0, 0.2));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.4]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.5]")));
            map.put("RARE", Pair.of(null, Property.build("[0.5, 0.8]")));
            map.put("EPIC", Pair.of(null, Property.build("[0.6, 1]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.6, 1, 0.5]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Treeborn");
                list.add("§7Grants §a+{0} §6☘ Foraging");
                list.add("§6Fortune§7, which increases your");
                list.add("§7chance at double logs.");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Vine Swing");
                list.add("§7Gain +§a{1} §f✦ Speed §7while");
                list.add("§7in The Park");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Evolved Axes");
                list.add("§7Reduce the cooldown of Jungle");
                list.add("§7Axe and Treecapitator by §a{2}%");
            }));
        })),

        OCELOT("FORAGING", "664dd492-3fcd-443b-9e61-4c7ebd9e4e10", "5657cd5c2989ff97570fec4ddcdc6926a68a3393250c1be1f0b114a1db1", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("ferocity", 0, 0.1));
            list.add(Stats.build("speed", 0, 0.5));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.2]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.25]")));
            map.put("RARE", Pair.of(null, Property.build("[0.25, 0.25]")));
            map.put("EPIC", Pair.of(null, Property.build("[0.3, 0.3]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.3, 0.3, 0.3]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Foraging Exp Boost");
                list.add("§7Boosts your Foraging exp by");
                list.add("§a{0}%");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Tree Hugger");
                list.add("§7Foraging minions work §a{1}%");
                list.add("§7faster while on your island");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Tree Essence");
                list.add("§7Gain a §a{2}% §7chance to get");
                list.add("§7exp from breaking a log");
            }));
        })),

        PARROT("ALCHEMY", "db4d678a-731a-49cc-8dae-2cee4a5b80c9", "5df4b3401a4d06ad66ac8b5c4d189618ae617f9c143071c8ac39a563cf4e4208", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("intelligence", 0, 1));
            list.add(Stats.build("crit_damage", 0, 0.1, true));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", Pair.of(Property.build("[1, 5]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.15, 0.35]")));
            map.put("LEGENDARY", Pair.of(Property.build("[1, 5, 5, 0.2]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.2, 0.4, 0.3, 0.2]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Flamboyant");
                list.add("§7Adds §a{0} §7levels to");
                list.add("§7intimidation accessories");
                list.add("");
                list.add("§6Repeat");
                list.add("§7Boosts potions duration by");
                list.add("§a{1}%");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Bird Discourse");
                list.add("§7Gives §c+{2}❁ Strength §7to");
                list.add("§7players within §a20 §7blocks");
                list.add("§8Doesn't stack");
                list.add("");
                list.add("§6Parrot Feather Infusion");
                list.add("§7When summoned or in your pets");
                list.add("§7menu, boost the duration of");
                list.add("§7consumed §cGod Potions §7by");
                list.add("§a{3}%");
            }));
        })),

        //TODO Check stats, because i don't have it D:
        PHOENIX("COMBAT", "4173bc61-9e2f-3c84-8d31-4517e64062ab", "23aaf7b1a778949696cb99d4f04ad1aa518ceee256c72e5ed65bfa5c2d88d9e", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("intelligence", 51, 0.99));
            list.add(Stats.build("strength", 10, 0.4));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", Pair.of(Property.build("[10.1, 2, 1.1, 2]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.099, 0.02, 0.119, 0.02]")));
            map.put("LEGENDARY", Pair.of(Property.build("[15.2, 2, 0, 2]"), Property.build(DisplayMode.DISPLAY_AT_LEVEL_1, "[0.148, 0.02, 0.139, 0.03]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Rekindle");
                list.add("§7Before death, become §eimmune");
                list.add("§7and gain §c{0} §c❁ Strength");
                list.add("§7for §a{1} §7seconds");
                list.add("§83 minutes cooldown");
                list.add("");
                list.add("§6Fourth Flare");
                list.add("§7On 4th melee strike, §6ignite");
                list.add("§7mobs, dealing §c{2}x §7your");
                list.add("§9☠ Crit Damage §7each second");
                list.add("§7for §a{3} §7seconds");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Magic Bird");
                list.add("§7You may always fly on your");
                list.add("§7private island");
                list.add("");
                list.add("§6Eternal Coins");
                list.add("§7Don't lose coins from death.");
            }));
        })),

        PIG("FARMING", "e1e1c2e4-1ed2-473d-bde2-3ec718535399", "621668ef7cb79dd9c22ce3d1f3f4cb6e2559893b6df4a469514e667c16aa4", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("speed", 0, 0.25));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.3]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.4]")));
            map.put("RARE", Pair.of(null, Property.build("[0.4, 0.4]")));
            map.put("EPIC", Pair.of(null, Property.build("[0.5, 0.5]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.5, 0.5]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Ridable");
                list.add("§7Right-click your summoned pet");
                list.add("§7to ride it!");
                list.add("");
                list.add("§6Run");
                list.add("§7Increases the speed of your");
                list.add("§7mount by §a{0}%");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Sprint");
                list.add("§7While holding an Enchanted");
                list.add("§7Carrot on a Stick, increase the");
                list.add("§7speed of your mount by §a{1}%");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Trample");
                list.add("§7While on your private island,");
                list.add("§7break all crops your pig rides");
                list.add("§7over");
            }));
        })),

        PIGMAN("COMBAT", "e3410337-d22b-4427-beab-d9ceae561d2c", "63d9cb6513f2072e5d4e426d70a5557bc398554c880d4e7b7ec8ef4945eb02f2", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("ferocity", 0, 0.05));
            list.add(Stats.build("strength", 0, 0.5));
            list.add(Stats.build("defense", 0, 0.5));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", Pair.of(null, Property.build("[0.3, 0.4, 0.25]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.3, 0.4, 0.25, 0.25]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Bacon Farmer");
                list.add("§7Pig minions work §a{0}%");
                list.add("§7faster while on your island");
                list.add("");
                list.add("§6Pork Master");
                list.add("§7Buffs the Pigman sword by §a{1}");
                list.add("§c❁ Damage §7and §a{2} §c❁");
                list.add("§cStrength");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Giant Slayer");
                list.add("§7Deal §a{3}% §7extra damage to");
                list.add("§7monsters level 100 and up");
            }));
        })),

        RABBIT("FARMING", "389b150b-1aed-4bd8-af89-129043e007d1", "117bffc1972acd7f3b4a8f43b5b6c7534695b8fd62677e0306b2831574b", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("speed", 0, 0.2));
            list.add(Stats.build("health", 0, 1));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.3]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.4]")));
            map.put("RARE", Pair.of(null, Property.build("[0.4, 0.25]")));
            map.put("EPIC", Pair.of(null, Property.build("[0.5, 0.3]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.5, 0.3, 0.3]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Happy Feet");
                list.add("§7Jump potions also give §a+{0}");
                list.add("§7speed");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Farming Exp Boost");
                list.add("§7Boosts your Farming exp by");
                list.add("§a{1}%");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Efficient Farming");
                list.add("§7Farming minions work §a{2}%");
                list.add("§7faster while on your island");
            }));
        })),

        RAT("COMBAT", "5e1345bb-612a-4d93-9619-25cc24d04c6b", "a8abb471db0ab78703011979dc8b40798a941f3a4dec3ec61cbeec2af8cffe8", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("crit_damage", 0, 0.1, true));
            list.add(Stats.build("health", 0, 1));
            list.add(Stats.build("strength", 0, 0.5));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("LEGENDARY", Pair.of(Property.build("[2, 20]"), Property.build("[0.05, 0.4]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Morph");
                list.add("§7Right-click your summoned pet");
                list.add("§7to morph into it!");
                list.add("");
                list.add("§6CHEESE!");
                list.add("§7As a Rat, you smell");
                list.add("§e§lCHEESE§7 nearby! Yummy!");
                list.add("");
                list.add("§6Rat's Blessing");
                list.add("§7Has a chance to grant a random");
                list.add("§7player §b+{0}✯ Magic Find§7 for");
                list.add("§a{1}§7 seconds after finding a");
                list.add("§7yummy piece of Cheese! If the");
                list.add("§7player gets a drop during this");
                list.add("§7buff, you have a §a20%§7 to get");
                list.add("§7it too.");
            }));
        })),

        ROCK("MINING", "1887aa6a-240a-4927-b868-7d3631f03577", "cb2b5d48e57577563aca31735519cb622219bc058b1f34648b67b8e71bc0fa", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("true_defense", 0, 0.1));
            list.add(Stats.build("defense", 0, 2));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("RARE", Pair.of(null, Property.build("[0.2]")));
            map.put("EPIC", Pair.of(null, Property.build("[0.25]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.25, 0.3]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Ridable");
                list.add("§7Right-click your summoned pet");
                list.add("§7to ride it!");
                list.add("");
                list.add("§6Sailing Stone");
                list.add("§7Sneak to move your rock to");
                list.add("§7your location (15s cooldown)");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Fortify");
                list.add("§7While sitting on your rock,");
                list.add("§7gain +§a{0}%§7 defense");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Steady Ground");
                list.add("§7While sitting on your rock,");
                list.add("§7gain +§a{1}%§7 damage");
            }));
        })),

        SHEEP("ALCHEMY", "37bacd66-7fe6-39e3-81cf-82911daf648b", "64e22a46047d272e89a1cfa13e9734b7e12827e235c2012c1a95962874da0", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("intelligence", 0, 2));
            list.add(Stats.build("ability_damage", 0, 0.2, true));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.1]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.1]")));
            map.put("RARE", Pair.of(null, Property.build("[0.1, 0.1]")));
            map.put("EPIC", Pair.of(null, Property.build("[0.2, 0.1]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.2, 0.1, 0.25]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Mana Saver");
                list.add("§7Reduces the mana cost of");
                list.add("§7abilities by §a{0}%");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Overheal");
                list.add("§7Gives a §a{1}% §7shield after");
                list.add("§7not taking damage for 10s");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Dungeon Wizard");
                list.add("§7Increases your total mana by");
                list.add("§a{2}% §7while in dungeons");
            }));
        })),

        SILVERFISH("MINING", "79e570d8-f66e-375c-9e70-97224ccd5692", "da91dab8391af5fda54acd2c0b18fbd819b865e1a8f1d623813fa761e924540", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("health", 0, 0.2));
            list.add(Stats.build("defense", 0, 1));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(Property.build("[0.1]"), Property.build("[0.049]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.1]")));
            map.put("RARE", Pair.of(null, Property.build("[0.1, 0.25]")));
            map.put("EPIC", Pair.of(Property.build("[0.1, 0]"), Property.build("[0.149, 0.3]")));
            map.put("LEGENDARY", Pair.of(Property.build("[0.1, 0]"), Property.build("[0.149, 0.3]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6True Defense Boost");
                list.add("§7Boosts your §f❂ True");
                list.add("§fDefense§7 by §a{0}");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Mining Exp Boost");
                list.add("§7Boosts your Mining exp by");
                list.add("§a{1}%");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Dexterity");
                list.add("§7Gives permanent haste III");
            }));
        })),

        SKELETON("COMBAT", "baee4f79-051d-4b7e-9323-58494878ef5a", "fca445749251bdd898fb83f667844e38a1dff79a1529f79a42447a0599310ea4", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("crit_damage", 0, 0.3, true));
            list.add(Stats.build("crit_chance", 0, 0.15, true));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.1]")));
            map.put("UNCOMMON", Pair.of(Property.build("[0.1]"), Property.build("[0.149]")));
            map.put("RARE", Pair.of(Property.build("[0.1, 0.1]"), Property.build("[0.149, 0.149]")));
            map.put("EPIC", Pair.of(Property.build("[0, 0.2]"), Property.build("[0.2, 0.168]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.2, 0.2]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Bone Arrows");
                list.add("§7Increase arrow damage by");
                list.add("§a{0}%§7 which is tripled while");
                list.add("§7in dungeons");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Combo");
                list.add("§7Gain a combo stack for every");
                list.add("§7bow hit granting +§a3 §c❁");
                list.add("§cStrength§7. Max §a{1} §7stacks,");
                list.add("§7stacks disappear after 8");
                list.add("§7seconds.");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Skeletal Defense");
                list.add("§7Your skeleton shoots an arrow");
                list.add("§7dealing §a30x §7your §9☠ Crit");
                list.add("§9Damage §7when a mob gets close");
                list.add("§7to you (5s cooldown).");
            }));
        })),

        SKELETON_HORSE("COMBAT", "8dfd0bbb-7ce2-444e-ad9a-0eb9518eaffd", "47effce35132c86ff72bcae77dfbb1d22587e94df3cbc2570ed17cf8973a", make(Lists.newLinkedList(), list ->
        {
            list.add("§7Run around town on your scary,");
            list.add("§7yet trusty, steed!");
        }), make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("intelligence", 0, 1));
            list.add(Stats.build("speed", 0, 0.5));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("LEGENDARY", Pair.of(null, Property.build("[1.5, 0.4]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Ridable");
                list.add("§7Right-click your summoned pet");
                list.add("§7to ride it!");
                list.add("");
                list.add("§6Run");
                list.add("§7Increases the speed of your");
                list.add("§7mount by §a{0}%");
                list.add("");
                list.add("§6Ride Into Battle");
                list.add("§7While riding your horse, gain");
                list.add("§7+§a{1}%§7 bow damage");
            }));
        })),

        SNOWMAN("COMBAT", "b2b19dcd-dc67-31df-a790-e6cf07ae12ac", "11136616d8c4a87a54ce78a97b551610c2b2c8f6d410bc38b858f974b113b208", make(Lists.newLinkedList(), list ->
        {
            list.add("§7Fight alongside your pet");
            list.add("§7Snowman!");
        }), make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("damage", 0, 0.25));
            list.add(Stats.build("crit_damage", 0, 0.25, true));
            list.add(Stats.build("strength", 0, 0.25));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("LEGENDARY", Pair.of(Property.build("[4, 0.1]"), Property.build("[0.04, 0.149]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Blizzard");
                list.add("§7Slow all enemies within §a{0}");
                list.add("§7blocks");
                list.add("");
                list.add("§6Frostbite");
                list.add("§7Your freezing aura slows enemy");
                list.add("§7attacks causing you to take");
                list.add("§a{1}% §7reduced damage");
                list.add("");
                list.add("§6Snow Cannon");
                list.add("§7Your snowman fires a snowball");
                list.add("§7dealing §a5x§7 your §c❁ Strength");
                list.add("§7when a mob gets close to you");
                list.add("§7(1s cooldown)");
            }));
        })),

        SPIDER("COMBAT", "7c63f3cf-a963-311a-aeca-3a075b417806", "cd541541daaff50896cd258bdbdd4cf80c3ba816735726078bfe393927e57f1", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("crit_chance", 0, 0.1, true));
            list.add(Stats.build("strength", 0, 0.1));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.1]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.1]")));
            map.put("RARE", Pair.of(null, Property.build("[0.1, 0.4]")));
            map.put("EPIC", Pair.of(null, Property.build("[0.1, 0.4]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.1, 0.4, 0.3]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6One With The Spider");
                list.add("§7Gain §a{0} §c❁ Strength §7for");
                list.add("§7every nearby spider");
                list.add("§8Max 10 spiders");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Web-weaver");
                list.add("§7Upon hitting a monster it");
                list.add("§7becomes slowed by §a{1}%");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Spider Whisperer");
                list.add("§7Spider and tarantula minions");
                list.add("§7work §a{2}% §7faster while on");
                list.add("§7your island");
            }));
        })),

        //TODO Stats
        SPIRIT("COMBAT", "38699d98-8bfa-4492-acfb-7191c7c3c3bb", "8d9ccc670677d0cebaad4058d6aaf9acfab09abea5d86379a059902f2fe22655", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("intelligence", 0, 1));
            list.add(Stats.build("speed", 0, 0.3));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("ALL", Pair.of(Property.build("[5.5]"), Property.build("[0.445]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Spirit Assistance");
                list.add("§7Spawns and assists you when");
                list.add("§7you are ghost in Dungeons.");
                list.add("");
                list.add("§6Spirit Cooldowns");
                list.add("§7Reduces the cooldown of your");
                list.add("§7ghost abilities in dungeons by");
                list.add("§a{0}%§7.");
                list.add("");
                list.add("§8This pet's perks are active");
                list.add("§8even when the pet is not");
                list.add("§8summoned!");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Spirit Assistance");
                list.add("§7Spawns and assists you when");
                list.add("§7you are ghost in Dungeons.");
                list.add("");
                list.add("§6Spirit Cooldowns");
                list.add("§7Reduces the cooldown of your");
                list.add("§7ghost abilities in dungeons by");
                list.add("§a{0}%§7.");
                list.add("");
                list.add("§6Half Life");
                list.add("§7If you are the first player to");
                list.add("§7die in a dungeon, the score");
                list.add("§7penalty for that death is");
                list.add("§7reduced to §a1§7.");
                list.add("");
                list.add("§8This pet's perks are active");
                list.add("§8even when the pet is not");
                list.add("§8summoned!");
            }));
        }), "REPLACE"),

        SQUID("FISHING", "7b5da593-80d3-39f4-8220-2cef27c5b9d9", "01433be242366af126da434b8735df1eb5b3cb2cede39145974e9c483607bac", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("health", 0, 0.5));
            list.add(Stats.build("intelligence", 0, 0.5));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.5]")));
            map.put("UNCOMMON", Pair.of(Property.build("[0.8]"), Property.build("[0.742]")));
            map.put("RARE", Pair.of(Property.build("[0.8, 0, 0.1]"), Property.build("[0.742, 0.3, 0.149]")));
            map.put("EPIC", Pair.of(null, Property.build("[1, 0.4, 0.2]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[1, 0.4, 0.2, 0.3]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6More Ink");
                list.add("§7Gain a §a{0}% §7chance to get");
                list.add("§7double drops from squids");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Ink Specialty");
                list.add("§7Buffs the Ink Wand by §a{1} §c❁");
                list.add("§cDamage §7and §a{2} §c❁ Strength");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Fishing Exp Boost");
                list.add("§7Boosts your Fishing exp by");
                list.add("§a{3}%");
            }));
        })),

        TARANTULA("COMBAT", "3e5474d4-4365-3ea7-b4bc-b4edc54da341", "8300986ed0a04ea79904f6ae53f49ed3a0ff5b1df62bba622ecbd3777f156df8", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("crit_damage", 0, 0.3, true));
            list.add(Stats.build("crit_chance", 0, 0.1, true));
            list.add(Stats.build("strength", 0, 0.1));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", Pair.of(null, Property.build("[0.3, 0.5]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.3, 0.5, 0.4]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Webbed Cells");
                list.add("§7Anti-healing is §a{0}% §7less");
                list.add("§7effective against you");
                list.add("");
                list.add("§6Eight Legs");
                list.add("§7Decreases the mana cost of");
                list.add("§7Spider, Tarantula and Thorn's");
                list.add("§7boots by §a{1}%");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Arachnid Slayer");
                list.add("§7Gain +§a{2}% §7more combat xp");
                list.add("§7from spiders");
            }));
        })),

        TIGER("COMBAT", "33a69ead-44ac-3791-9425-52109aacdaa6", "fc42638744922b5fcf62cd9bf27eeab91b2e72d6c70e86cc5aa3883993e9d84", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("crit_damage", 0, 0.5, true));
            list.add(Stats.build("crit_chance", 0, 0.05, true));
            list.add(Stats.build("strength", 5, 0.1));
            list.add(Stats.build("ferocity", 0, 0.25));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.15]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.33]")));
            map.put("RARE", Pair.of(null, Property.build("[0.33, 0.3]")));
            map.put("EPIC", Pair.of(Property.build("[0, 0.6]"), Property.build("[0.5, 0.544]")));
            map.put("LEGENDARY", Pair.of(Property.build("[0, 0.6, 0]"), Property.build("[0.5, 0.544, 0.2]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Merciless Swipe");
                list.add("§7Gain §c+{0}% §c⫽ Ferocity.");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Hemorrhage");
                list.add("§7Melee attacks reduce healing");
                list.add("§7by §6{1}% §7for §a10s");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Apex Predator");
                list.add("§7Deal §c+{2}% §7damage against");
                list.add("§7targets with no other mobs");
                list.add("§7within §a15 §7blocks");
            }));
        })),

        //TODO Stats
        TURTLE("COMBAT", "f10d652b-906b-3065-adf5-9817983201ca", "212b58c841b394863dbcc54de1c2ad2648af8f03e648988c1f9cef0bc20ee23c", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("defense", 0, 1));
            list.add(Stats.build("health", 0, 0.5));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", Pair.of(Property.build("[3.2, 5.3, 0]"), Property.build("[0.168, 0.347, 0.2]")));
            map.put("LEGENDARY", Pair.of(Property.build("[3.2, 5.3, 0]"), Property.build("[0.168, 0.447, 0.25]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Turtle Tactics");
                list.add("§7Gain §a+{0}% §a❈ Defense");
                list.add("");
                list.add("§6Genius Amniote");
                list.add("§7Gain §a+{1}❈ Defense §7and");
                list.add("§7regen §c+{2}❤ §7per second when");
                list.add("§7near or in water");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Unflippable");
                list.add("§7Gain §aimmunity §7to knockback");
            }));
        })),

        WITHER_SKELETON("MINING", "d928ce5e-e75e-3cdc-aaf1-0c93d49b5c31", "f5ec964645a8efac76be2f160d7c9956362f32b6517390c59c3085034f050cff", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("crit_damage", 0, 0.25, true));
            list.add(Stats.build("strength", 0, 0.25));
            list.add(Stats.build("intelligence", 0, 0.25));
            list.add(Stats.build("crit_chance", 0, 0.05, true));
            list.add(Stats.build("defense", 0, 0.25));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", Pair.of(null, Property.build("[0.3, 0.5]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.3, 0.5, 2]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("EPIC", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Stronger Bones");
                list.add("§7Take §a{0}% §7less damage from");
                list.add("§7skeletons");
                list.add("");
                list.add("§6Wither Blood");
                list.add("§7Deal §a{1}% §7more damage to");
                list.add("§7wither mobs");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Death's Touch");
                list.add("§7Upon hitting an enemy inflict");
                list.add("§7the wither effect for §a{2}%");
                list.add("§7damage over 3 seconds");
                list.add("§8Does not stack");
            }));
        })),

        WOLF("COMBAT", "85b4606a-2fc7-4451-aa82-3b1afaeee9cd", "dc3dd984bb659849bd52994046964c22725f717e986b12d548fd169367d494", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("crit_damage", 0, 0.1, true));
            list.add(Stats.build("speed", 0, 0.2));
            list.add(Stats.build("health", 0, 0.5));
            list.add(Stats.build("true_defense", 0, 0.1));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.1]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.2]")));
            map.put("RARE", Pair.of(null, Property.build("[0.2, 0.1]")));
            map.put("EPIC", Pair.of(Property.build("[0, 0.1]"), Property.build("[0.3, 0.149]")));
            map.put("LEGENDARY", Pair.of(Property.build("[0, 0.1, 0]"), Property.build("[0.3, 0.149, 0.3]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Alpha Dog");
                list.add("§7Take §a{0}% §7less damage");
                list.add("§7from wolves");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Pack Leader");
                list.add("§7Gain §a{1} §9☠ Crit Damage");
                list.add("§7for every nearby wolf monsters");
                list.add("§8Max 10 wolves");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Combat Exp Boost");
                list.add("§7Boosts your Combat exp by");
                list.add("§a{2}%");
            }));
        })),

        ZOMBIE("COMBAT", "1c760ea5-2e91-3c2e-b52a-e17d11733658", "56fc854bb84cf4b7697297973e02b79bc10698460b51a639c60e5e417734e11", make(Lists.newLinkedList(), list ->
        {
            list.add(Stats.build("health", 0, 1));
            list.add(Stats.build("crit_damage", 0, 0.3, true));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", Pair.of(null, Property.build("[0.2]")));
            map.put("UNCOMMON", Pair.of(null, Property.build("[0.2]")));
            map.put("RARE", Pair.of(null, Property.build("[0.25, 0.25]")));
            map.put("EPIC", Pair.of(null, Property.build("[0.25, 0.25]")));
            map.put("LEGENDARY", Pair.of(null, Property.build("[0.25, 0.25, 0.2]")));
        }), make(Maps.newLinkedHashMap(), map ->
        {
            map.put("COMMON", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Chomp");
                list.add("§7Gain +§a{0} §7hp per zombie");
                list.add("§7kill");
            }));
            map.put("RARE", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Rotten Blade");
                list.add("§7Deal §a{1}% §7more damage to");
                list.add("§7zombies");
            }));
            map.put("LEGENDARY", make(Lists.newLinkedList(), list ->
            {
                list.add("§6Living Dead");
                list.add("§7Increases the defense of all");
                list.add("§7undead armor sets by §a{2}%");
            }));
        }));

        String type;
        String uuid;
        String value;
        List<String> desc;
        List<Stats> stats;
        Map<String, Object> descStats;
        Map<String, List<String>> petLore;
        String loreMode;

        PetType(String type, String uuid, String value, List<String> desc, List<Stats> stats, Map<String, Object> descStats, Map<String, List<String>> petLore, String loreMode)
        {
            this.type = type;
            this.uuid = uuid;
            this.value = value;
            this.desc = desc;
            this.stats = stats;
            this.descStats = descStats;
            this.petLore = petLore;
            this.loreMode = loreMode;
        }

        PetType(String type, String uuid, String value, List<String> desc, List<Stats> stats, Map<String, Object> descStats, Map<String, List<String>> petLore)
        {
            this(type, uuid, value, desc, stats, descStats, petLore, null);
        }

        PetType(String type, String uuid, String value, List<Stats> stats, Map<String, Object> descStats, Map<String, List<String>> petLore, String loreMode)
        {
            this(type, uuid, value, null, stats, descStats, petLore, loreMode);
        }

        PetType(String type, String uuid, String value, List<Stats> stats, Map<String, Object> descStats, Map<String, List<String>> petLore)
        {
            this(type, uuid, value, stats, descStats, petLore, null);
        }

        PetType(String type, String uuid, String value, List<Stats> stats, Map<String, Object> descStats)
        {
            this(type, uuid, value, stats, descStats, null, null);
        }

        PetType(String type, String uuid, String value)
        {
            this(type, uuid, value, null, null, null, null, null);
        }
    }

    enum HeldItem
    {
        PET_ITEM_ALL_SKILLS_BOOST_COMMON(formatName("ALL_SKILLS_BOOST"), EnumChatFormatting.WHITE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a10% §7pet exp");
            list.add("§7for all skills.");
        })),
        PET_ITEM_BIG_TEETH_COMMON(formatName("BIG_TEETH"), EnumChatFormatting.WHITE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases §9☣ Crit Chance §7by");
            list.add("§a5");
        }), new Stats[] {Stats.build("crit_chance", 5, 0)}),
        PET_ITEM_IRON_CLAWS_COMMON(formatName("IRON_CLAWS"), EnumChatFormatting.WHITE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases the pet's §9☠ Crit");
            list.add("§9Damage §7by §a40% §7and §9☣ Crit");
            list.add("§9Chance §7by §a40%");
        }), new Stats[] {Stats.build("crit_damage", 40, 0, true), Stats.build("crit_chance", 40, 0, true)}),
        PET_ITEM_SHARPENED_CLAWS_UNCOMMON(formatName("SHARPENED_CLAWS"), EnumChatFormatting.GREEN, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases §9☠ Crit Damage §7by");
            list.add("§a15");
        }), new Stats[] {Stats.build("crit_damage", 15, 0)}),
        PET_ITEM_HARDENED_SCALES_UNCOMMON(formatName("HARDENED_SCALES"), EnumChatFormatting.GREEN, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases §a❈ Defense §7by");
            list.add("§a25");
        }), new Stats[] {Stats.build("defense", 25, 0)}),
        PET_ITEM_BUBBLEGUM(EnumChatFormatting.BLUE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Your pet fuses its power");
            list.add("§7with placed §aOrbs §7to");
            list.add("§7give them §a2x §7duration.");
        })),
        PET_ITEM_LUCKY_CLOVER(EnumChatFormatting.DARK_PURPLE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases §b✯ Magic Find");
            list.add("§7by §a7.");
        }), new Stats[] {Stats.build("magic_find", 7, 0)}),
        PET_ITEM_TEXTBOOK(EnumChatFormatting.GOLD, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases the pet's §b✎");
            list.add("§bIntelligence §7by §a100%");
        }), new Stats[] {Stats.build("intelligence", 100, 0)}),
        PET_ITEM_SADDLE(EnumChatFormatting.GREEN, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases horse speed by");
            list.add("§a50% §7and jump boost by");
            list.add("§a100%");
        })),
        PET_ITEM_EXP_SHARE(EnumChatFormatting.DARK_PURPLE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7While unequipped this pet");
            list.add("§7gains §a25% §7of the");
            list.add("§7equipped pet's xp, this is");
            list.add("§7split between all pets");
            list.add("§7holding the item.");
        })),
        PET_ITEM_TIER_BOOST(EnumChatFormatting.GOLD, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Boosts the §ararity §7of your");
            list.add("§7pet by 1 tier!");
        })),
        PET_ITEM_COMBAT_SKILL_BOOST_COMMON(formatName("COMBAT_SKILL_BOOST"), EnumChatFormatting.WHITE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a20% §7pet exp");
            list.add("§7for Combat.");
        })),
        PET_ITEM_COMBAT_SKILL_BOOST_UNCOMMON(formatName("COMBAT_SKILL_BOOST"), EnumChatFormatting.GREEN, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a30% §7pet exp");
            list.add("§7for Combat.");
        })),
        PET_ITEM_COMBAT_SKILL_BOOST_RARE(formatName("COMBAT_SKILL_BOOST"), EnumChatFormatting.BLUE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a40% §7pet exp");
            list.add("§7for Combat.");
        })),
        PET_ITEM_COMBAT_SKILL_BOOST_EPIC(formatName("COMBAT_SKILL_BOOST"), EnumChatFormatting.DARK_PURPLE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a50% §7pet exp");
            list.add("§7for Combat.");
        })),
        PET_ITEM_FISHING_SKILL_BOOST_COMMON(formatName("FISHING_SKILL_BOOST"), EnumChatFormatting.WHITE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a20% §7pet exp");
            list.add("§7for Fishing.");
        })),
        PET_ITEM_FISHING_SKILL_BOOST_UNCOMMON(formatName("FISHING_SKILL_BOOST"), EnumChatFormatting.GREEN, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a30% §7pet exp");
            list.add("§7for Fishing.");
        })),
        PET_ITEM_FISHING_SKILL_BOOST_RARE(formatName("FISHING_SKILL_BOOST"), EnumChatFormatting.BLUE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a40% §7pet exp");
            list.add("§7for Fishing.");
        })),
        PET_ITEM_FISHING_SKILL_BOOST_EPIC(formatName("FISHING_SKILL_BOOST"), EnumChatFormatting.DARK_PURPLE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a50% §7pet exp");
            list.add("§7for Fishing.");
        })),
        PET_ITEM_FORAGING_SKILL_BOOST_COMMON(formatName("FORAGING_SKILL_BOOST"), EnumChatFormatting.WHITE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a20% §7pet exp");
            list.add("§7for Foraging.");
        })),
        PET_ITEM_FORAGING_SKILL_BOOST_UNCOMMON(formatName("FORAGING_SKILL_BOOST"), EnumChatFormatting.GREEN, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a30% §7pet exp");
            list.add("§7for Foraging.");
        })),
        PET_ITEM_FORAGING_SKILL_BOOST_RARE(formatName("FORAGING_SKILL_BOOST"), EnumChatFormatting.BLUE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a40% §7pet exp");
            list.add("§7for Foraging.");
        })),
        PET_ITEM_FORAGING_SKILL_BOOST_EPIC(formatName("FORAGING_SKILL_BOOST"), EnumChatFormatting.DARK_PURPLE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a50% §7pet exp");
            list.add("§7for Foraging.");
        })),
        PET_ITEM_MINING_SKILL_BOOST_COMMON(formatName("MINING_SKILL_BOOST"), EnumChatFormatting.WHITE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a20% §7pet exp");
            list.add("§7for Mining.");
        })),
        PET_ITEM_MINING_SKILL_BOOST_UNCOMMON(formatName("MINING_SKILL_BOOST"), EnumChatFormatting.GREEN, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a30% §7pet exp");
            list.add("§7for Mining.");
        })),
        PET_ITEM_MINING_SKILL_BOOST_RARE(formatName("MINING_SKILL_BOOST"), EnumChatFormatting.BLUE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a40% §7pet exp");
            list.add("§7for Mining.");
        })),
        PET_ITEM_MINING_SKILL_BOOST_EPIC(formatName("MINING_SKILL_BOOST"), EnumChatFormatting.DARK_PURPLE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a50% §7pet exp");
            list.add("§7for Mining.");
        })),
        PET_ITEM_FARMING_SKILL_BOOST_COMMON(formatName("FARMING_SKILL_BOOST"), EnumChatFormatting.WHITE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a20% §7pet exp");
            list.add("§7for Farming.");
        })),
        PET_ITEM_FARMING_SKILL_BOOST_UNCOMMON(formatName("FARMING_SKILL_BOOST"), EnumChatFormatting.GREEN, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a30% §7pet exp");
            list.add("§7for Farming.");
        })),
        PET_ITEM_FARMING_SKILL_BOOST_RARE(formatName("FARMING_SKILL_BOOST"), EnumChatFormatting.BLUE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a40% §7pet exp");
            list.add("§7for Farming.");
        })),
        PET_ITEM_FARMING_SKILL_BOOST_EPIC(formatName("FARMING_SKILL_BOOST"), EnumChatFormatting.DARK_PURPLE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a50% §7pet exp");
            list.add("§7for Farming.");
        })),
        REINFORCED_SCALES(EnumChatFormatting.BLUE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases §a❈ Defense §7by");
            list.add("§a40");
        }), new Stats[] {Stats.build("defense", 40, 0)}),
        GOLD_CLAWS(EnumChatFormatting.GREEN, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases the pet's §9☠ Crit");
            list.add("§9Damage §7by §a50% §7and §9☣ Crit");
            list.add("§9Chance §7by §a50%");
        }), new Stats[] {Stats.build("crit_damage", 50, 0, true), Stats.build("crit_chance", 50, 0, true)}),
        ALL_SKILLS_SUPER_BOOST(EnumChatFormatting.WHITE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gives +§a20% §7pet exp for all");
            list.add("§7skills");
        })),
        BIGGER_TEETH(EnumChatFormatting.GREEN, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases §9☣ Crit Chance §7by");
            list.add("§a10");
        }), new Stats[] {Stats.build("crit_chance", 10, 0)}),
        SERRATED_CLAWS(EnumChatFormatting.BLUE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases §9☠ Crit Damage §7by");
            list.add("§a25");
        }), new Stats[] {Stats.build("crit_damage", 25, 0)}),
        WASHED_UP_SOUVENIR("Washed-up Souvenir", EnumChatFormatting.GOLD, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases §3α Sea");
            list.add("§3Creature Chance §7by §a5.");
        }), new Stats[] {Stats.build("sea_creature_chance", 5, 0)}),
        ANTIQUE_REMEDIES(EnumChatFormatting.DARK_PURPLE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases the pet's §c❁");
            list.add("§cStrength §7by §a80%");
        }), new Stats[] {Stats.build("strength", 80, 0, true)}),
        CROCHET_TIGER_PLUSHIE(EnumChatFormatting.DARK_PURPLE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases §e⚔ Bonus");
            list.add("§eAttack Speed §7by §a35.");
        }), new Stats[] {Stats.build("attack_speed", 35, 0)}),
        DWARF_TURTLE_SHELMET(EnumChatFormatting.BLUE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Makes the pet's owner");
            list.add("§7immune to knockback.");
        })),
        MINOS_RELIC(EnumChatFormatting.DARK_PURPLE, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases all pet stats");
            list.add("§7by §a33.3%§7.");
        })),
        PET_ITEM_SPOOKY_CUPCAKE(EnumChatFormatting.GREEN, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Increases §c❁ Strength");
            list.add("§7by §a30 §7and §f✦ Speed");
            list.add("§7by §a20.");
        }), new Stats[] {Stats.build("strength", 30, 0), Stats.build("speed", 20, 0)}),
        PET_ITEM_VAMPIRE_FANG(EnumChatFormatting.GOLD, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Upgrades a Bat pet from");
            list.add("§6Legendary §7to §dMythic");
            list.add("§7adding a bonus perk and");
            list.add("§7bonus stats!");
        })),
        PET_ITEM_TOY_JERRY("Jerry 3D Glasses", EnumChatFormatting.GOLD, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Upgrades a Jerry pet from");
            list.add("§6Legendary §7to §dMythic");
            list.add("§7and granting it a new perk!");
        })),
        REAPER_GEM(EnumChatFormatting.GOLD, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Gain §c8⫽ Ferocity §7for 5s on");
            list.add("§7kill");
        })),
        PET_ITEM_FLYING_PIG(EnumChatFormatting.GREEN, make(Lists.newLinkedList(), list ->
        {
            list.add("§7Grants your pig pet the");
            list.add("§7ability to fly while on");
            list.add("§7your private island! You");
            list.add("§7also don't need to hold a");
            list.add("§7carrot on a stick to");
            list.add("§7control your pig.");
        }));

        String altName;
        EnumChatFormatting color;
        List<String> lore;
        Stats[] stats;

        HeldItem(EnumChatFormatting color, List<String> lore)
        {
            this(null, color, lore, null);
        }

        HeldItem(EnumChatFormatting color, List<String> lore, Stats[] stats)
        {
            this(null, color, lore, stats);
        }

        HeldItem(String altName, EnumChatFormatting color, List<String> lore)
        {
            this(altName, color, lore, null);
        }

        HeldItem(String altName, EnumChatFormatting color, List<String> lore, Stats[] stats)
        {
            this.altName = altName;
            this.color = color;
            this.lore = lore;
            this.stats = stats;
        }

        boolean isUpgradeToNextRarity()
        {
            return this == PET_ITEM_TIER_BOOST || this == PET_ITEM_VAMPIRE_FANG || this == PET_ITEM_TOY_JERRY;
        }

        static String formatName(String name)
        {
            return WordUtils.capitalize(name.toLowerCase(Locale.ROOT).replace("_", " "));
        }
    }

    static class PetScore
    {
        int score;
        int magic_find;

        PetScore(int score, int magic_find)
        {
            this.score = score;
            this.magic_find = magic_find;
        }
    }

    static class PetSkin
    {
        String skin;
        String texture;
        String name;
        EnumChatFormatting color;
        String uuid;

        PetSkin(String skin, String texture, EnumChatFormatting color, String name, String uuid)
        {
            this.skin = skin;
            this.texture = texture;
            this.name = name;
            this.color = color;
            this.uuid = uuid;
        }
    }

    static class RarityIndex
    {
        String type;
        int index;

        RarityIndex(String type, int index)
        {
            this.type = type;
            this.index = index;
        }
    }
}