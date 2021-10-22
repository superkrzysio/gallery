package kw.tools.gallery.processing;

import ij.IJ;
import ij.ImagePlus;
import org.springframework.stereotype.Component;

/**
 * Wrapper for image processing library.
 */
@Component
public class ImgUtils
{
    public static void init()
    {
        // workaround for bug in ImageJ, that when IJ.openImage() is called from inside Spring-managed class,
        // it tries to start awt window and fails,
        // but works when IJ.openImage() is called for the first time from outside of Spring context (e.g. in main())
        IJ.openImage(ImgUtils.class.getClassLoader().getResource("foo.jpg").getPath());
    }

    public void resizeToWidth(String source, String target, int width)
    {
        ImagePlus im = IJ.openImage(source);
        int imgWidth = im.getWidth();
        int imgHeight = im.getHeight();
        int height = Math.round((float) width / imgWidth * imgHeight);
        im = im.resize(width, height, "bicubic");
        IJ.save(im, target);
    }
}
