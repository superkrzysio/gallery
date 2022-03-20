package kw.tools.gallery.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Repository
{
    @Id
    private String id;

    private String path;

    public Repository()
    {
    }

    public Repository(final String path)
    {
        this.path = path;
        this.id = createSafeName(path);
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
        this.id = createSafeName(path);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public static String createSafeName(String path)
    {
        return path.replaceAll("[^\\w]", "-");
    }

}
