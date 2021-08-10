package com.stevekung.skyblockcatia.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.stevekung.skyblockcatia.core.SkyBlockcatia;
import dev.architectury.platform.Platform;

public class DataUtils
{
    @Deprecated
    public static BufferedReader get(String path) throws Exception
    {
        var url = new URL("https://raw.githubusercontent.com/SteveKunG/SkyBlockcatia/1.8.9/" + path);

        try
        {
            return new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedReader getData(String fileName)
    {
        if (Platform.isDevelopmentEnvironment())
        {
            return new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/skyblockcatia/api/" + fileName)));
        }

        try
        {
            var url = new URL("https://raw.githubusercontent.com/SteveKunG/SkyBlockcatia/skyblock_data/" + fileName);
            return new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            SkyBlockcatia.LOGGER.error("Couldn't get {} from remote, using local data", fileName);
            return new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/skyblockcatia/api/" + fileName)));
        }
    }
}