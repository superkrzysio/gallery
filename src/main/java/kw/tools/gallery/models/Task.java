package kw.tools.gallery.models;

import javax.persistence.*;

@Entity
public abstract class Task implements Runnable
{
    public enum Status
    {RUNNABLE, RUNNING, ERROR, FINISHED, ABORTED}

    @Id
    @GeneratedValue
    protected Integer id;

    @Enumerated
    protected Status status;

    @Access(AccessType.FIELD)
    protected StringBuilder logs = new StringBuilder();

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public String getLogs()
    {
        return logs.toString();
    }

    public void setLogs(String logs)
    {
        this.logs = new StringBuilder();
        this.logs.append(logs);
    }

    public void addLog(String log)
    {
        if (logs.length() > 0)
            logs.append("\n");
        logs.append(log);
    }

}
