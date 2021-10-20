package kw.tools.gallery.processing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SingleImageThumbnailing extends AbstractThumbnailing
{
    private ProcessingStatus status = new ProcessingStatus();

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

    @Override
    public void generate(String path, String targetId)
    {
        try
        {
            createDir(path, targetId);
            List<Path> images = getImages(path);
            String imstr = images.stream().map(img -> img.getFileName().toString()).collect(Collectors.joining(", "));
            System.out.println("Path: '" + path + "', images: " + imstr);

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public ProcessingStatus getStatus()
    {
        return null;
    }
}
