package kw.tools.gallery.models;

import java.util.Set;

public class Repository
{
    private String path;
    private String id;
    private Set<Gallery> galleries;

    public Repository()
    {
    }

    public Repository(final String path)
    {
        this.path = path;
        this.id = getSafeName();
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
        this.id = getSafeName();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getSafeName()
    {
        return path.replaceAll("[^\\w]", "-");
    }

    public Set<Gallery> getGalleries()
    {
        return galleries;
    }

    public void setGalleries(Set<Gallery> galleries)
    {
        this.galleries = galleries;
    }

    public void addGallery(Gallery gallery)
    {
        this.galleries.add(gallery);
    }
}
