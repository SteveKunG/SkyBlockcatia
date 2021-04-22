package com.stevekung.skyblockcatia.config;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

import com.stevekung.skyblockcatia.core.SkyBlockcatiaMod;
import com.stevekung.stevekungslib.utils.ConfigHandlerBase;
import com.stevekung.stevekungslib.utils.TextComponentUtils;

public class ConfigHandlerSB extends ConfigHandlerBase
{
    private SkyBlockcatiaConfig config;

    public ConfigHandlerSB()
    {
        super(SkyBlockcatiaMod.MOD_ID);
    }

    public SkyBlockcatiaConfig getConfig()
    {
        if (this.config == null)
        {
            try
            {
                this.loadConfig();
            }
            catch (IOException e)
            {
                SkyBlockcatiaMod.LOGGER.error("Failed to load config, using default.", e);
                return new SkyBlockcatiaConfig();
            }
        }
        return this.config;
    }

    @Override
    public void loadConfig() throws IOException
    {
        this.configFile.getParentFile().mkdirs();

        if (!this.configFile.exists())
        {
            SkyBlockcatiaMod.LOGGER.error("Unable to find config file, creating new one.");
            this.config = new SkyBlockcatiaConfig();
            this.saveConfig();
        }
        else
        {
            this.config = GSON.fromJson(ConfigHandlerBase.readFile(this.configFile.toPath().toString(), Charset.defaultCharset()), SkyBlockcatiaConfig.class);
        }
    }

    @Override
    public void saveConfig() throws IOException
    {
        this.configFile.getParentFile().mkdirs();
        FileWriter writer = new FileWriter(this.configFile);
        TextComponentUtils.toJson(this.config, writer);
        writer.close();
    }
}