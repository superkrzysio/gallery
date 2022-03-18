package kw.tools.gallery.taskengine;

import kw.tools.gallery.taskengine.utils.EmptyTask;
import kw.tools.gallery.taskengine.utils.EmptyTaskRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.awaitility.Awaitility.await;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TaskEngineBombingTest
{
    @Autowired
    private TaskEngineService taskEngineService;

    @Autowired
    private EmptyTaskRepository emptyTaskRepository;

    @Autowired
    private TaskEngineService taskEngine;

    public static final int HIGH_NUMBER_O_TASKS = 10000;

    @AfterEach
    public void stopTaskEngine()
    {
        taskEngine.stop();
    }

    @BeforeEach
    public void startTaskEngine()
    {
        taskEngine.start();
    }

    @Test
    public void shouldProcessHighNumberOfTasks()
    {
        Task[] tasks = new Task[HIGH_NUMBER_O_TASKS];
        for (int x = 0; x < HIGH_NUMBER_O_TASKS; x++)
        {
            tasks[x] = new EmptyTask();
        }
        emptyTaskRepository.saveAll(Arrays.asList(tasks));
        await()
                .atMost(Duration.of(5, ChronoUnit.MINUTES))
                .until(() -> taskEngine.getQueueSize() == 0 && !taskEngine.hasFutureTask());

        for (int x = 0; x < HIGH_NUMBER_O_TASKS; x++)
        {
            Task t = refresh(tasks[x]);
            Assertions.assertThat(t.getStatus()).isEqualTo(Task.Status.FINISHED);
        }
    }

    private Task refresh(Task t)
    {
        return emptyTaskRepository.findById(t.getId()).orElseThrow();
    }
}
