package com.stevekung.skyblockcatia.utils;

import java.text.DecimalFormat;

public class NumberUtils
{
    public static String formatCompact(long number)
    {
        if (number < 1000)
        {
            return String.valueOf(number);
        }
        int exp = (int) (Math.log(number) / Math.log(1000));
        return String.format("%.1f%c", number / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    public static boolean isNumeric(CharSequence cs)
    {
        int sz = cs.length();

        for (int i = 0; i < sz; i++)
        {
            if (!Character.isDigit(cs.charAt(i)) && cs.charAt(i) != '.')
            {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumericWithKM(CharSequence cs)
    {
        int sz = cs.length();

        for (int i = 0; i < sz; i++)
        {
            if (!Character.isDigit(cs.charAt(i)) && !(cs.charAt(i) == 'k' || cs.charAt(i) == 'm'))
            {
                return false;
            }
        }
        return true;
    }

    public static String formatWithM(int number)
    {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number;
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;

        if (value >= 3 && base < suffix.length)
        {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        }
        else
        {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    public static String intToRoman(int num)
    {
        StringBuilder sb = new StringBuilder();
        int times = 0;
        String[] romans = new String[] { "I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D", "CM", "M" };
        int[] ints = new int[] { 1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000 };

        for (int i = ints.length - 1; i >= 0; i--)
        {
            times = num / ints[i];
            num %= ints[i];

            while (times > 0)
            {
                sb.append(romans[i]);
                times--;
            }
        }
        return sb.toString();
    }
}