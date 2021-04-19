package com.stevekung.skyblockcatia.utils;

import java.io.*;
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

    public static BufferedReader getData(String fileName) throws IOException
    {
        URL url = new URL("https://raw.githubusercontent.com/SteveKunG/SkyBlockcatia/skyblock_data/" + fileName);

        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), StandardCharsets.UTF_8));
            return in;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            LoggerIN.error("Couldn't get {} from remote, using local data", fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(DataGetter.class.getResource("/assets/skyblockcatia/api/" + fileName).getFile())), StandardCharsets.UTF_8));
            return in;
        }
    }
}