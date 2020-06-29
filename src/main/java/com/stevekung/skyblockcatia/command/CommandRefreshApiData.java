package com.stevekung.skyblockcatia.command;

import java.io.BufferedReader;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.gui.api.ExpProgress;
import com.stevekung.skyblockcatia.gui.api.PlayerStatsBonus;
import com.stevekung.skyblockcatia.utils.CommonUtils;
import com.stevekung.skyblockcatia.utils.CurlExecutor;
import com.stevekung.skyblockcatia.utils.SkyBlockAPIUtils;
import com.stevekung.skyblockcatia.utils.SkyBlockMinion;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class CommandRefreshApiData extends ClientCommandBase
{
    @Override
    public String getCommandName()
    {
        return "sbapirefresh";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        SkyBlockcatiaMod.SUPPORTERS_NAME.clear();

        CommonUtils.runAsync(() ->
        {
            try
            {
                BufferedReader reader = CurlExecutor.execute("SKYBLOCKCATIA_USERNAME");
                String inputLine;

                while ((inputLine = reader.readLine()) != null)
                {
                    SkyBlockcatiaMod.SUPPORTERS_NAME.add(inputLine);
                }

                ExpProgress.SKILL = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.SKILL);
                ExpProgress.ZOMBIE_SLAYER = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.ZOMBIE_SLAYER);
                ExpProgress.SPIDER_SLAYER = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.SPIDER_SLAYER);
                ExpProgress.WOLF_SLAYER = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.WOLF_SLAYER);
                ExpProgress.RUNECRAFTING = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.RUNECRAFTING);
                ExpProgress.PET_COMMON = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.PET_0);
                ExpProgress.PET_UNCOMMON = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.PET_1);
                ExpProgress.PET_RARE = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.PET_2);
                ExpProgress.PET_EPIC = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.PET_3);
                ExpProgress.PET_LEGENDARY = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.PET_4);

                for (PlayerStatsBonus.Type type : PlayerStatsBonus.Type.VALUES)
                {
                    PlayerStatsBonus.getBonusFromRemote(type);
                }
                SkyBlockAPIUtils.getFairySouls();
                SkyBlockMinion.getMinionSlotFromRemote();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }
}