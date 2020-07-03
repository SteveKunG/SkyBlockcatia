package com.stevekung.skyblockcatia.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class ApiDataExecutor
{
    public static BufferedReader execute(String path)
    {
        try
        {
            URL url = new URL("https://raw.githubusercontent.com/SteveKunG/SkyblockData/master/" + path);
            URLConnection connection = url.openConnection();
            return new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}