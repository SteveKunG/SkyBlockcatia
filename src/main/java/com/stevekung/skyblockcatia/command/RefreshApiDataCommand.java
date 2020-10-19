package com.stevekung.skyblockcatia.command;

import java.io.BufferedReader;

import com.mojang.brigadier.CommandDispatcher;
import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.skyblockcatia.utils.DataGetter;
import com.stevekung.stevekungslib.utils.CommonUtils;
import com.stevekung.stevekungslib.utils.client.ClientUtils;
import com.stevekung.stevekungslib.utils.client.command.ClientCommands;
import com.stevekung.stevekungslib.utils.client.command.IClientCommand;
import com.stevekung.stevekungslib.utils.client.command.IClientSuggestionProvider;

import net.minecraft.util.text.TextFormatting;

public class RefreshApiDataCommand implements IClientCommand
{
    @Override
    public void register(CommandDispatcher<IClientSuggestionProvider> dispatcher)
    {
        dispatcher.register(ClientCommands.literal("sbapirefresh").executes(requirement -> RefreshApiDataCommand.refreshApiData()));
    }

    private static int refreshApiData()
    {
        ClientUtils.printClientMessage("Processing refresh API data...", TextFormatting.GREEN);
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
                ClientUtils.printClientMessage("Finishing refresh API data!", TextFormatting.GREEN);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                ClientUtils.printClientMessage("Found a problem while refreshing API data, See log for more info", TextFormatting.RED);
            }
        });
        return 1;
    }
}