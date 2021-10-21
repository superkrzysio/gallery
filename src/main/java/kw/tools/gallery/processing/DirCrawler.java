package kw.tools.gallery.processing;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.Consumer;

public interface DirCrawler
{
    void forEach(String path, Consumer<Path> action) throws IOException;

    Iterator<Path> iterator(String path) throws IOException;
}
