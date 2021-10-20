package kw.tools.gallery.models;

import net.bytebuddy.utility.RandomString;

public class Gallery
{
    private static final int ID_LENGTH = 20;
    private int pictureCount;
    private String path;
    private String name;
    private int rating;
    private Repository repository;

    private final String id;

    public Gallery()
    {
        id = RandomString.make(ID_LENGTH);
    }

    public int getPictureCount()
    {
        return pictureCount;
    }

    public void setPictureCount(int pictureCount)
    {
        this.pictureCount = pictureCount;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getRating()
    {
        return rating;
    }

    public void setRating(int rating)
    {
        this.rating = rating;
    }

    public String getId()
    {
        return id;
    }

    public Repository getRepository()
    {
        return repository;
    }

    public void setRepository(Repository repository)
    {
        this.repository = repository;
    }
}
