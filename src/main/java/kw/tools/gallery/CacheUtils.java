package kw.tools.gallery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

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

    public Path getCacheDirForRepository(String repositoryId)
    {
        return Path.of(cacheDir).resolve(repositoryId);
    }

    public String getCacheDirForGallery(String repoId, String galleryId)
    {
        return getCacheDirPathForGallery(repoId, galleryId).toString();
    }

    public Path getCacheDirPathForGallery(String repoId, String galleryId)
    {
        return Path.of(cacheDir).resolve(repoId).resolve(galleryId).toAbsolutePath();
    }

    private static String createDir(String rootDir, String outputDir, String outputId)
    {
        String fullpath = Path.of(rootDir).resolve(outputDir).resolve(outputId).toString();

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
            Files.walk(Path.of(cacheDir).resolve(repositoryId).resolve(galleryId))
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
