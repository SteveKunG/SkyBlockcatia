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
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod(SkyBlockcatia.MOD_ID)
public class SkyBlockcatiaForge
{
    public static final ModVersionChecker CHECKER = new ModVersionChecker(SkyBlockcatia.MOD_ID);

    public SkyBlockcatiaForge()
    {
        EventBuses.registerModEventBus(SkyBlockcatia.MOD_ID, ForgeCommonUtils.getModEventBus());
        SkyBlockcatia.init();
        ForgeCommonUtils.registerConfig(ModConfig.Type.CLIENT, SkyBlockcatiaConfig.GENERAL_SPEC);
        ForgeCommonUtils.registerClientOnly();
        ForgeCommonUtils.registerConfigScreen((mc, screen) -> ForgeCommonUtils.openConfigFile(screen, SkyBlockcatia.MOD_ID, ModConfig.Type.CLIENT));

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
        SkyBlockcatiaForge.CHECKER.startCheck();
    }
}