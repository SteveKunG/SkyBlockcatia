package com.stevekung.skyblockcatia.utils;

import java.util.Calendar;

public class CalendarUtils
{
    public static boolean isHalloweenDay()
    {
        return CalendarUtils.getMonth(10) && CalendarUtils.getDay(31, 0);
    }

    public static boolean isChristmasDay()
    {
        return CalendarUtils.getMonth(12) && CalendarUtils.getDay(24, 1);
    }

    public static boolean isMorePlanetsBirthDay()
    {
        return CalendarUtils.getMonth(3) && CalendarUtils.getDay(31, 1);
    }

    public static boolean isSteveKunGBirthDay()
    {
        return CalendarUtils.getMonth(2) && CalendarUtils.getDay(2, 0);
    }

    private static boolean getMonth(int month)
    {
        return Calendar.getInstance().get(Calendar.MONTH) + 1 == month;
    }

    private static boolean getDay(int day, int flag)
    {
        if (flag == 0)
        {
            return Calendar.getInstance().get(Calendar.DATE) == day;
        }
        else if (flag == 1)
        {
            return Calendar.getInstance().get(Calendar.DATE) >= day && Calendar.getInstance().get(Calendar.DATE) <= day + 2;
        }
        return false;
    }
}