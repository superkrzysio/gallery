package kw.tools.gallery.persistence;

import kw.tools.gallery.models.GalleryTask;
import kw.tools.gallery.taskengine.Task;
import kw.tools.gallery.taskengine.TaskRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GalleryTaskRepository<T extends GalleryTask> extends TaskRepository<GalleryTask>
{
    List<T> findByCategory(String category);

    @Query("SELECT t FROM Task t WHERE t.status IN (:statuses)")
    List<T> findByStatuses(@Param("statuses") List<Task.Status> statuses);

    @Query("SELECT t FROM GalleryTask t WHERE category=:category AND LENGTH(t.logs) > 0")
    List<T> findWithLogs(@Param("category") String category);

    @Query("SELECT t FROM Task t WHERE t.category=:category AND t.status IN (:statuses)")
    List<T> findByCategoryAndStatuses(@Param("category") String category, @Param("statuses") List<Task.Status> statuses);
}
