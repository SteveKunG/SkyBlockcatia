package com.stevekung.skyblockcatia.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DataGetter
{
    public static BufferedReader get(String path) throws Exception
    {
        URL url = new URL("https://raw.githubusercontent.com/SteveKunG/SkyBlockcatia/1.8.9/" + path);

        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), StandardCharsets.UTF_8));
            return in;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedReader getData(String fileName)
    {
        try
        {
            URL url = new URL("https://raw.githubusercontent.com/SteveKunG/SkyBlockcatia/skyblock_data/" + fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), StandardCharsets.UTF_8));
            return in;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            LoggerIN.error("Couldn't get {} from remote, using local data", fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("assets/skyblockcatia/api/" + fileName)));
            return in;
        }
    }
}