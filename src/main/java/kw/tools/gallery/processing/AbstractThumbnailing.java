package kw.tools.gallery.processing;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractThumbnailing implements Thumbnailing
{
    private final static String PATH_SEPARATOR = "/";
    private final static String[] allowedExtensions = {"JPG", "PNG", "JPEG"};
    private final static Map<String, List<Path>> memoizedImages = new ConcurrentHashMap<>();

    protected static void createDir(String outputDir, String outputId) throws IOException
    {
        Files.createDirectories(Path.of(outputDir + PATH_SEPARATOR + outputId));
    }

    public static List<Path> getImages(String source)
    {
        if (!memoizedImages.containsKey(source))
        {
            try
            {
                memoizedImages.put(source, Files.list(Path.of(source))
                        .filter(item -> Files.isRegularFile(item))
                        .filter(item -> Arrays.stream(allowedExtensions).anyMatch(
                                ext -> item.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(ext.toLowerCase(Locale.ROOT))
                        ))
                        .collect(Collectors.toList())
                );
            } catch (IOException e)
            {
                memoizedImages.put(source, Collections.EMPTY_LIST);
            }
        }
        return memoizedImages.get(source);
    }
}
