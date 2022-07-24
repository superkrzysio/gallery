package kw.tools.gallery.persistence;

public class GallerySearchCriteria
{
    public enum RatingSearchMode
    {
        ZERO, POSITIVE, NONE
    }

    public String repositoryId;
    public RatingSearchMode rating;

    public GallerySearchCriteria()
    {
    }

    public GallerySearchCriteria withRepository(String repositoryId)
    {
        this.repositoryId = repositoryId;
        return this;
    }

    public GallerySearchCriteria withRating(RatingSearchMode rating)
    {
        this.rating = rating;
        return this;
    }
}
