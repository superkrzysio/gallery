package kw.tools.gallery.processing.impl;

import kw.tools.gallery.processing.ScanningTask;
import kw.tools.gallery.processing.TaskFactory;
import kw.tools.gallery.processing.ThumbnailingTask;
import org.springframework.stereotype.Component;

@Component
public class TaskFactoryImpl implements TaskFactory
{
    @Override
    public ThumbnailingTask createThumbnailingTask(String repoId, String source, String target)
    {
        ThumbnailingTask task = new ThumbnailingTask();
        task.setCategory(repoId);
        task.setSource(source);
        task.setTarget(target);
        return task;
    }

    @Override
    public ScanningTask createScanningTask(String category, String path)
    {
        ScanningTask t = new ScanningTask();
        t.setCategory(category);
        t.setPath(path);
        return t;
    }
}
