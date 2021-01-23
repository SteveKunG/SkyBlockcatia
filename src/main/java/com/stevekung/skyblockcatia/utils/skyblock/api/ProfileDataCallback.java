package com.stevekung.skyblockcatia.utils.skyblock.api;

import java.util.List;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.util.text.ITextComponent;

public class ProfileDataCallback
{
    private JsonObject sbProfile;
    private String sbProfileId;
    private ITextComponent profileName;
    private String username;
    private String displayName;
    private ITextComponent gameMode;
    private String uuid;
    private String guild;
    private GameProfile profile;
    private long lastSave;
    private List<ITextComponent> islandMembers;

    public ProfileDataCallback(String sbProfileId, ITextComponent profileName, String username, String displayName, ITextComponent gameMode, String guild, String uuid, GameProfile profile, long lastSave)
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

    public ProfileDataCallback(JsonObject sbProfile, String username, String displayName, ITextComponent gameMode, String guild, String uuid, GameProfile gameProfile, long lastSave, List<ITextComponent> islandMembers)
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

    public JsonObject getSkyblockProfile()
    {
        return this.sbProfile;
    }

    public String getProfileId()
    {
        return this.sbProfile == null ? this.sbProfileId : this.sbProfile.get("profile_id").getAsString();
    }

    public ITextComponent getProfileName()
    {
        return this.sbProfile == null ? this.profileName : TextComponentUtils.component(this.sbProfile.get("cute_name").getAsString());
    }

    public String getUsername()
    {
        return this.username;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public ITextComponent getGameMode()
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

    public List<ITextComponent> getIslandMembers()
    {
        return this.islandMembers;
    }
}