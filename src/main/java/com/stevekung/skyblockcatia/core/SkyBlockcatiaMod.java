package com.stevekung.skyblockcatia.core;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.stevekung.skyblockcatia.command.*;
import com.stevekung.skyblockcatia.config.ConfigManagerIN;
import com.stevekung.skyblockcatia.config.ExtendedConfig;
import com.stevekung.skyblockcatia.event.ClientEventHandler;
import com.stevekung.skyblockcatia.event.HUDRenderEventHandler;
import com.stevekung.skyblockcatia.event.HypixelEventHandler;
import com.stevekung.skyblockcatia.event.MainEventHandler;
import com.stevekung.skyblockcatia.gui.GuiChatExtended;
import com.stevekung.skyblockcatia.gui.api.ExpProgress;
import com.stevekung.skyblockcatia.gui.api.PlayerStatsBonus;
import com.stevekung.skyblockcatia.handler.KeyBindingHandler;
import com.stevekung.skyblockcatia.utils.*;

import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = SkyBlockcatiaMod.MOD_ID, name = SkyBlockcatiaMod.NAME, version = SkyBlockcatiaMod.VERSION, dependencies = SkyBlockcatiaMod.DEPENDENCIES, clientSideOnly = true, guiFactory = SkyBlockcatiaMod.GUI_FACTORY, certificateFingerprint = SkyBlockcatiaMod.CERTIFICATE)
public class SkyBlockcatiaMod
{
    protected static final String NAME = "SkyBlockcatia";
    public static final String MOD_ID = "skyblockcatia";
    private static final int MAJOR_VERSION = 1;
    private static final int MINOR_VERSION = 0;
    private static final int BUILD_VERSION = 0;
    protected static final String GUI_FACTORY = "com.stevekung.skyblockcatia.config.ConfigGuiFactory";
    public static final String VERSION = SkyBlockcatiaMod.MAJOR_VERSION + "." + SkyBlockcatiaMod.MINOR_VERSION + "." + SkyBlockcatiaMod.BUILD_VERSION;
    protected static final String FORGE_VERSION = "after:Forge@[11.15.1.2318,);";
    protected static final String CERTIFICATE = "@FINGERPRINT@";
    protected static final String DEPENDENCIES = "after:skyblockaddons@[1.5.0,); " + SkyBlockcatiaMod.FORGE_VERSION;
    private static final String URL = "https://minecraft.curseforge.com/projects/skyblockcatia";

    public static final File profile = new File(ExtendedConfig.userDir, "profile.txt");
    private static final Splitter COLON_SPLITTER = Splitter.on(':');
    public static boolean isSkyblockAddonsLoaded = Loader.isModLoaded("skyblockaddons");
    public static boolean isIngameAccountSwitcherLoaded = Loader.isModLoaded("IngameAccountSwitcher");
    public static boolean isVanillaEnhancementsLoaded = Loader.isModLoaded("enhancements");
    public static boolean isPatcherLoaded = Loader.isModLoaded("patcher");

    private static boolean githubDown;
    private static boolean noUUID;
    public static boolean noCurl;
    private static final List<String> HARDCODE_UUID = new ArrayList<>();
    public static final List<String> SUPPORTERS_NAME = new CopyOnWriteArrayList<>();
    public static final List<String> SUPPORTERS_UUID = new ArrayList<>();
    public static UUID CURRENT_UUID;
    public static boolean isDevelopment;

