package com.stevekung.skyblockcatia.gui.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiRenderInfoSettings extends GuiScreen
{
    private final GuiScreen parent;
    private GuiConfigButtonRowList optionsRowList;
    private static final List<ExtendedConfig.Options> OPTIONS = new ArrayList<>();

    static
    {
        OPTIONS.add(ExtendedConfig.Options.FPS);
        OPTIONS.add(ExtendedConfig.Options.XYZ);
        OPTIONS.add(ExtendedConfig.Options.DIRECTION);
        OPTIONS.add(ExtendedConfig.Options.BIOME);
        OPTIONS.add(ExtendedConfig.Options.PING);
        OPTIONS.add(ExtendedConfig.Options.PING_TO_SECOND);
        OPTIONS.add(ExtendedConfig.Options.SERVER_IP);
        OPTIONS.add(ExtendedConfig.Options.SERVER_IP_MC);
        OPTIONS.add(ExtendedConfig.Options.EQUIPMENT_HUD);
        OPTIONS.add(ExtendedConfig.Options.EQUIPMENT_ARMOR_ITEMS);
        OPTIONS.add(ExtendedConfig.Options.EQUIPMENT_HAND_ITEMS);
        OPTIONS.add(ExtendedConfig.Options.POTION_HUD);
        OPTIONS.add(ExtendedConfig.Options.REAL_TIME);
        OPTIONS.add(ExtendedConfig.Options.GAME_TIME);
        OPTIONS.add(ExtendedConfig.Options.GAME_WEATHER);
        OPTIONS.add(ExtendedConfig.Options.MOON_PHASE);
        OPTIONS.add(ExtendedConfig.Options.POTION_ICON);
        OPTIONS.add(ExtendedConfig.Options.ALTERNATE_POTION_COLOR);
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

        ExtendedConfig.Options[] options = new ExtendedConfig.Options[OPTIONS.size()];
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
            ExtendedConfig.instance.save();
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
                ExtendedConfig.instance.save();
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