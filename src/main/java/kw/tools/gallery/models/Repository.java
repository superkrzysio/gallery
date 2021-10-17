package kw.tools.gallery.models;

public class Repository
{
    private String path;
    private String id;

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

    public String getSafeName()
    {
        return path.replaceAll("[^\\w]", "-");
    }
}
