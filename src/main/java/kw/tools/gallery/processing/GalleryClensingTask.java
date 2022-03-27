package kw.tools.gallery.processing;

import kw.tools.gallery.models.GalleryTask;
import kw.tools.gallery.services.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Task that removes a gallery from DB if the original directory is not a sane gallery. Will not remove original
 * files or directory.
 */
@Entity
public class GalleryClensingTask extends GalleryTask
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
                galleryService.softDelete(gal.getId());
            }
        });
    }
}
