package kw.tools.gallery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CacheUtils
{
    private final static Logger LOG = LoggerFactory.getLogger(CacheUtils.class);
    private final static Map<String, Map<String, Path>> pathCache = new ConcurrentHashMap<>();

    @Value("${cache.dir}")
    private String cacheDir;

    public String getCacheDirForGallery(String repoId, String galleryId)
    {
        return getCacheDirPathForGallery(repoId, galleryId).toString();
    }

    public Path getCacheDirPathForGallery(String repoId, String galleryId)
    {
        return pathCache.computeIfAbsent(repoId, k -> new ConcurrentHashMap<>())
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

    public boolean galleryDirExists(String path)
    {
        return Files.exists(Path.of(path));
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

    /**
     * Delete cache for the whole repository.
     */
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

    /**
     * Delete thumbnails and possibly other cached data for the given gallery.
     */
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

    /**
     * Simply clear the directory contents. <br />
     * Not recursive. Does not remove directories and their contents. <br />
     * Safety check is performed if <tt>path</tt> is inside <tt>cache.dir</tt>.
     */
    public void clear(String path)
    {
        if (!path.startsWith(cacheDir))
        {
            throw new IllegalArgumentException(String.format("Unsafe call of clear(path) method, '%s' is not inside cache dir '%s'", path, cacheDir));
        }

        try
        {
            for (Iterator<Path> iter = Files.list(Path.of(path)).iterator(); iter.hasNext(); )
            {
                Path p = iter.next();
                if (Files.isRegularFile(p, LinkOption.NOFOLLOW_LINKS))
                    Files.delete(p);
            }
        } catch (IOException e)
        {
            LOG.error(String.format("Unsuccessful clearing '%s', because: ", e));
            throw new UncheckedIOException(String.format("Could not clear: '%s'", path), e);
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
