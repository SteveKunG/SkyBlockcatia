package debug;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import com.google.gson.*;

public class SkyBlockAPIViewer
{
    public static String PLAYER_NAME;
    public static String SKYBLOCK_PROFILE;

    public static void main(String[] args) throws IOException
    {
        PLAYER_NAME = "https://api.hypixel.net/player?key=f306b0be-dd0c-42ec-8f4d-5213be92b6b3&name=";
        SKYBLOCK_PROFILE = "https://api.hypixel.net/skyblock/profiles?key=f306b0be-dd0c-42ec-8f4d-5213be92b6b3&uuid=";
        SkyBlockAPIViewer.getPlayerData(getUUID("stevekung"));
    }

    static String getName(String uuid) throws JsonSyntaxException, IOException
    {
        URL url = new URL("https://api.mojang.com/user/profiles/" + uuid.replace("-", "") + "/names");
        JsonArray array = new JsonParser().parse(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonArray();
        String name = array.get(array.size() - 1).getAsJsonObject().get("name").getAsString();
        return name;
    }

    static String getUUID(String name)
    {
        try
        {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            String read = IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8);
            JsonElement obj = new JsonParser().parse(read);

            if (obj.getAsJsonObject() == null)
            {
                throw new IOException("Skipped name: " + name);
            }
            else
            {
                return obj.getAsJsonObject().get("id").getAsString().replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");
            }
        }
        catch (IOException e) {}
        return null;
    }

    private static void getPlayerData(String sbProfileId) throws IOException
    {
        URL url = new URL(SKYBLOCK_PROFILE + sbProfileId);
        JsonObject obj = new JsonParser().parse(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(obj));
    }
}