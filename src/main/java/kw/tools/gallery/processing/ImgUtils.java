package kw.tools.gallery.processing;

import ij.IJ;
import ij.ImagePlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Wrapper for image processing library.
 */
@Component
public class ImgUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(ImgUtils.class);

    public static void init()
    {
        // workaround for bug in ImageJ, that when IJ.openImage() is called from inside Spring-managed class,
        // it tries to start awt window and fails,
        // but works when IJ.openImage() is called for the first time from outside of Spring context (e.g. in main())
        IJ.openImage(kw.tools.gallery.processing.ImgUtils.class.getClassLoader().getResource("foo.jpg").getPath());
    }

    public void resizeToWidth(String source, String target, int width) throws ImageIOException
    {
        if (LOG.isDebugEnabled())
        {
            LOG.debug("Processing '{}' into '{}'", source, target);
        }

        ImagePlus im = null;
        try
        {
            im = IJ.openImage(source);
            if (im == null)
                throw new IllegalStateException("Library returned null");
        } catch (Exception e)
        {
            LOG.error("Could not open image '{}': ", source, e);
            throw new ImageIOException(String.format("Could not open image '%s'", source), e);
        }
        int imgWidth = im.getWidth();
        int imgHeight = im.getHeight();
        int height = Math.round((float) width / imgWidth * imgHeight);
        im = im.resize(width, height, "bicubic");

        try
        {
            IJ.save(im, target);
        } catch (Exception e)
        {
            LOG.error("Could not save image '{}': ", target, e);
            throw new ImageIOException(String.format("Could not save image '%s'", target), e);
        }
    }
}
