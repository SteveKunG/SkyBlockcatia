package com.stevekung.skyblockcatia.gui;

import java.util.List;

import com.stevekung.skyblockcatia.gui.api.GuiSkyBlockData;
import com.stevekung.skyblockcatia.gui.api.ProfileDataCallback;
import com.stevekung.skyblockcatia.utils.TimeUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;

public class SkyBlockProfileButton extends Button
{
    private List<ProfileDataCallback> profiles;
    private final ProfileDataCallback callback;
    private final Minecraft mc;

    public SkyBlockProfileButton(int x, int y, int width, int height, ProfileDataCallback callback)
    {
        super(x, y, width, height, callback.getProfileName(), null);
        this.mc = Minecraft.getInstance();
        this.callback = callback;
    }

    @Override
    public void onPress()
    {
        this.mc.displayGuiScreen(new GuiSkyBlockData(this.profiles, this.callback));
    }

    public long getLastSave()
    {
        return this.callback.getLastSave();
    }

    public void setProfileList(List<ProfileDataCallback> profiles)
    {
        this.profiles = profiles;
    }

    public String getLastActive()
    {
        String time = "Invalid data!";

        if (this.callback.getLastSave() > 0)
        {
            time = TimeUtils.getRelativeTime(this.callback.getLastSave());
        }
        return "Last active: " + time;
    }
}