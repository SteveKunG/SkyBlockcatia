package com.stevekung.skyblockcatia.core;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import com.stevekung.skyblockcatia.command.*;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaConfig;
import com.stevekung.skyblockcatia.config.SkyBlockcatiaSettings;
import com.stevekung.skyblockcatia.event.handler.*;
import com.stevekung.skyblockcatia.gui.GuiChatExtended;
import com.stevekung.skyblockcatia.keybinding.KeyBindingsSB;
import com.stevekung.skyblockcatia.utils.*;
import com.stevekung.skyblockcatia.utils.skyblock.SBAPIUtils;
import com.stevekung.skyblockcatia.utils.skyblock.SBMinions;
import com.stevekung.skyblockcatia.utils.skyblock.SBPets;
import com.stevekung.skyblockcatia.utils.skyblock.api.ExpProgress;
import com.stevekung.skyblockcatia.utils.skyblock.api.PlayerStatsBonus;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = SkyBlockcatiaMod.MOD_ID, name = SkyBlockcatiaMod.NAME, version = SkyBlockcatiaMod.VERSION, dependencies = SkyBlockcatiaMod.DEPENDENCIES, clientSideOnly = true, updateJSON = SkyBlockcatiaMod.JSON_URL, guiFactory = SkyBlockcatiaMod.GUI_FACTORY, certificateFingerprint = SkyBlockcatiaMod.CERTIFICATE)
public class SkyBlockcatiaMod
{
    protected static final String NAME = "SkyBlockcatia";
    public static final String MOD_ID = "skyblockcatia";
    private static final int MAJOR_VERSION = 1;
    private static final int MINOR_VERSION = 0;
    private static final int BUILD_VERSION = 14;
    protected static final String GUI_FACTORY = "com.stevekung.skyblockcatia.config.ConfigGuiFactory";
    public static final String VERSION = SkyBlockcatiaMod.MAJOR_VERSION + "." + SkyBlockcatiaMod.MINOR_VERSION + "." + SkyBlockcatiaMod.BUILD_VERSION;
    protected static final String FORGE_VERSION = "required-after:Forge@[11.15.1.2318,);";
    protected static final String CERTIFICATE = "@FINGERPRINT@";
    protected static final String DEPENDENCIES = "after:skyblockaddons@[1.5.0,); " + SkyBlockcatiaMod.FORGE_VERSION;
    private static final String URL = "https://www.curseforge.com/minecraft/mc-mods/skyblockcatia";
    protected static final String JSON_URL = "https://raw.githubusercontent.com/SteveKunG/VersionCheckLibrary/master/skyblockcatia_version.json";

    @Instance(MOD_ID)
    private static SkyBlockcatiaMod INSTANCE;
    public static VersionChecker CHECKER;

    public static final File profile = new File(SkyBlockcatiaSettings.userDir, "profile.txt");
    private static final Splitter COLON_SPLITTER = Splitter.on(':');
    public static boolean isSkyblockAddonsLoaded = Loader.isModLoaded("skyblockaddons");
    public static boolean isIngameAccountSwitcherLoaded = Loader.isModLoaded("IngameAccountSwitcher");
    public static boolean isVanillaEnhancementsLoaded = Loader.isModLoaded("enhancements");
    public static boolean isPatcherLoaded = Loader.isModLoaded("patcher");
    public static boolean isTextOverflowScrollLoaded = Loader.isModLoaded("text_overflow_scroll");
    public static boolean isNotEnoughUpdatesLoaded = Loader.isModLoaded("notenoughupdates");

    public static final List<String> SUPPORTERS_NAME = new CopyOnWriteArrayList<>();
    public static boolean isDevelopment;

