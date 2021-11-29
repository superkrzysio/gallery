package kw.tools.gallery.services;

import kw.tools.gallery.processing.Task;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Task factory class, allows managing tasks handles.
 */
@Service
public class TaskService
{
    private final HashMap<String, Set<Task>> tasks = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

//    public Task execute(Runnable from, String id)
//    {
//        Task t = new Task(from, id);
//        return executeTask(t);
//    }
//
//    public Task execute(Runnable from)
//    {
//        Task t = new Task(from);
//        return executeTask(t);
//    }

    public Task execute(Task t)
    {
        if (!tasks.containsKey(t.getCategory()))
        {
            tasks.put(t.getCategory(), new HashSet<>());
        }
        tasks.get(t.getCategory()).add(t);
        executor.execute(t);
        return t;
    }

    public List<Task> getByCategory(String category)
    {
        return category.isBlank() ? getAllTasksStream().collect(Collectors.toList()) : new ArrayList<>(getNonNullForCategory(category));
    }

    /**
     * Return tasks by category name and list of statuses. If category is blank, filter all tasks by statuses.
     *
     * @param category
     * @param statuses
     * @return
     */
    public List<Task> getByCategoryStatus(String category, Task.Status... statuses)
    {
        Stream<Task> selected = category.isBlank() ? getAllTasksStream() : getByCategory(category).stream();
        return selected
                .filter(t -> Arrays.asList(statuses).contains(t.getStatus()))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Task> getByStatus(Task.Status... statuses)
    {
        return getAllTasks()
                .stream()
                .filter(t -> Arrays.asList(statuses).contains(t.getStatus()))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Task.Status> getStatuses(String category)
    {
        return getNonNullForCategory(category).stream().map(Task::getStatus).collect(Collectors.toList());
    }

    public void clearByCategory(String category)
    {
        tasks.remove(category);
    }

    private Set<Task> getNonNullForCategory(String category)
    {
        return tasks.get(category) != null ? tasks.get(category) : new HashSet<>();
    }

    public List<Task> getAllTasks()
    {
        return getAllTasksStream().collect(Collectors.toList());
    }

    private Stream<Task> getAllTasksStream()
    {
        return tasks.values().stream().flatMap(Collection::stream);
    }
}