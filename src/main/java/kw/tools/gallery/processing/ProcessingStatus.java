package kw.tools.gallery.processing;

import org.apache.tomcat.jni.Proc;

public class ProcessingStatus
{
    public enum WorkStatus { IDLE, WORKING }

    public WorkStatus status = WorkStatus.IDLE;
    public int done;
    public int toDo;

    public ProcessingStatus done(int done)
    {
        this.done = done;
        return this;
    }

    public ProcessingStatus toDo(int toDo)
    {
        this.toDo = toDo;
        return this;
    }

    public ProcessingStatus status(WorkStatus status)
    {
        this.status = status;
        return this;
    }

    public int getProgress()
    {
        return done / toDo;
    }

}
