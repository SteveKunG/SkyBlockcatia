package com.stevekung.skyblockcatia.core;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import com.google.common.collect.Lists;
import com.stevekung.stevekungslib.utils.LoggerBase;
import net.minecraftforge.fml.loading.FMLLoader;

public class SkyBlockcatiaForgeMixinConfigPlugin implements IMixinConfigPlugin
{
    static final LoggerBase LOGGER = new LoggerBase("SkyBlockcatia:Forge MixinConfig");
    static boolean foundOptifine;

    static
    {
        foundOptifine = findAndDetectModClass("net/optifine/Config.class", "OptiFine");
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
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins()
    {
        List<String> mixins = Lists.newArrayList();

        if (FMLLoader.isProduction())
        {
            if (foundOptifine)
            {
                mixins.add("optifine.renderer.MixinBlockEntityWithoutLevelRendererOptifine");
            }
            mixins.add("gui.screens.inventory.MixinAbstractContainerScreen");
        }
        else
        {
            mixins.add("gui.screens.inventory.MixinAbstractContainerScreenDev");
        }
        return mixins;
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