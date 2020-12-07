package com.stevekung.skyblockcatia.gui.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.gui.config.widget.GuiConfigTextFieldRowList;
import com.stevekung.skyblockcatia.utils.ColorUtils;
import com.stevekung.skyblockcatia.utils.ColorUtils.RGB;
import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiRenderInfoCustomColorSettings extends GuiScreen
{
    private final GuiScreen parent;
    private GuiConfigTextFieldRowList optionsRowList;
    private static final List<SkyBlockcatiaSettings.Options> OPTIONS = new ArrayList<>();

    static
    {
        OPTIONS.add(SkyBlockcatiaSettings.Options.FPS_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.XYZ_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.BIOME_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.DIRECTION_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.PING_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.PING_TO_SECOND_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.SERVER_IP_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.EQUIPMENT_STATUS_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.ARROW_COUNT_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.REAL_TIME_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.GAME_TIME_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.GAME_WEATHER_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.MOON_PHASE_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.JUNGLE_AXE_COOLDOWN_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.GRAPPLING_HOOK_COOLDOWN_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.ZEALOT_RESPAWN_COOLDOWN_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.PLACED_SUMMONING_EYE_COLOR);

        OPTIONS.add(SkyBlockcatiaSettings.Options.FPS_VALUE_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.FPS_26_AND_40_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.FPS_LOW_25_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.XYZ_VALUE_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.DIRECTION_VALUE_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.BIOME_VALUE_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.PING_VALUE_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.PING_200_AND_300_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.PING_300_AND_500_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.PING_MAX_500_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.SERVER_IP_VALUE_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.REAL_TIME_HHMMSS_VALUE_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.REAL_TIME_DDMMYY_VALUE_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.GAME_TIME_VALUE_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.GAME_WEATHER_VALUE_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.MOON_PHASE_VALUE_COLOR);
        OPTIONS.add(SkyBlockcatiaSettings.Options.PLACED_SUMMONING_EYE_VALUE_COLOR);
    }

    public GuiRenderInfoCustomColorSettings(GuiScreen parent)
    {
        this.parent = parent;
    }

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(200, this.width / 2 + 5, this.height - 27, 100, 20, LangUtils.translate("gui.done")));
        this.buttonList.add(new GuiButton(201, this.width / 2 - 105, this.height - 27, 100, 20, LangUtils.translate("message.preview")));

        SkyBlockcatiaSettings.Options[] options = new SkyBlockcatiaSettings.Options[OPTIONS.size()];
        options = OPTIONS.toArray(options);
        this.optionsRowList = new GuiConfigTextFieldRowList(this.parent, this.width, this.height, 32, this.height - 32, 25, options);
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen()
    {
        this.optionsRowList.updateCursorCounter();
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
            SkyBlockcatiaSettings.instance.save();
            this.mc.displayGuiScreen(this.parent);
        }
        this.optionsRowList.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.optionsRowList.mouseClickedText(mouseX, mouseY, mouseButton);
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
            this.optionsRowList.saveCurrentValue();
            SkyBlockcatiaSettings.instance.save();

            if (button.id == 200)
            {
                this.mc.displayGuiScreen(this.parent);
            }
            if (button.id == 201)
            {
                this.mc.displayGuiScreen(new GuiRenderPreview(this, "render_info"));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.optionsRowList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, LangUtils.translate("extended_config.render_info_custom_color.title"), this.width / 2, 5, 16777215);

        for (int i = 0; i < this.optionsRowList.getSize(); ++i)
        {
            if (this.optionsRowList.selected == i)
            {
                SkyBlockcatiaSettings.Options options = this.optionsRowList.getListEntry(i).getTextField().getOption();
                RGB rgb = ColorUtils.stringToRGB(this.optionsRowList.getListEntry(i).getTextField().getText());
                this.drawCenteredString(this.fontRendererObj, LangUtils.translate("message.example") + ": " + rgb.toColoredFont() + options.getTranslation(), this.width / 2, 15, 16777215);
            }
            if (this.optionsRowList.selected == -1)
            {
                this.drawCenteredString(this.fontRendererObj, "Color Format is '255,255,255'", this.width / 2, 15, 16777215);
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}