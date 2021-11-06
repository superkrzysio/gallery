package kw.tools.gallery.controllers;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RepositoryController
{
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private Thumbnailing singleImageThumbnailing;

    @Autowired
    private Thumbnailing multiImageThumbnailing;

    @Autowired
    private DirCrawler dirCrawler;

    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private Tasks tasks;

    private final static Logger LOG = LoggerFactory.getLogger(RepositoryController.class);

    @RequestMapping("/repositories")
    public ModelAndView listRepositories(@RequestParam(name = "error", required = false) final String error)
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("repositories");

        // todo: clean code
        Transaction tx = null;
        try (Session session = sessionFactory.openSession())
        {
            tx = session.beginTransaction();
            List<Repository> repos = session.createQuery("FROM Repository").list();
            tx.rollback();
            modelAndView.addObject("repositories", repos);
        } catch (HibernateException e)
        {
            if (tx != null && tx.isActive())
            {
                tx.rollback();
            }
            modelAndView.addObject("error", e.getMessage());
        }
        if (error != null && !error.isBlank())
        {
            modelAndView.addObject("error", "Additional error");
        }
        return modelAndView;
    }

    @RequestMapping("/repositories/view/{id}")
    public ModelAndView viewRepository(@PathVariable("id") String id)
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("view-single-repository");

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
            modelAndView.addObject("repository", repo);
            tx.rollback();
        } catch (HibernateException e)
        {
            if (tx != null && tx.isActive())
            {
                tx.rollback();
            }
        }
        return modelAndView;
    }

    @RequestMapping("/repositories/add")
    public RedirectView addRepository(@ModelAttribute Repository repository)
    {
        repository.setPath(stripTrailingSlash(repository.getPath()));

        // todo: clean code
        Transaction tx = null;
        Session session = sessionFactory.openSession();
        try
        {
            tx = session.beginTransaction();
            // skip if exists
            if (session.get(Repository.class, repository.getId()) != null)
            {
                return new RedirectView("/repositories");
            }

            session.save(repository);

            // 1. create repository in db
            // task:
            // 2. find all valid galleries
            // 3. create entries in db for them
            // 4. generate thumbnails for each gallery

            generate(repository, session);

            tx.commit();
            return new RedirectView("/repositories");
        } catch (HibernateException | IOException e)
        {
            // todo: remove unfinished thumb creation or even introduce transaction layer for files and rollback it
            LOG.error("Failed to add new repository", e);
            return new RedirectView("/repositories?error=1");
        } finally
        {
            if (tx != null && tx.getStatus().canRollback())
            {
                tx.rollback();
            }
            session.close();
        }
    }

    @RequestMapping("/repositories/regenerate/{repo}")
    public RedirectView regenerateRepository(@PathVariable("repo") String id)
    {
        Transaction tx = null;
        Session session = sessionFactory.openSession();
        try
        {
            tx = session.beginTransaction();
            Repository repo = session.get(Repository.class, id);
            if (repo == null)
            {
                return new RedirectView("/repositories");
            }
            cacheUtils.delete(repo.getId());
            generate(repo, session);
            return new RedirectView("/repositories");
        } catch (IOException e)
        {
            LOG.error("Failed to regenerate a repository", e);
            return new RedirectView("/repositories?error=1");
        } finally
        {
            if (tx != null && tx.getStatus().canRollback())
            {
                tx.rollback();
            }
            session.close();
        }
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

    @RequestMapping("/repositories/delete/{id}")
    public RedirectView deleteRepository(@PathVariable("id") String id)
    {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession())
        {
            tx = session.beginTransaction();
            Repository repo = session.load(Repository.class, id);
            session.delete(repo);
            cacheUtils.delete(repo.getId());
            tx.commit();
        }
        return new RedirectView("/repositories");
    }

    private String stripTrailingSlash(String path)
    {
        if (path.endsWith("/"))
        {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
