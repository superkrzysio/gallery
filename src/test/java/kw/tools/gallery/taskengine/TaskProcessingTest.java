package kw.tools.gallery.taskengine;

import kw.tools.gallery.models.Task;
import kw.tools.gallery.persistence.TaskRepository;
import kw.tools.gallery.taskengine.core.TaskEngineControl;
import kw.tools.gallery.taskengine.utils.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * High level task engine tests
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TaskProcessingTest
{
    @Autowired
    private TaskRepository<?> taskRepository;

    @Autowired
    private ChainingTaskRepository chainingTaskRepository;

    @Autowired
    private EmptyTaskRepository emptyTaskRepository;

    @Autowired
    private ErroneousTaskRepository erroneousTaskRepository;

    @Autowired
    private TaskEngineControl taskEngine;

    private final CustomFluentAssertions customAssertions = new CustomFluentAssertions();

    @AfterEach
    public void shutdownTaskEngine() throws ExecutionException, InterruptedException
    {
        taskEngine.stop().get();
        chainingTaskRepository.deleteAll();
        emptyTaskRepository.deleteAll();
    }

    @Test
    public void shouldPickUpTaskAfterStarting()
    {
        assertThat(taskEngine.isRunning()).isFalse();
        Task t = new EmptyTask();
        emptyTaskRepository.save(t);
        taskEngine.start();
        await().until(() -> taskEngine.getQueueSize() == 0);
        t = refresh(t);
        customAssertions.assertThat(t).isFinished();
    }

    @Test
    public void shouldPickUpTaskWhilePolling()
    {
        taskEngine.start();
        Task t = new EmptyTask();
        emptyTaskRepository.save(t);
        await().until(() -> taskEngine.getQueueSize() == 0);
        t = refresh(t);
        customAssertions.assertThat(t).isFinished();
    }

    @Test
    public void shouldNotProcessAlreadyProcessedTasks()
    {
        taskEngine.start();

        Task taskRunnable = new EmptyTask();
        emptyTaskRepository.save(taskRunnable);

        Task taskError = new EmptyTask();
        taskError.setStatus(Task.Status.ERROR);
        emptyTaskRepository.save(taskError);

        Task taskAborted = new EmptyTask();
        taskAborted.setStatus(Task.Status.ABORTED);
        emptyTaskRepository.save(taskAborted);

        // "forlorn" task, this may be subject to forlorn task recovery in future and test may fail
        Task taskRunning = new EmptyTask();
        taskRunning.setStatus(Task.Status.RUNNING);
        emptyTaskRepository.save(taskRunning);

        Task taskQueued = new EmptyTask();
        taskQueued.setStatus(Task.Status.QUEUED);
        emptyTaskRepository.save(taskQueued);

        Task lastTask = new EmptyTask();
        emptyTaskRepository.save(lastTask);

        await().until(() -> taskEngine.getQueueSize() == 0 && !taskEngine.hasFutureTask());

        taskRunnable = refresh(taskRunnable);
        lastTask = refresh(lastTask);

        customAssertions.assertThat(taskRunnable).isFinished();
        customAssertions.assertThat(lastTask).isFinished();

        taskError = refresh(taskError);
        taskAborted = refresh(taskAborted);
        taskRunning = refresh(taskRunning);
        taskQueued = refresh(taskQueued);

        assertThat(taskError.getStatus()).isEqualTo(Task.Status.ERROR);
        assertThat(taskAborted.getStatus()).isEqualTo(Task.Status.ABORTED);
        assertThat(taskRunning.getStatus()).isEqualTo(Task.Status.RUNNING);
        assertThat(taskQueued.getStatus()).isEqualTo(Task.Status.QUEUED);
        assertThat(taskEngine.getCompletedTaskCount()).isEqualTo(2);
    }

    @Test
    public void shouldExecuteTaskChain()
    {
        taskEngine.start();
        ChainingTask t = new ChainingTask(emptyTaskRepository);     // it creates another task
        chainingTaskRepository.save(t);

        await().until(() -> taskEngine.getQueueSize() == 0 && !taskEngine.hasFutureTask());
        t = (ChainingTask) refresh(t);

        customAssertions.assertThat(t).isFinished();
        assertThat(taskEngine.getCompletedTaskCount()).isEqualTo(2);
    }

    @Test
    public void shouldSetErrorStatusOnTaskError()
    {
        String errorMessage = "Task failed successfully";
        taskEngine.start();

        Task first = new EmptyTask();
        emptyTaskRepository.save(first);

        Task error = new ErroneousTask(errorMessage);
        erroneousTaskRepository.save(error);

        Task last = new EmptyTask();
        emptyTaskRepository.save(last);

        await().until(() -> taskEngine.getQueueSize() == 0 && !taskEngine.hasFutureTask());

        first = refresh(first);
        last = refresh(last);
        error = refresh(error);

        customAssertions.assertThat(first).isFinished();
        customAssertions.assertThat(last).isFinished();
        customAssertions.assertThat(error).isError().and().logsContain(errorMessage);
        assertThat(taskEngine.getCompletedTaskCount()).isEqualTo(3);
    }

    private Task refresh(Task t)
    {
        return taskRepository.findById(t.getId()).orElseThrow();
    }

    private static class CustomFluentAssertions
    {
        TaskAssertions assertThat(Task t)
        {
            return new TaskAssertions(t);
        }
    }

    private static class TaskAssertions
    {
        Task t;

        public TaskAssertions(Task t)
        {
            this.t = t;
        }

        TaskAssertions isFinished()
        {
            assertThat(t.getStatus()).isEqualTo(Task.Status.FINISHED);
            return this;
        }

        TaskAssertions isError()
        {
            assertThat(t.getStatus()).isEqualTo(Task.Status.ERROR);
            assertThat(t.getLogs()).isNotBlank();
            return this;
        }

        TaskAssertions logsContain(String contain)
        {
            assertThat(t.getLogs()).contains(contain);
            return this;
        }

        TaskAssertions and()
        {
            return this;
        }

    }
}
