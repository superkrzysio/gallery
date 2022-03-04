package kw.tools.gallery.taskengine.utils;

import kw.tools.gallery.models.Task;

import javax.persistence.Entity;

@Entity
public class ErroneousTask extends Task
{
    private final String errorMessage;

    public ErroneousTask(String message)
    {
        this.errorMessage = message;
    }

    @Override
    public void run()
    {
        throw new RuntimeException(errorMessage);
    }
}
