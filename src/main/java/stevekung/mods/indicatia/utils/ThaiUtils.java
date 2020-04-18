package stevekung.mods.indicatia.utils;

public class ThaiUtils
{
    public static final String LOWER_CHARS = "\u0E38\u0E39\u0E3A";
    public static final String SPECIAL_UPPER_CHARS = "\u0E48\u0E49\u0E4A\u0E4B";
    public static final String UPPER_CHARS = "\u0E31\u0E34\u0E35\u0E36\u0E37\u0E47\u0E4C\u0E4D\u0E4E" + SPECIAL_UPPER_CHARS;

    public static boolean isSpecialThaiChar(char ch)
    {
        return ThaiUtils.isUpperThaiChar(ch) || ThaiUtils.isLowerThaiChar(ch);
    }

    public static boolean isUpperThaiChar(char ch)
    {
        return UPPER_CHARS.indexOf(ch) != -1;
    }

    public static boolean isLowerThaiChar(char ch)
    {
        return LOWER_CHARS.indexOf(ch) != -1;
    }
}