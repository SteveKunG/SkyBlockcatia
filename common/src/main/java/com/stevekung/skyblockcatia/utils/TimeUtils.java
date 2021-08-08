package com.stevekung.skyblockcatia.utils;

import java.util.Timer;
import java.util.TimerTask;

public class TimeUtils
{
    public static String getRelativeTime(long timeDiff)
    {
        timeDiff = timeDiff / 1000;
        long current = System.currentTimeMillis() / 1000;
        long timeElapsed = current - timeDiff;

        if (timeElapsed <= 60)
        {
            if (timeElapsed <= 0)
            {
                return "just now";
            }
            return TimeUtils.convertCorrectTime((int) timeElapsed, "second", false);
        }
        else
        {
            int minutes = Math.round(timeElapsed / 60F);

            if (minutes <= 60)
            {
                return TimeUtils.convertCorrectTime(minutes, "minute", false);
            }
            else
            {
                int hours = Math.round(timeElapsed / 3600F);

                if (hours <= 24)
                {
                    return TimeUtils.convertCorrectTime(hours, "hour", true);
                }
                else
                {
                    int days = Math.round(timeElapsed / 86400F);

                    if (days <= 7)
                    {
                        return TimeUtils.convertCorrectTime(days, "day", false);
                    }
                    else
                    {
                        int weeks = Math.round(timeElapsed / 604800F);

                        if (weeks <= 4)
                        {
                            return TimeUtils.convertCorrectTime(weeks, "week", false);
                        }
                        else
                        {
                            int months = Math.round(timeElapsed / 2600640F);

                            if (months <= 12)
                            {
                                return TimeUtils.convertCorrectTime(months, "month", false);
                            }
                            else
                            {
                                int years = Math.round(timeElapsed / 31207680F);
                                return TimeUtils.convertCorrectTime(years, "year", false);
                            }
                        }
                    }
                }
            }
        }
    }

    public static String getRelativeDay(long timeDiff)
    {
        timeDiff = timeDiff / 1000;
        long current = System.currentTimeMillis() / 1000;
        long timeElapsed = current - timeDiff;
        int days = Math.round(timeElapsed / 86400F);
        return days + " day" + (days == 1 ? "" : "s");
    }

    public static void schedule(Runnable runnable, long delay)
    {
        TimerTask task = new TimerTask()
        {
            @Override
            public void run()
            {
                runnable.run();
            }
        };
        new Timer().schedule(task, delay);
    }

    private static String convertCorrectTime(int time, String text, boolean an)
    {
        return (time == 1 ? an ? "an" : "a" : time) + " " + text + (time == 1 ? "" : "s") + " ago";
    }
}