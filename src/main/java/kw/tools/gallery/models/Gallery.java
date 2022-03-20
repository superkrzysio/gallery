package kw.tools.gallery.models;

import net.bytebuddy.utility.RandomString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Gallery
{
    private static final int ID_LENGTH = 20;
    @Id
    private String id = RandomString.make(ID_LENGTH);

    private int pictureCount;

    @Column(length = 2000)
    private String path;
    private String name;
    private int rating;
    private String repositoryId;

    @Transient
    private List<String> thumbnails = new ArrayList<>();

    public Gallery()
    {
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

    public String getRepositoryId()
    {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId)
    {
        this.repositoryId = repositoryId;
    }

    public List<String> getThumbnails()
    {
        return Collections.unmodifiableList(thumbnails);
    }

    public void setThumbnails(List<String> thumbnails)
    {
        this.thumbnails = thumbnails;
    }

    public void addThumbnail(String thumb)
    {
        thumbnails.add(thumb);
    }

    public void setId(String id)
    {
        this.id = id;
    }
}
