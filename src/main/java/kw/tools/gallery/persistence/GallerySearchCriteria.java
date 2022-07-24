package kw.tools.gallery.persistence;

public class GallerySearchCriteria
{
    public enum RatingSearchMode
    {
        ZERO("No rating"),
        POSITIVE("Rating present"),
        NONE("Both");

        private final String description;

        RatingSearchMode(String description)
        {
            this.description = description;
        }

        public String getDescription()
        {
            return description;
        }
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
