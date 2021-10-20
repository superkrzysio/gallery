package kw.tools.gallery.processing;

public abstract class AbstractThumbnailing implements Thumbnailing
{
    private String outputId;
    private String outputDir;

    public AbstractThumbnailing withOutputId(String outputId)
    {
        this.outputId = outputId;
        return this;
    }

    public AbstractThumbnailing withOutputDir(String outputDir)
    {
        this.outputDir = outputDir;
        return this;
    }

    protected void createDir()
    {

    }
}
