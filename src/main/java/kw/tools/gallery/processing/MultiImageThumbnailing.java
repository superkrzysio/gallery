package kw.tools.gallery.processing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Component
public class MultiImageThumbnailing extends AbstractThumbnailing
{
    @Value("${cache.dir}")
    private String cacheDir;

    @Value("${thumbnails.multiimage.count}")
    private int imageCount;

    @Value("${thumbnails.multiimage.width}")
    private int width;

    @Override
    public void generate(String path, String targetId)
    {
        try
        {
            createDir(path, targetId);
            List<Path> images = getImages(path);

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("Multimage in path: " + path);
    }

    @Override
    public ProcessingStatus getStatus()
    {
        return null;
    }
}
