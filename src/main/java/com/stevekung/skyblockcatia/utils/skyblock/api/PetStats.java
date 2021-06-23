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
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.stevekung.skyblockcatia.utils.GameProfileUtils;
import com.stevekung.skyblockcatia.utils.LoggerIN;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBPets;

import net.minecraft.util.MathHelper;

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
    private static final Gson GSON = new Gson();
    private double axeCooldown;

    public void setClientStatByType(SBPets.Type type, int level, boolean active)
    {
        if (type.getType().equals("MONKEY"))
        {
            this.setAxeCooldown(active ? level * 0.5D : 0);
            System.out.println("axe cooldown: " + this.axeCooldown);
            System.out.println("real axe cooldown: " + this.getAxeCooldown(2000));
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
            URL urlSB = new URL(SBAPIUtils.SKYBLOCK_PROFILES + uuid);
            SkyblockProfiles sbProfiles = GSON.fromJson(IOUtils.toString(urlSB.openConnection().getInputStream(), StandardCharsets.UTF_8), SkyblockProfiles.class);
            List<Profile> profiles = Lists.newArrayList();

            if (sbProfiles != null)
            {
                SkyblockProfiles.Profile[] profilesList = sbProfiles.getProfiles();
                boolean hasOneProfile = profilesList.length == 1;

                for (SkyblockProfiles.Profile profile : profilesList)
                {
                    long lastSave = -1;
                    SkyblockProfiles.Profile availableProfile;

                    for (Map.Entry<String, SkyblockProfiles.Members> entry : profile.getMembers().entrySet())
                    {
                        if (!entry.getKey().equals(uuid))
                        {
                            continue;
                        }
                        lastSave = entry.getValue().getLastSave();
                    }

                    availableProfile = profile;
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

    private static void refreshPetStats(SkyblockProfiles.Profile profile, String uuid) throws JsonSyntaxException, IOException
    {
        Map<String, SkyblockProfiles.Members> profiles = profile.getMembers();

        for (Map.Entry<String, SkyblockProfiles.Members> entry : profiles.entrySet())
        {
            String userUUID = entry.getKey();

            if (userUUID.equals(uuid))
            {
                SkyblockProfiles.Members currentUserProfile = profiles.get(userUUID);
                SkyblockProfiles.Pets[] pets = currentUserProfile.getPets();

                if (pets == null || pets.length <= 0)
                {
                    return;
                }

                for (SkyblockProfiles.Pets pet : pets)
                {
                    double exp = pet.getExp();
                    String petRarity = pet.getTier();
                    String heldItemObj = pet.getHeldItem();
                    boolean active = pet.isActive();
                    SBPets.HeldItem heldItem = null;

                    if (heldItemObj != null)
                    {
                        heldItem = SBPets.PETS.getHeldItemByName(heldItemObj);

                        if (heldItem == null)
                        {
                            LoggerIN.warning("Found an unknown pet item!, type: {}", heldItemObj);
                        }
                    }

                    SBPets.Tier tier = SBPets.Tier.valueOf(petRarity);
                    String petType = pet.getType();

                    if (heldItem != null && heldItem.isUpgrade())
                    {
                        tier = tier.getNextRarity();
                    }

                    int level = PetStats.checkPetLevel(exp, tier);

                    try
                    {
                        SBPets.Type type = SBPets.PETS.getTypeByName(petType);
                        PetStats.INSTANCE.setClientStatByType(type, level, active);
                    }
                    catch (Exception e) {e.printStackTrace();}
                }
                break;
            }
        }
    }

    private static int checkPetLevel(double petExp, SBPets.Tier tier)
    {
        int index = SBPets.PETS.getIndex().get(tier.name());
        int xpRequired = 0;
        int currentLvl = 0;
        double xpTotal = 0;
        double xpToNextLvl = 0;
        double currentXp = 0;

        for (int i = index; i < 99 + index; i++)
        {
            if (petExp >= xpTotal)
            {
                int level = SBPets.PETS.getLeveling()[i];
                xpTotal += level;
                currentLvl = i - index + 1;
                xpRequired = level;
            }
        }

        if (currentLvl < 100)
        {
            xpToNextLvl = MathHelper.ceiling_double_int(xpTotal - petExp);
            currentXp = xpRequired - xpToNextLvl;
        }
        else if (currentLvl == 100)
        {
            xpToNextLvl = MathHelper.ceiling_double_int(Math.abs(xpTotal - petExp));
            currentXp = xpRequired - xpToNextLvl;
        }

        if (petExp >= xpTotal || currentXp >= xpRequired)
        {
            currentLvl = 100;
        }
        return currentLvl;
    }

    static class Profile
    {
        SkyblockProfiles.Profile profile;
        long lastSave;

        Profile(SkyblockProfiles.Profile profile, long lastSave)
        {
            this.profile = profile;
            this.lastSave = lastSave;
        }
    }
}