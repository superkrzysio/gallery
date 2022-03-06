package kw.tools.gallery.taskengine.utils;

import kw.tools.gallery.taskengine.core.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class ChainingTask extends Task
{
    private static final Logger LOG = LoggerFactory.getLogger(ChainingTask.class);

    @Transient
    private EmptyTaskRepository emptyTaskRepository;

    @Transient
    private static EmptyTask chainedTask;           // dirty, but can't serialize other task

    public ChainingTask()
    {
    }

    public ChainingTask(EmptyTaskRepository repository)
    {
        LOG.info("Chaining task creating [" + id + "]");
        this.emptyTaskRepository = repository;
    }

    @Override
    public void run()
    {
        LOG.info("Chaining task executing [" + id + "]");
        chainedTask = new EmptyTask();
        emptyTaskRepository.save(chainedTask);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        super.setApplicationContext(applicationContext);
        emptyTaskRepository = applicationContext.getBean(EmptyTaskRepository.class);
    }

    public EmptyTask getChainedTask()
    {
        return chainedTask;
    }
}
