package i.am.cal.antisteal.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FS {
    public static String getFileContent(
            InputStream fis) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    public static String pathToPortableString(Path p) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        Path root = p.getRoot();
        if (root != null) {
            sb.append(root.toString().replace('\\', '/'));
            /* root elements appear to contain their
             * own ending separator, so we don't set "first" to false
             */
        }
        for (Path element : p) {
            if (first)
            {
                first = false;
            }
            else
            {
                sb.append("/");
            }
            sb.append(element.toString());
        }
        return sb.toString();
    }
}
