package kw.tools.gallery.processing;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Class that crawls through directory structure and performs an action if the directory contains any regular file.
 */
public interface DirCrawler
{
    void forEach(String path, Consumer<Path> action) throws IOException;

    Iterator<Path> iterator(String path) throws IOException;
}
