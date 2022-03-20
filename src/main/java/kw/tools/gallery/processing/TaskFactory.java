package kw.tools.gallery.processing;

public interface TaskFactory
{
    /**
     * Creates a task that will generate thumbs from the <tt>path</tt> folder.
     * Further generation configuration will be wired at runtime.
     */
    ThumbnailingTask createThumbnailingTask(String repoId, String source, String target);

    /**
     * Creates a task that will crawl the directory structure and perform further actions.
     */
    ScanningTask createScanningTask(String category, String path);

}
