package kw.tools.gallery.services;

import kw.tools.gallery.models.GalleryTask;
import kw.tools.gallery.persistence.GalleryTaskRepository;
import kw.tools.gallery.taskengine.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Task managing class, designed to fetch, create and save tasks.
 * Saving a task means picking it up by the task engine, so there is no direct dependency on task engine,
 * except this comment.
 */
@Service
public class TaskService
{
    @Autowired
    private GalleryTaskRepository<GalleryTask> taskRepository;

    /**
     * Get tasks by category. Category must be set.
     */
    public List<GalleryTask> getByCategory(String category)
    {
        requireCategory(category);
        return taskRepository.findByCategory(category);
    }

    /**
     * Return tasks by category name and list of statuses. Category must be set.
     */
    public List<GalleryTask> getByCategoryAndStatus(String category, Task.Status... statuses)
    {
        requireCategory(category);
        return taskRepository.findByCategoryAndStatuses(category, Arrays.asList(statuses));
    }

    /**
     * Get ALL tasks by statuses.
     */
    public List<GalleryTask> getByStatus(Task.Status... statuses)
    {
        return taskRepository.findByStatuses(Arrays.asList(statuses));
    }

    public List<Task.Status> getStatusesForCategory(String category)
    {
        return getByCategory(category).stream().map(Task::getStatus).collect(Collectors.toList());
    }

    public List<GalleryTask> getWithLogsOnly(String category)
    {
        requireCategory(category);
        return taskRepository.findWithLogs(category);
    }

    private static void requireCategory(String category)
    {
        Objects.requireNonNull(category);
        if (category.isBlank())
        {
            throw new IllegalArgumentException("Category must be set but found blank");
        }
    }

    public List<Task> getAll()
    {
        return taskRepository.findAll();
    }
}