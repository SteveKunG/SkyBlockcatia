package com.stevekung.skyblockcatia.core;

import java.io.BufferedReader;
import java.util.List;

import com.google.common.collect.Lists;
import com.stevekung.skyblockcatia.event.handler.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.event.handler.MainEventHandler;
import com.stevekung.skyblockcatia.event.handler.SkyBlockEventHandler;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.utils.CompatibilityUtils;
import com.stevekung.skyblockcatia.utils.DataUtils;
import com.stevekung.skyblockcatia.utils.ToastLog;
import com.stevekung.skyblockcatia.utils.skyblock.SBMinions;
import com.stevekung.skyblockcatia.utils.skyblock.SBPets;
import com.stevekung.skyblockcatia.utils.skyblock.SBSkills;
import com.stevekung.skyblockcatia.utils.skyblock.SBSlayers;
import com.stevekung.skyblockcatia.utils.skyblock.api.ExpProgress;
import com.stevekung.skyblockcatia.utils.skyblock.api.PlayerStatsBonus;
import com.stevekung.stevekungslib.utils.CommonUtils;
import com.stevekung.stevekungslib.utils.LoggerBase;

public class SkyBlockcatiaMod
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
                BufferedReader reader = DataUtils.getData("supporters_username");
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
    }

    public static void init()
    {
        CommonUtils.runAsync(SkyBlockcatiaMod::downloadAPIData);
        CompatibilityUtils.init();
        KeyBindingHandler.init();
        new MainEventHandler();
        new HUDRenderEventHandler();
        new SkyBlockEventHandler();
    }

    private static void downloadAPIData()
    {
        try
        {
            ExpProgress.DUNGEON = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.DUNGEON);

            for (PlayerStatsBonus.Type type : PlayerStatsBonus.Type.VALUES)
            {
                PlayerStatsBonus.getBonusFromRemote(type);
            }

            SBMinions.getMinions();
            SBPets.getPets();
            SBSlayers.getSlayers();
            SBSkills.getSkills();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }
}