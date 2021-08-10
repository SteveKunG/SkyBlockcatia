package com.stevekung.skyblockcatia.core;

import java.util.List;

import com.google.common.collect.Lists;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.utils.CompatibilityUtils;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.skyblockcatia.utils.ToastLog;
import com.stevekung.skyblockcatia.utils.skyblock.*;
import com.stevekung.stevekungslib.utils.CommonUtils;
import com.stevekung.stevekungslib.utils.LoggerBase;

public class SkyBlockcatia
{
    public static final String MOD_ID = "skyblockcatia";
    public static final LoggerBase LOGGER = new LoggerBase("SkyBlockcatia");
    public static final List<String> SUPPORTERS_NAME = Lists.newCopyOnWriteArrayList();

    static
    {
        ToastLog.setup();

        CommonUtils.runAsync(() ->
        {
            try
            {
                var reader = DataUtils.getData("supporters_username");
                String inputLine;

                while ((inputLine = reader.readLine()) != null)
                {
                    SUPPORTERS_NAME.add(inputLine);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
        if (!SkyBlockcatiaSettings.SBC_DIR.exists())
        {
            SkyBlockcatiaSettings.SBC_DIR.mkdirs();
        }
        if (!SkyBlockcatiaSettings.PROFILE.exists())
        {
            SkyBlockcatia.LOGGER.info("Creating SkyBlockcatia settings");
            SkyBlockcatiaSettings.INSTANCE.save();
        }
        else
        {
            SkyBlockcatia.LOGGER.info("Loading SkyBlockcatia settings");
            SkyBlockcatiaSettings.INSTANCE.load();
        }
    }

    public static void init()
    {
        CommonUtils.runAsync(SkyBlockcatia::downloadAPIData);
        SkyBlockcatiaSettings.INSTANCE.load();
        CompatibilityUtils.init();
        KeyBindingHandler.init();
        new MainEventHandler();
        new HUDRenderEventHandler(true);
        new SkyBlockEventHandler(true);
    }

    private static void downloadAPIData()
    {
        try
        {
            SBMinions.getMinions();
            SBPets.getPets();
            SBSlayers.getSlayers();
            SBSkills.getSkills();
            SBStats.getStats();
            SBDungeons.getDungeons();
            SBAPIUtils.getMisc();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
}