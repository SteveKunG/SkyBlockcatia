package debug;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class UpdateName
{
    public static void main(String[] arg) throws IOException, JsonSyntaxException
    {
        List<String> uuidList = Lists.newArrayList();
        List<String> nameList = Lists.newArrayList();

        Path path = Paths.get("M:\\Modding\\SkyBlockcatia\\SkyblockData\\supporters_uuid");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8));
        String inputLine;

        while ((inputLine = br.readLine()) != null)
        {
            uuidList.add(inputLine);
        }

        for (String uuid : uuidList)
        {
            String name = getName(uuid);
            nameList.add(name);
            System.out.println(name);
        }

        Path file = Paths.get("M:\\Modding\\SkyBlockcatia\\SkyblockData\\supporters_username");
        Files.write(file, nameList, StandardCharsets.UTF_8);
        file = Paths.get("M:\\Modding\\SkyBlockcatia\\SkyBlockcatia_1.8.9\\src\\main\\resources\\assets\\skyblockcatia\\api\\supporters_username");
        Files.write(file, nameList, StandardCharsets.UTF_8);
        file = Paths.get("M:\\Modding\\SkyBlockcatia\\SkyBlockcatia_1.16.5_architectury\\common\\src\\main\\resources\\assets\\skyblockcatia\\api\\supporters_username");
        Files.write(file, nameList, StandardCharsets.UTF_8);
        file = Paths.get("M:\\Modding\\SkyBlockcatia\\SkyBlockcatia_1.17.x_architectury\\common\\src\\main\\resources\\assets\\skyblockcatia\\api\\supporters_username");
        Files.write(file, nameList, StandardCharsets.UTF_8);
    }

    static String getName(String uuid) throws JsonSyntaxException, IOException
    {
        URL url = new URL("https://api.mojang.com/user/profiles/" + uuid.replace("-", "") + "/names");
        JsonArray array = new JsonParser().parse(IOUtils.toString(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).getAsJsonArray();
        return array.get(array.size() - 1).getAsJsonObject().get("name").getAsString();
    }
}