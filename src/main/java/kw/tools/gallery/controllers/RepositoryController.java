package kw.tools.gallery.controllers;

import kw.tools.gallery.models.Repository;
import kw.tools.gallery.processing.DirCrawler;
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

@RestController
public class RepositoryController
{
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private Thumbnailing thumbnailing;

    @Autowired
    private DirCrawler dirCrawler;

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
            List<Repository> repos = (List<Repository>) session.createQuery("FROM Repository").list();
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
    public String viewRepository(@PathVariable("id") String id)
    {
        return "Viewing id: " + id;
    }

    @RequestMapping("/repositories/add")
    public RedirectView addRepository(@ModelAttribute Repository repository)
    {
        Transaction tx = null;
        Session session = sessionFactory.openSession();
        try
        {
            tx = session.beginTransaction();
            // just add if not exists
            if (session.get(Repository.class, repository.getId()) != null)
            {
                return new RedirectView("/repositories");
            }
            dirCrawler.forEach(repository.getPath(), path -> thumbnailing.generate(path));

            session.save(repository);
            tx.commit();
            return new RedirectView("/repositories");
        } catch (HibernateException | IOException e)
        {
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
}
