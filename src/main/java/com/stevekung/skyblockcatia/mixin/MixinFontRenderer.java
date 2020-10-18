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
import com.stevekung.stevekungslib.utils.GameProfileUtils;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer
{
    @ModifyVariable(method = "renderString(Ljava/lang/String;FFILnet/minecraft/util/math/vector/Matrix4f;ZZ)I", at = @At("HEAD"), argsOnly = true)
    private String renderString(String text)
    {
        if (SkyBlockEventHandler.isSkyBlock && text != null)
        {
            if (SBExtendedConfig.INSTANCE.supportersFancyColor)
            {
                for (String name : SkyBlockcatiaMod.SUPPORTERS_NAME)
                {
                    if (text.contains(name))
                    {
                        return this.replaceSupportersName(text, name);
                    }
                }
            }
            if (text.contains("SteveKunG") && !GameProfileUtils.isSteveKunG())
            {
                return this.replaceSupportersName(text, "SteveKunG");
            }
        }
        return text;
    }

    private String replaceSupportersName(String text, String name)
    {
        String namePatt = "(?:(?:\\u00a7[0-9a-fbr])\\B(?:" + name + ")\\b)|(?:\\u00a7[rb]" + name + "\\u00a7r)|\\b" + name + "\\b";
        Pattern prevColor = Pattern.compile("(?:.*\\B(?:(?<color>\\u00a7[0-9a-fbr])" + name + ")\\b.*)");
        Matcher prevColorMat = prevColor.matcher(text);

        if (prevColorMat.matches())
        {
            return text.replaceAll(namePatt, TextComponentUtils.formatted(name).setStyle(Style.EMPTY.setColor(Color.fromHex(ColorUtils.toHex(36, 224, 186)))).getString() + prevColorMat.group("color"));
        }
        return text.replaceAll(namePatt, TextComponentUtils.formatted(name).setStyle(Style.EMPTY.setColor(Color.fromHex(ColorUtils.toHex(36, 224, 186)))).getString() + name + TextFormatting.WHITE);
    }
}