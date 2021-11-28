package kw.tools.gallery.processing;

public abstract class ThumbnailingTask extends AbstractTask
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
