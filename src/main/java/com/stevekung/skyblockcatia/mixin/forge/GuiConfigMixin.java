package com.stevekung.skyblockcatia.mixin.forge;

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
    private final GuiConfig that = (GuiConfig) (Object) this;

    @Override
    protected void keyTyped(char eventChar, int eventKey)
    {
        if (eventKey == Keyboard.KEY_ESCAPE)
        {
            try
            {
                if ((this.that.configID != null || this.that.parentScreen == null || !(this.that.parentScreen instanceof GuiConfig)) && this.that.entryList.hasChangedEntry(true))
                {
                    boolean requiresMcRestart = this.that.entryList.saveConfigElements();

                    if (Loader.isModLoaded(this.that.modID))
                    {
                        ConfigChangedEvent event = new OnConfigChangedEvent(this.that.modID, this.that.configID, this.that.isWorldRunning, requiresMcRestart);
                        MinecraftForge.EVENT_BUS.post(event);

                        if (!event.getResult().equals(Result.DENY))
                        {
                            MinecraftForge.EVENT_BUS.post(new PostConfigChangedEvent(this.that.modID, this.that.configID, this.that.isWorldRunning, requiresMcRestart));
                        }

                        if (requiresMcRestart)
                        {
                            this.that.mc.displayGuiScreen(new GuiMessageDialog(this.that.parentScreen, "fml.configgui.gameRestartTitle", new ChatComponentText(I18n.format("fml.configgui.gameRestartRequired")), "fml.configgui.confirmRestartMessage"));
                        }
                    }
                }
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
            this.mc.displayGuiScreen(this.that.parentScreen);
        }
        else
        {
            this.that.entryList.keyTyped(eventChar, eventKey);
        }
    }
}