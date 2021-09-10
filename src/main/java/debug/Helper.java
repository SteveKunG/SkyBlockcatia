package debug;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

public class Helper
{
    public static void writeFile(Object src, File file)
    {
        try (FileWriter writer = new FileWriter(file))
        {
            toJson(src, writer);
            Desktop.getDesktop().open(file);
            System.out.println("success");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    static void toJson(Object src, Appendable writer) throws JsonIOException
    {
        if (src != null)
        {
            toJson(src, src.getClass(), writer);
        }
        else
        {
            toJson(JsonNull.INSTANCE, writer);
        }
    }

    static void toJson(Object src, Type typeOfSrc, Appendable writer) throws JsonIOException
    {
        try
        {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonWriter jsonWriter = newJsonWriter(Streams.writerForAppendable(writer));
            gson.toJson(src, typeOfSrc, jsonWriter);
        }
        catch (IOException e)
        {
            throw new JsonIOException(e);
        }
    }

    static JsonWriter newJsonWriter(Writer writer) throws IOException
    {
        JsonWriter jsonWriter = new JsonWriter(writer);
        jsonWriter.setIndent("    ");
        jsonWriter.setSerializeNulls(false);
        return jsonWriter;
    }

    public static void exportJson(Map<String, Object> maps, String name)
    {
        File file = new File("M:/Modding/SkyBlockcatia/SkyblockData", name + ".json");
        File file2 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.8.9/src/main/resources/assets/skyblockcatia/api", name + ".json");
        File file3 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.16.5_architectury/common/src/main/resources/assets/skyblockcatia/api", name + ".json");
        File file4 = new File("M:/Modding/SkyBlockcatia/SkyBlockcatia_1.17.x_architectury/common/src/main/resources/assets/skyblockcatia/api", name + ".json");

        Helper.writeFile(maps, file);
        Helper.writeFile(maps, file2);
        Helper.writeFile(maps, file3);
        Helper.writeFile(maps, file4);
    }

    public static <T> T make(T object, Consumer<T> consumer)
    {
        consumer.accept(object);
        return object;
    }
}