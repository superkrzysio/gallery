package kw.tools.gallery.taskengine.utils;

import kw.tools.gallery.models.Task;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class ChainingTask extends Task
{
    @Transient
    private final EmptyTaskRepository emptyTaskRepository;

    @Transient
    private final EmptyTask chainedTask;

    public ChainingTask(EmptyTaskRepository repository)
    {
        this.emptyTaskRepository = repository;
        this.chainedTask = new EmptyTask();
    }

    @Override
    public void run()
    {
        emptyTaskRepository.save(chainedTask);
    }

    public EmptyTask getChainedTask()
    {
        return chainedTask;
    }
}
