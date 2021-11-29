package kw.tools.gallery.views;

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
import kw.tools.gallery.processing.Task;
import kw.tools.gallery.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;

@Route("/tasks")
public class TasksView extends VerticalLayout implements HasUrlParameter<String>
{
    private String repositoryId = "";
    private final Grid<Task> currentTasksGrid;
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

        currentTasksGrid = new Grid<>();
        currentTasksGrid.setSelectionMode(Grid.SelectionMode.NONE);
        currentTasksGrid.addColumn(Task::getId).setHeader("Thread name");
        currentTasksGrid.addColumn(task -> task.getStatus().toString().toLowerCase(Locale.ROOT)).setHeader("Status");

        // all the fuss just to display newlines
        currentTasksGrid.addColumn(new ComponentRenderer<>(task -> {
            HorizontalLayout messageRows = new HorizontalLayout();
            for (int i = 0; i < task.getMessages().size(); i++)
            {
                if (i > 0) messageRows.add(new HtmlComponent("br"));
                messageRows.add(task.getMessages().get(i));
            }
            return messageRows;
        })).setHeader("Error messages");
        currentTasksGrid.setMaxHeight(50, Unit.PERCENTAGE);
        currentTasksGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        currentTasksGrid.setMultiSort(true);

        progressBar.setMin(0);
        progressBar.setMax(taskService.getByCategory(repositoryId).size());
        progressBar.setWidth(75, Unit.PERCENTAGE);
        add(progressBar);

        refreshGridControls();

        HorizontalLayout gridControls = new HorizontalLayout(refreshThreadsButton, filterCreated, filterRunning,
                filterFinished, filterError, filterHasMessages);
        gridControls.setAlignItems(Alignment.CENTER);
        add(gridControls);

        add(currentTasksGrid);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String param)
    {
        this.repositoryId = param;
        refreshGridControls();
    }

    private void refreshGridControls()
    {
        currentTasksGrid.setItems(filterTasks());
        filterCreated.setLabel(String.format("Created (%d)", taskService.getByCategoryStatus(repositoryId, Task.Status.CREATED).size()));
        filterRunning.setLabel(String.format("Running (%d)", taskService.getByCategoryStatus(repositoryId, Task.Status.WORKING).size()));
        filterFinished.setLabel(
                String.format("Finished (%d)", taskService.getByCategoryStatus(repositoryId, Task.Status.FINISHED).size()));
        filterError.setLabel(String.format("Error (%d)", taskService.getByCategoryStatus(repositoryId, Task.Status.ERROR).size()));
        filterHasMessages.setLabel(String.format("Only with error messages (%d)",
                taskService.getAllTasks().stream().filter(t -> !t.getMessages().isEmpty()).count()));
        progressBar.setValue(taskService.getByCategoryStatus(repositoryId, Task.Status.FINISHED, Task.Status.ERROR).size());
    }

    private List<Task> filterTasks()
    {
        List<Task> tasks = repositoryId.isBlank() ? taskService.getAllTasks() : taskService.getByCategory(repositoryId);
        tasks.removeIf(t -> filterHasMessages.getValue() && t.getMessages().isEmpty());
        tasks.removeIf(t -> !filterCreated.getValue() && t.getStatus() == Task.Status.CREATED);
        tasks.removeIf(t -> !filterError.getValue() && t.getStatus() == Task.Status.ERROR);
        tasks.removeIf(t -> !filterFinished.getValue() && t.getStatus() == Task.Status.FINISHED);
        tasks.removeIf(t -> !filterRunning.getValue() && t.getStatus() == Task.Status.WORKING);
        return tasks;
    }

}
