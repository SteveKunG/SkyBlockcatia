package com.stevekung.skyblockcatia.gui.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.gui.config.widget.GuiConfigButtonRowList;
import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiOffsetSettings extends GuiScreen
{
    private final GuiScreen parent;
    private GuiConfigButtonRowList optionsRowList;
    private static final List<SkyBlockcatiaSettings.Options> OPTIONS = new ArrayList<>();

    static
    {
        OPTIONS.add(SkyBlockcatiaSettings.Options.ARMOR_HUD_Y);
        OPTIONS.add(SkyBlockcatiaSettings.Options.POTION_HUD_Y);
        OPTIONS.add(SkyBlockcatiaSettings.Options.MAXIMUM_POTION_DISPLAY);
        OPTIONS.add(SkyBlockcatiaSettings.Options.POTION_LENGTH_Y_OFFSET);
        OPTIONS.add(SkyBlockcatiaSettings.Options.POTION_LENGTH_Y_OFFSET_OVERLAP);
    }

    public GuiOffsetSettings(GuiScreen parent)
    {
        this.parent = parent;
    }

    @Override
    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(200, this.width / 2 + 5, this.height - 25, 100, 20, LangUtils.translate("gui.done")));
        this.buttonList.add(new GuiButton(201, this.width / 2 - 105, this.height - 25, 100, 20, LangUtils.translate("message.preview")));

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
            SkyBlockcatiaSettings.INSTANCE.save();

            if (button.id == 200)
            {
                this.mc.displayGuiScreen(this.parent);
            }
            if (button.id == 201)
            {
                this.mc.displayGuiScreen(new GuiRenderPreview(this, "offset"));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.optionsRowList.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, LangUtils.translate("extended_config.offset.title"), this.width / 2, 5, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}