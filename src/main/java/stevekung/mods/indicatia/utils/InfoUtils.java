package stevekung.mods.indicatia.utils;

import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.NetworkPlayerInfo;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.indicatia.event.HypixelEventHandler;
import stevekung.mods.indicatia.event.IndicatiaEventHandler;

public class InfoUtils
{
    public static final InfoUtils INSTANCE = new InfoUtils();
    private final java.util.Timer timer = new java.util.Timer();

    public int getPing()
    {
        NetworkPlayerInfo info = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(Minecraft.getMinecraft().thePlayer.getUniqueID());

        if (HypixelEventHandler.isSkyBlock)
        {
            return IndicatiaEventHandler.currentServerPing;
        }
        if (info != null)
        {
            if (info.getResponseTime() > 0)
            {
                return info.getResponseTime();
            }
            else
            {
                return IndicatiaEventHandler.currentServerPing;
            }
        }
        return 0;
    }

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

    public String getCurrentGameTime(long worldTicks)
    {
        int hours = (int)((worldTicks / 1000 + 6) % 24);
        int minutes = (int)(60 * (worldTicks % 1000) / 1000);
        String sminutes = "" + minutes;
        String shours = "" + hours;
        String ampm = hours >= 12 ? "PM" : "AM";

        if (hours <= 9)
        {
            shours = 0 + "" + hours;
        }
        if (minutes <= 9)
        {
            sminutes = 0 + "" + minutes;
        }
        return ColorUtils.stringToRGB(ExtendedConfig.instance.gameTimeColor).toColoredFont() + "Game: " + ColorUtils.stringToRGB(ExtendedConfig.instance.gameTimeValueColor).toColoredFont() + shours + ":" + sminutes + " " + ampm;
    }

    public String getMoonPhase(Minecraft mc)
    {
        int[] moonPhaseFactors = { 4, 3, 2, 1, 0, -1, -2, -3 };
        int phase = moonPhaseFactors[mc.theWorld.provider.getMoonPhase(mc.theWorld.getWorldTime())];
        String status;

        switch (phase)
        {
        case 4:
        default:
            status = "Full Moon";
            break;
        case 3:
            status = "Waning Gibbous";
            break;
        case 2:
            status = "Last Quarter";
            break;
        case 1:
            status = "Waning Crescent";
            break;
        case 0:
            status = "New Moon";
            break;
        case -1:
            status = "Waxing Crescent";
            break;
        case -2:
            status = "First Quarter";
            break;
        case -3:
            status = "Waxing Gibbous";
            break;
        }
        return ColorUtils.stringToRGB(ExtendedConfig.instance.moonPhaseColor).toColoredFont() + "Moon Phase: " + ColorUtils.stringToRGB(ExtendedConfig.instance.moonPhaseValueColor).toColoredFont() + status;
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