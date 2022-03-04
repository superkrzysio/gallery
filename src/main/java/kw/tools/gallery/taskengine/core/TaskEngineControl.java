package kw.tools.gallery.taskengine.core;

import java.util.concurrent.CompletableFuture;

/**
 * Control panel for task engine.
 */
public class TaskEngineControl
{

    /**
     * Start the Task Engine thread according to current settings.
     */
    public synchronized CompletableFuture<Void> start()
    {
        return null;
    }

    /**
     * Graceful restart. Does a graceful stop and start, after applying any new settins.
     */
    public synchronized CompletableFuture<Void> restart()
    {
        return null;
    }

    /**
     * Graceful stop.
     * Will wait for current running tasks to finish. Can try to interrupt them if possible.
     */
    public synchronized CompletableFuture<Void> stop()
    {
        return null;
    }

    /**
     * Kill all the running tasks, update their status and shut down task engine immediately.
     */
    public synchronized void kill()
    {

    }

    /**
     * Liveness check
     *
     * @return
     */
    public boolean isRunning()
    {
        return false;
    }
}
