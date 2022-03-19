package kw.tools.gallery.processing.impl;

import kw.tools.gallery.processing.ThumbnailingTask;
import kw.tools.gallery.processing.ThumbnailingTaskFactory;
import kw.tools.gallery.taskengine.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ThumbnailingTaskFactoryImpl implements ThumbnailingTaskFactory
{
    public enum Strategy
    {SINGLE_IMAGE, MULTI_IMAGE}

    @Value("${thumbnailing.strategy}")
    private String strategy;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Task create(String repoId, String source, String target)
    {
        ThumbnailingTask task = applicationContext.getBean(strategy, ThumbnailingTask.class);
        task.setSource(source);
        task.setTarget(target);
        task.setCategory(repoId);
        return task;
    }
}
