package kw.tools.gallery.processing;

import kw.tools.gallery.models.GalleryTask;
import kw.tools.gallery.services.GalleryService;
import kw.tools.gallery.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.IOException;
import java.util.Optional;

/**
 * Task that crawls directory structure, creates a gallery entity for each nonempty folder and schedules thumbnail
 * generation.
 */
@Entity
public class ScanningTask extends GalleryTask
{
    @Column(length = 2000)
    private String path;

    @Autowired
    @Transient
    private DirCrawler dirCrawler;

    @Autowired
    @Transient
    private GalleryService galleryService;

    @Autowired
    @Transient
    private TaskService taskService;

    @Override
    public void run()
    {
        try
        {
            dirCrawler.forEach(path, p -> {
                Optional<String> galId = galleryService.createIfNotExist(category, p);
                galId.ifPresent(id -> taskService.createThumbnailingTask(
                        category,
                        p.toString(),
                        galleryService.getThumbnailDir(category, galId.get())
                ));
            });
        } catch (IOException e)
        {
            addLog(String.format("Error when processing path '%s': %s", path, e));
        }
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public void setDirCrawler(DirCrawler dirCrawler)
    {
        this.dirCrawler = dirCrawler;
    }

    public void setGalleryService(GalleryService galleryService)
    {
        this.galleryService = galleryService;
    }

    public void setTaskService(TaskService taskService)
    {
        this.taskService = taskService;
    }
}
