package com.stevekung.skyblockcatia.core;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.stevekung.stevekungslib.utils.LoggerBase;

public class SkyBlockcatiaMixinConfigPlugin implements IMixinConfigPlugin
{
    static final LoggerBase LOGGER = new LoggerBase("SkyBlockcatia MixinConfig");
    static boolean foundOptifine;

    static
    {
        foundOptifine = Thread.currentThread().getContextClassLoader().getResourceAsStream("net/optifine/Config.class") != null;

        if (foundOptifine)
        {
            LOGGER.info("OptiFine detected!");
        }
        else
        {
            LOGGER.info("OptiFine not found!");
        }
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
        if (mixinClassName.equals("com.stevekung.skyblockcatia.mixin.MixinItemStackTileEntityRendererOptifine"))
        {
            return foundOptifine;
        }
        else if (mixinClassName.equals("com.stevekung.skyblockcatia.mixin.MixinItemStackTileEntityRenderer"))
        {
            return !foundOptifine;
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
}