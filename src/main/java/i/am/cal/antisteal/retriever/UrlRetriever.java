package i.am.cal.antisteal.retriever;

import java.nio.file.Path;

public interface UrlRetriever {

    String[] getUrls();

    Path getPath();
}