package com.stevekung.skyblockcatia.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CurlExecutor
{
    public static BufferedReader execute(String path) throws IOException
    {
        String[] command = {"curl", "-s", "https://a7f8ca675bb7134dde14dceab711b51eb6e61dbb@raw.githubusercontent.com/SteveKunG/SkyBlockcatia/1.8.9/" + path};
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);

        try
        {
            Process process = builder.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            return in;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}