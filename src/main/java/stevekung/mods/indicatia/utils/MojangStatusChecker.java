package stevekung.mods.indicatia.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public enum MojangStatusChecker
{
    MAIN_WEBSITE("Main Website", "minecraft.net"),
    MC_SESSION_SERVER("Minecraft Session Server", "session.minecraft.net"),
    TEXTURES_SERVICE("Minecraft Textures Service", "textures.minecraft.net"),
    MOJANG_ACCOUNT_SERVICE("Mojang Account Service", "account.mojang.com"),
    MOJANG_SESSION_SERVER("Mojang Session Server", "sessionserver.mojang.com"),
    MOJANG_AUTHENTICATION_SERVER("Mojang Authentication Server", "authserver.mojang.com"),
    MOJANG_PUBLIC_API("Mojang Public API", "api.mojang.com"),
    MOJANG_MAIN_WEBSITE("Mojang Main Website", "mojang.com");

    private String name;
    private String serviceURL;
    public static final MojangStatusChecker[] values = MojangStatusChecker.values();

    private MojangStatusChecker(String name, String serviceURL)
    {
        this.name = name;
        this.serviceURL = serviceURL;
    }

    public String getName()
    {
        return this.name;
    }

    public MojangServerStatus getServiceStatus()
    {
        try
        {
            URL url = new URL("http://status.mojang.com/check?service=" + this.serviceURL);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
            JsonElement jsonElement = new JsonParser().parse(bufferedReader).getAsJsonObject().get(this.serviceURL);
            return MojangServerStatus.get(jsonElement.getAsString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            LoggerIN.error("Cannot get status data from Mojang!");
            return MojangServerStatus.UNKNOWN;
        }
    }
}