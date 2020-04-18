package stevekung.mods.indicatia.utils;

import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.BiomeManager;

public class CachedEnum
{
    public static final EnumDyeColor[] dyeColorValues = EnumDyeColor.values();
    public static final BiomeManager.BiomeType[] biomeValues = BiomeManager.BiomeType.values();
    public static final EnumFacing[] facingValues = EnumFacing.values();
    public static final EnumFacing.Axis[] axisValues = EnumFacing.Axis.values();
    public static final EnumChatFormatting[] textFormatValues = EnumChatFormatting.values();
    public static final EnumAction[] actionValues = EnumAction.values();
    public static final SkyBlockLocation[] locationValues = SkyBlockLocation.values();
}