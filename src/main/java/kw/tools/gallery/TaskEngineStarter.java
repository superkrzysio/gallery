package kw.tools.gallery;

import kw.tools.gallery.taskengine.TaskEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TaskEngineStarter
{
    @Autowired
    private TaskEngineService taskEngine;

    @PostConstruct
    public void startTaskEngine()
    {
        taskEngine.start();
    }
}
