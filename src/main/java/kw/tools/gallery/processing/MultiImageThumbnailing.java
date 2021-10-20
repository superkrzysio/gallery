package kw.tools.gallery.processing;

public class MultiImageThumbnailing extends AbstractThumbnailing
{
    @Override
    public void generate(String path)
    {
        System.out.println("Multimage in path: " + path);
    }

    @Override
    public ProcessingStatus getStatus()
    {
        return null;
    }
}
