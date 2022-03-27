package kw.tools.gallery.processing;

import kw.tools.gallery.models.GalleryTask;
import kw.tools.gallery.services.GalleryService;
import kw.tools.gallery.services.RepositoryService;
import kw.tools.gallery.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Task that fully removes a repository, plus all galleries and cache. Leaves original files untouched.
 */
@Entity
public class RepositoryRemovingTask extends GalleryTask
{
    @Autowired
    @Transient
    private RepositoryService repositoryService;

    @Autowired
    @Transient
    private GalleryService galleryService;

    @Autowired
    @Transient
    private TaskService taskService;

    @Override
    public void run()
    {
        galleryService.getAll(category).forEach(gal -> galleryService.softDelete(gal));
        taskService.getByCategory(category).forEach(task -> taskService.delete(task));
        repositoryService.delete(category);
    }
}
