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
    static boolean foundOptifabric;

    static
    {
        foundOptifine = findAndDetectModClass("net/optifine/Config.class", "OptiFine");
        foundOptifabric = findAndDetectModClass("me/modmuss50/optifabric/mod/OptifabricSetup.class", "OptiFabric");
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
        if (foundOptifine || foundOptifabric)
        {
            return !mixinClassName.equals("com.stevekung.skyblockcatia.mixin.renderer.MixinBlockEntityWithoutLevelRenderer");
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
        boolean found = Thread.currentThread().getContextClassLoader().getResourceAsStream(classPath) != null;
        LOGGER.info(found ? modName + " detected!" : modName + " not detected!");
        return found;
    }
}