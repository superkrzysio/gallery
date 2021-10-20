package kw.tools.gallery.processing;

import java.io.IOException;
import java.util.function.Consumer;

public interface DirCrawler
{
    void forEach(String path, Consumer<String> action) throws IOException;
}
