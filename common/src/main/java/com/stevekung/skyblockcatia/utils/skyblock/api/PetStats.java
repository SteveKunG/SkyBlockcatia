package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;
import com.stevekung.skyblockcatia.core.SkyBlockcatia;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBPets;
import com.stevekung.stevekungslib.utils.GameProfileUtils;
import net.minecraft.util.Mth;

/**
 * Prototype version
 *
 * @author SteveKunG
 */
@Deprecated
public class PetStats
{
    public static final PetStats INSTANCE = new PetStats();
    private double axeCooldown;

    public void setClientStatByType(SBPets.Type type, int level, boolean active)
    {
        if (type.type().equals("MONKEY"))
        {
            this.setAxeCooldown(active ? level * 0.5D : 0);
            System.out.println("axe cooldown: " + this.axeCooldown);
            System.out.println("real axe cooldown: " + this.getAxeCooldown(2000));
        }
    }

    public int getAxeCooldown(int base)
    {
        return (int) (base - base * this.axeCooldown / 100.0D);
    }

    public void setAxeCooldown(double axeCooldown)
    {
        this.axeCooldown = axeCooldown;
    }

    public static void scheduleDownloadPetStats()
    {
        try
        {
            var uuid = GameProfileUtils.getUUID().toString().replace("-", "");
            var urlSB = new URL(SBAPIUtils.APIUrl.SKYBLOCK_PROFILES.getUrl() + uuid);
            var sbProfiles = SkyBlockcatia.GSON.fromJson(IOUtils.toString(urlSB.openConnection().getInputStream(), StandardCharsets.UTF_8), SkyblockProfiles.class);
            var profiles = Lists.<Profile>newArrayList();

            if (sbProfiles != null)
            {
                var profilesList = sbProfiles.profiles();
                var hasOneProfile = profilesList.length == 1;

                for (var profile : profilesList)
                {
                    var lastSave = -1L;

                    for (var entry : profile.members().entrySet())
                    {
                        if (!entry.getKey().equals(uuid))
                        {
                            continue;
                        }
                        lastSave = entry.getValue().getLastSave();
                    }

                    profiles.add(new Profile(profile, lastSave));

                    if (hasOneProfile)
                    {
                        break;
                    }
                }
                profiles.sort((profile1, profile2) -> new CompareToBuilder().append(profile2.lastSave, profile1.lastSave).build());
            }
            var profile = Iterables.getFirst(profiles, null);
            PetStats.refreshPetStats(profile.profile, uuid);
        }
        catch (JsonSyntaxException | IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void refreshPetStats(SkyblockProfiles.Profile profile, String uuid) throws JsonSyntaxException, IOException
    {
        var profiles = profile.members();

        for (var entry : profiles.entrySet())
        {
            var userUUID = entry.getKey();

            if (userUUID.equals(uuid))
            {
                var currentUserProfile = profiles.get(userUUID);
                var pets = currentUserProfile.getPets();

                if (pets == null || pets.length <= 0)
                {
                    return;
                }

                for (var pet : pets)
                {
                    var exp = pet.exp();
                    var petRarity = pet.tier();
                    var heldItemObj = pet.heldItem();
                    var active = pet.active();
                    SBPets.HeldItem heldItem = null;

                    if (heldItemObj != null)
                    {
                        heldItem = SBPets.PETS.getHeldItemByName(heldItemObj);

                        if (heldItem == null)
                        {
                            SkyBlockcatia.LOGGER.warning("Found an unknown pet item!, type: {}", heldItemObj);
                        }
                    }

                    var tier = SBPets.Tier.valueOf(petRarity);
                    var petType = pet.type();

                    if (heldItem != null && heldItem.isUpgrade())
                    {
                        tier = tier.getNextRarity();
                    }

                    var level = PetStats.checkPetLevel(exp, tier);

                    try
                    {
                        var type = SBPets.PETS.getTypeByName(petType);
                        PetStats.INSTANCE.setClientStatByType(type, level, active);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    private static int checkPetLevel(double petExp, SBPets.Tier tier)
    {
        var index = SBPets.PETS.index().get(tier.name());
        var xpRequired = 0;
        var currentLvl = 0;
        var xpTotal = 0D;
        double xpToNextLvl;
        var currentXp = 0D;

        for (var i = index; i < 99 + index; i++)
        {
            if (petExp >= xpTotal)
            {
                var level = SBPets.PETS.leveling()[i];
                xpTotal += level;
                currentLvl = i - index + 1;
                xpRequired = level;
            }
        }

        if (currentLvl < 100)
        {
            xpToNextLvl = Mth.ceil(xpTotal - petExp);
            currentXp = xpRequired - xpToNextLvl;
        }
        else if (currentLvl == 100)
        {
            xpToNextLvl = Mth.ceil(Math.abs(xpTotal - petExp));
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