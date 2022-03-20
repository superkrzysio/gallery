package kw.tools.gallery.processing;

import kw.tools.gallery.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A low level component for listing images in a location.
 */
@Component
public class ImageAccessor
{
    private final static Logger LOG = LoggerFactory.getLogger(ImageAccessor.class);

    private final Map<String, List<Path>> memoizedImages = new ConcurrentHashMap<>();

    @Value("${image.allowed.extensions}")
    private String[] allowedExtensions;

    @Autowired
    private FilenameEncoder filenameEncoder;

    @Autowired
    private CacheUtils cacheUtils;

    /**
     * Retrieve all allowed images from the given location.
     */
    public List<Path> getImages(Path source)
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

    /**
     * Retrieve all allowed images from the given location.
     */
    public List<String> getImages(String source)
    {
        return getImages(Path.of(source)).stream().map(p -> p.toString()).collect(Collectors.toList());
    }

    /**
     * Retrieve list of paths to thumbnails for a gallery
     */
    public List<String> getThumbs(String repoId, String galId)
    {
        String source = cacheUtils.getCacheDirForGallery(repoId, galId);
        return filenameEncoder.decodeThumbFilename(source);
    }
}
