package kw.tools.gallery.processing;

import kw.tools.gallery.models.GalleryTask;

public abstract class ThumbnailingTask extends GalleryTask
{
    protected String source;
    protected String target;

    public void setSource(String source)
    {
        this.source = source;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }
}
