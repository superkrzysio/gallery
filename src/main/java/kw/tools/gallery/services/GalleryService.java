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

    @Transactional
    public void delete(String id)
    {
        Gallery gal = galleryRepository.getById(id);
        cacheUtils.delete(gal.getRepositoryId(), gal.getId());
        cacheUtils.deletePhysically(gal.getPath());
        galleryRepository.delete(gal);
    }

    /**
     * Get full Gallery object with thumbnails field calculated and filled with thumb URLs.
     *
     * @param repoId
     * @return
     */
    public List<Gallery> getFullForRepo(String repoId)
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
     * Create a gallery, count its images and save in DB.
     * @return Created gallery ID
     */
    public Optional<String> create(String repoId, Path path)
    {
        int imageCount = imageAccessor.getImages(path).size();
        if (imageCount == 0)
        {
            LOG.warn("Empty gallery dir '{}', skipping save", path.toString());
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

    public int getGalleryCountForRepo(String id)
    {
        return galleryRepository.countByRepositoryId(id);
    }

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

    public String getThumbnailDir(String repoId, String galId)
    {
        return cacheUtils.getCacheDirForGallery(repoId, galId);
    }
}
