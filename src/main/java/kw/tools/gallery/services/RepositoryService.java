package kw.tools.gallery.services;

import kw.tools.gallery.CacheUtils;
import kw.tools.gallery.models.Gallery;
import kw.tools.gallery.models.Repository;
import kw.tools.gallery.persistence.GalleryRepository;
import kw.tools.gallery.persistence.RepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepositoryService
{
    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private GalleryRepository galleryRepository;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @Autowired
    private ProcessingService processingService;

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
        Optional<Repository> maybeRepo = repositoryRepository.findById(id);
        if (maybeRepo.isEmpty())
        {
            return null;
        }
        Repository repo = maybeRepo.get();
        cacheUtils.delete(repo.getId());
        // todo: actually do not delete all galleries (and lose flags, tags, ratings, etc)
        //  but only re-generate thumbnails for existing galleries and scan for any new
        galleryRepository.deleteByRepositoryId(repo.getId());
        generate(repo);
        return repo;
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
        for (ProcessingService.GalleryFolderDTO dto : processingService.fetchGalleries(repository.getPath()))
        {
            Gallery gallery = new Gallery();
            gallery.setName(dto.filename);
            gallery.setPath(dto.fullPath);
            gallery.setPictureCount(dto.pictureCount);
            gallery.setRepositoryId(repository.getId());
            galleryRepository.save(gallery);

            processingService.generate(repository.getId(), gallery.getId(), gallery.getPath());
        }
    }

    public Repository get(String repoId)
    {
        return repositoryRepository.getById(repoId);
    }
}
