package kw.tools.gallery.processing;

import org.springframework.stereotype.Component;

@Component
public class TaskFactory
{
    /**
     * Creates a task that will generate thumbs from the <tt>path</tt> folder.
     * Further generation configuration will be wired at runtime.
     */
    public ThumbnailingTask createThumbnailingTask(String repoId, String source, String target)
    {
        ThumbnailingTask task = new ThumbnailingTask();
        task.setCategory(repoId);
        task.setSource(source);
        task.setTarget(target);
        return task;
    }

    /**
     * Creates a task that will crawl the directory structure and perform further actions.
     */
    public ScanningTask createScanningTask(String category, String path)
    {
        ScanningTask t = new ScanningTask();
        t.setCategory(category);
        t.setPath(path);
        return t;
    }
}
