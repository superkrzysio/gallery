package kw.tools.gallery.taskengine.utils;

import kw.tools.gallery.taskengine.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class ErroneousTask extends Task
{
    private static final Logger LOG = LoggerFactory.getLogger(ErroneousTask.class);

    private String errorMessage;

    public ErroneousTask()
    {
    }

    public ErroneousTask(String message)
    {
        LOG.info("Erroneous task creating");
        this.errorMessage = message;
    }

    @Override
    public void run()
    {
        LOG.info("Erroneous task running");
        Objects.requireNonNull(errorMessage);
        throw new RuntimeException(errorMessage);
    }
}
