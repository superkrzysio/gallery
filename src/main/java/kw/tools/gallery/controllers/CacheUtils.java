package kw.tools.gallery.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class CacheUtils
{
    private final static Logger LOG = LoggerFactory.getLogger(CacheUtils.class);
    public final static String PATH_SEPARATOR = "/";

    @Value("${cache.dir}")
    private String cacheDir;

    public String generateGalleryDir(String repositoryId, String galleryId)
    {
        return createDir(cacheDir, repositoryId, galleryId);
    }

    private static String createDir(String rootDir, String outputDir, String outputId)
    {
        String fullpath = rootDir + PATH_SEPARATOR + outputDir + PATH_SEPARATOR + outputId;

        if (LOG.isDebugEnabled())
        {
            LOG.debug("Creating cache dir: " + fullpath);
        }
        try
        {
            return Files.createDirectories(Path.of(fullpath)).toString() + PATH_SEPARATOR;
        } catch (IOException e)
        {
            LOG.error("Could not create cache dir: " + fullpath, e);
            return null;
        }
    }
}
