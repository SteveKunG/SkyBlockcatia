package stevekung.mods.indicatia.utils;

import com.google.gson.JsonParseException;

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class JsonUtils
{
    public static IChatComponent rawTextToJson(String raw)
    {
        IChatComponent json = create("Cannot parse json format! ").setChatStyle(red());

        try
        {
            json = IChatComponent.Serializer.jsonToComponent("[{" + raw + "}]");
        }
        catch (JsonParseException e)
        {
            if (Minecraft.getMinecraft().thePlayer.ticksExisted % 300 == 0)
            {
                Minecraft.getMinecraft().thePlayer.addChatMessage(create(e.getMessage()).setChatStyle(red()));
            }
        }
        return json;
    }

    public static ChatComponentText create(String text)
    {
        return new ChatComponentText(text);
    }

    public static ChatStyle style()
    {
        return new ChatStyle();
    }

    public static ClickEvent click(ClickEvent.Action action, String url)
    {
        return new ClickEvent(action, url);
    }

    public static HoverEvent hover(HoverEvent.Action action, IChatComponent text)
    {
        return new HoverEvent(action, text);
    }

    public static ChatStyle black()
    {
        return style().setColor(EnumChatFormatting.BLACK);
    }

    public static ChatStyle darkBlue()
    {
        return style().setColor(EnumChatFormatting.DARK_BLUE);
    }

    public static ChatStyle darkGreen()
    {
        return style().setColor(EnumChatFormatting.DARK_GREEN);
    }

    public static ChatStyle darkAqua()
    {
        return style().setColor(EnumChatFormatting.DARK_AQUA);
    }

    public static ChatStyle darkRed()
    {
        return style().setColor(EnumChatFormatting.DARK_RED);
    }

    public static ChatStyle darkPurple()
    {
        return style().setColor(EnumChatFormatting.DARK_PURPLE);
    }

    public static ChatStyle gold()
    {
        return style().setColor(EnumChatFormatting.GOLD);
    }

    public static ChatStyle gray()
    {
        return style().setColor(EnumChatFormatting.GRAY);
    }

    public static ChatStyle darkGray()
    {
        return style().setColor(EnumChatFormatting.DARK_GRAY);
    }

    public static ChatStyle blue()
    {
        return style().setColor(EnumChatFormatting.BLUE);
    }

    public static ChatStyle green()
    {
        return style().setColor(EnumChatFormatting.GREEN);
    }

    public static ChatStyle aqua()
    {
        return style().setColor(EnumChatFormatting.AQUA);
    }

    public static ChatStyle red()
    {
        return style().setColor(EnumChatFormatting.RED);
    }

    public static ChatStyle lightPurple()
    {
        return style().setColor(EnumChatFormatting.LIGHT_PURPLE);
    }

    public static ChatStyle yellow()
    {
        return style().setColor(EnumChatFormatting.YELLOW);
    }

    public static ChatStyle white()
    {
        return style().setColor(EnumChatFormatting.WHITE);
    }
}