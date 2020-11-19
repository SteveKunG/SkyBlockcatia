package com.stevekung.skyblockcatia.utils;

import java.awt.Desktop;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInput;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class CommonUtils
{
    private static final ExecutorService POOL = Executors.newFixedThreadPool(100, new ThreadFactory()
    {
        private final AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable runnable)
        {
            return new Thread(runnable, String.format("Thread %s", this.counter.incrementAndGet()));
        }
    });

    public static void runAsync(Runnable runnable)
    {
        CompletableFuture.runAsync(runnable, CommonUtils.POOL);
    }

    public static void registerEventHandler(Object event)
    {
        MinecraftForge.EVENT_BUS.register(event);
    }

    public static void unregisterEventHandler(Object event)
    {
        MinecraftForge.EVENT_BUS.unregister(event);
    }

    public static void registerGuiHandler(Object obj, IGuiHandler handler)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(obj, handler);
    }

    public static String ticksToElapsedTime(int ticks)
    {
        int i = ticks / 20;
        int j = i / 60;
        i = i % 60;
        return i < 10 ? j + ":0" + i : j + ":" + i;
    }

    public static void onInputUpdate(EntityPlayer player, MovementInput movementInput)
    {
        MinecraftForge.EVENT_BUS.post(new InputUpdateEvent(player, movementInput));
    }

    public static void openLink(String url)
    {
        try
        {
            URI uri = new URI(url);
            Desktop.getDesktop().browse(uri);
        }
        catch (Exception e)
        {
            LoggerIN.info("Couldn't open link {}", url);
            e.printStackTrace();
        }
    }

    public static String getRelativeTime(long timeDiff)
    {
        timeDiff = timeDiff / 1000;
        long current = System.currentTimeMillis() / 1000;
        long timeElapsed = current - timeDiff;
        long seconds = timeElapsed;

        if (seconds <= 60)
        {
            if (seconds <= 0)
            {
                return "just now";
            }
            return CommonUtils.convertCorrectTime((int)seconds, "second", false);
        }
        else
        {
            int minutes = Math.round(timeElapsed / 60);

            if (minutes <= 60)
            {
                return CommonUtils.convertCorrectTime(minutes, "minute", false);
            }
            else
            {
                int hours = Math.round(timeElapsed / 3600);

                if (hours <= 24)
                {
                    return CommonUtils.convertCorrectTime(hours, "hour", true);
                }
                else
                {
                    int days = Math.round(timeElapsed / 86400);

                    if (days <= 7)
                    {
                        return CommonUtils.convertCorrectTime(days, "day", false);
                    }
                    else
                    {
                        int weeks = Math.round(timeElapsed / 604800);

                        if (weeks <= 4)
                        {
                            return CommonUtils.convertCorrectTime(weeks, "week", false);
                        }
                        else
                        {
                            int months = Math.round(timeElapsed / 2600640);

                            if (months <= 12)
                            {
                                return CommonUtils.convertCorrectTime(months, "month", false);
                            }
                            else
                            {
                                int years = Math.round(timeElapsed / 31207680);
                                return CommonUtils.convertCorrectTime(years, "year", false);
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
        int days = Math.round(timeElapsed / 86400);
        return days + " day" + (days == 1 ? "" : "s");
    }

    public static Collection<NetworkPlayerInfo> getPlayerInfoMap(NetHandlerPlayClient handler)
    {
        return handler.getPlayerInfoMap().stream().filter(info -> !info.getGameProfile().getName().startsWith("!")).collect(Collectors.toList());
    }

    private static String convertCorrectTime(int time, String text, boolean an)
    {
        return (time == 1 ? an ? "an" : "a" : time) + " " + text + (time == 1 ? "" : "s") + " ago";
    }
}