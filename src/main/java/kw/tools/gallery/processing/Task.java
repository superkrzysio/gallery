package kw.tools.gallery.processing;

public interface Task extends Runnable
{
    void executeImpl() throws Exception;

    String getCategory();
}
