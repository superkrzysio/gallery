package kw.tools.gallery.controllers;

import kw.tools.gallery.CacheUtils;
import kw.tools.gallery.models.Gallery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GalleryController
{
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private CacheUtils cacheUtils;

    @RequestMapping("/gallery/delete/{id}")
    public String deletePhysically(@PathVariable("id") String id)
    {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession())
        {
            tx = session.beginTransaction();
            Gallery gal = session.load(Gallery.class, id);
            cacheUtils.deletePhysically(gal.getPath());
            cacheUtils.delete(gal.getRepository().getId(), gal.getId());
            session.delete(gal);
            gal.getRepository().getGalleries().remove(gal);
            session.save(gal.getRepository());
            tx.commit();
        }
        return "OK";
    }
}
