package com.stevekung.skyblockcatia.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.stevekung.skyblockcatia.config.SBExtendedConfig;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.stevekungslib.utils.ColorUtils;
import com.stevekung.stevekungslib.utils.GameProfileUtils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Matrix4f;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer
{
    //TODO
    @Redirect(method = "renderString(Ljava/lang/String;FFILnet/minecraft/util/math/vector/Matrix4f;ZZ)I", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/FontRenderer.func_238411_a_(Ljava/lang/String;FFIZLnet/minecraft/util/math/vector/Matrix4f;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ZIIZ)I"))
    private int renderString(FontRenderer fontRenderer, String text, float x, float y, int color, boolean dropShadow, Matrix4f matrix4f, IRenderTypeBuffer buffer, boolean transparent, int colorBackground, int packedLight, boolean bidi)
    {
        if (SkyBlockEventHandler.isSkyBlock)
        {
            if (SBExtendedConfig.INSTANCE.supportersFancyColor)
            {
                for (String name : SkyBlockcatiaMod.SUPPORTERS_NAME)
                {
                    if (text.contains(name))
                    {
                        color = ColorUtils.to32Bit(36, 224, 186, 255);
                    }
                }
            }
            if (text.contains("SteveKunG") && !GameProfileUtils.isSteveKunG())
            {
                color = ColorUtils.to32Bit(36, 224, 186, 255);
            }
        }
        return fontRenderer.func_238411_a_(text, x, y, color, dropShadow, matrix4f, buffer, transparent, colorBackground, packedLight, bidi);
    }

    //    private String replaceSupportersName(String text, String name)
    //    {
    //        String namePatt = "(?:(?:\\u00a7[0-9a-fbr])\\B(?:" + name + ")\\b)|(?:\\u00a7[rb]" + name + "\\u00a7r)|\\b" + name + "\\b";
    //        Pattern prevColor = Pattern.compile("(?:.*\\B(?:(?<color>\\u00a7[0-9a-fbr])" + name + ")\\b.*)");
    //        Matcher prevColorMat = prevColor.matcher(text);
    //
    //        if (prevColorMat.matches())
    //        {
    //            return text.replaceAll(namePatt, TextComponentUtils.formatted(name).setStyle(Style.EMPTY.setColor(Color.fromHex(ColorUtils.toHex(36, 224, 186)))).getString() + prevColorMat.group("color"));
    //        }
    //        return text.replaceAll(namePatt, TextComponentUtils.formatted(name).setStyle(Style.EMPTY.setColor(Color.fromHex(ColorUtils.toHex(36, 224, 186)))).getString() + name + TextFormatting.WHITE);
    //    }
}