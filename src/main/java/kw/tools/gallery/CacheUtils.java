package kw.tools.gallery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Component
public class CacheUtils
{
    private final static Logger LOG = LoggerFactory.getLogger(CacheUtils.class);
    private final static Map<String, Map<String, Path>> pathCache = new HashMap<>();

    @Value("${cache.dir}")
    private String cacheDir;

    public String getCacheDirForGallery(String repoId, String galleryId)
    {
        return getCacheDirPathForGallery(repoId, galleryId).toString();
    }

    public Path getCacheDirPathForGallery(String repoId, String galleryId)
    {
        return pathCache.computeIfAbsent(repoId, k -> new HashMap<>())
                .computeIfAbsent(galleryId, g -> {
                    Path dir = Path.of(cacheDir).resolve(repoId).resolve(galleryId).toAbsolutePath();
                    createDir(dir);
                    return dir;
                });
    }

    private static void createDir(Path dir)
    {
        try
        {
            Files.createDirectories(dir);
        } catch (IOException e)
        {
            LOG.error("Could not create cache dir: " + dir, e);
            throw new UncheckedIOException(e);
        }
    }

    public void deletePhysically(String folder)
    {
        try
        {
            Files.walk(Path.of(folder))
                    .sorted(Comparator.reverseOrder())
                    .forEach(this::delete);
        } catch (IOException e)
        {
            LOG.error(String.format("Could not traverse repository %s", folder));
        }
    }

    public void delete(String repositoryId)
    {
        try
        {
            Files.walk(Path.of(cacheDir).resolve(repositoryId))
                    .sorted(Comparator.reverseOrder())
                    .forEach(this::delete);
        } catch (IOException e)
        {
            LOG.error(String.format("Could not traverse repository %s", repositoryId));
        }
    }

    public void delete(String repositoryId, String galleryId)
    {
        try
        {
            Files.walk(getCacheDirPathForGallery(repositoryId, galleryId))
                    .sorted(Comparator.reverseOrder())
                    .forEach(this::delete);
        } catch (IOException e)
        {
            LOG.error(String.format("Could not traverse gallery %s in repo %s", galleryId, repositoryId));
        }
    }

    private void delete(Path file)
    {
        try
        {
            Files.delete(file);
        } catch (IOException e)
        {
            LOG.error(String.format("Could not delete file or directory: %s", file), e);
        }
    }
}
