package kw.tools.gallery.processing;

import java.util.List;

public interface ThumbnailSelector
{
    enum Strategy
    {FIRST, SPREAD, LAST}

    /**
     * Select some images using the selected {@link Strategy}.<br/>
     * Does not modify the list but not guaranteed to return cloned list.
     *
     * @param images
     * @return
     */
    List<String> select(List<String> images);
}
