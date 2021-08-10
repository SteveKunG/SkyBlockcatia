package com.stevekung.skyblockcatia.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.stevekung.stevekungslib.utils.GameProfileUtils;

public class ToastLog
{
    private static final Logger LOG = LogManager.getLogger("SkyBlockcatia");
    private static PrintWriter logWriter;

    public static void logToast(Object object)
    {
        var message = object == null ? "null" : object.toString();
        var preLine = new SimpleDateFormat("[HH:mm:ss]").format(new Date()) + " [" + Level.DEBUG.name() + "] ";

        for (var line : message.split("\\n"))
        {
            LOG.log(Level.DEBUG, line);
            logWriter.println(preLine + line);
        }
        logWriter.flush();
    }

    public static void setup()
    {
        var logDirectory = new File("./logs/skyblockcatia/" + GameProfileUtils.getUUID().toString() + "/");
        logDirectory.mkdirs();
        var dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var now = LocalDateTime.now();
        var logFile = new File(logDirectory, dtf.format(now) + "-toast.log");

        try
        {
            logFile.createNewFile();
            logWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(logFile), StandardCharsets.UTF_8));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
