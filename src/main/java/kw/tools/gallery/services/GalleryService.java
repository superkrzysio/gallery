package kw.tools.gallery.services;

import kw.tools.gallery.CacheUtils;
import kw.tools.gallery.models.Gallery;
import kw.tools.gallery.persistence.GalleryRepository;
import kw.tools.gallery.processing.ImageAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Gallery management service
 */
@Service
public class GalleryService
{
    private static final Logger LOG = LoggerFactory.getLogger(GalleryService.class);

    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private CdnService cdnService;

    @Autowired
    private ImageAccessor imageAccessor;

    @Value("${system.file.viewer.command}")
    private String fileViewerCommand;

    /**
     * Delete all the stored data related to the gallery and also <strong>all original files and the directory</strong>. <br />
     * It should not be ever called from a batch process for safety of your files - it <strong>will</strong> remove all non-image
     * files from the gallery.
     */
    @Transactional
    public void hardDelete(String galId)
    {
        Gallery gal = galleryRepository.getById(galId);
        cacheUtils.deletePhysically(gal.getPath());
        softDelete(gal);
    }

    /**
     * Delete the gallery from DB and thumbs. Leave original files untouched.
     */
    @Transactional
    public void softDelete(String galId)
    {
        softDelete(galleryRepository.getById(galId));
    }

    /**
     * Delete the gallery from DB and thumbs. Leave original files untouched.
     */
    @Transactional
    public void softDelete(Gallery gallery)
    {
        cacheUtils.delete(gallery.getRepositoryId(), gallery.getId());
        galleryRepository.delete(gallery);
    }

    /**
     * Get full Gallery object with thumbnails field calculated and filled with thumb URLs.
     */
    public List<Gallery> getAllFull(String repoId)
    {
        List<Gallery> galleries = galleryRepository.findByRepositoryId(repoId);
        for (Gallery gal : galleries)
        {
            gal.setThumbnails(
                    imageAccessor.getThumbs(repoId, gal.getId())
                            .stream()
                            .map(thumb -> cdnService.getCdnUrl(repoId, gal.getId(), thumb))
                            .collect(Collectors.toList())
            );
        }
        return galleries;
    }

    /**
     * Simply get all Galleries from DB.
     */
    public List<Gallery> getAll(String repoId)
    {
        return galleryRepository.findByRepositoryId(repoId);
    }

    /**
     * Create a gallery, count its images and save in DB.
     *
     * @return Created gallery ID or empty if the path has no images or if gallery already exists.
     */
    public Optional<String> createIfNotExist(String repoId, Path path)
    {
        if (galleryRepository.findByRepositoryIdAndPath(repoId, path.toString()).isPresent())
            return Optional.empty();

        int imageCount = imageAccessor.getImages(path).size();
        if (imageCount == 0)
        {
            LOG.warn("Empty gallery dir '{}', skipping save", path);
            return Optional.empty();
        }
        Gallery gallery = new Gallery();
        gallery.setName(path.getFileName().toString());
        gallery.setPath(path.toString());
        gallery.setPictureCount(imageCount);
        gallery.setRepositoryId(repoId);
        galleryRepository.save(gallery);
        return Optional.of(gallery.getId());
    }

    /**
     * Count galleries.
     */
    public int getGalleryCountForRepo(String id)
    {
        return galleryRepository.countByRepositoryId(id);
    }

    /**
     * TODO: switch to soft delete and do not delete all with a service, call softDelete from a task
     */
    public void deleteAll(String repoId)
    {
        galleryRepository.deleteByRepositoryId(repoId);
    }

    public void openInFileBrowser(String path)
    {
        // this application is not meant to be exposed to the internet
        try
        {
            Runtime.getRuntime().exec(String.format(fileViewerCommand, path));
        } catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Get path to the thumbnail directory for the given gallery.
     */
    public String getThumbnailDir(String repoId, String galId)
    {
        return cacheUtils.getCacheDirForGallery(repoId, galId);
    }

    /**
     * Check if the directory is a valid gallery. Returns false when e.g. path does not exists (because
     * it has been removed) or contains no images.
     */
    public boolean checkGallerySanity(Gallery gal)
    {
        if (!cacheUtils.galleryDirExists(gal.getPath()))
        {
            return false;
        }

        if (imageAccessor.getImages(gal.getPath()).size() == 0)
        {
            return false;
        }

        return true;
    }

}
