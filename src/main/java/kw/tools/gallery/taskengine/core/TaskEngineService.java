package kw.tools.gallery.taskengine.core;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Control panel for task engine, the frontend for the whole engine. It is a standalone feature that simply works
 * in background and performs logic defined as tasks.
 * Tasks can persist their own data (they must extend kw.tools.gallery.taskengine.core.Task) and can perform
 * their own behaviour by implementing Runnable.
 * Big TODO: task recovery: clean task statuses in DB, when task engine was shut down or shut down abruptly, causing
 *           tasks to remain in RUNNING or QUEUED state but are not in queue.
 * Even bigger TODO: Possibly introduce transactions for tasks with some rollback logic
 * Other TODO: Implement task interruption to make use of ABORTED status and shutdownNow()
 * Moar TODO: Introduce @Idempotent annotation for tasks, marking them rerunnable.
 */
@Service
public class TaskEngineService
{
    private static final Logger LOG = LoggerFactory.getLogger(TaskEngineService.class);

    @Autowired
    private TaskEnginePolling taskEnginePolling;

    private Thread taskEngineMainThread;

    public static final int SHUTDOWN_TIMEOUT = 60;

    /**
     * Start the Task Engine thread according to current settings.
     * TODO: settings, e.g. thread count, poll interval, task fetch strategy
     */
    public synchronized void start()
    {
        requireTaskEngineRunning(false);
        taskEngineMainThread = Executors.defaultThreadFactory().newThread(taskEnginePolling);
        taskEngineMainThread.start();
    }

    /**
     * Graceful restart. Does a graceful stop and start, after applying any new settins.
     */
    public synchronized CompletableFuture<Void> restart()
    {
        requireTaskEngineRunning(true);
        return stop().thenRun(this::start);
    }

    /**
     * Graceful stop.
     * Will wait for current running tasks to finish. Tries to interrupt them.
     */
    public synchronized CompletableFuture<Void> stop()
    {
        requireTaskEngineRunning(true);
        return new CompletableFuture<Void>().completeAsync(() -> {
            LOG.debug("Interrupting taskEngineMainThread");
            taskEngineMainThread.interrupt();
            LOG.debug("Awaiting taskEngineMainThread shutdown");
            taskEnginePolling.awaitShutdown(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
            LOG.debug("taskEngineMainThread said to be shut down");
            return null;
        });
    }

    /**
     * Kill all the running tasks, update their status and shut down task engine immediately.
     */
    public synchronized void kill()
    {
        requireTaskEngineRunning(true);
        throw new NotImplementedException();
    }

    public int getQueueSize()
    {
        return taskEnginePolling.getQueueSize();
    }

    private void requireTaskEngineRunning(boolean state)
    {
        if (isRunning() != state)
        {
            throw new IllegalStateException("Task engine " + (state ? "not" : "already") + " running");
        }
    }

    /**
     * Liveness check
     */
    public boolean isRunning()
    {
        return taskEngineMainThread != null && taskEngineMainThread.isAlive();
    }

    public boolean hasFutureTask()
    {
        return taskEnginePolling.hasFutureTask();
    }

    public long getCompletedTaskCount()
    {
        return taskEnginePolling.getCompletedTaskCount();
    }
}
