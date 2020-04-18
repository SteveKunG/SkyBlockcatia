package stevekung.mods.indicatia.gui.config;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StringUtils;
import stevekung.mods.indicatia.config.EnumEquipment;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.indicatia.renderer.HUDInfo;
import stevekung.mods.indicatia.utils.InfoUtils;

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
            HUDInfo.renderPotionHUD(this.mc);

            if (EnumEquipment.Direction.getById(ExtendedConfig.instance.equipmentDirection).equalsIgnoreCase("vertical"))
            {
                HUDInfo.renderVerticalEquippedItems(this.mc);
            }
            else
            {
                HUDInfo.renderHorizontalEquippedItems(this.mc);
            }
        }
        if (this.type.equals("render_info"))
        {
            List<String> leftInfo = new LinkedList<>();
            List<String> rightInfo = new LinkedList<>();
            HUDInfo.renderVerticalEquippedItems(this.mc);

            // left info
            if (!this.mc.isSingleplayer())
            {
                leftInfo.add(HUDInfo.getPing());
                leftInfo.add(HUDInfo.getPingToSecond());

                if (this.mc.getCurrentServerData() != null)
                {
                    leftInfo.add(HUDInfo.getServerIP(this.mc));
                }
            }

            leftInfo.add(HUDInfo.getFPS());
            leftInfo.add(HUDInfo.getXYZ(this.mc));

            if (this.mc.thePlayer.dimension == -1)
            {
                leftInfo.add(HUDInfo.getOverworldXYZFromNether(this.mc));
            }

            leftInfo.add(HUDInfo.renderDirection(this.mc));
            leftInfo.add(HUDInfo.getBiome(this.mc));

            // right info
            rightInfo.add(HUDInfo.getCurrentTime());
            rightInfo.add(HUDInfo.getCurrentGameTime(this.mc));

            if (this.mc.theWorld.isRaining())
            {
                rightInfo.add(HUDInfo.getGameWeather(this.mc));
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
                    this.fontRendererObj.drawString(string, ExtendedConfig.instance.swapRenderInfo ? xOffset : 3.0625F, yOffset, 16777215, true);
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
                    this.fontRendererObj.drawString(string, ExtendedConfig.instance.swapRenderInfo ? 3.0625F : xOffset, yOffset, 16777215, true);
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