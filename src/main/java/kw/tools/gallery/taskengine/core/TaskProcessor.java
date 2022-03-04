package kw.tools.gallery.taskengine.core;

import kw.tools.gallery.models.Task;
import kw.tools.gallery.persistence.TaskRepository;
import org.springframework.context.ApplicationContext;

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
        getTaskRepository().save(task);

    }

    private TaskRepository<?> getTaskRepository()
    {
        return ctx.getBean(TaskRepository.class);
    }
}
