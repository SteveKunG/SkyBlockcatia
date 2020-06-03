package com.stevekung.skyblockcatia.utils.skyblock.api;

import com.mojang.authlib.GameProfile;

public class ProfileDataCallback
{
    private final String sbProfileId;
    private final String profileName;
    private final String username;
    private final String displayName;
    private final String uuid;
    private final String guild;
    private final GameProfile profile;
    private final long lastSave;

    public ProfileDataCallback(String sbProfileId, String profileName, String username, String displayName, String guild, String uuid, GameProfile profile, long lastSave)
    {
        this.sbProfileId = sbProfileId;
        this.profileName = profileName;
        this.username = username;
        this.displayName = displayName;
        this.guild = guild;
        this.uuid = uuid;
        this.profile = profile;
        this.lastSave = lastSave;
    }

    public String getProfileId()
    {
        return this.sbProfileId;
    }

    public String getProfileName()
    {
        return this.profileName;
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getDisplayName()
    {
        return this.displayName;
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
}