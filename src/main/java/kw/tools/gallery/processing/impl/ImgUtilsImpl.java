package kw.tools.gallery.processing.impl;

import ij.IJ;
import ij.ImagePlus;
import kw.tools.gallery.processing.ImgUtils;
import org.springframework.stereotype.Component;

/**
 * Wrapper for image processing library.
 */
@Component
public class ImgUtilsImpl implements ImgUtils
{
    @Override
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
