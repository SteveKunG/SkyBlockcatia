package com.stevekung.skyblockcatia.gui.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.gui.config.widget.GuiConfigButtonRowList;
import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiRenderInfoSettings extends GuiScreen
{
    private final GuiScreen parent;
    private GuiConfigButtonRowList optionsRowList;
    private static final List<SkyBlockcatiaSettings.Options> OPTIONS = new ArrayList<>();

    static
    {
        OPTIONS.add(SkyBlockcatiaSettings.Options.FPS);
        OPTIONS.add(SkyBlockcatiaSettings.Options.XYZ);
        OPTIONS.add(SkyBlockcatiaSettings.Options.DIRECTION);
        OPTIONS.add(SkyBlockcatiaSettings.Options.BIOME);
        OPTIONS.add(SkyBlockcatiaSettings.Options.PING);
        OPTIONS.add(SkyBlockcatiaSettings.Options.PING_TO_SECOND);
        OPTIONS.add(SkyBlockcatiaSettings.Options.SERVER_IP);
        OPTIONS.add(SkyBlockcatiaSettings.Options.SERVER_IP_MC);
        OPTIONS.add(SkyBlockcatiaSettings.Options.EQUIPMENT_HUD);
        OPTIONS.add(SkyBlockcatiaSettings.Options.EQUIPMENT_ARMOR_ITEMS);
        OPTIONS.add(SkyBlockcatiaSettings.Options.EQUIPMENT_HAND_ITEMS);
        OPTIONS.add(SkyBlockcatiaSettings.Options.POTION_HUD);
        OPTIONS.add(SkyBlockcatiaSettings.Options.REAL_TIME);
        OPTIONS.add(SkyBlockcatiaSettings.Options.TWENTY_FOUR_TIME);
        OPTIONS.add(SkyBlockcatiaSettings.Options.GAME_TIME);
        OPTIONS.add(SkyBlockcatiaSettings.Options.GAME_WEATHER);
        OPTIONS.add(SkyBlockcatiaSettings.Options.MOON_PHASE);
        OPTIONS.add(SkyBlockcatiaSettings.Options.POTION_ICON);
        OPTIONS.add(SkyBlockcatiaSettings.Options.ALTERNATE_POTION_COLOR);
    }

    public GuiRenderInfoSettings(GuiScreen parent)
    {
        this.parent = parent;
    }

    @Override
    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height - 27, LangUtils.translate("gui.done")));

        SkyBlockcatiaSettings.Options[] options = new SkyBlockcatiaSettings.Options[OPTIONS.size()];
        options = OPTIONS.toArray(options);
        this.optionsRowList = new GuiConfigButtonRowList(this.width, this.height, 32, this.height - 32, 25, options, false);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.optionsRowList.handleMouseInput();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            SkyBlockcatiaSettings.INSTANCE.save();
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.optionsRowList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        super.mouseReleased(mouseX, mouseY, state);
        this.optionsRowList.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 200)
            {
                SkyBlockcatiaSettings.INSTANCE.save();
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.optionsRowList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, LangUtils.translate("extended_config.render_info.title"), this.width / 2, 5, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}