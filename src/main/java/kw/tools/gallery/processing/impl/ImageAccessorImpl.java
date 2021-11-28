package kw.tools.gallery.processing.impl;

import kw.tools.gallery.CacheUtils;
import kw.tools.gallery.processing.FilenameEncoder;
import kw.tools.gallery.processing.ImageAccessor;
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

@Component
public class ImageAccessorImpl implements ImageAccessor
{
    private final static Logger LOG = LoggerFactory.getLogger(ImageAccessorImpl.class);

    private final Map<String, List<Path>> memoizedImages = new ConcurrentHashMap<>();

    @Value("${image.allowed.extensions}")
    private String[] allowedExtensions;

    @Autowired
    private FilenameEncoder filenameEncoder;

    @Autowired
    private CacheUtils cacheUtils;

    @Override
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

    @Override
    public List<String> getImages(String source)
    {
        return getImages(Path.of(source)).stream().map(p -> p.toString()).collect(Collectors.toList());
    }

    @Override
    public List<String> getThumbs(String repoId, String galId)
    {
        String source = cacheUtils.getCacheDirForGallery(repoId, galId);
        return filenameEncoder.decodeThumbFilename(source);
    }
}