    static
    {
        HARDCODE_UUID.add("84b5eb0f-11d8-464b-881d-4bba203cc77b");
        HARDCODE_UUID.add("e2d72023-34b9-45c2-825b-63ae2d1b2f36");
        HARDCODE_UUID.add("4675476a-46e5-45ee-89a5-010dc02996d9");
        HARDCODE_UUID.add("f1dfdd47-6e03-4c2d-b766-e414c7b77f10");
        HARDCODE_UUID.add("07e864c4-90d6-4c86-8df2-adf98a843e9e");
        HARDCODE_UUID.add("2cd88ad0-89b1-4ca7-907e-78066fe36b08");
        HARDCODE_UUID.add("f81a81c1-92fc-4714-b8ed-f811e6c61550");
        HARDCODE_UUID.add("266513e1-6c83-4d87-bbc5-d3934f9ca329");

        try
        {
            SkyBlockcatiaMod.isDevelopment = Launch.classLoader.getClassBytes("net.minecraft.world.World") != null;
        }
        catch (Exception e) {}

        SkyBlockcatiaMod.initProfileFile();
        LoggerIN.setup();

        if (!isDevelopment)
        {
            SkyBlockcatiaMod.kuy();
            CommonUtils.runAsync(() ->
            {
                try
                {
                    BufferedReader reader = CurlExecutor.execute("SKYBLOCKCATIA_USERNAME");
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
    }

    public static final Block.SoundType CROPS = new Block.SoundType("crops", 1.0F, 1.0F)
    {
        @Override
        public String getBreakSound()
        {
            return "skyblockcatia:block.crop.break";
        }

        @Override
        public String getPlaceSound()
        {
            return "";
        }

        @Override
        public String getStepSound()
        {
            return "step.grass";
        }
    };

    public static final Block.SoundType NETHERWARTS = new Block.SoundType("netherwart", 1.0F, 1.0F)
    {
        @Override
        public String getBreakSound()
        {
            return "skyblockcatia:block.nether_wart.break";
        }

        @Override
        public String getPlaceSound()
        {
            return "";
        }

        @Override
        public String getStepSound()
        {
            return "step.grass";
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (noUUID)
        {
            throw new WhitelistException();
        }
        if (githubDown)
        {
            throw new GitHubDownException();
        }
        if (noCurl)
        {
            throw new NoCurlException();
        }

        SkyBlockcatiaMod.CURRENT_UUID = GameProfileUtils.getUUID();
        SkyBlockcatiaMod.init(event.getModMetadata());
        ConfigManagerIN.init(new File(event.getModConfigurationDirectory(), "skyblockcatia.cfg"));
        KeyBindingHandler.init();

        if (ConfigManagerIN.enableOldFishingRodRenderModel)
        {
            ModelLoader.setCustomModelResourceLocation(Items.fishing_rod, 0, new ModelResourceLocation("skyblockcatia:fishing_rod", "inventory"));
            ModelBakery.registerItemVariants(Items.fishing_rod, new ModelResourceLocation("skyblockcatia:fishing_rod_cast", "inventory"));
            LoggerIN.info("Successfully replacing vanilla Fishing Rod item model");
        }

        CommonUtils.registerEventHandler(this);
        CommonUtils.registerEventHandler(new MainEventHandler());
        CommonUtils.registerEventHandler(new HUDRenderEventHandler());
        CommonUtils.registerEventHandler(new HypixelEventHandler());
        CommonUtils.registerEventHandler(new ClientEventHandler());

        ClientUtils.registerCommand(new CommandMojangStatusCheck());
        ClientUtils.registerCommand(new CommandSkyBlockcatia());
        ClientUtils.registerCommand(new CommandProfile());
        ClientUtils.registerCommand(new CommandApiViewer());
        ClientUtils.registerCommand(new CommandBazaarViewer());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        SkyBlockcatiaMod.loadProfileOption();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        GuiChatRegistry.register(new GuiChatExtended());
        new ThreadMinigameData().run();
        SkyBlockAPIUtils.setApiKey();
        CommonUtils.runAsync(() ->
        {
            try
            {
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
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        });
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(MainEventHandler::getBazaarData, 0, 10, TimeUnit.SECONDS);

        if (ClientUtils.isEffectiveClient())
        {
            ColorUtils.init();
        }
        Blocks.wheat.setStepSound(SkyBlockcatiaMod.CROPS);
        Blocks.carrots.setStepSound(SkyBlockcatiaMod.CROPS);
        Blocks.potatoes.setStepSound(SkyBlockcatiaMod.CROPS);
        Blocks.nether_wart.setStepSound(SkyBlockcatiaMod.NETHERWARTS);
        Blocks.noteblock.setStepSound(Block.soundTypeWood);
        Blocks.jukebox.setStepSound(Block.soundTypeWood);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID.equalsIgnoreCase(SkyBlockcatiaMod.MOD_ID))
        {
            ConfigManagerIN.syncConfig(false);
            SkyBlockAPIUtils.setApiKey();
        }
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event)
    {
        if (isDevelopment)
        {
            LoggerIN.info("Development environment detected! Ignore certificate check.");
        }
        else
        {
            throw new RuntimeException("Invalid fingerprint detected! This version will NOT be support or responsible by the author!");
        }
    }

    private static void init(ModMetadata info)
    {
        info.autogenerated = false;
        info.modId = SkyBlockcatiaMod.MOD_ID;
        info.name = SkyBlockcatiaMod.NAME;
        info.version = SkyBlockcatiaMod.VERSION;
        info.description = "Simple in-game info and utilities!";
        info.url = SkyBlockcatiaMod.URL;
        info.authorList = Arrays.asList("SteveKunG");
    }

    private static void loadProfileOption()
    {
        if (!profile.exists())
        {
            return;
        }
        if (!ExtendedConfig.defaultConfig.exists())
        {
            LoggerIN.info("Initializing default profile...");
            ExtendedConfig.instance.setCurrentProfile("default");
            ExtendedConfig.instance.save();
        }

        NBTTagCompound nbt = new NBTTagCompound();

        try (BufferedReader reader = Files.newReader(profile, Charsets.UTF_8))
        {
            reader.lines().forEach(option ->
            {
                try
                {
                    Iterator<String> iterator = SkyBlockcatiaMod.COLON_SPLITTER.omitEmptyStrings().limit(2).split(option).iterator();
                    nbt.setString(iterator.next(), iterator.next());
                }
                catch (Exception e) {}
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        for (String property : nbt.getKeySet())
        {
            String key = nbt.getString(property);

            if ("profile".equals(property))
            {
                LoggerIN.info("Loaded current profile by name '{}'", key);
                ExtendedConfig.instance.setCurrentProfile(key);
                ExtendedConfig.instance.load();
            }
        }
    }

    private static void initProfileFile()
    {
        if (!ExtendedConfig.skyblockcatiaDir.exists())
        {
            ExtendedConfig.skyblockcatiaDir.mkdirs();
        }
        else if (!ExtendedConfig.userDir.exists())
        {
            ExtendedConfig.userDir.mkdirs();
        }

        File profile = new File(ExtendedConfig.userDir, "profile.txt");

        if (!profile.exists())
        {
            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(profile), StandardCharsets.UTF_8)))
            {
                writer.println("profile:default");
                LoggerIN.info("Creating profile option at {}", profile.getPath());
            }
            catch (IOException e)
            {
                LoggerIN.error("Failed to save profile");
                e.printStackTrace();
            }
        }
    }

    private static void kuy()
    {
        List<String> uuidList = new ArrayList<>();

        try
        {
            BufferedReader reader = CurlExecutor.execute("SKYBLOCKCATIA_UUID");
            String inputLine;

            while ((inputLine = reader.readLine()) != null)
            {
                uuidList.add(inputLine);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            if (!isDevelopment)
            {
                SkyBlockcatiaMod.githubDown = true;
            }
        }

        uuidList.addAll(HARDCODE_UUID);
        SUPPORTERS_UUID.addAll(uuidList);

        if (!uuidList.stream().anyMatch(text ->
        {
            return GameProfileUtils.getUUID().toString().equals(text);
        }))
        {
            if (!isDevelopment)
            {
                SkyBlockcatiaMod.noUUID = true;
            }
        }
    }
}