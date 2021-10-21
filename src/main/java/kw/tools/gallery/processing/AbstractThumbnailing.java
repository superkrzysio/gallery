package kw.tools.gallery.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractThumbnailing implements Thumbnailing
{

    private final static String[] allowedExtensions = {"JPG", "PNG", "JPEG"};
    private final static Map<String, List<Path>> memoizedImages = new ConcurrentHashMap<>();
    private final static Logger LOG = LoggerFactory.getLogger(AbstractThumbnailing.class);


    public static List<Path> getImages(Path source)
    {
        String key = source.toString();
        if (!memoizedImages.containsKey(key))
        {
            try
            {
                memoizedImages.put(key, Files.list(source)
                        .filter(item -> Files.isRegularFile(item))
                        .filter(item -> Arrays.stream(allowedExtensions).anyMatch(
                                ext -> item.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(ext.toLowerCase(Locale.ROOT))
                        ))
                        .sorted()
                        .collect(Collectors.toList())
                );
            } catch (IOException e)
            {
                memoizedImages.put(key, Collections.EMPTY_LIST);
                LOG.warn("Could not resolve path for: '" + key + "', directory skipped");
            }
        }
        return memoizedImages.get(key);
    }

    public static List<String> getImages(String source)
    {
        return getImages(Path.of(source)).stream().map(p -> p.toString()).collect(Collectors.toList());
    }
}
