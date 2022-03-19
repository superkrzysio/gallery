package kw.tools.gallery.models;

import kw.tools.gallery.taskengine.Task;

import javax.persistence.Entity;

@Entity
public abstract class GalleryTask extends Task
{
    protected String category;

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }
}
