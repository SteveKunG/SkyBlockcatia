package com.stevekung.skyblockcatia.mixin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.stevekungslib.utils.ColorUtils;

import net.minecraft.client.gui.FontRenderer;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer
{
    @ModifyVariable(method = "renderString(Ljava/lang/String;FFILnet/minecraft/client/renderer/Matrix4f;Z)I", at = @At("HEAD"), argsOnly = true)
    private String renderString(String text)
    {
        if (SkyBlockEventHandler.isSkyBlock && SBExtendedConfig.INSTANCE.supportersFancyColor)
        {
            for (String name : SkyBlockcatiaMod.SUPPORTERS_NAME)
            {
                if (text != null && text.contains(name))
                {
                    String namePatt = "(?:(?:\\u00a7[0-9a-fbr])\\B(?:" + name + ")\\b)|(?:\\u00a7[rb]" + name + "\\u00a7r)|\\b" + name + "\\b";
                    Pattern prevColor = Pattern.compile("(?:.*\\B(?:(?<color>\\u00a7[0-9a-fbr])" + name + ")\\b.*)");
                    Matcher prevColorMat = prevColor.matcher(text);

                    if (prevColorMat.matches())
                    {
                        return text.replaceAll(namePatt, ColorUtils.stringToRGB("36,224,186").toColoredFont() + name + prevColorMat.group("color"));
                    }
                    return text.replaceAll(namePatt, ColorUtils.stringToRGB("36,224,186").toColoredFont() + name + ColorUtils.stringToRGB("255,255,255").toColoredFont());
                }
            }
        }
        return text;
    }
}