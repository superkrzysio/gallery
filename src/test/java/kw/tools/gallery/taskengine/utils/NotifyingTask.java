package kw.tools.gallery.taskengine.utils;

import kw.tools.gallery.models.Task;

import javax.persistence.Entity;

@Entity
public class NotifyingTask extends Task
{
    @Override
    public synchronized void run()
    {
        try
        {
            this.wait();        // waiting for main test thread
            Thread.sleep(100);
            this.notify();
        } catch (InterruptedException e)
        {
            System.err.println("Test task interrupted");
        }
    }
}