    static
    {
        try
        {
            SkyBlockcatiaMod.isDevelopment = Launch.classLoader.getClassBytes("net.minecraft.world.World") != null;
        }
        catch (Exception e) {}

        SkyBlockcatiaMod.initProfileFile();
        LoggerIN.setup();
        CommonUtils.runAsync(() ->
        {
            try
            {
                BufferedReader reader = DataGetter.get("SKYBLOCKCATIA_USERNAME");
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

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        SkyBlockcatiaMod.init(event.getModMetadata());
        SkyBlockcatiaConfig.init(new File(event.getModConfigurationDirectory(), "skyblockcatia.cfg"));
        KeyBindingsSB.init();
        CHECKER = new VersionChecker(INSTANCE, "SkyBlockcatia", URL);

        CommonUtils.registerEventHandler(this);
        CommonUtils.registerEventHandler(new MainEventHandler());
        CommonUtils.registerEventHandler(new HUDRenderEventHandler());
        CommonUtils.registerEventHandler(new SkyBlockEventHandler());
        CommonUtils.registerEventHandler(new ClientEventHandler());
        CommonUtils.registerEventHandler(new ToastTestEventHandler());

        ClientUtils.registerCommand(new CommandMojangStatusCheck());
        ClientUtils.registerCommand(new CommandSkyBlockcatia());
        ClientUtils.registerCommand(new CommandProfile());
        ClientUtils.registerCommand(new CommandApiViewer());
        ClientUtils.registerCommand(new CommandBazaarViewer());
        ClientUtils.registerCommand(new CommandRefreshApiData());
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
        SBAPIUtils.setApiKey();
        CommonUtils.runAsync(SkyBlockcatiaMod::downloadAPIData);
        ColorUtils.init();
        CHECKER.startCheck();
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID.equalsIgnoreCase(SkyBlockcatiaMod.MOD_ID))
        {
            SkyBlockcatiaConfig.syncConfig(false);
            SBAPIUtils.setApiKey();
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

    public static void downloadAPIData()
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
            ExpProgress.DUNGEON = ExpProgress.getXpProgressFromRemote(ExpProgress.Type.DUNGEON);

            for (PlayerStatsBonus.Type type : PlayerStatsBonus.Type.VALUES)
            {
                PlayerStatsBonus.getBonusFromRemote(type);
            }

            SBAPIUtils.getFairySouls();
            SBMinions.getMinionSlotFromRemote();
            SBPets.getPetSkins();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    private static void init(ModMetadata info)
    {
        info.autogenerated = false;
        info.modId = SkyBlockcatiaMod.MOD_ID;
        info.name = SkyBlockcatiaMod.NAME;
        info.version = SkyBlockcatiaMod.VERSION;
        info.description = "Useful Hypixel Skyblock features, QOL and In-game API Viewer!";
        info.credits = "Credit to LeaPhant, matdoes for inspiration to make In-game API Viewer possible";
        info.url = SkyBlockcatiaMod.URL;
        info.authorList = Arrays.asList("SteveKunG");
        info.logoFile = "assets/skyblockcatia/logo.png";
    }

    private static void loadProfileOption()
    {
        if (!profile.exists())
        {
            return;
        }
        if (!SkyBlockcatiaSettings.defaultConfig.exists())
        {
            LoggerIN.info("Initializing default profile...");
            SkyBlockcatiaSettings.INSTANCE.setCurrentProfile("default");
            SkyBlockcatiaSettings.INSTANCE.save();
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
                SkyBlockcatiaSettings.INSTANCE.setCurrentProfile(key);
                SkyBlockcatiaSettings.INSTANCE.load();
            }
        }
    }

    private static void initProfileFile()
    {
        if (!SkyBlockcatiaSettings.skyblockcatiaDir.exists())
        {
            SkyBlockcatiaSettings.skyblockcatiaDir.mkdirs();
        }
        else if (!SkyBlockcatiaSettings.userDir.exists())
        {
            SkyBlockcatiaSettings.userDir.mkdirs();
        }

        File profile = new File(SkyBlockcatiaSettings.userDir, "profile.txt");

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
}