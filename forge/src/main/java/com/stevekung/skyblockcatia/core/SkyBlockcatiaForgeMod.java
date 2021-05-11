package com.stevekung.skyblockcatia.core;

import com.stevekung.skyblockcatia.command.BazaarViewerCommand;
import com.stevekung.skyblockcatia.command.SkyBlockAPIViewerCommand;
import com.stevekung.skyblockcatia.command.SkyBlockcatiaCommand;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.event.handler.SBClientEventHandler;
import com.stevekung.skyblockcatia.event.handler.ToastTestEventHandler;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import com.stevekung.stevekungslib.utils.ForgeCommonUtils;
import com.stevekung.stevekungslib.utils.ModVersionChecker;
import com.stevekung.stevekungslib.utils.client.command.ClientCommands;
import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod(SkyBlockcatiaMod.MOD_ID)
public class SkyBlockcatiaForgeMod
{
    public static final ModVersionChecker CHECKER = new ModVersionChecker(SkyBlockcatiaMod.MOD_ID);

    public SkyBlockcatiaForgeMod()
    {
        EventBuses.registerModEventBus(SkyBlockcatiaMod.MOD_ID, ForgeCommonUtils.getModEventBus());
        SkyBlockcatiaMod.init();
        ForgeCommonUtils.registerConfig(ModConfig.Type.CLIENT, SkyBlockcatiaConfig.GENERAL_BUILDER);
        ForgeCommonUtils.registerClientOnly();
        ForgeCommonUtils.registerConfigScreen(() -> (mc, screen) -> ForgeCommonUtils.openConfigFile(screen, SkyBlockcatiaMod.MOD_ID, ModConfig.Type.CLIENT));

        ForgeCommonUtils.registerModEventBus(SkyBlockcatiaConfig.class);
        ForgeCommonUtils.addModListener(this::phaseOne);
        ForgeCommonUtils.addModListener(this::loadComplete);
    }

    private void phaseOne(FMLCommonSetupEvent event)
    {
        ForgeCommonUtils.registerEventHandler(this);
        ForgeCommonUtils.registerEventHandler(new SBClientEventHandler());
        ForgeCommonUtils.registerEventHandler(new ToastTestEventHandler());

        ClientCommands.register(new SkyBlockAPIViewerCommand());
        ClientCommands.register(new BazaarViewerCommand());
        ClientCommands.register(new SkyBlockcatiaCommand());
    }

    private void loadComplete(FMLLoadCompleteEvent event)
    {
        SBAPIUtils.setApiKey();
        SkyBlockcatiaForgeMod.CHECKER.startCheck();
    }
}