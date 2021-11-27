package kw.tools.gallery.processing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Generates one big image containing some small thumbnails. Only a number of images of the whole gallery are selected
 * as a representative.
 */
@Component
public class SingleImageThumbnailing extends AbstractThumbnailing
{
    private final ProcessingStatus status = new ProcessingStatus();

    @Value("${cache.dir}")
    private String cacheDir;

    @Value("${thumbnails.singleimage.count.vertical}")
    private int verticalCount;

    @Value("${thumbnails.singleimage.count.horizontal}")
    private int horizontalCount;

    @Value("${thumbnails.singleimage.width}")
    private int width;

    @Value("${thumbnails.singleimage.height}")
    private int height;

    @Value("${thumbnails.selection.strategy}")
    private ThumbnailSelector.Strategy selectionStrategy;

    @Autowired
    private ThumbnailSelectionFactory thumbnailSelectionFactory;

    @Override
    public void generate(String source, String targetId)
    {
        List<String> images = getImages(source);
        images = thumbnailSelectionFactory.get(selectionStrategy, verticalCount * horizontalCount).select(images);
        // todo
    }

    @Override
    public List<String> retrieve(String repoId, String galId)
    {
        return null;
    }

    @Override
    public ProcessingStatus getStatus()
    {
        return null;
    }
}
