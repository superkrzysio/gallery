package kw.tools.gallery.taskengine.core;

import kw.tools.gallery.models.Task;
import kw.tools.gallery.persistence.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Class responsible for polling new tasks into the executor queue.
 */
@Component
public class TaskEnginePolling implements Runnable
{
    @Autowired
    private TaskRepository<?> taskRepository;

    @Autowired
    private ApplicationContext applicationContext;

    private ThreadPoolExecutor threadPoolExecutor;

    private static final int THREAD_COUNT = 5;
    private static final int QUEUE_SIZE = 10;
    private static final Duration POLL_INTERVAL = Duration.of(500, ChronoUnit.MILLIS);

    /**
     * Main task engine loop. Keeps adding tasks to the executor queue until full or no more tasks left.
     */
    @Override
    public void run()
    {
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_COUNT);

        while(!Thread.interrupted())
        {
            if (threadPoolExecutor.getQueue().remainingCapacity() < QUEUE_SIZE)
            {
                Task task = fetchTask();
                if (task != null)
                {
                    threadPoolExecutor.execute(new TaskProcessor(task, applicationContext));
                }
                else
                {
                    sleep();
                }
            }
            else
            {
                sleep();
            }
        }
        threadPoolExecutor.shutdownNow();
    }

    public boolean awaitShutdown(long timeout, TimeUnit timeUnit)
    {
        try
        {
            return threadPoolExecutor.awaitTermination(timeout, timeUnit);
        } catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private void sleep()
    {
        try
        {
            Thread.sleep(POLL_INTERVAL.toMillis());
        } catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    // FIFO strategy - TODO: this logic does not fit this class
    private Task fetchTask()
    {
        synchronized (taskRepository)
        {
            return taskRepository.findFirstByStatusOrderByIdAsc(Task.Status.RUNNABLE).orElse(null);
        }
    }
}
