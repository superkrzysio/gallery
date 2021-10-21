package kw.tools.gallery.processing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

@Component
public class MultiImageThumbnailing extends AbstractThumbnailing
{
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

    @Override
    public void generate(String source, String target)
    {
        List<String> images = getImages(source);
        images = thumbnailSelectionFactory.get(selectionStrategy, imageCount).select(images);

        for (String filename : images)
        {
            imgUtils.resizeToWidth(filename, encodeThumbFilename(target, filename), width);
        }
    }

    private String encodeThumbFilename(String targetDir, String originalPath)
    {
        return targetDir +
                this.getClass().getName() +
                "_" +
                selectionStrategy.name().toLowerCase(Locale.ROOT) +
                "_" +
                Path.of(originalPath).getFileName().toString();
    }

    private List<String> decodeThumbFilename()
    {
        // todo: return all thumbs encoded by this class
        return null;
    }

    @Override
    public ProcessingStatus getStatus()
    {
        return null;
    }
}
