package i.am.cal.antisteal.util;

import java.net.URI;
import java.net.URISyntaxException;

public class URLS {
    public static String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
}
