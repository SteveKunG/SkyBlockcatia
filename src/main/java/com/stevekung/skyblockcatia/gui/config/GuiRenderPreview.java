package com.stevekung.skyblockcatia.gui.config;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.stevekung.skyblockcatia.config.Equipments;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.hud.InfoOverlays;
import com.stevekung.skyblockcatia.hud.InfoUtils;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StringUtils;

public class GuiRenderPreview extends GuiScreen
{
    private final GuiScreen parent;
    private final String type;

    public GuiRenderPreview(GuiScreen parent, String type)
    {
        this.parent = parent;
        this.type = type;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1)
        {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.type.equals("offset"))
        {
            InfoOverlays.renderPotionHUD(this.mc);

            if (Equipments.Direction.byId(SkyBlockcatiaSettings.instance.equipmentDirection) == Equipments.Direction.VERTICAL)
            {
                InfoOverlays.renderVerticalEquippedItems(this.mc);
            }
            else
            {
                InfoOverlays.renderHorizontalEquippedItems(this.mc);
            }
        }
        if (this.type.equals("render_info"))
        {
            List<String> leftInfo = new LinkedList<>();
            List<String> rightInfo = new LinkedList<>();
            InfoOverlays.renderVerticalEquippedItems(this.mc);

            // left info
            if (!this.mc.isSingleplayer())
            {
                leftInfo.add(InfoOverlays.getPing());
                leftInfo.add(InfoOverlays.getPingToSecond());

                if (this.mc.getCurrentServerData() != null)
                {
                    leftInfo.add(InfoOverlays.getServerIP(this.mc));
                }
            }

            leftInfo.add(InfoOverlays.getFPS());
            leftInfo.add(InfoOverlays.getXYZ(this.mc));

            if (this.mc.thePlayer.dimension == -1)
            {
                leftInfo.add(InfoOverlays.getOverworldXYZFromNether(this.mc));
            }

            leftInfo.add(InfoOverlays.renderDirection(this.mc));
            leftInfo.add(InfoOverlays.getBiome(this.mc));

            // right info
            rightInfo.add(InfoOverlays.getCurrentTime());
            rightInfo.add(InfoOverlays.getCurrentGameTime(this.mc));

            if (this.mc.theWorld.isRaining())
            {
                rightInfo.add(InfoOverlays.getGameWeather(this.mc));
            }

            rightInfo.add(InfoUtils.INSTANCE.getMoonPhase(this.mc));

            // left info
            for (int i = 0; i < leftInfo.size(); ++i)
            {
                ScaledResolution res = new ScaledResolution(this.mc);
                String string = leftInfo.get(i);
                float fontHeight = this.fontRendererObj.FONT_HEIGHT + 1;
                float yOffset = 3 + fontHeight * i;
                float xOffset = res.getScaledWidth() - 2 - this.fontRendererObj.getStringWidth(string);

                if (!StringUtils.isNullOrEmpty(string))
                {
                    this.fontRendererObj.drawString(string, SkyBlockcatiaSettings.instance.swapRenderInfo ? xOffset : 3.0625F, yOffset, 16777215, true);
                }
            }

            // right info
            for (int i = 0; i < rightInfo.size(); ++i)
            {
                ScaledResolution res = new ScaledResolution(this.mc);
                String string = rightInfo.get(i);
                float fontHeight = this.fontRendererObj.FONT_HEIGHT + 1;
                float yOffset = 3 + fontHeight * i;
                float xOffset = res.getScaledWidth() - 2 - this.fontRendererObj.getStringWidth(string);

                if (!StringUtils.isNullOrEmpty(string))
                {
                    this.fontRendererObj.drawString(string, SkyBlockcatiaSettings.instance.swapRenderInfo ? 3.0625F : xOffset, yOffset, 16777215, true);
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return true;
    }
}