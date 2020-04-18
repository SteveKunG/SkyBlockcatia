package stevekung.mods.indicatia.gui.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.indicatia.utils.ColorUtils;
import stevekung.mods.indicatia.utils.ColorUtils.RGB;
import stevekung.mods.indicatia.utils.LangUtils;

public class GuiRenderInfoCustomColorSettings extends GuiScreen
{
    private final GuiScreen parent;
    private GuiConfigTextFieldRowList optionsRowList;
    private static final List<ExtendedConfig.Options> OPTIONS = new ArrayList<>();

    static
    {
        OPTIONS.add(ExtendedConfig.Options.FPS_COLOR);
        OPTIONS.add(ExtendedConfig.Options.XYZ_COLOR);
        OPTIONS.add(ExtendedConfig.Options.BIOME_COLOR);
        OPTIONS.add(ExtendedConfig.Options.DIRECTION_COLOR);
        OPTIONS.add(ExtendedConfig.Options.PING_COLOR);
        OPTIONS.add(ExtendedConfig.Options.PING_TO_SECOND_COLOR);
        OPTIONS.add(ExtendedConfig.Options.SERVER_IP_COLOR);
        OPTIONS.add(ExtendedConfig.Options.EQUIPMENT_STATUS_COLOR);
        OPTIONS.add(ExtendedConfig.Options.ARROW_COUNT_COLOR);
        OPTIONS.add(ExtendedConfig.Options.BAIT_COUNT_COLOR);
        OPTIONS.add(ExtendedConfig.Options.REAL_TIME_COLOR);
        OPTIONS.add(ExtendedConfig.Options.GAME_TIME_COLOR);
        OPTIONS.add(ExtendedConfig.Options.GAME_WEATHER_COLOR);
        OPTIONS.add(ExtendedConfig.Options.MOON_PHASE_COLOR);
        OPTIONS.add(ExtendedConfig.Options.JUNGLE_AXE_COOLDOWN_COLOR);
        OPTIONS.add(ExtendedConfig.Options.GRAPPLING_HOOK_COOLDOWN_COLOR);
        OPTIONS.add(ExtendedConfig.Options.ZEALOT_RESPAWN_COOLDOWN_COLOR);
        OPTIONS.add(ExtendedConfig.Options.PLACED_SUMMONING_EYE_COLOR);

        OPTIONS.add(ExtendedConfig.Options.FPS_VALUE_COLOR);
        OPTIONS.add(ExtendedConfig.Options.FPS_26_AND_40_COLOR);
        OPTIONS.add(ExtendedConfig.Options.FPS_LOW_25_COLOR);
        OPTIONS.add(ExtendedConfig.Options.XYZ_VALUE_COLOR);
        OPTIONS.add(ExtendedConfig.Options.DIRECTION_VALUE_COLOR);
        OPTIONS.add(ExtendedConfig.Options.BIOME_VALUE_COLOR);
        OPTIONS.add(ExtendedConfig.Options.PING_VALUE_COLOR);
        OPTIONS.add(ExtendedConfig.Options.PING_200_AND_300_COLOR);
        OPTIONS.add(ExtendedConfig.Options.PING_300_AND_500_COLOR);
        OPTIONS.add(ExtendedConfig.Options.PING_MAX_500_COLOR);
        OPTIONS.add(ExtendedConfig.Options.SERVER_IP_VALUE_COLOR);
        OPTIONS.add(ExtendedConfig.Options.REAL_TIME_HHMMSS_VALUE_COLOR);
        OPTIONS.add(ExtendedConfig.Options.REAL_TIME_DDMMYY_VALUE_COLOR);
        OPTIONS.add(ExtendedConfig.Options.GAME_TIME_VALUE_COLOR);
        OPTIONS.add(ExtendedConfig.Options.GAME_WEATHER_VALUE_COLOR);
        OPTIONS.add(ExtendedConfig.Options.MOON_PHASE_VALUE_COLOR);
        OPTIONS.add(ExtendedConfig.Options.PLACED_SUMMONING_EYE_VALUE_COLOR);
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

        ExtendedConfig.Options[] options = new ExtendedConfig.Options[OPTIONS.size()];
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
            ExtendedConfig.instance.save();
        }
        this.optionsRowList.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
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
            ExtendedConfig.instance.save();

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
                ExtendedConfig.Options options = this.optionsRowList.getListEntry(i).getTextField().getOption();
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