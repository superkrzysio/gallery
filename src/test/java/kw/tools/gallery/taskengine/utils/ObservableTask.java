package kw.tools.gallery.taskengine.utils;

import kw.tools.gallery.models.Task;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.function.Consumer;

@Entity
public class ObservableTask extends Task
{
    @Transient
    private final Consumer<Task> observer;

    public ObservableTask(Consumer<Task> observer)
    {
        this.observer = observer;
    }

    @Override
    public synchronized void run()
    {
        try
        {
            Thread.sleep(100);
            observer.accept(this);
        } catch (InterruptedException e)
        {
            System.err.println("Reporting test task interrupted");
        }
    }
}
