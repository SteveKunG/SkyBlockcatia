package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.util.List;

import com.mojang.authlib.GameProfile;

public class ProfileDataCallback
{
    private SkyblockProfiles.Profile sbProfile;
    private String sbProfileId;
    private String profileName;
    private String username;
    private String displayName;
    private String gameMode;
    private String uuid;
    private String guild;
    private GameProfile profile;
    private long lastSave;
    private List<String> islandMembers;

    public ProfileDataCallback(String sbProfileId, String profileName, String username, String displayName, String gameMode, String guild, String uuid, GameProfile profile, long lastSave)
    {
        this.sbProfileId = sbProfileId;
        this.profileName = profileName;
        this.username = username;
        this.displayName = displayName;
        this.gameMode = gameMode;
        this.guild = guild;
        this.uuid = uuid;
        this.profile = profile;
        this.lastSave = lastSave;
    }

    public ProfileDataCallback(SkyblockProfiles.Profile sbProfile, String username, String displayName, String gameMode, String guild, String uuid, GameProfile gameProfile, long lastSave, List<String> islandMembers)
    {
        this.sbProfile = sbProfile;
        this.username = username;
        this.displayName = displayName;
        this.gameMode = gameMode;
        this.guild = guild;
        this.uuid = uuid;
        this.profile = gameProfile;
        this.lastSave = lastSave;
        this.islandMembers = islandMembers;
    }

    public SkyblockProfiles.Profile getSkyblockProfile()
    {
        return this.sbProfile;
    }

    public String getProfileId()
    {
        return this.sbProfile == null ? this.sbProfileId : this.sbProfile.getProfileId();
    }

    public String getProfileName()
    {
        return this.sbProfile == null ? this.profileName : this.sbProfile.getCuteName();
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public String getGameMode()
    {
        return this.gameMode;
    }

    public String getGuild()
    {
        return this.guild;
    }

    public String getUUID()
    {
        return this.uuid;
    }

    public GameProfile getGameProfile()
    {
        return this.profile;
    }

    public long getLastSave()
    {
        return this.lastSave;
    }

    public List<String> getIslandMembers()
    {
        return this.islandMembers;
    }
}