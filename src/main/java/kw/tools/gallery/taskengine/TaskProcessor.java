package kw.tools.gallery.taskengine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

/**
 * Class with additional logic wrapping the actual task execution
 */
public class TaskProcessor implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(TaskProcessor.class);

    private final ApplicationContext ctx;
    private final Task task;

    public TaskProcessor(Task task, ApplicationContext applicationContext)
    {
        this.ctx = applicationContext;
        this.task = task;

        // allow autowiring in tasks
        ctx.getAutowireCapableBeanFactory().autowireBeanProperties(this.task, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
    }

    @Override
    public void run()
    {
        task.setExecutionStartedTimestamp(System.currentTimeMillis());
        task.setStatus(Task.Status.RUNNING);
        getTaskRepository().save(task);
        try
        {
            task.run();
            task.setStatus(Task.Status.FINISHED);
        } catch (Exception e)
        {
            task.setStatus(Task.Status.ERROR);
            task.addLog(e.toString());
            if (LOG.isDebugEnabled())
            {
                LOG.debug(String.format("Error in task '%s': ", task.getName()), e);
            }
        }
        task.setExecutionFinishedTimestamp(System.currentTimeMillis());
        getTaskRepository().save(task);

    }

    private TaskRepository<?> getTaskRepository()
    {
        return ctx.getBean("taskRepository", TaskRepository.class);
    }
}
