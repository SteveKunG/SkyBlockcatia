package stevekung.mods.indicatia.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import stevekung.mods.indicatia.gui.api.GuiSkyBlockData;
import stevekung.mods.indicatia.gui.api.ProfileDataCallback;
import stevekung.mods.indicatia.utils.CommonUtils;

public class GuiSBProfileButton extends GuiButton
{
    private List<ProfileDataCallback> profiles;
    private final ProfileDataCallback callback;
    private final Minecraft mc;

    public GuiSBProfileButton(int id, int x, int y, int width, int height, ProfileDataCallback callback)
    {
        super(id, x, y, width, height, callback.getProfileName());
        this.mc = Minecraft.getMinecraft();
        this.callback = callback;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height)
        {
            this.mc.displayGuiScreen(new GuiSkyBlockData(this.profiles, this.callback));
            return true;
        }
        return false;
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
            time = CommonUtils.getRelativeTime(this.callback.getLastSave());
        }
        return "Last active: " + time;
    }
}