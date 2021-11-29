package kw.tools.gallery.processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class AbstractTask implements Task
{
    public String id = UUID.randomUUID().toString();
    public String category = "";

    public List<String> errorMessages = new ArrayList<>();

    public Status status = Status.CREATED;

    @Override
    public void run()
    {
        status = Status.WORKING;
        try
        {
            executeImpl();
        } catch (Exception e)
        {
            status = Status.ERROR;
            errorMessages.add(e.getMessage());
            return;
        }

        status = Status.FINISHED;
    }

    @Override
    public String getCategory()
    {
        return category;
    }

    @Override
    public Task.Status getStatus()
    {
        return status;
    }

    @Override
    public List<String> getMessages()
    {
        return Collections.unmodifiableList(errorMessages);
    }

    @Override
    public String getId()
    {
        return id;
    }
}
