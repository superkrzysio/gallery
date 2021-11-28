package kw.tools.gallery.processing;

import java.nio.file.Path;
import java.util.List;

public interface ImageAccessor
{
    /**
     * Retrieve all allowed images from the given location.
     *
     * @param source
     * @return
     */
    List<Path> getImages(Path source);

    /**
     * Retrieve all allowed images from the given location.
     *
     * @param source
     * @return
     */
    List<String> getImages(String source);

    /**
     * Retrieve list of paths to thumbnails for a gallery
     *
     * @param repoId
     * @param galId
     * @return
     */
    List<String> getThumbs(String repoId, String galId);
}
