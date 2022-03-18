package kw.tools.gallery.taskengine;

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
public class TaskEngineServiceTest
{
    @Autowired
    private TaskEngineService taskEngineService;

    @Autowired
    TaskRepository<?> taskRepository;

    @BeforeEach
    public void cleanTaskEngine()
    {
        try
        {
            taskEngineService.kill();
        } catch (Exception e)
        {
        }
        taskRepository.deleteAll();
    }

    @Test
    public void testStartingAndLaterStopping() throws ExecutionException, InterruptedException
    {
        assertFalse(taskEngineService.isRunning());
        taskEngineService.start();
        assertTrue(taskEngineService.isRunning());
        Thread.sleep(3000);
        taskEngineService.stop().get();
        assertFalse(taskEngineService.isRunning());
    }

    @Test
    public void testRestart() throws ExecutionException, InterruptedException
    {
        assertFalse(taskEngineService.isRunning());
        taskEngineService.start();
        assertTrue(taskEngineService.isRunning());
        Thread.sleep(3000);
        taskEngineService.restart().get();
        assertTrue(taskEngineService.isRunning());
        taskEngineService.stop().get();
    }


}
