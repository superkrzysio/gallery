package kw.tools.gallery.taskengine;

import org.springframework.context.ApplicationContext;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Task implements Runnable
{
    public enum Status
    {RUNNABLE, RUNNING, ERROR, FINISHED, ABORTED, QUEUED}

    @Id
    @GeneratedValue
    protected Integer id;

    @Enumerated
    @Column(nullable = false)
    protected Status status;

    @Lob
    @Column(nullable = false)
    protected String logs;

    @Column(nullable = false)
    protected Long createdTimestamp;

    protected Long executionStartedTimestamp;

    protected Long executionFinishedTimestamp;

    @Transient
    protected ApplicationContext applicationContext;

    @PrePersist
    private void preInsert()
    {
        if (this.createdTimestamp == null)
            this.createdTimestamp = System.currentTimeMillis();

        if (this.logs == null)
            this.logs = "";

        if (this.status == null)
            this.status = Status.RUNNABLE;
    }

    public Integer getId()
    {
        return id;
    }

    public String getName()
    {
        return this.getClass().getSimpleName() + "-" + id;
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
        return logs;
    }

    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    public void setLogs(String logs)
    {
        this.logs = logs;
    }

    public void addLog(String log)
    {
        if (logs.length() > 0)
            logs += "\n";
        logs += log;
    }

    @Override
    public String toString()
    {
        return "Task {" + this.getClass().getSimpleName() + ", id=" + id + "}";
    }

    public long getCreatedTimestamp()
    {
        return createdTimestamp;
    }

    public long getExecutionStartedTimestamp()
    {
        return executionStartedTimestamp;
    }

    public void setExecutionStartedTimestamp(long executionStartedTimestamp)
    {
        this.executionStartedTimestamp = executionStartedTimestamp;
    }

    public long getExecutionFinishedTimestamp()
    {
        return executionFinishedTimestamp;
    }

    public void setExecutionFinishedTimestamp(long executionFinishedTimestamp)
    {
        this.executionFinishedTimestamp = executionFinishedTimestamp;
    }
}
