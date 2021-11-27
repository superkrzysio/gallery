package kw.tools.gallery.services;

import kw.tools.gallery.CacheUtils;
import kw.tools.gallery.models.Gallery;
import kw.tools.gallery.persistence.GalleryRepository;
import kw.tools.gallery.persistence.RepositoryRepository;
import kw.tools.gallery.processing.DirCrawler;
import kw.tools.gallery.processing.Tasks;
import kw.tools.gallery.processing.Thumbnailing;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GalleryService
{
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private DirCrawler dirCrawler;

    @Autowired
    private Tasks tasks;

    @Autowired
    private Thumbnailing singleImageThumbnailing;

    @Autowired
    private Thumbnailing multiImageThumbnailing;

    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private RepositoryRepository repositoryRepository;

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

    public List<Gallery> getFullForRepo(String repoId)
    {
        List<Gallery> galleries = galleryRepository.findByRepositoryId(repoId);
        for (Gallery gal : galleries)
        {
            gal.setThumbnails(multiImageThumbnailing.retrieve(cacheUtils.getCacheDirForGallery(repoId, gal.getId()))
                    .stream()
                    .map(thumb -> String.format("/cdn/%s/%s/%s", repoId, gal.getId(), thumb))
                    // todo: very obfuscated way to refer to other endpoint :(
                    // and endpoint knowledge should be in the Vaadin view layer
                    .collect(Collectors.toList()));
        }
        return galleries;
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
        // critical security threat, but this application is not meant to be exposed to internet
        try
        {
            Runtime.getRuntime().exec(String.format(fileViewerCommand, path));
        } catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }
}
