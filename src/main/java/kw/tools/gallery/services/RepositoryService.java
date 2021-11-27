package kw.tools.gallery.services;

import kw.tools.gallery.CacheUtils;
import kw.tools.gallery.models.Gallery;
import kw.tools.gallery.models.Repository;
import kw.tools.gallery.persistence.GalleryRepository;
import kw.tools.gallery.persistence.RepositoryRepository;
import kw.tools.gallery.processing.AbstractThumbnailing;
import kw.tools.gallery.processing.DirCrawler;
import kw.tools.gallery.processing.Tasks;
import kw.tools.gallery.processing.Thumbnailing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;

@Service
public class RepositoryService
{
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

    public List<Repository> getAll()
    {
        return repositoryRepository.findAll();
    }

    public Repository add(String path)
    {
        Repository repository = new Repository();
        repository.setPath(stripTrailingSlash(path));

        repositoryRepository.save(repository);

        generate(repository);
        // todo: remove unfinished thumb creation or even introduce transaction layer for files and rollback it if something fails
        return repository;
    }

    public Repository regenerate(String id)
    {
        Optional<Repository> repo = repositoryRepository.findById(id);
        if (repo.isEmpty())
        {
            return null;
        }
        cacheUtils.delete(repo.get().getId());
        generate(repo.get());
        return repo.get();
    }

    public Repository delete(String id)
    {
        Repository repo = repositoryRepository.getById(id);
        cacheUtils.delete(repo.getId());
        repositoryRepository.delete(repo);
        return repo;
    }

    private String stripTrailingSlash(String path)
    {
        if (path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private void generate(Repository repository)
    {
        try
        {
            dirCrawler.forEach(repository.getPath(), path -> { // todo: this should be in background processing
                if (AbstractThumbnailing.getImages(path.toString()).isEmpty())
                {
                    System.out.println("Found empty path: " + path);
                    return;
                }
                Gallery gallery = new Gallery();
                gallery.setName(path.getFileName().toString());
                gallery.setPath(path.toString());
                gallery.setPictureCount(AbstractThumbnailing.getImages(path.toString()).size());
                gallery.setRepositoryId(repository.getId());
                galleryRepository.save(gallery);
                //                singleImageThumbnailing.generate(path.toString(), repository.getId());
                tasks.execute(() -> multiImageThumbnailing.generate(path.toString(), cacheUtils.generateGalleryDir(repository.getId(), gallery.getId())));
            });
        } catch (IOException e)
        {
            // todo
            throw new UncheckedIOException(e);
        }
    }

    public Repository get(String repoId)
    {
        return repositoryRepository.getById(repoId);
    }
}
