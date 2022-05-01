package com.stevekung.skyblockcatia.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.input.Mouse;

import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.gui.widget.button.GuiButtonCustomize;
import com.stevekung.skyblockcatia.gui.widget.button.GuiDropdownMinigamesButton;
import com.stevekung.skyblockcatia.gui.widget.button.GuiDropdownMinigamesButton.IDropboxCallback;
import com.stevekung.skyblockcatia.hud.InfoUtils;
import com.stevekung.skyblockcatia.utils.IGuiChat;
import com.stevekung.skyblockcatia.utils.MinigameCommand;
import com.stevekung.skyblockcatia.utils.MinigameData;
import com.stevekung.skyblockcatia.utils.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GuiChatExtended implements IGuiChat, IDropboxCallback
{
    private GuiDropdownMinigamesButton dropdown;
    private int prevSelect = -1;
    private final Minecraft mc;

    public GuiChatExtended()
    {
        this.mc = Minecraft.getMinecraft();
    }

    @Override
    public void initGui(List<GuiButton> buttonList, int width, int height)
    {
        this.updateButton(buttonList, width, height);
    }

    @Override
    public void drawScreen(List<GuiButton> buttonList, int mouseX, int mouseY, float partialTicks)
    {
        buttonList.forEach(button ->
        {
            if (button instanceof GuiButtonCustomize)
            {
                GuiButtonCustomize customButton = (GuiButtonCustomize)button;
                boolean isHover = mouseX >= customButton.xPosition && mouseY >= customButton.yPosition && mouseX < customButton.xPosition + customButton.width && mouseY < customButton.yPosition + customButton.height;

                if (isHover && customButton.visible)
                {
                    GuiUtils.drawHoveringText(Collections.singletonList(customButton.getTooltips()), mouseX, mouseY, this.mc.currentScreen.width, this.mc.currentScreen.height, -1, this.mc.fontRendererObj);
                    GlStateManager.disableLighting();
                }
            }
        });
    }

    @Override
    public void updateScreen(List<GuiButton> buttonList, int width, int height)
    {
        if (InfoUtils.INSTANCE.isHypixel())
        {
            if (this.prevSelect != SkyBlockcatiaSettings.INSTANCE.selectedHypixelMinigame)
            {
                this.updateButton(buttonList, width, height);
                this.prevSelect = SkyBlockcatiaSettings.INSTANCE.selectedHypixelMinigame;
            }

            if (this.dropdown != null)
            {
                boolean clicked = !this.dropdown.dropdownClicked;

                buttonList.forEach(button ->
                {
                    if (button instanceof GuiButtonCustomize)
                    {
                        GuiButtonCustomize buttonCustom = (GuiButtonCustomize)button;
                        buttonCustom.visible = clicked;
                    }
                });
            }
        }
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        if (player == null || !(mc.currentScreen instanceof GuiChat))
        {
            return;
        }

        if (button instanceof GuiButtonCustomize)
        {
            GuiButtonCustomize buttomCustom = (GuiButtonCustomize)button;

            if (button.id == buttomCustom.id)
            {
                player.sendChatMessage(buttomCustom.command);
            }
        }
    }

    @Override
    public void onGuiClosed()
    {
        SkyBlockcatiaSettings.INSTANCE.save();
    }

    @Override
    public void handleMouseInput(int width, int height)
    {
        Minecraft mc = Minecraft.getMinecraft();
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;

        if (this.dropdown != null && this.dropdown.dropdownClicked && this.dropdown.isHoverDropdown(mouseX, mouseY))
        {
            int i = Mouse.getEventDWheel();

            if (i != 0)
            {
                if (i > 1)
                {
                    i = -1;
                }
                if (i < -1)
                {
                    i = 1;
                }
                if (GuiScreen.isCtrlKeyDown())
                {
                    i *= 7;
                }
                this.dropdown.scroll(i);
            }
        }
    }

    @Override
    public void onSelectionChanged(GuiDropdownMinigamesButton dropdown, int selection)
    {
        SkyBlockcatiaSettings.INSTANCE.selectedHypixelMinigame = selection;
        SkyBlockcatiaSettings.INSTANCE.save();
    }

    @Override
    public int getInitialSelection(GuiDropdownMinigamesButton dropdown)
    {
        return SkyBlockcatiaSettings.INSTANCE.selectedHypixelMinigame;
    }

    private void updateButton(List<GuiButton> buttonList, int width, int height)
    {
        Minecraft mc = Minecraft.getMinecraft();
        buttonList.clear();

        if (InfoUtils.INSTANCE.isHypixel())
        {
            if (!SkyBlockcatiaConfig.enableShortcutGameButton || this.mc.gameSettings.showDebugInfo)
            {
                return;
            }

            List<String> list = new ArrayList<>();

            for (MinigameData data : MinigameData.getMinigameData())
            {
                list.add(data.getName());
            }

            String max = Collections.max(list, Comparator.comparing(String::length));
            int length = mc.fontRendererObj.getStringWidth(max) + 32;

            buttonList.add(this.dropdown = new GuiDropdownMinigamesButton(this, width - length, 2, list));
            this.dropdown.width = length;
            this.prevSelect = SkyBlockcatiaSettings.INSTANCE.selectedHypixelMinigame;

            List<GuiButtonCustomize> gameBtn = new ArrayList<>();
            int xPos2 = width - 99;

            if (this.prevSelect > list.size())
            {
                this.prevSelect = 0;
                SkyBlockcatiaSettings.INSTANCE.selectedHypixelMinigame = 0;
            }

            for (MinigameData data : MinigameData.getMinigameData())
            {
                for (MinigameCommand command : data.getCommands())
                {
                    if (data.getName().equals(list.get(this.prevSelect)))
                    {
                        ItemStack skull = null;

                        if (!StringUtils.isNullOrEmpty(command.getUUID()))
                        {
                            String texture = command.getTexture();
                            skull = RenderUtils.getSkullItemStack(command.getUUID(), texture);
                        }
                        gameBtn.add(new GuiButtonCustomize(width, command.getName(), command.getCommand(), command.isMinigame(), skull));
                    }
                }
            }

            for (int i = 0; i < gameBtn.size(); i++)
            {
                GuiButtonCustomize button = gameBtn.get(i);

                if (i >= 6 && i <= 10)
                {
                    button.xPosition = xPos2 - 136;
                    button.yPosition = 41;
                }
                else if (i >= 11 && i <= 15)
                {
                    button.xPosition = xPos2 - 241;
                    button.yPosition = 62;
                }
                else if (i >= 16 && i <= 20)
                {
                    button.xPosition = xPos2 - 346;
                    button.yPosition = 83;
                }
                else if (i >= 21)
                {
                    button.xPosition = xPos2 - 451;
                    button.yPosition = 104;
                }
                button.xPosition += 21 * i;
                buttonList.add(button);
            }
        }

        for (GuiButton button : buttonList)
        {
            if (!button.getClass().equals(GuiDropdownMinigamesButton.class) && !(button.id >= 0 && button.id <= 203))
            {
                button.visible = false;
            }
        }
    }
}