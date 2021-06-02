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
    static boolean foundRenderPlayerApi;
    static boolean foundSmoothFont;

    static
    {
        foundPatcher = findAndDetectModClass("club.sk1er.patcher.Patcher", "Patcher");
        foundPlayerApi = findAndDetectModClass("api.player.client.ClientPlayerAPI", "PlayerAPI");
        foundRenderPlayerApi = findAndDetectModClass("api.player.render.RenderPlayerAPI", "RenderPlayerAPI");
        foundSmoothFont = findAndDetectModClass("bre.smoothfont.mod_SmoothFont", "Smooth Font");
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
        if (mixinClassName.equals("com.stevekung.skyblockcatia.mixin.patcher.FontRendererHookMixin"))
        {
            return foundPatcher;
        }
        else if (mixinClassName.equals("com.stevekung.skyblockcatia.mixin.player_api.entity.EntityPlayerSPMixin"))
        {
            return foundPlayerApi;
        }
        else if (mixinClassName.equals("com.stevekung.skyblockcatia.mixin.entity.EntityPlayerSPMixin"))
        {
            return !foundPlayerApi;
        }
        else if (mixinClassName.equals("com.stevekung.skyblockcatia.mixin.render_player_api.renderer.entity.RenderPlayerMixin"))
        {
            return foundRenderPlayerApi;
        }
        else if (mixinClassName.equals("com.stevekung.skyblockcatia.mixin.renderer.entity.RenderPlayerMixin"))
        {
            return !foundRenderPlayerApi;
        }
        else if (mixinClassName.equals("com.stevekung.skyblockcatia.mixin.gui.FontRendererMixin"))
        {
            return !foundSmoothFont;
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

    private static boolean findAndDetectModClass(String classPath, String modName)
    {
        boolean found = false;

        try
        {
            found = Launch.classLoader.getClassBytes(classPath) != null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        LOGGER.info(found ? modName + " detected!" : modName + " not detected!");
        return found;
    }
}