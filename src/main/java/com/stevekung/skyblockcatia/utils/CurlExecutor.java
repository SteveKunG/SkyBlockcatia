package com.stevekung.skyblockcatia.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;

public class CurlExecutor
{
    public static BufferedReader execute(String path) throws Exception
    {
        String[] command = {"curl", "-s", "-S", "https://a7f8ca675bb7134dde14dceab711b51eb6e61dbb@raw.githubusercontent.com/SteveKunG/SkyBlockcatia/1.8.9/" + path, "--ssl-no-revoke"};
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);

        try
        {
            Process process = builder.start();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            return in;
        }
        catch (Exception e)
        {
            SkyBlockcatiaMod.noCurl = true;
            e.printStackTrace();
        }
        return null;
    }
}