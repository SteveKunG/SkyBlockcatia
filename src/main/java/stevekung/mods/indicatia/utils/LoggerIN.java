package stevekung.mods.indicatia.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerIN
{
    private static final Logger LOG = LogManager.getLogger("Indicatia");
    private static File logFile;
    private static PrintWriter logWriter;

    public static void info(String message)
    {
        LoggerIN.LOG.info(message);
    }

    public static void error(String message)
    {
        LoggerIN.LOG.error(message);
    }

    public static void warning(String message)
    {
        LoggerIN.LOG.warn(message);
    }

    public static void info(String message, Object... obj)
    {
        LoggerIN.LOG.info(message, obj);
    }

    public static void error(String message, Object... obj)
    {
        LoggerIN.LOG.error(message, obj);
    }

    public static void warning(String message, Object... obj)
    {
        LoggerIN.LOG.warn(message, obj);
    }

    public static void logToast(Object object)
    {
        String message = object == null ? "null" : object.toString();
        String preLine = new SimpleDateFormat("[HH:mm:ss]").format(new Date()) + " [" + Level.DEBUG.name() + "] ";

        for (String line : message.split("\\n"))
        {
            LoggerIN.LOG.log(Level.DEBUG, line);
            logWriter.println(preLine + line);
        }
        logWriter.flush();
    }

    public static void setup()
    {
        File logDirectory = new File("./logs/indicatia/");
        logDirectory.mkdirs();
        logFile = new File(logDirectory, "indicatia-toast.log");

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