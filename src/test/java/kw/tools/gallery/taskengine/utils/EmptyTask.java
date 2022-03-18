package kw.tools.gallery.taskengine.utils;

import kw.tools.gallery.taskengine.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;

@Entity
public class EmptyTask extends Task
{
    private static final Logger LOG = LoggerFactory.getLogger(EmptyTask.class);

    @Override
    public void run()
    {
        LOG.info("Empty task running [" + id + "]");
        // nothing
    }
}
