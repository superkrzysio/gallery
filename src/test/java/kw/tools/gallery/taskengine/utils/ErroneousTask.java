package kw.tools.gallery.taskengine.utils;

import kw.tools.gallery.models.Task;

import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class ErroneousTask extends Task
{
    private final String errorMessage;

    public ErroneousTask()
    {
        errorMessage = null;
    }

    public ErroneousTask(String message)
    {
        this.errorMessage = message;
    }

    @Override
    public void run()
    {
        Objects.requireNonNull(errorMessage);
        throw new RuntimeException(errorMessage);
    }
}
