package kw.tools.gallery.processing;

import kw.tools.gallery.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Generates thumbnails as many separate images to be displayed on frontend. Only the given number of all images
 * is thumbnailed as representative.
 */
@Component
@Scope("prototype")
public class MultiImageThumbnailingTask extends ThumbnailingTask
{
    private final static Logger LOG = LoggerFactory.getLogger(MultiImageThumbnailingTask.class);

    @Value("${thumbnails.multiimage.count}")
    private int imageCount;

    @Value("${thumbnails.multiimage.width}")
    private int width;

    @Value("${thumbnails.selection.strategy}")
    private ThumbnailSelector.Strategy selectionStrategy;

    @Autowired
    private ThumbnailSelectionFactory thumbnailSelectionFactory;

    @Autowired
    private ImgUtils imgUtils;

    @Autowired
    private CacheUtils cacheUtils;

    @Autowired
    private FilenameEncoder filenameEncoder;

    @Autowired
    private ImageAccessor imageAccessor;

    private String source;
    private String target;


    @Override
    public void executeImpl()
    {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        List<String> images = imageAccessor.getImages(source);
        images = thumbnailSelectionFactory.get(selectionStrategy, imageCount).select(images);

        for (String filename : images)
        {
            imgUtils.resizeToWidth(filename, filenameEncoder.encodeThumbFilename(target, filename), width);
        }
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }
}
