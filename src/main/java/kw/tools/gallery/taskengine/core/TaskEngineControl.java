package kw.tools.gallery.taskengine.core;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Control panel for task engine.
 */
@Component
public class TaskEngineControl
{
    @Autowired
    private TaskEnginePolling taskEnginePolling;

    private Thread taskEngineMainThread;

    public static final int SHUTDOWN_TIMEOUT = 60;

    /**
     * Start the Task Engine thread according to current settings.
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
            taskEngineMainThread.interrupt();
            taskEnginePolling.awaitShutdown(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
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
     *
     * @return
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
