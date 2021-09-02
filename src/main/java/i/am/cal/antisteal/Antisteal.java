package i.am.cal.antisteal;

import java.awt.Desktop;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import i.am.cal.antisteal.retriever.MacOSUrlRetriever;
import i.am.cal.antisteal.retriever.UrlRetriever;
import i.am.cal.antisteal.retriever.WindowsUrlRetriever;
import i.am.cal.antisteal.util.FS;
import i.am.cal.antisteal.util.OS;

public class Antisteal {
    /**
     * Check if the mod is considered stolen.
     *
     * @param pathToFile Path to your mod file. See documentation on how to get.
     * @param closeEvent This is run when Antisteal wants to kill the Minecraft Process
     * @param whitelist  The whitelisted domains. See documentation for format and storage.
     * @param yourClass The class of your mod initializer or mod.
     */
    public static void check(Path pathToFile, CloseEvent closeEvent, Map<String, String> whitelist, Class<?> yourClass) {
        if (!Files.exists(pathToFile)) {
            return;
        }

        UrlRetriever retriever = null;
        if (OS.isWindows()) {
            retriever = new WindowsUrlRetriever(pathToFile);
        }
        if (OS.isMac()) {
            retriever = new MacOSUrlRetriever(pathToFile);
        }
        if (OS.isUnix() || OS.isSolaris() || OS.getOS().equals("err")) {
            return;
        }

        assert retriever != null;
        boolean valid = retriever.getUrls().length == 0;

        try {
            InputStream blacklist = yourClass.getResourceAsStream("/blacklist.txt");
            if (blacklist == null) {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://api.stopmodreposts.org/sites.txt").openConnection();
                connection.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();
                content.toString();
                String[] parts = content.toString().split("\\\\r?\\\\n");
                boolean tVld = false;
                for (String domain : parts) {
                    if(tVld)
                    {
                        break;
                    }
                    for (String url : retriever.getUrls()) {
                        if(url.contains(domain)) {
                            tVld = true;
                            break;
                        }
                    }
                }
                valid = !tVld;
            } else {
                String content = new BufferedReader(
                        new InputStreamReader(blacklist, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));

                new String(Base64.getDecoder().decode(content));
                String[] parts = content.toString().split("\\\\r?\\\\n");
                boolean tVld = false;
                for (String domain : parts) {
                    if(tVld)
                    {
                        break;
                    }
                    for (String url : retriever.getUrls()) {
                        if(url.contains(domain)) {
                            tVld = true;
                            break;
                        }
                    }
                }
                valid = !tVld;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        if (!valid) {
            InputStream stream = yourClass.getResourceAsStream("/stolen.html");

            File temp;
            try {
                temp = File.createTempFile("stolenmod", ".html");
                FileWriter fileWriter = new FileWriter(temp);
                String content = FS.getFileContent(stream);
                content = content.replaceAll("%jar_name%", pathToFile.getFileName().toString());
                content = content.replaceAll("%jar_loc%", "<code>" + FS.pathToPortableString(pathToFile) + "</code>");
                content = content.replaceAll("%jar_downloaded%", String.join(", ", retriever.getUrls()));
                content = content.replaceAll("%date%", new Date().toString());

                TString ref = new TString();

                whitelist.forEach((String a, String b) -> ref.trustedString = ref.trustedString + "<a href=\"" + b + "\">" + a + "</a><br>\n");

                content = content.replace("%ul_content%", ref.trustedString);

                fileWriter.write(content);
                fileWriter.close();
                File finalTemp = temp;
                Timer timer = new java.util.Timer();
                TimerTask task = new java.util.TimerTask() {
                    @Override
                    public void run() {
                        finalTemp.delete();
                        timer.cancel();
                    }
                };
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(temp.toURI());
                } else {
                    Runtime runtime = Runtime.getRuntime();
                    if (System.getenv("OS") != null && System.getenv("OS").contains("Windows")) {
                        runtime.exec("cmd /c start /wait " + temp);
                    } else {
                        runtime.exec("open " + temp);
                    }
                    timer.schedule(
                            task,
                            1000
                            );
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            closeEvent.close();
        }
    }

    @FunctionalInterface
    public interface CloseEvent {
        void close();
    }

    public static class TString
    {
        String trustedString = "";
    }
}
