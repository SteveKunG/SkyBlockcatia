package com.stevekung.skyblockcatia.utils;

import java.util.Map;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.versioning.ComparableVersion;

public class VersionChecker
{
    private final Object mod;
    private final String modName;
    private final String url;
    private String latestVersion;
    private boolean failed;

    public VersionChecker(Object mod, String modName, String url)
    {
        this.mod = mod;
        this.modName = modName;
        this.url = url;
    }

    public void startCheck()
    {
        ForgeVersion.CheckResult result = ForgeVersion.getResult(Loader.instance().getModObjectList().inverse().get(this.mod));

        for (Map.Entry<ComparableVersion, String> entry : result.changes.entrySet())
        {
            ComparableVersion version = entry.getKey();

            if (result.status == ForgeVersion.Status.OUTDATED)
            {
                this.latestVersion = version.toString();
            }
        }
    }

    public void startCheckIfFailed()
    {
        ForgeVersion.CheckResult result = ForgeVersion.getResult(Loader.instance().getModObjectList().inverse().get(this.mod));
        this.failed = result.status == ForgeVersion.Status.FAILED || result.status == ForgeVersion.Status.PENDING;
    }

    public void printInfo(EntityPlayerSP player)
    {
        if (this.failed)
        {
            player.addChatMessage(JsonUtils.create("Unable to check latest version of " + this.formatText(EnumChatFormatting.DARK_RED, this.modName + "!") + "!, Please check your internet connection.").setChatStyle(JsonUtils.red().setBold(true)));
            return;
        }
        if (this.latestVersion != null)
        {
            String text = String.format("New version of %s is available %s for %s", this.formatText(EnumChatFormatting.AQUA, this.modName), this.formatText(EnumChatFormatting.GREEN, "v" + this.latestVersion), this.formatText(EnumChatFormatting.BLUE, "Minecraft " + ForgeVersion.mcVersion));
            player.addChatMessage(JsonUtils.create(text));
            player.addChatMessage(JsonUtils.create("Download Link ").setChatStyle(JsonUtils.style().setColor(EnumChatFormatting.YELLOW)).appendSibling(JsonUtils.create("[CLICK HERE]").setChatStyle(JsonUtils.style().setColor(EnumChatFormatting.RED).setChatHoverEvent(JsonUtils.hover(HoverEvent.Action.SHOW_TEXT, JsonUtils.create("Click Here!").setChatStyle(JsonUtils.style().setColor(EnumChatFormatting.DARK_GREEN)))).setChatClickEvent(JsonUtils.click(ClickEvent.Action.OPEN_URL, this.url)))));
        }
    }

    private String formatText(EnumChatFormatting color, String text)
    {
        return color + text + EnumChatFormatting.WHITE;
    }
}