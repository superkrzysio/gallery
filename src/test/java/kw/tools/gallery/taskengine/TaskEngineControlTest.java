package kw.tools.gallery.taskengine;

import kw.tools.gallery.persistence.TaskRepository;
import kw.tools.gallery.taskengine.core.TaskEngineControl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TaskEngineControlTest
{
    @Autowired
    private TaskEngineControl taskEngine;

    @Autowired
    TaskRepository taskRepository;

    @BeforeEach
    public void cleanTaskEngine()
    {
        taskEngine.kill();
        taskRepository.deleteAll();
    }

    @Test
    public void testStartingAndStopping() throws ExecutionException, InterruptedException
    {
        assertFalse(taskEngine.isRunning());
        taskEngine.start().get();
        assertTrue(taskEngine.isRunning());
        taskEngine.stop().get();
        assertFalse(taskEngine.isRunning());
    }

    @Test
    public void testRestart() throws ExecutionException, InterruptedException
    {
        assertFalse(taskEngine.isRunning());
        taskEngine.start().get();
        assertTrue(taskEngine.isRunning());
        taskEngine.restart().get();
        assertTrue(taskEngine.isRunning());
        taskEngine.stop().get();
    }


}
