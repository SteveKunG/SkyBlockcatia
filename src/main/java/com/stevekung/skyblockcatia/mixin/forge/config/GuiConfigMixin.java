package com.stevekung.skyblockcatia.mixin.forge.config;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiMessageDialog;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

@Mixin(GuiConfig.class)
public class GuiConfigMixin extends GuiScreen
{
    @Override
    protected void keyTyped(char eventChar, int eventKey)
    {
        GuiConfig config = (GuiConfig)(Object)this;

        if (eventKey == Keyboard.KEY_ESCAPE)
        {
            try
            {
                if ((config.configID != null || config.parentScreen == null || !(config.parentScreen instanceof GuiConfig)) && config.entryList.hasChangedEntry(true))
                {
                    boolean requiresMcRestart = config.entryList.saveConfigElements();

                    if (Loader.isModLoaded(config.modID))
                    {
                        ConfigChangedEvent event = new OnConfigChangedEvent(config.modID, config.configID, config.isWorldRunning, requiresMcRestart);
                        MinecraftForge.EVENT_BUS.post(event);

                        if (!event.getResult().equals(Result.DENY))
                        {
                            MinecraftForge.EVENT_BUS.post(new PostConfigChangedEvent(config.modID, config.configID, config.isWorldRunning, requiresMcRestart));
                        }

                        if (requiresMcRestart)
                        {
                            config.mc.displayGuiScreen(new GuiMessageDialog(config.parentScreen, "fml.configgui.gameRestartTitle", new ChatComponentText(I18n.format("fml.configgui.gameRestartRequired")), "fml.configgui.confirmRestartMessage"));
                        }
                    }
                }
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
            this.mc.displayGuiScreen(config.parentScreen);
        }
        else
        {
            config.entryList.keyTyped(eventChar, eventKey);
        }
    }
}