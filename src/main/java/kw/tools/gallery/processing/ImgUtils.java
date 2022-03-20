package kw.tools.gallery.processing;

import ij.IJ;
import kw.tools.gallery.processing.impl.ImageIOException;

public interface ImgUtils
{
    static void init()
    {
        // workaround for bug in ImageJ, that when IJ.openImage() is called from inside Spring-managed class,
        // it tries to start awt window and fails,
        // but works when IJ.openImage() is called for the first time from outside of Spring context (e.g. in main())
        IJ.openImage(ImgUtils.class.getClassLoader().getResource("foo.jpg").getPath());
    }

    void resizeToWidth(String source, String target, int width) throws ImageIOException;
}
