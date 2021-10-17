package kw.tools.gallery.controllers;

import kw.tools.gallery.models.Repository;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@RestController
public class RepositoryController
{
    @Autowired
    private SessionFactory sessionFactory;


    @RequestMapping("/repositories")
    public ModelAndView listRepositories(@RequestParam(name="error", required = false) final String error)
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

        try (Session session = sessionFactory.openSession())
        {
            tx = session.beginTransaction();
            // just add if not exists
            if (session.get(Repository.class, repository.getId()) == null)
            {
                session.save(repository);
            }
            tx.commit();
            return new RedirectView("/repositories");
        } catch (HibernateException e)
        {
            if (tx != null && tx.isActive())
            {
                tx.rollback();
            }
            System.err.println(e.getMessage());
            return new RedirectView("/repositories?error=1");
        }
    }
}
