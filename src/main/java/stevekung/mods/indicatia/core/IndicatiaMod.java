package stevekung.mods.indicatia.core;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.io.Files;

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
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stevekung.mods.indicatia.command.CommandApiViewer;
import stevekung.mods.indicatia.command.CommandIndicatia;
import stevekung.mods.indicatia.command.CommandMojangStatusCheck;
import stevekung.mods.indicatia.command.CommandProfile;
import stevekung.mods.indicatia.config.ConfigManagerIN;
import stevekung.mods.indicatia.config.ExtendedConfig;
import stevekung.mods.indicatia.event.ClientEventHandler;
import stevekung.mods.indicatia.event.HUDRenderEventHandler;
import stevekung.mods.indicatia.event.HypixelEventHandler;
import stevekung.mods.indicatia.event.IndicatiaEventHandler;
import stevekung.mods.indicatia.gui.GuiIndicatiaChat;
import stevekung.mods.indicatia.gui.api.ExpProgress;
import stevekung.mods.indicatia.gui.api.PlayerStatsBonus;
import stevekung.mods.indicatia.handler.KeyBindingHandler;
import stevekung.mods.indicatia.utils.*;

@Mod(modid = IndicatiaMod.MOD_ID, name = IndicatiaMod.NAME, version = IndicatiaMod.VERSION, dependencies = IndicatiaMod.DEPENDENCIES, clientSideOnly = true, guiFactory = IndicatiaMod.GUI_FACTORY)
public class IndicatiaMod
{
    protected static final String NAME = "Indicatia";
    public static final String MOD_ID = "indicatia";
    private static final int MAJOR_VERSION = 1;
    private static final int MINOR_VERSION = 0;
    private static final int BUILD_VERSION = 0;
    public static final int DEV_BUILD = 101;
    protected static final String GUI_FACTORY = "stevekung.mods.indicatia.config.ConfigGuiFactory";
    public static final String VERSION = IndicatiaMod.MAJOR_VERSION + "." + IndicatiaMod.MINOR_VERSION + "." + IndicatiaMod.BUILD_VERSION;
    protected static final String FORGE_VERSION = "after:Forge@[11.15.1.1722,);";
    protected static final String DEPENDENCIES = "after:skyblockaddons@[1.5.0-beta.16,); " + IndicatiaMod.FORGE_VERSION;
    private static final String URL = "https://minecraft.curseforge.com/projects/indicatia";

    public static final File profile = new File(ExtendedConfig.userDir, "profile.txt");
    private static final Splitter COLON_SPLITTER = Splitter.on(':');
    public static boolean isSkyblockAddonsLoaded = Loader.isModLoaded("skyblockaddons");
    public static boolean isIngameAccountSwitcherLoaded = Loader.isModLoaded("IngameAccountSwitcher");
    public static boolean isVanillaEnhancementsLoaded = Loader.isModLoaded("enhancements");

    static
    {
        IndicatiaMod.initProfileFile();
        LoggerIN.setup();
    }

    public static final Block.SoundType CROPS = new Block.SoundType("crops", 1.0F, 1.0F)
    {
        @Override
        public String getBreakSound()
        {
            return "indicatia:block.crop.break";
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
            return "indicatia:block.nether_wart.break";
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
        if (!(Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment"))
        {
            IndicatiaMod.checkUUID();
        }

        IndicatiaMod.init(event.getModMetadata());
        ConfigManagerIN.init(new File(event.getModConfigurationDirectory(), "indicatia.cfg"));
        KeyBindingHandler.init();

        if (ConfigManagerIN.enableOldFishingRodRenderModel)
        {
            ModelLoader.setCustomModelResourceLocation(Items.fishing_rod, 0, new ModelResourceLocation("indicatia:fishing_rod", "inventory"));
            ModelBakery.registerItemVariants(Items.fishing_rod, new ModelResourceLocation("indicatia:fishing_rod_cast", "inventory"));
            LoggerIN.info("Successfully replacing vanilla Fishing Rod item model");
        }

        CommonUtils.registerEventHandler(this);
        CommonUtils.registerEventHandler(new IndicatiaEventHandler());
        CommonUtils.registerEventHandler(new HUDRenderEventHandler());
        CommonUtils.registerEventHandler(new HypixelEventHandler());
        CommonUtils.registerEventHandler(new ClientEventHandler());

        ClientUtils.registerCommand(new CommandMojangStatusCheck());
        ClientUtils.registerCommand(new CommandIndicatia());
        ClientUtils.registerCommand(new CommandProfile());
        ClientUtils.registerCommand(new CommandApiViewer());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        IndicatiaMod.loadProfileOption();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        GuiChatRegistry.register(new GuiIndicatiaChat());
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
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        });

        if (ClientUtils.isEffectiveClient())
        {
            ColorUtils.init();
        }
        Blocks.wheat.setStepSound(IndicatiaMod.CROPS);
        Blocks.carrots.setStepSound(IndicatiaMod.CROPS);
        Blocks.potatoes.setStepSound(IndicatiaMod.CROPS);
        Blocks.nether_wart.setStepSound(IndicatiaMod.NETHERWARTS);
        Blocks.noteblock.setStepSound(Block.soundTypeWood);
        Blocks.jukebox.setStepSound(Block.soundTypeWood);
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID.equalsIgnoreCase(IndicatiaMod.MOD_ID))
        {
            ConfigManagerIN.syncConfig(false);
            SkyBlockAPIUtils.setApiKey();
        }
    }

    private static void init(ModMetadata info)
    {
        info.autogenerated = false;
        info.modId = IndicatiaMod.MOD_ID;
        info.name = IndicatiaMod.NAME;
        info.version = IndicatiaMod.VERSION;
        info.description = "Simple in-game info and utilities!";
        info.url = IndicatiaMod.URL;
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
            LoggerIN.info("Initializing created default Indicatia profile...");
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
                    Iterator<String> iterator = IndicatiaMod.COLON_SPLITTER.omitEmptyStrings().limit(2).split(option).iterator();
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
        if (!ExtendedConfig.indicatiaDir.exists())
        {
            ExtendedConfig.indicatiaDir.mkdirs();
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

    private static void checkUUID()
    {
        List<String> uuidList = new ArrayList<>();

        try
        {
            InputStream input = new URL("https://raw.githubusercontent.com/SteveKunG/SupporterData/master/skyblock_uuid").openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String inputLine;

            while ((inputLine = reader.readLine()) != null)
            {
                uuidList.add(inputLine);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new WhitelistException();
        }

        if (!uuidList.stream().anyMatch(text ->
        {
            String[] split = text.split(":");
            String uuid = split[0];
            return GameProfileUtils.getUUID().toString().equals(uuid);
        }))
        {
            throw new InvalidUUIDException();
        }
        //        if (!uuidList.stream().anyMatch(text ->
        //        {
        //            String[] split = text.split(":");
        //            int devBuild = Integer.valueOf(split[1]);
        //            return IndicatiaMod.DEV_BUILD == devBuild;
        //        }))
        //        {
        //            System.out.println("Not allowed to use this dev build!");
        //            FMLCommonHandler.instance().handleExit(0);
        //        }
    }
}