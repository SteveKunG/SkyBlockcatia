package i.am.cal.antisteal.retriever;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public class WindowsUrlRetriever implements UrlRetriever {


    private final Path path;
    private final String[] urls;


    @Override
    public String[] getUrls() {
        return this.urls;
    }

    @Override
    public Path getPath() {
        return this.path;
    }

    @SuppressWarnings("null")
    public WindowsUrlRetriever(Path path) {
        this.path = path;
        Properties prop = this.read();
        if(!prop.contains("HostUrl"))
        {
            prop.setProperty("HostUrl", "");
        }
        if(!prop.contains("ReferrerUrl"))
        {
            prop.setProperty("ReferrerUrl", "");
        }
        List<String> tempUrls = new ArrayList<>(Lists.newArrayList((String) prop.get("HostUrl"), (String) prop.get("ReferrerUrl")));
        List<String> ttempUrls = new ArrayList<>();
        for (String o : tempUrls) {
            if(o != null || o.isEmpty()) {
                ttempUrls.add(o);
            }
        }
        this.urls = ttempUrls.toArray(new String[ttempUrls.size()]);
    }

    //Windows vodoo
    private Properties read() {
        List<String> parsedADS = new ArrayList<>();

        final String command = "cmd.exe /c dir " + this.path + " /r"; // listing of given Path.

        final Pattern pattern = Pattern.compile(
                "\\s*"                 // any amount of whitespace
                + "[0123456789,]+\\s*"   // digits (with possible comma), whitespace
                + "([^:]+:"    // group 1 = file name, then colon,
                + "[^:]+:"     // then ADS, then colon,
                + ".+)");      // then everything else.

        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;

                while ((line = br.readLine()) != null) {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.matches()) {
                        parsedADS.add(matcher.group(1));
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new Properties();
        }

        String targetADS = null;

        for (String parsedAD : parsedADS)
        {
            if(parsedAD.contains(":Zone.Identifier:$DATA"))
            {
                targetADS = parsedAD.replace(":$DATA", "");
            }
        }

        if(targetADS == null) {
            return new Properties();
        }

        targetADS = this.path.toString().replace(this.path.getFileName().toString(), targetADS);

        List<String> contents = new ArrayList<>();
        try {
            File file = new File(targetADS);
            try (BufferedReader bf = new BufferedReader( new FileReader(file))) {
                contents = bf.lines().collect(Collectors.toList());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new Properties();
        }
        contents.remove("[ZoneTransfer]");
        String _contents = String.join("\n", contents);
        Properties properties = new Properties();
        try {
            properties.load(new StringReader(_contents));
        } catch (IOException e) {
            e.printStackTrace();
            return new Properties();
        }
        return properties;
    }


}
