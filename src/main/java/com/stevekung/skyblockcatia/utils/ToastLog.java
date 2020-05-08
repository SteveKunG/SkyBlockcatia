package com.stevekung.skyblockcatia.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ToastLog
{
    private static final Logger LOG = LogManager.getLogger("SkyBlockcatia");
    private static File logFile;
    private static PrintWriter logWriter;

    public static void logToast(Object object)
    {
        String message = object == null ? "null" : object.toString();
        String preLine = new SimpleDateFormat("[HH:mm:ss]").format(new Date()) + " [" + Level.DEBUG.name() + "] ";

        for (String line : message.split("\\n"))
        {
            LOG.log(Level.DEBUG, line);
            logWriter.println(preLine + line);
        }
        logWriter.flush();
    }

    public static void setup()
    {
        File logDirectory = new File("./logs/skyblockcatia/");
        logDirectory.mkdirs();
        logFile = new File(logDirectory, "skyblockcatia-toast.log");

        try
        {
            logFile.createNewFile();
            logWriter = new PrintWriter(new FileWriter(logFile));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
