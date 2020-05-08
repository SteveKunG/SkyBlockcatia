package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.stevekungslib.utils.ColorUtils;

import net.minecraft.client.gui.FontRenderer;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer
{
    @ModifyVariable(method = "renderString(Ljava/lang/String;FFILnet/minecraft/client/renderer/Matrix4f;Z)I", at = @At("HEAD"))
    private String renderString(String text)
    {
        if (ExtendedConfig.INSTANCE.supportersFancyColor)
        {
            for (String name : SkyBlockcatiaMod.SUPPORTERS_NAME)
            {
                if (text.contains(name))
                {
                    text = text.replaceAll("\\u00a7[0-9a-fbr]" + name + "|\\u00a7[rb]" + name + "\\u00a7r|\\b" + name + "\\b", ColorUtils.stringToRGB("36,224,186").toColoredFont() + name + ColorUtils.stringToRGB("255,255,255").toColoredFont());
                }
            }
        }
        return text;
    }
}