package kw.tools.gallery.processing;

import java.util.List;

public interface Task extends Runnable
{
    enum Status
    {CREATED, WORKING, FINISHED, ERROR}

    void executeImpl() throws Exception;

    String getCategory();

    Status getStatus();

    List<String> getMessages();

    String getId();
}
