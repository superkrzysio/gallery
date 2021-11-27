package kw.tools.gallery.processing;

import kw.tools.gallery.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Generates thumbnails as many separate images to be displayed on frontend. Only the given number of all images
 * is thumbnailed as representative.
 */
@Component
public class MultiImageThumbnailing extends AbstractThumbnailing
{
    private final static Logger LOG = LoggerFactory.getLogger(MultiImageThumbnailing.class);

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

    @Override
    public List<String> retrieve(String repoId, String galId)
    {
        String source = cacheUtils.getCacheDirForGallery(repoId, galId);
        return decodeThumbFilename(source);
    }

    private String encodeThumbFilename(String targetDir, String originalPath)
    {
        return targetDir +
                filenameEncodingConstantPart() +
                Path.of(originalPath).getFileName().toString();
    }

    private List<String> decodeThumbFilename(String source)
    {
        try
        {
            return Files.list(Path.of(source))
                    .filter(Files::isRegularFile)
                    .filter(file -> getFilename(file).startsWith(filenameEncodingConstantPart()))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e)
        {
            LOG.error("Wrong source cache folder given to retrieve thumbnails: " + source, e);
            return Collections.EMPTY_LIST;
        }
    }

    private String filenameEncodingConstantPart()
    {
        return this.getClass().getName() +
                "_" +
                selectionStrategy.name().toLowerCase(Locale.ROOT) +
                "_";
    }

    private String getFilename(Path p)
    {
        return p.getFileName().toString();
    }

    @Override
    public ProcessingStatus getStatus()
    {
        return null;
    }
}
