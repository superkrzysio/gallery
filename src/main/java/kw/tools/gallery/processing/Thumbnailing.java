package kw.tools.gallery.processing;

import java.util.List;

/**
 * Responsible for handling thumbnails: generating thumbs for a gallery and retrieving them from cache.
 */
public interface Thumbnailing
{
    void generate(String source, String target);

    List<String> retrieve(String source);

    ProcessingStatus getStatus();
}
