package kw.tools.gallery.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import kw.tools.gallery.models.GalleryTask;
import kw.tools.gallery.services.TaskService;
import kw.tools.gallery.taskengine.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Route("/tasks")
public class TasksView extends VerticalLayout implements HasUrlParameter<String>
{
    private static final Logger LOG = LoggerFactory.getLogger(TasksView.class);

    private String repositoryId = "";
    private final Grid<GalleryTask> currentTasksGrid;
    private final Checkbox filterCreated;
    private final Checkbox filterRunning;
    private final Checkbox filterError;
    private final Checkbox filterHasMessages;
    private final Checkbox filterFinished;
    ProgressBar progressBar = new ProgressBar();

    private final TaskService taskService;

    public TasksView(@Autowired TaskService taskService)
    {

        this.taskService = taskService;

        add(new Anchor("/", "Back"));

        add(new Span("Currently executed tasks:"));

        Button refreshThreadsButton = new Button("Refresh", VaadinIcon.REFRESH.create());
        refreshThreadsButton.addClickListener(e -> refreshGridControls());

        filterCreated = new Checkbox();
        filterCreated.addValueChangeListener(e -> refreshGridControls());

        filterRunning = new Checkbox();
        filterRunning.setEnabled(true);
        filterRunning.addValueChangeListener(e -> refreshGridControls());

        filterError = new Checkbox();
        filterError.setEnabled(true);
        filterError.addValueChangeListener(e -> refreshGridControls());

        filterFinished = new Checkbox();
        filterFinished.addValueChangeListener(e -> refreshGridControls());

        filterHasMessages = new Checkbox();
        filterHasMessages.setEnabled(true);
        filterHasMessages.addValueChangeListener(e -> refreshGridControls());

        Button clearTasksButton = new Button("Remove visible");
        clearTasksButton.addClickListener(this::removeVisible);

        currentTasksGrid = new Grid<>();
        currentTasksGrid.setSelectionMode(Grid.SelectionMode.NONE);
        currentTasksGrid.addColumn(Task::getName).setHeader("Thread name");
        currentTasksGrid.addColumn(task -> task.getStatus().toString().toLowerCase(Locale.ROOT)).setHeader("Status");

        // all the fuss just to display newlines
        currentTasksGrid.addColumn(new ComponentRenderer<>(task -> {
            HorizontalLayout messageRows = new HorizontalLayout();
            for (int i = 0; i < task.getLogs().split("\n").length; i++)
            {
                if (i > 0) messageRows.add(new HtmlComponent("br"));
                messageRows.add(task.getLogs().split("\n")[i]);
            }
            return messageRows;
        })).setHeader("Error messages");
        currentTasksGrid.setMaxHeight(50, Unit.PERCENTAGE);
        currentTasksGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        currentTasksGrid.setMultiSort(true);


        progressBar.setWidth(75, Unit.PERCENTAGE);
        add(progressBar);

        HorizontalLayout gridControls = new HorizontalLayout(refreshThreadsButton, filterCreated, filterRunning,
                filterFinished, filterError, filterHasMessages, clearTasksButton);
        gridControls.setAlignItems(Alignment.CENTER);
        add(gridControls);

        add(currentTasksGrid);
    }

    private void removeVisible(ClickEvent<Button> buttonClickEvent)
    {
        filterTasks().forEach(taskService::delete);
        refreshGridControls();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String param)
    {
        this.repositoryId = param;
        progressBar.setMin(0);
        progressBar.setMax(taskService.getByCategory(repositoryId).size());
        refreshGridControls();
    }

    private void refreshGridControls()
    {
        currentTasksGrid.setItems(filterTasks());
        filterCreated.setLabel(String.format("Created (%d)", taskService.getByCategoryAndStatus(repositoryId, Task.Status.RUNNABLE).size()));
        filterRunning.setLabel(String.format("Running (%d)", taskService.getByCategoryAndStatus(repositoryId, Task.Status.RUNNING).size()));
        filterFinished.setLabel(
                String.format("Finished (%d)", taskService.getByCategoryAndStatus(repositoryId, Task.Status.FINISHED).size()));
        filterError.setLabel(String.format("Error (%d)", taskService.getByCategoryAndStatus(repositoryId, Task.Status.ERROR).size()));
        filterHasMessages.setLabel(String.format("Only with error messages (%d)", taskService.getWithLogsOnly(repositoryId).size()));
        progressBar.setValue(taskService.getByCategoryAndStatus(repositoryId, Task.Status.FINISHED, Task.Status.ERROR).size());
    }

    private List<GalleryTask> filterTasks()
    {
        Set<Task.Status> statusfilter = new HashSet<>();
        if (Boolean.TRUE.equals(filterCreated.getValue()))
        {
            statusfilter.add(Task.Status.RUNNABLE);
            statusfilter.add(Task.Status.QUEUED);
        }

        if (Boolean.TRUE.equals(filterError.getValue()))
        {
            statusfilter.add(Task.Status.ERROR);
            statusfilter.add(Task.Status.ABORTED);
        }

        if (Boolean.TRUE.equals(filterFinished.getValue()))
        {
            statusfilter.add(Task.Status.FINISHED);
        }

        if (Boolean.TRUE.equals(filterRunning.getValue()))
        {
            statusfilter.add(Task.Status.RUNNING);
        }

        List<GalleryTask> tasks = taskService.getByCategoryAndStatus(repositoryId, statusfilter.toArray(new Task.Status[0]));
        if (Boolean.TRUE.equals(filterHasMessages.getValue()))
            tasks.removeIf(t -> t.getLogs().isEmpty());
        return tasks;
    }

}
