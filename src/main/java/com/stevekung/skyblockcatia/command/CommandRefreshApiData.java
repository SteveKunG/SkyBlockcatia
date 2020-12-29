package com.stevekung.skyblockcatia.command;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.List;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.utils.ClientUtils;
import com.stevekung.skyblockcatia.utils.CommonUtils;
import com.stevekung.skyblockcatia.utils.DataGetter;
import com.stevekung.skyblockcatia.utils.JsonUtils;

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
    public List<String> getCommandAliases()
    {
        return Arrays.asList("sbcapirefresh");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        ClientUtils.printClientMessage("Processing refresh API data...", JsonUtils.green());
        SkyBlockcatiaMod.SUPPORTERS_NAME.clear();

        CommonUtils.runAsync(() ->
        {
            try
            {
                BufferedReader reader = DataGetter.get("SKYBLOCKCATIA_USERNAME");
                String inputLine;

                while ((inputLine = reader.readLine()) != null)
                {
                    SkyBlockcatiaMod.SUPPORTERS_NAME.add(inputLine);
                }

                SkyBlockcatiaMod.downloadAPIData();
                ClientUtils.printClientMessage("Finishing refresh API data!", JsonUtils.green());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                ClientUtils.printClientMessage("Found a problem while refreshing API data, See log for more info", JsonUtils.red());
            }
        });
    }
}