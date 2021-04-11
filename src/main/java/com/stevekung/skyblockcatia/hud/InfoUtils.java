package com.stevekung.skyblockcatia.hud;

import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stevekung.skyblockcatia.utils.JsonUtils;
import com.stevekung.skyblockcatia.utils.LangUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public class InfoUtils
{
    public static final InfoUtils INSTANCE = new InfoUtils();
    private final java.util.Timer timer = new java.util.Timer();

    public boolean isHypixel()
    {
        ServerData server = Minecraft.getMinecraft().getCurrentServerData();

        if (server != null)
        {
            Pattern pattern = Pattern.compile("^(?:(?:(?:.*\\.)?hypixel\\.net)|(?:209\\.222\\.115\\.\\d{1,3}))(?::\\d{1,5})?$", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(server.serverIP);
            return matcher.find();
        }
        return false;
    }

    public int parseInt(String input, String type)
    {
        try
        {
            return Integer.parseInt(input);
        }
        catch (NumberFormatException e)
        {
            Minecraft.getMinecraft().thePlayer.addChatMessage(JsonUtils.create(LangUtils.translate("commands.generic.num.invalid", input) + " in " + type + " setting").setChatStyle(JsonUtils.red()));
            return 0;
        }
    }

    public TimerTask schedule(Runnable runnable, long delay)
    {
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                runnable.run();
            }
        };
        this.timer.schedule(task, delay);
        return task;
    }
}