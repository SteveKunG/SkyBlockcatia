package stevekung.mods.indicatia.utils;

import net.minecraft.util.EnumChatFormatting;

public enum MojangServerStatus
{
    ONLINE("Online", EnumChatFormatting.GREEN),
    UNSTABLE("Unstable", EnumChatFormatting.YELLOW),
    OFFLINE("Offline", EnumChatFormatting.DARK_RED),
    UNKNOWN("Unknown", EnumChatFormatting.RED);

    private String status;
    private EnumChatFormatting color;

    private MojangServerStatus(String status, EnumChatFormatting color)
    {
        this.status = status;
        this.color = color;
    }

    public String getStatus()
    {
        return this.status;
    }

    public EnumChatFormatting getColor()
    {
        return this.color;
    }

    public static MojangServerStatus get(String status)
    {
        if (status.equalsIgnoreCase("green"))
        {
            return MojangServerStatus.ONLINE;
        }
        else if (status.equalsIgnoreCase("yellow"))
        {
            return MojangServerStatus.UNSTABLE;
        }
        else if (status.equalsIgnoreCase("red"))
        {
            return MojangServerStatus.OFFLINE;
        }
        else
        {
            return MojangServerStatus.UNKNOWN;
        }
    }
}