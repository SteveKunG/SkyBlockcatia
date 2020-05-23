package com.stevekung.skyblockcatia.gui.config;

import java.io.IOException;

import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiCustomColorSettings extends GuiScreen
{
    private final GuiScreen parent;

    public GuiCustomColorSettings(GuiScreen parent)
    {
        this.parent = parent;
    }

    @Override
    public void initGui()
    {
        this.buttonList.add(new GuiButton(100, this.width / 2 - 155, this.height / 6 - 12, 150, 20, LangUtils.translate("extended_config.render_info_custom_color.title")));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, LangUtils.translate("gui.done")));
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
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 100)
            {
                this.mc.displayGuiScreen(new GuiRenderInfoCustomColorSettings(this));
            }
            if (button.id == 200)
            {
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, LangUtils.translate("extended_config.custom_color.title"), this.width / 2, 15, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}