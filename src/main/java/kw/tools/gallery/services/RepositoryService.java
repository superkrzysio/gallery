package kw.tools.gallery.services;

import kw.tools.gallery.CacheUtils;
import kw.tools.gallery.models.Repository;
import kw.tools.gallery.persistence.RepositoryRepository;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Repository management service
 */
@Service
public class RepositoryService
{
    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private RepositoryRepository repositoryRepository;

    public List<Repository> getAll()
    {
        return repositoryRepository.findAll();
    }

    public Repository add(String path)
    {
        Repository repository = new Repository();
        path = stripTrailingSlash(path);
        String id = stripHomeDirectory(path);
        repository.setPath(path);
        repository.setId(Repository.createSafeName(id));
        repositoryRepository.save(repository);
        return repository;
    }

    public Repository regenerate(String id)
    {
        throw new NotImplementedException("Deimplemented");
//        Optional<Repository> maybeRepo = repositoryRepository.findById(id);
//        if (maybeRepo.isEmpty())
//        {
//            return null;
//        }
//        Repository repo = maybeRepo.get();
//        cacheUtils.delete(repo.getId());
//        // todo: actually do not delete all galleries (and lose flags, tags, ratings, etc)
//        //  but only re-generate thumbnails for existing galleries and scan for any new
//        galleryRepository.deleteByRepositoryId(repo.getId());
////        generate(repo);
//        return repo;
    }

    public Repository delete(String id)
    {
        Repository repo = repositoryRepository.getById(id);
        cacheUtils.delete(repo.getId());
        repositoryRepository.delete(repo);
        return repo;
    }

    private static String stripTrailingSlash(String path)
    {
        if (path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private static String stripHomeDirectory(String path)
    {
        if (path.startsWith("/home/"))
        {
            List<String> parts = new ArrayList<>(Arrays.asList(path.split("/")));
            parts.subList(0, 3).clear();
            path = String.join("/", parts);
        }
        return path;
    }

    public Repository get(String repoId)
    {
        return repositoryRepository.getById(repoId);
    }
}
