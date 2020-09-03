package com.stevekung.skyblockcatia.gui.config;

import java.util.ArrayList;
import java.util.List;

import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.config.ExtendedConfig.Options;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;

public class GuiConfigButtonRowList extends GuiListExtended
{
    private final List<GuiConfigButtonRowList.Row> options = new ArrayList<>();

    public GuiConfigButtonRowList(int width, int height, int top, int bottom, int slotHeight, ExtendedConfig.Options[] options, boolean singleRow)
    {
        super(Minecraft.getMinecraft(), width, height, top, bottom, slotHeight);
        this.field_148163_i = false;

        if (singleRow)
        {
            for (Options exoptions : options)
            {
                GuiButton button = this.createButton(width / 2 - 100, 0, 200, exoptions);
                this.options.add(new GuiConfigButtonRowList.Row(button, null));
            }
        }
        else
        {
            for (int i = 0; i < options.length; i += 2)
            {
                ExtendedConfig.Options exoptions = options[i];
                ExtendedConfig.Options exoptions1 = i < options.length - 1 ? options[i + 1] : null;
                GuiButton button = this.createButton(width / 2 - 165, 0, 160, exoptions);
                GuiButton button1 = this.createButton(width / 2 - 160 + 160, 0, 160, exoptions1);
                this.options.add(new GuiConfigButtonRowList.Row(button, button1));
            }
        }
    }

    private GuiButton createButton(int x, int y, int width, ExtendedConfig.Options options)
    {
        if (options == null)
        {
            return null;
        }
        else
        {
            int i = options.getOrdinal();
            return options.isFloat() ? new GuiConfigSlider(i, x, y, width, options) : new GuiConfigButton(i, x, y, width, options, ExtendedConfig.instance.getKeyBinding(options));
        }
    }

    @Override
    public GuiConfigButtonRowList.Row getListEntry(int index)
    {
        return this.options.get(index);
    }

    @Override
    protected int getSize()
    {
        return this.options.size();
    }

    @Override
    public int getListWidth()
    {
        return 400;
    }

    @Override
    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 40;
    }

    public static class Row implements GuiListExtended.IGuiListEntry
    {
        private final Minecraft mc = Minecraft.getMinecraft();
        private final GuiButton buttonA;
        private final GuiButton buttonB;

        public Row(GuiButton buttonA, GuiButton buttonB)
        {
            this.buttonA = buttonA;
            this.buttonB = buttonB;
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected)
        {
            if (this.buttonA != null)
            {
                this.buttonA.yPosition = y;
                this.buttonA.drawButton(this.mc, mouseX, mouseY);
            }
            if (this.buttonB != null)
            {
                this.buttonB.yPosition = y;
                this.buttonB.drawButton(this.mc, mouseX, mouseY);
            }
        }

        @Override
        public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY)
        {
            if (this.buttonA.mousePressed(this.mc, mouseX, mouseY))
            {
                if (this.buttonA instanceof GuiConfigButton)
                {
                    if (mouseEvent == 0)
                    {
                        ExtendedConfig.instance.setOptionValue(((GuiConfigButton)this.buttonA).getOption(), 1);
                        this.buttonA.displayString = ExtendedConfig.instance.getKeyBinding(ExtendedConfig.Options.byOrdinal(this.buttonA.id));
                        this.buttonA.playPressSound(this.mc.getSoundHandler());
                    }
                }
                if (this.buttonA instanceof GuiConfigSlider)
                {
                    this.buttonA.playPressSound(this.mc.getSoundHandler());
                }
                return true;
            }
            else if (this.buttonB != null && this.buttonB.mousePressed(this.mc, mouseX, mouseY))
            {
                if (this.buttonB instanceof GuiConfigButton)
                {
                    if (mouseEvent == 0)
                    {
                        ExtendedConfig.instance.setOptionValue(((GuiConfigButton)this.buttonB).getOption(), 1);
                        this.buttonB.displayString = ExtendedConfig.instance.getKeyBinding(ExtendedConfig.Options.byOrdinal(this.buttonB.id));
                        this.buttonB.playPressSound(this.mc.getSoundHandler());
                    }
                }
                if (this.buttonB instanceof GuiConfigSlider)
                {
                    this.buttonB.playPressSound(this.mc.getSoundHandler());
                }
                return true;
            }
            else
            {
                return false;
            }
        }

        @Override
        public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY)
        {
            if (this.buttonA != null)
            {
                this.buttonA.mouseReleased(x, y);
            }
            if (this.buttonB != null)
            {
                this.buttonB.mouseReleased(x, y);
            }
        }

        @Override
        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {}
    }
}