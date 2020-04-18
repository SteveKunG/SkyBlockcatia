package stevekung.mods.indicatia.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.indicatia.utils.ColorUtils;

public class GuiDropdownMinigames extends GuiButton
{
    private static final ResourceLocation texture = new ResourceLocation("indicatia:textures/gui/dropdown.png");
    public boolean dropdownClicked;
    public int selectedMinigame = -1;
    private final List<String> minigameLists;
    private final IDropboxCallback parentClass;
    private int displayLength;

    public GuiDropdownMinigames(IDropboxCallback parentClass, int x, int y, List<String> minigameLists)
    {
        super(0, x, y, 15, 15, "");
        this.parentClass = parentClass;
        this.minigameLists = minigameLists;

        if (this.minigameLists.size() <= 6)
        {
            this.displayLength = this.minigameLists.size();
        }
        else
        {
            this.displayLength = 6;
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        int hoverColor = 150;

        if (!this.dropdownClicked && this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height)
        {
            hoverColor = 180;
        }

        if (this.selectedMinigame == -1)
        {
            int initSelect = this.parentClass.getInitialSelection(this);
            int size = this.minigameLists.size() + ExtendedConfig.instance.hypixelMinigameScrollPos;

            if (initSelect > size || ExtendedConfig.instance.selectedHypixelMinigame > size || size == 1)
            {
                initSelect = 0;
                ExtendedConfig.instance.hypixelMinigameScrollPos = 0;
                ExtendedConfig.instance.selectedHypixelMinigame = 0;
            }
            this.selectedMinigame = initSelect;
        }

        if (this.visible)
        {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width - 15, this.yPosition + (this.dropdownClicked ? this.height * this.displayLength : this.height), ColorUtils.to32BitColor(255, 0, 0, 0));
            Gui.drawRect(this.xPosition + 1, this.yPosition + 1, this.xPosition + this.width - 16, this.yPosition + (this.dropdownClicked ? this.height * this.displayLength : this.height) - 1, ColorUtils.to32BitColor(255, hoverColor, hoverColor, hoverColor));
            Gui.drawRect(this.xPosition + this.width - 15, this.yPosition, this.xPosition + this.width - 1, this.yPosition + this.height, ColorUtils.to32BitColor(255, 0, 0, 0));
            Gui.drawRect(this.xPosition + this.width - 15, this.yPosition + 1, this.xPosition + this.width - 2, this.yPosition + this.height - 1, ColorUtils.to32BitColor(255, 150, 150, 150));

            if (this.displayLength > 1 && this.dropdownClicked && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width - 16 && mouseY < this.yPosition + this.height * this.displayLength)
            {
                int hoverPos = (mouseY - this.yPosition) / this.height;
                Gui.drawRect(this.xPosition + 1, this.yPosition + this.height * hoverPos + 1, this.xPosition + this.width - 16, this.yPosition + this.height * (hoverPos + 1) - 1, ColorUtils.to32BitColor(255, 180, 180, 180));
            }

            for (int i = 0; i + ExtendedConfig.instance.hypixelMinigameScrollPos < this.minigameLists.size() && i < this.displayLength; ++i)
            {
                String minigames = this.minigameLists.get(i + ExtendedConfig.instance.hypixelMinigameScrollPos);

                if (minigames != null)
                {
                    if (this.dropdownClicked)
                    {
                        mc.fontRendererObj.drawStringWithShadow(minigames, this.xPosition + this.width / 2 - 7 - mc.fontRendererObj.getStringWidth(minigames) / 2, this.yPosition + (this.height - 6) / 2 + this.height * i, ColorUtils.to32BitColor(255, 255, 255, 255));
                    }
                    else
                    {
                        mc.fontRendererObj.drawStringWithShadow(this.minigameLists.get(this.selectedMinigame), this.xPosition + this.width / 2 - 7 - mc.fontRendererObj.getStringWidth(this.minigameLists.get(this.selectedMinigame)) / 2, this.yPosition + (this.height - 6) / 2, ColorUtils.to32BitColor(255, 255, 255, 255));
                    }
                }
            }
            mc.renderEngine.bindTexture(GuiDropdownMinigames.texture);
            Gui.drawModalRectWithCustomSizedTexture(this.xPosition + this.width - 12, this.yPosition + 5, 0, 0, 7, 4, 7, 4);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
        if (this.displayLength == 1)
        {
            return false;
        }
        if (!this.dropdownClicked)
        {
            if (this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height)
            {
                this.dropdownClicked = true;
                return true;
            }
        }
        else
        {
            if (this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width - 16 && mouseY < this.yPosition + this.height * this.displayLength)
            {
                int optionClicked = (mouseY - this.yPosition) / this.height + ExtendedConfig.instance.hypixelMinigameScrollPos;
                this.selectedMinigame = optionClicked % this.minigameLists.size();
                this.dropdownClicked = false;
                this.parentClass.onSelectionChanged(this, this.selectedMinigame);
                return true;
            }
            else
            {
                this.dropdownClicked = false;
                return false;
            }
        }
        return false;
    }

    public void scroll(int amount)
    {
        ExtendedConfig.instance.hypixelMinigameScrollPos += amount;
        int i = this.minigameLists.size();

        if (ExtendedConfig.instance.hypixelMinigameScrollPos > i - this.displayLength || ExtendedConfig.instance.hypixelMinigameScrollPos <= 0)
        {
            ExtendedConfig.instance.hypixelMinigameScrollPos = 0;
        }
    }

    public boolean isHoverDropdown(int mouseX, int mouseY)
    {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width - 16 && mouseY < this.yPosition + this.height * this.displayLength;
    }

    public interface IDropboxCallback
    {
        void onSelectionChanged(GuiDropdownMinigames dropdown, int selection);

        int getInitialSelection(GuiDropdownMinigames dropdown);
    }
}