package kw.tools.gallery.services;

import kw.tools.gallery.CacheUtils;
import kw.tools.gallery.models.Repository;
import kw.tools.gallery.persistence.RepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    /**
     * Create a repository and save it into DB.<br/>
     *
     * @return Created repository object.
     */
    // Possible todo: inconsistent signature with GalleryService.create() which performs similar operation.
    public Repository add(String path)
    {
        Repository repository = new Repository();
        path = stripTrailingSlash(path);
        String id = stripHomeDirectory(path);
        id = removeEdgeDashes(id);
        id = removeMultipleDashes(id);
        repository.setPath(path);
        repository.setId(Repository.createSafeName(id));
        repositoryRepository.save(repository);
        return repository;
    }

    private String removeMultipleDashes(String id)
    {
        while (id.contains("--"))
            id = id.replaceAll("--", "-");
        return id;
    }

    private String removeEdgeDashes(String id)
    {
        while (id.startsWith("-"))
            id = id.substring(1);
        while (id.endsWith("-"))
            id = id.substring(0, id.length() - 1);
        return id;
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

    public Optional<Repository> get(String repoId)
    {
        return repositoryRepository.findById(repoId);
    }
}
