package kw.tools.gallery.taskengine;

import kw.tools.gallery.models.Task;
import kw.tools.gallery.taskengine.core.TaskEngineControl;
import kw.tools.gallery.taskengine.utils.*;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * High level task engine tests
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TaskProcessingTest
{
    @Autowired
    private NotifyingTaskRepository notifyingTaskRepository;

    @Autowired
    private ObservableTaskRepository observableTaskRepository;

    @Autowired
    private ChainingTaskRepository chainingTaskRepository;

    @Autowired
    private EmptyTaskRepository emptyTaskRepository;

    @Autowired
    private ErroneousTaskRepository erroneousTaskRepository;

    @Autowired
    private TaskEngineControl taskEngine;

    @Autowired
    private EntityManager em;

    private CustomFluentAssertions customAssertions;

    private static final int TEST_TIMEOUT = 10000;

    @BeforeEach
    public void cleanupTaskEngine() throws ExecutionException, InterruptedException
    {
        taskEngine.stop().get();
        notifyingTaskRepository.deleteAll();
        observableTaskRepository.deleteAll();
        chainingTaskRepository.deleteAll();
        emptyTaskRepository.deleteAll();
    }


    @Test
    public void shouldPickUpTaskAfterStarting() throws InterruptedException
    {
        assertThat(taskEngine.isRunning()).isFalse();
        Task t = new NotifyingTask();
        notifyingTaskRepository.save(t);
        taskEngine.start();
        t.notify();
        t.wait(TEST_TIMEOUT);
        customAssertions.assertThat(t).isFinished();
    }

    @Test
    public void shouldPickUpTaskWhilePolling() throws ExecutionException, InterruptedException
    {
        taskEngine.start().get();
        Task t = new NotifyingTask();
        notifyingTaskRepository.save(t);
        t.notify();
        t.wait(TEST_TIMEOUT);
        customAssertions.assertThat(t).isFinished();
    }

    @Test
    public void shouldPickUpTasksInFIFOOrder()
    {
        taskEngine.start();
        List<Task> createdTasks = new ArrayList<>();
        List<Task> finishedTasks = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < 5; i++)
        {
            createdTasks.add(new ObservableTask(finishedTasks::add));
        }
        observableTaskRepository.saveAll(createdTasks);

        for (Task t : createdTasks)
        {
            customAssertions.assertThat(t).isFinished();
        }

        assertThat(finishedTasks).containsExactlyElementsOf(createdTasks);
    }

    @Test
    public void shouldNotProcessAlreadyProcessedTasks()
    {
        AtomicInteger latch = new AtomicInteger(0);
        taskEngine.start();

        Task taskRunnable = new EmptyTask();
        emptyTaskRepository.save(taskRunnable);

        Task taskError = new ObservableTask(t -> latch.addAndGet(1));
        taskError.setStatus(Task.Status.ERROR);
        observableTaskRepository.save(taskError);

        Task taskAborted = new ObservableTask(t -> latch.addAndGet(2));
        taskError.setStatus(Task.Status.ABORTED);
        observableTaskRepository.save(taskAborted);

        // "forlorn" task, this may be subject to forlorn task recovery in future and test may fail
        Task taskRunning = new ObservableTask(t -> latch.addAndGet(4));
        taskError.setStatus(Task.Status.ERROR);
        observableTaskRepository.save(taskRunning);

        Task lastTask = new EmptyTask();
        emptyTaskRepository.save(lastTask);

        customAssertions.assertThat(taskRunnable).isFinished();
        customAssertions.assertThat(lastTask).isFinished();

        assertThat(taskError.getStatus()).isEqualTo(Task.Status.ERROR);
        assertThat(taskAborted.getStatus()).isEqualTo(Task.Status.ABORTED);
        assertThat(taskRunning.getStatus()).isEqualTo(Task.Status.RUNNING);
        assertThat(latch).isEqualTo(new AtomicInteger(0));
    }

    @Test
    public void shouldExecuteTaskChain()
    {
        taskEngine.start();
        ChainingTask t = new ChainingTask(emptyTaskRepository);
        chainingTaskRepository.save(t);
        customAssertions.assertThat(t).isFinished();
        customAssertions.assertThat(t.getChainedTask()).isFinished();
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

        customAssertions.assertThat(first).isFinished();
        customAssertions.assertThat(last).isFinished();
        customAssertions.assertThat(error).isError().and().logsContain(errorMessage);
    }

    class CustomFluentAssertions
    {
        TaskAssertions assertThat(Task t)
        {
            return new TaskAssertions(t);
        }
    }

    class TaskAssertions
    {
        Task t;

        public TaskAssertions(Task t)
        {
            this.t = t;
        }

        TaskAssertions isFinished()
        {
            await();
            assertThat(t.getStatus()).isEqualTo(Task.Status.FINISHED);
            return this;
        }

        TaskAssertions isError()
        {
            await();
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

        private void await()
        {
            Awaitility.await()
                    .atMost(TEST_TIMEOUT, TimeUnit.MILLISECONDS)
                    .pollInSameThread()
                    .until(() -> {
                        em.refresh(t);
                        return t.getStatus() != Task.Status.RUNNABLE;
                    });
        }
    }
}
