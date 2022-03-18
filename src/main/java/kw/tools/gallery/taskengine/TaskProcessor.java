package kw.tools.gallery.taskengine;

import org.springframework.context.ApplicationContext;

/**
 * Class with additional logic wrapping the actual task execution
 */
public class TaskProcessor implements Runnable
{
    private final ApplicationContext ctx;
    private final Task task;

    public TaskProcessor(Task task, ApplicationContext applicationContext)
    {
        this.ctx = applicationContext;
        this.task = task;
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
            task.addLog(e.getMessage());
        }
        task.setExecutionFinishedTimestamp(System.currentTimeMillis());
        getTaskRepository().save(task);

    }

    private TaskRepository<?> getTaskRepository()
    {
        return ctx.getBean("taskRepository", TaskRepository.class);
    }
}
