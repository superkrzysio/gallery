package kw.tools.gallery.processing;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Task factory class, allows managing tasks handles.
 */
@Component
public class Tasks
{
    private final HashMap<String, Task> all = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public Task execute(Runnable from, String id)
    {
        Task t = new Task(from, id);
        return executeTask(t);
    }

    public Task execute(Runnable from)
    {
        Task t = new Task(from);
        return executeTask(t);
    }

    private Task executeTask(Task t)
    {
        all.put(t.id, t);
        executor.execute(t);
        return t;
    }

    public Task getById(String id)
    {
        return all.get(id);
    }

    public Collection<Task> getAll()
    {
        return all.values();
    }

    public Collection<Task> getFinished()
    {
        return all.values().stream().filter(t -> t.status.equals(Task.Status.FINISHED)).collect(Collectors.toList());
    }

    public static class Task implements Runnable
    {
        public final Runnable runnable;
        public final String id;

        public enum Status
        {CREATED, WORKING, FINISHED}

        public Status status = Status.CREATED;

        public Task(Runnable runnable)
        {
            this.runnable = runnable;
            id = UUID.randomUUID().toString();
        }

        public Task(Runnable runnable, String id)
        {
            this.runnable = runnable;
            this.id = id;
        }

        @Override
        public void run()
        {
            status = Status.WORKING;
            try
            {
                runnable.run();
            } finally
            {
                status = Status.FINISHED;
            }
        }
    }
}