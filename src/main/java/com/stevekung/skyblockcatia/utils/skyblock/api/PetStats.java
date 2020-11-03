package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.*;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBPets;
import com.stevekung.stevekungslib.utils.GameProfileUtils;

import net.minecraft.util.math.MathHelper;

/**
 *
 * Prototype version
 * @author SteveKunG
 *
 */
@Deprecated
public class PetStats
{
    public static final PetStats INSTANCE = new PetStats();
    private double axeCooldown;

    public void setClientStatByType(SBPets.Type type, int level, boolean active)
    {
        switch (type)
        {
        case MONKEY:
            this.setAxeCooldown(active ? level * 0.5D : 0);
            System.out.println("axe cooldown: " + this.axeCooldown);
            System.out.println("real axe cooldown: " + this.getAxeCooldown(2000));
            break;
        default:
            break;
        }
    }

    public int getAxeCooldown(int base)
    {
        return (int)(base - base * this.axeCooldown / 100.0D);
    }

    public void setAxeCooldown(double axeCooldown)
    {
        this.axeCooldown = axeCooldown;
    }

    public static void scheduleDownloadPetStats()
    {
        try
        {
            String uuid = GameProfileUtils.getUUID().toString().replace("-", "");
            URL urlSB = new URL(SBAPIUtils.APIUrl.SKYBLOCK_PROFILES.getUrl() + uuid);
            JsonObject objSB = new JsonParser().parse(IOUtils.toString(urlSB.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
            JsonElement sbProfile = objSB.get("profiles");
            List<Profile> profiles = Lists.newArrayList();

            if (!sbProfile.isJsonNull())
            {
                JsonArray profilesList = sbProfile.getAsJsonArray();
                boolean hasOneProfile = profilesList.size() == 1;

                for (JsonElement profile : profilesList)
                {
                    long lastSave = -1;
                    JsonObject availableProfile = null;

                    for (Map.Entry<String, JsonElement> entry : profile.getAsJsonObject().get("members").getAsJsonObject().entrySet())
                    {
                        if (!entry.getKey().equals(uuid))
                        {
                            continue;
                        }
                        JsonElement lastSaveEle = entry.getValue().getAsJsonObject().get("last_save");
                        lastSave = lastSaveEle == null ? -1 : lastSaveEle.getAsLong();
                    }

                    availableProfile = profile.getAsJsonObject();
                    profiles.add(new Profile(availableProfile, lastSave));

                    if (hasOneProfile)
                    {
                        break;
                    }
                }
                profiles.sort((profile1, profile2) -> new CompareToBuilder().append(profile2.lastSave, profile1.lastSave).build());
            }
            Profile profile = Iterables.getFirst(profiles, null);
            PetStats.refreshPetStats(profile.profile, uuid);
        }
        catch (JsonSyntaxException | IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void refreshPetStats(JsonObject profile, String uuid) throws JsonSyntaxException, IOException
    {
        JsonObject profiles = profile.get("members").getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : profiles.entrySet())
        {
            String userUUID = entry.getKey();

            if (userUUID.equals(uuid))
            {
                JsonObject currentUserProfile = profiles.get(userUUID).getAsJsonObject();
                JsonElement petsObj = currentUserProfile.get("pets");

                if (petsObj == null)
                {
                    return;
                }

                JsonArray pets = petsObj.getAsJsonArray();

                if (pets.size() > 0)
                {
                    for (JsonElement element : pets)
                    {
                        double exp = 0.0D;
                        String petRarity = SBPets.Tier.COMMON.name();
                        JsonElement heldItemObj = element.getAsJsonObject().get("heldItem");
                        SBPets.HeldItem heldItem = null;

                        if (element.getAsJsonObject().get("exp") != null)
                        {
                            exp = element.getAsJsonObject().get("exp").getAsDouble();
                        }
                        if (element.getAsJsonObject().get("tier") != null)
                        {
                            petRarity = element.getAsJsonObject().get("tier").getAsString();
                        }
                        if (heldItemObj != null && !heldItemObj.isJsonNull())
                        {
                            try
                            {
                                heldItem = SBPets.HeldItem.valueOf(heldItemObj.getAsString());
                            }
                            catch (Exception e) {e.printStackTrace();}
                        }

                        SBPets.Tier tier = SBPets.Tier.valueOf(petRarity);
                        boolean active = element.getAsJsonObject().get("active").getAsBoolean();
                        String petType = element.getAsJsonObject().get("type").getAsString();

                        if (heldItem != null && heldItem == SBPets.HeldItem.PET_ITEM_TIER_BOOST)
                        {
                            tier = SBPets.Tier.values()[Math.min(SBPets.Tier.values().length - 1, tier.ordinal() + 1)];
                        }

                        int level = PetStats.checkPetLevel(exp, tier);

                        try
                        {
                            SBPets.Type type = SBPets.Type.valueOf(petType);
                            PetStats.INSTANCE.setClientStatByType(type, level, active);
                        }
                        catch (Exception e) {e.printStackTrace();}
                    }
                }
                break;
            }
        }
    }

    private static int checkPetLevel(double petExp, SBPets.Tier tier)
    {
        ExpProgress[] progress = tier.getProgression();
        int xpRequired = 0;
        int currentLvl = 0;
        double xpTotal = 0;
        double xpToNextLvl = 0;
        double currentXp = 0;

        for (int x = 0; x < progress.length; ++x)
        {
            if (petExp >= xpTotal)
            {
                xpTotal += progress[x].getXp();
                currentLvl = x + 1;
                xpRequired = (int)progress[x].getXp();
            }
        }

        if (currentLvl < progress.length)
        {
            xpToNextLvl = MathHelper.ceil(xpTotal - petExp);
            currentXp = xpRequired - xpToNextLvl;
        }
        else if (currentLvl == progress.length)
        {
            xpToNextLvl = MathHelper.ceil(Math.abs(xpTotal - petExp));
            currentXp = xpRequired - xpToNextLvl;
        }

        if (petExp >= xpTotal || currentXp >= xpRequired)
        {
            currentLvl = progress.length + 1;
        }
        return currentLvl;
    }

    static class Profile
    {
        JsonObject profile;
        long lastSave;

        Profile(JsonObject profile, long lastSave)
        {
            this.profile = profile;
            this.lastSave = lastSave;
        }
    }
}