package kw.tools.gallery.services;

import kw.tools.gallery.CacheUtils;
import kw.tools.gallery.processing.AbstractThumbnailing;
import kw.tools.gallery.processing.DirCrawler;
import kw.tools.gallery.processing.MultiImageThumbnailing;
import kw.tools.gallery.processing.Tasks;
import org.springframework.beans.factory.annotation.Autowired;
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
    private MultiImageThumbnailing multiImageThumbnailing;

    @Autowired
    private CacheUtils cacheUtils;

    public List<GalleryFolderDTO> fetchGalleries(String inPath)
    {
        List<GalleryFolderDTO> result = new ArrayList<>();

        try
        {
            dirCrawler.forEach(inPath, galPath -> { // todo: this should be in background processing and clean code
                if (AbstractThumbnailing.getImages(galPath.toString()).isEmpty())
                {
                    // System.out.println("Found empty path: " + galPath);  // todo: logger
                    return;
                }
                //                singleImageThumbnailing.generate(path.toString(), repository.getId());
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
        tasks.execute(() -> multiImageThumbnailing.generate(galleryPath, cacheUtils.generateGalleryDir(repositoryId, galleryId)));
    }

    public static class GalleryFolderDTO
    {
        public String filename;
        public String fullPath;
        public Integer pictureCount;
    }
}
