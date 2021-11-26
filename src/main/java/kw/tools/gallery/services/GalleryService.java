package kw.tools.gallery.services;

import kw.tools.gallery.CacheUtils;
import kw.tools.gallery.models.Gallery;
import kw.tools.gallery.processing.DirCrawler;
import kw.tools.gallery.processing.Tasks;
import kw.tools.gallery.processing.Thumbnailing;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GalleryService
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

    public void delete(String id)
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
    }
}
