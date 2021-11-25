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



    private final static Logger LOG = LoggerFactory.getLogger(RepositoryController.class);

//    @RequestMapping("/repositories/view/{id}")
//    public ModelAndView viewRepository(@PathVariable("id") String id)
//    {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("view-single-repository");
//
//        Transaction tx = null;
//        try (Session session = sessionFactory.openSession())
//        {
//            tx = session.beginTransaction();
//            Repository repo = session.load(Repository.class, id);
//            repo.getGalleries().stream().forEach(
//                    gal -> gal.setThumbnails(
//                            multiImageThumbnailing.retrieve(cacheUtils.getCacheDirForGallery(repo.getId(), gal.getId()))
//                                    .stream()
//                                    .map(thumb -> String.format("/cdn/%s/%s/%s", repo.getId(), gal.getId(), thumb))
//                                    // todo: very obfuscated way to refer to other endpoint :(
//                                    .collect(Collectors.toList())
//                    )
//            );
//            modelAndView.addObject("repository", repo);
//            tx.rollback();
//        } catch (HibernateException e)
//        {
//            if (tx != null && tx.isActive())
//            {
//                tx.rollback();
//            }
//        }
//        return modelAndView;
//    }

}
