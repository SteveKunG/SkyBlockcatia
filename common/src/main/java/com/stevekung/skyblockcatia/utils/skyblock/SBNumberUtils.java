package com.stevekung.skyblockcatia.utils.skyblock;

import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

public class SBNumberUtils
{
    public static boolean isNumericWithKM(CharSequence cs)
    {
        if (StringUtils.isEmpty(cs))
        {
            return false;
        }

        var sz = cs.length();

        for (var i = 0; i < sz; i++)
        {
            if (!Character.isDigit(cs.charAt(i)) && !(cs.charAt(i) == 'k' || cs.charAt(i) == 'm'))
            {
                return false;
            }
        }
        return true;
    }

    public static String formatWithM(double number)
    {
        var suffix = new char[] {' ', 'K', 'M', 'B', 'T', 'P', 'E'};
        var value = (int) Math.floor(Math.log10(number));
        var base = value / 3;

        if (value >= 3 && base < suffix.length)
        {
            return new DecimalFormat("#0.0").format(number / Math.pow(10, base * 3)) + suffix[base];
        }
        else
        {
            return new DecimalFormat("#,##0").format(number);
        }
    }
}