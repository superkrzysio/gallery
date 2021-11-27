package kw.tools.gallery.processing;

import java.util.List;

/**
 * Responsible for handling thumbnails: generating thumbs for a gallery and retrieving them from cache.
 */
public interface Thumbnailing
{
    void generate(String source, String target);

    /**
     * Retrieve list of paths to thumbnails for a gallery
     * @param repoId
     * @param galId
     * @return
     */
    List<String> retrieve(String repoId, String galId);

    ProcessingStatus getStatus();
}
