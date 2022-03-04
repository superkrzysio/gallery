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
    private TaskEngineControl taskEngineControl;

    @Autowired
    TaskRepository<?> taskRepository;

    @BeforeEach
    public void cleanTaskEngine()
    {
        try
        {
            taskEngineControl.kill();
        } catch (Exception e) { }
        taskRepository.deleteAll();
    }

    @Test
    public void testStartingAndStopping() throws ExecutionException, InterruptedException
    {
        assertFalse(taskEngineControl.isRunning());
        taskEngineControl.start();
        assertTrue(taskEngineControl.isRunning());
        taskEngineControl.stop().get();
        assertFalse(taskEngineControl.isRunning());
    }

    @Test
    public void testRestart() throws ExecutionException, InterruptedException
    {
        assertFalse(taskEngineControl.isRunning());
        taskEngineControl.start();
        assertTrue(taskEngineControl.isRunning());
        taskEngineControl.restart().get();
        assertTrue(taskEngineControl.isRunning());
        taskEngineControl.stop().get();
    }


}
