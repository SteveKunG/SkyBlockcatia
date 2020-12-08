package com.stevekung.skyblockcatia.core;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.minecraft.launchwrapper.Launch;

public class SkyBlockcatiaMixinConfigPlugin implements IMixinConfigPlugin
{
    static final Logger LOGGER = LogManager.getLogger("SkyBlockcatia MixinConfig");
    static boolean foundPatcher;
    static boolean foundPlayerApi;

    static
    {
        try
        {
            foundPatcher = Launch.classLoader.getClassBytes("club.sk1er.patcher.Patcher") != null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        try
        {
            foundPlayerApi = Launch.classLoader.getClassBytes("api.player.client.ClientPlayerAPI") != null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        printModInfo(foundPatcher, "Patcher");
        printModInfo(foundPlayerApi, "PlayerAPI");
    }

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig()
    {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
    {
        if (mixinClassName.equals("com.stevekung.skyblockcatia.mixin.FontRendererHookMixin"))
        {
            return foundPatcher;
        }
        else if (mixinClassName.equals("com.stevekung.skyblockcatia.mixin.playerapi.EntityPlayerSPMixin"))
        {
            return foundPlayerApi;
        }
        else if (mixinClassName.equals("com.stevekung.skyblockcatia.mixin.EntityPlayerSPMixin"))
        {
            return !foundPlayerApi;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins()
    {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    private static void printModInfo(boolean check, String modName)
    {
        if (check)
        {
            LOGGER.info(modName + " detected!");
        }
        else
        {
            LOGGER.info(modName + " not detected!");
        }
    }
}