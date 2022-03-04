package kw.tools.gallery.taskengine.utils;

import kw.tools.gallery.models.Task;

import javax.persistence.Entity;

@Entity
public class EmptyTask extends Task
{
    @Override
    public void run()
    {
        // nothing
    }
}
