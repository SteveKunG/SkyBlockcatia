package stevekung.mods.indicatia.config;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import stevekung.mods.indicatia.core.IndicatiaMod;
import stevekung.mods.indicatia.utils.LangUtils;

public class ConfigGuiFactory implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft mc) {}

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass()
    {
        return GuiMainConfig.class;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element)
    {
        return null;
    }

    public static class GuiMainConfig extends GuiConfig
    {
        public GuiMainConfig(GuiScreen gui)
        {
            super(gui, ConfigManagerIN.getConfigElements(), IndicatiaMod.MOD_ID, false, false, LangUtils.translate("gui.config.indicatia.name"));
        }
    }
}