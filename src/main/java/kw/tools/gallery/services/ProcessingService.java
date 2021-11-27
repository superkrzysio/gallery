package kw.tools.gallery.services;

import kw.tools.gallery.CacheUtils;
import kw.tools.gallery.processing.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessingService
{
    @Autowired
    private DirCrawler dirCrawler;

    @Autowired
    private Tasks tasks;

    @Autowired
    private Thumbnailing thumbnailing;

    @Autowired
    private CacheUtils cacheUtils;

    public List<GalleryFolderDTO> fetchGalleries(String inPath)
    {
        List<GalleryFolderDTO> result = new ArrayList<>();

        try
        {
            dirCrawler.forEach(inPath, galPath -> {
                if (AbstractThumbnailing.getImages(galPath.toString()).isEmpty())
                {
                    // System.out.println("Found empty path: " + galPath);  // todo: logger
                    return;
                }
                GalleryFolderDTO dto = new GalleryFolderDTO();
                dto.filename = galPath.getFileName().toString();
                dto.fullPath = galPath.toString();
                dto.pictureCount = AbstractThumbnailing.getImages(galPath.toString()).size();
                result.add(dto);
            });
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public void generate(String repositoryId, String galleryId, String galleryPath)
    {
        tasks.execute(() -> thumbnailing.generate(galleryPath, cacheUtils.generateGalleryDir(repositoryId, galleryId)));
    }

    public static class GalleryFolderDTO
    {
        public String filename;
        public String fullPath;
        public Integer pictureCount;
    }
}
