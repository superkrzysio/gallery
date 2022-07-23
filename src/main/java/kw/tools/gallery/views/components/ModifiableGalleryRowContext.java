package kw.tools.gallery.views.components;

import kw.tools.gallery.models.Gallery;

/**
 * This class allows to replace context content, instead of whole context objects.
 * Simplifies data replacement in components.
 */
public class ModifiableGalleryRowContext extends GalleryRowContext
{
    public void setGallery(Gallery gallery)
    {
        this.gallery = gallery;
    }

    public void setActions(CurrentRowActions actions)
    {
        this.actions = actions;
    }
}
