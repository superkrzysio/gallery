package kw.tools.gallery.services;

import kw.tools.gallery.CacheUtils;
import kw.tools.gallery.models.Gallery;
import kw.tools.gallery.models.Repository;
import kw.tools.gallery.processing.AbstractThumbnailing;
import kw.tools.gallery.processing.DirCrawler;
import kw.tools.gallery.processing.Tasks;
import kw.tools.gallery.processing.Thumbnailing;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RepositoryService
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

    public List<Repository> getAll()
    {
        List<Repository> results;
        Transaction tx = null;
        try (Session session = sessionFactory.openSession())
        {
            tx = session.beginTransaction();
            results = session.createQuery("FROM Repository").list();
            tx.rollback();
            return results;
        } catch (HibernateException e)
        {
            if (tx != null && tx.isActive())
            {
                tx.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    public Repository add(String path)
    {
        Repository repository = new Repository();
        repository.setPath(stripTrailingSlash(path));

        // todo: clean code
        Transaction tx = null;
        Session session = sessionFactory.openSession();
        try
        {
            tx = session.beginTransaction();
            // skip if exists
            Repository found = session.get(Repository.class, repository.getId());
            if (found != null)
            {
                return found;
            }

            session.save(repository);

            // 1. create repository in db
            // task:
            // 2. find all valid galleries
            // 3. create entries in db for them
            // 4. generate thumbnails for each gallery

            generate(repository, session);

            tx.commit();
            return repository;
        } catch (HibernateException | IOException e)
        {
            // todo: remove unfinished thumb creation or even introduce transaction layer for files and rollback it
            throw new RuntimeException(e);
        } finally
        {
            if (tx != null && tx.getStatus().canRollback())
            {
                tx.rollback();
            }
            session.close();
        }
    }

    public Repository regenerate(String id)
    {
        Transaction tx = null;
        Session session = sessionFactory.openSession();
        try
        {
            tx = session.beginTransaction();
            Repository repo = session.get(Repository.class, id);
            if (repo == null)
            {
                return null;
            }
            cacheUtils.delete(repo.getId());
            generate(repo, session);
            return repo;
        } catch (IOException e)
        {
            throw new UncheckedIOException(e);
        } finally
        {
            if (tx != null && tx.getStatus().canRollback())
            {
                tx.rollback();
            }
            session.close();
        }
    }

    public Repository delete(String id)
    {
        Transaction tx = null;
        Repository repo;
        try (Session session = sessionFactory.openSession())
        {
            tx = session.beginTransaction();
            repo = session.load(Repository.class, id);
            session.delete(repo);
            cacheUtils.delete(repo.getId());
            tx.commit();
        }
        return repo;
    }

    public Repository getFullForId(String id)
    {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession())
        {
            tx = session.beginTransaction();
            Repository repo = session.load(Repository.class, id);
            repo.getGalleries().stream().forEach(
                    gal -> gal.setThumbnails(
                            multiImageThumbnailing.retrieve(cacheUtils.getCacheDirForGallery(repo.getId(), gal.getId()))
                                    .stream()
                                    .map(thumb -> String.format("/cdn/%s/%s/%s", repo.getId(), gal.getId(), thumb))
                                    // todo: very obfuscated way to refer to other endpoint :(
                                    .collect(Collectors.toList())
                    )
            );
            tx.rollback();
            return repo;
        } catch (HibernateException e)
        {
            throw e;
//            if (tx != null && tx.isActive())
//            {
//                tx.rollback();
//            }
        }
    }

    private String stripTrailingSlash(String path)
    {
        if (path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private void generate(Repository repository, Session session) throws IOException
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
            gallery.setRepository(repository);
            session.save(gallery);
            System.out.println("Gallery " + gallery.getId() + " saved. Path: " + gallery.getPath());

//                singleImageThumbnailing.generate(path.toString(), repository.getId());
            tasks.execute(() -> multiImageThumbnailing.generate(path.toString(), cacheUtils.generateGalleryDir(repository.getId(), gallery.getId())));
        });
    }
}
