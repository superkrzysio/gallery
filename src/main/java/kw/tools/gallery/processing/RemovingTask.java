package kw.tools.gallery.processing;

import kw.tools.gallery.models.GalleryTask;
import kw.tools.gallery.services.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class RemovingTask extends GalleryTask
{
    @Autowired
    @Transient
    private GalleryService galleryService;

    @Override
    public void run()
    {
        galleryService.getAllFull(category).forEach(gal -> {
            if (!galleryService.checkGallerySanity(gal))
            {
                galleryService.hardDelete(gal.getId());
            }
        });
    }
}
