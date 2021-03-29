package com.stevekung.skyblockcatia.gui.widget.button;

import java.util.List;

import com.stevekung.skyblockcatia.gui.screen.SkyBlockAPIViewerScreen;
import com.stevekung.skyblockcatia.utils.TimeUtils;
import com.stevekung.skyblockcatia.utils.skyblock.api.ProfileDataCallback;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

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
        this.mc.displayGuiScreen(new SkyBlockAPIViewerScreen(this.profiles, this.callback));
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

    public ITextComponent getGameMode()
    {
        return TextComponentUtils.component("Game Mode: ").appendSibling(this.callback.getGameMode());
    }

    public List<ITextComponent> getIslandMembers()
    {
        return this.callback.getIslandMembers();
    }
}