package com.stevekung.skyblockcatia.tweaker;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.List;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import com.stevekung.skyblockcatia.utils.LoggerIN;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.SortingIndex(1)
public class MixinsLoadingPlugin implements ITweaker
{
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {}

    @SuppressWarnings("deprecation")
    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        MixinBootstrap.init();

        try
        {
            Class mixinsClass = Class.forName("org.spongepowered.asm.mixin.Mixins");
            Method addConfigurationMethod = mixinsClass.getMethod("addConfiguration", String.class);
            addConfigurationMethod.invoke(null, "mixins.skyblockcatia.json");
        }
        catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }

        MixinEnvironment.setCompatibilityLevel(MixinEnvironment.CompatibilityLevel.JAVA_8);
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);

        // Taken from Resource-Exploit-Fix by Sk1er
        CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();

        try
        {
            Class<?> aClass = Class.forName("net.minecraftforge.fml.relauncher.CoreModManager");
            Method getIgnoredMods = null;

            try
            {
                getIgnoredMods = aClass.getDeclaredMethod("getIgnoredMods");
            }
            catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            }

            try
            {
                if (getIgnoredMods == null)
                {
                    getIgnoredMods = aClass.getDeclaredMethod("getLoadedCoremods");
                }
            }
            catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            }

            if (codeSource != null)
            {
                URL location = codeSource.getLocation();

                try
                {
                    File file = new File(location.toURI());

                    if (file.isFile())
                    {
                        try
                        {
                            if (getIgnoredMods != null)
                            {
                                ((List<String>) getIgnoredMods.invoke(null)).remove(file.getName());
                            }
                        }
                        catch (Throwable t)
                        {
                            t.printStackTrace();
                        }
                    }
                }
                catch (URISyntaxException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                LoggerIN.error("No CodeSource, if this is not a development environment we might run into problems!");
            }
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getLaunchTarget()
    {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments()
    {
        return new String[0];
    }
}