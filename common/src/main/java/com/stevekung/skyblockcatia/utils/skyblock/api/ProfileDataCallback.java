package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.util.List;

import com.mojang.authlib.GameProfile;
import com.stevekung.stevekungslib.utils.TextComponentUtils;
import net.minecraft.network.chat.Component;

public class ProfileDataCallback
{
    private SkyblockProfiles.Profile sbProfile;
    private String sbProfileId;
    private Component profileName;
    private final String username;
    private final String displayName;
    private final Component gameMode;
    private final String uuid;
    private final String guild;
    private final GameProfile profile;
    private final long lastSave;
    private List<Component> islandMembers;

    public ProfileDataCallback(String sbProfileId, Component profileName, String username, String displayName, Component gameMode, String guild, String uuid, GameProfile profile, long lastSave)
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

    public ProfileDataCallback(SkyblockProfiles.Profile sbProfile, String username, String displayName, Component gameMode, String guild, String uuid, GameProfile gameProfile, long lastSave, List<Component> islandMembers)
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
        return this.sbProfile == null ? this.sbProfileId : this.sbProfile.profileId();
    }

    public Component getProfileName()
    {
        return this.sbProfile == null ? this.profileName : TextComponentUtils.component(this.sbProfile.cuteName());
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public Component getGameMode()
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

    public List<Component> getIslandMembers()
    {
        return this.islandMembers;
    }
}