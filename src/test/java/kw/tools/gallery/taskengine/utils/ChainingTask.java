package kw.tools.gallery.taskengine.utils;

import kw.tools.gallery.models.Task;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Objects;

@Entity
public class ChainingTask extends Task
{
    @Transient
    private final EmptyTaskRepository emptyTaskRepository;

    @Transient
    private final EmptyTask chainedTask;

    public ChainingTask()
    {
        emptyTaskRepository = null;
        chainedTask = null;
    }

    public ChainingTask(EmptyTaskRepository repository)
    {
        this.emptyTaskRepository = repository;
        this.chainedTask = new EmptyTask();
    }

    @Override
    public void run()
    {
        Objects.requireNonNull(emptyTaskRepository);
        emptyTaskRepository.save(chainedTask);
    }

    public EmptyTask getChainedTask()
    {
        return chainedTask;
    }
}
