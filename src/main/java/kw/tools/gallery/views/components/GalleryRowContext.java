package kw.tools.gallery.views.components;

import kw.tools.gallery.models.Gallery;

/**
 * Communication mean between component and a main RepositoryView.
 */
public class GalleryRowContext
{
    protected Gallery gallery;

    protected CurrentRowActions actions;

    public String getGalleryId()
    {
        return gallery == null ? "" : gallery.getId();
    }

    public int getGalleryRating()
    {
        return gallery == null ? 0 : gallery.getRating();
    }

    public String getGalleryName()
    {
        return gallery == null ? "" : gallery.getName();
    }

    public CurrentRowActions getActions()
    {
        return actions == null ? new CurrentRowActions.NoOp() : actions;
    }
}
