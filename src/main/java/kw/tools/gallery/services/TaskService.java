package kw.tools.gallery.services;

import kw.tools.gallery.processing.Task;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

    public Set<Task> getByCategory(String category)
    {
        return tasks.get(category);
    }

//    public Collection<Task> getAll()
//    {
//        return all.values();
//    }

//    public Collection<Task> getFinished()
//    {
//        return all.values().stream().filter(t -> t.status.equals(Task.Status.FINISHED)).collect(Collectors.toList());
//    }
}