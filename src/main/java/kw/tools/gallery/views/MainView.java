package kw.tools.gallery.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;
import kw.tools.gallery.models.Repository;
import kw.tools.gallery.services.GalleryService;
import kw.tools.gallery.services.ProcessingService;
import kw.tools.gallery.services.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class MainView extends VerticalLayout
{
    private final RepositoryService repositoryService;

    private final GalleryService galleryService;

    private final ProcessingService processingService;

    private final Grid<Repository> repositoryGrid = new Grid<>();
    private final TextField repositoryInput = new TextField();


    public MainView(@Autowired RepositoryService repositoryService,
                    @Autowired GalleryService galleryService,
                    @Autowired ProcessingService processingService)
    {
        this.repositoryService = repositoryService;
        this.galleryService = galleryService;
        this.processingService = processingService;

        setHeight(100, Unit.PERCENTAGE);

        repositoryInput.setPlaceholder("Add repository...");
        repositoryInput.setSizeFull();
        repositoryInput.addKeyPressListener(Key.ENTER, e -> addRepo(repositoryInput.getValue()));

        Button addButton = new Button();
        addButton.setText("Add");
        addButton.getElement().getThemeList().add("primary");
        addButton.addClickListener(e -> addRepo(repositoryInput.getValue()));
        HorizontalLayout addRepositoryLayout = new HorizontalLayout(repositoryInput, addButton);
        addRepositoryLayout.setWidth(75, Unit.PERCENTAGE);
        addRepositoryLayout.setAlignItems(Alignment.BASELINE);

        add(addRepositoryLayout);

        Button refreshButton = new Button("Refresh", VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(e -> refreshGrid());
        HorizontalLayout controlButtonsWrapper = new HorizontalLayout(refreshButton /*, new Anchor("/tasks", "Tasks")*/);
        controlButtonsWrapper.setAlignItems(Alignment.CENTER);
        add(controlButtonsWrapper);

        repositoryGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        repositoryGrid.setHeight(100, Unit.PERCENTAGE);

        repositoryGrid.addComponentColumn(repo -> {
            Anchor a = new Anchor();
            a.setHref("/repository/" + repo.getId());
            a.setText(repo.getId());
            return a;
        }).setKey("name").setHeader("Name").setSortable(true).setAutoWidth(true)
                .setFlexGrow(1).setResizable(true).setComparator(Repository::getId);

        repositoryGrid.addColumn(new TextRenderer<>(repository -> Integer.toString(galleryService.getGalleryCountForRepo(repository.getId()))))
                .setKey("galCount").setHeader("Galleries").setSortable(true).setAutoWidth(true)
                .setFlexGrow(0).setResizable(true);

        repositoryGrid.addComponentColumn(repository -> {
            VerticalLayout wrapper = new VerticalLayout();
            wrapper.add(new Anchor("tasks/" + repository.getId(), interpretProcessingStatus(processingService.getProcessingStatus(repository.getId()))));
            if (!interpretIsProcessingFinished(processingService.getProcessingStatus(repository.getId())))
            {
                ProgressBar pg = new ProgressBar();
                pg.setMin(0);
                pg.setMax(galleryService.getGalleryCountForRepo(repository.getId()));
                pg.setValue(processingService.getFinishedCount(repository.getId()));
                wrapper.add(pg);
            }
            wrapper.setAlignItems(Alignment.CENTER);
            return wrapper;
        }).setHeader("Status").setFlexGrow(0).setResizable(false);

        repositoryGrid.addColumn(new ComponentRenderer<>(repo -> {
            Button refresh = new Button();
            refresh.setIcon(VaadinIcon.REFRESH.create());
            refresh.addClickListener(e -> regenerateRepo(repo.getId()));
            Button delete = new Button();
            delete.setIcon(VaadinIcon.TRASH.create());
            delete.addClickListener(e -> deleteRepo(repo.getId()));
            return new HorizontalLayout(refresh, delete);
        })).setKey("actions").setHeader("Actions").setSortable(true).setAutoWidth(true)
                .setFlexGrow(0).setResizable(true).setComparator(Repository::getId);
        repositoryGrid.setWidth(50, Unit.PERCENTAGE);

        // todo: add status column

        refreshGrid();

        add(repositoryGrid);


    }

    private void addRepo(String path)
    {
        repositoryService.add(path);
        refreshGrid();
    }

    private void refreshGrid()
    {
        repositoryGrid.setItems(repositoryService.getAll());
    }

    private void regenerateRepo(String id)
    {
        repositoryService.regenerate(id);
    }

    private void deleteRepo(String id)
    {
        Page page = UI.getCurrent().getPage();
        page.executeJs(String.format("return confirm(\"Are you sure you want to delete '%s' with all its galleries and data?\")", id)).then(result -> {
            if (result.asBoolean())
            {
                repositoryService.delete(id);
                galleryService.deleteAll(id);
                refreshGrid();
            }
        });
    }

    private Component interpretProcessingStatus(ProcessingService.ProcessingStatus status)
    {
        switch (status)
        {
            case SUCCESSFUL:
                return VaadinIcon.CHECK.create();
            case ERRORS:
                return VaadinIcon.WARNING.create();
            case IDLE:
                return VaadinIcon.QUESTION.create();
            case WORKING:
                return VaadinIcon.HOURGLASS.create();
            case UNEXPECTED:
                return new Span("Unexpected status");
            default:
                return new Span("Status not implemented!");
        }
    }

    private boolean interpretIsProcessingFinished(ProcessingService.ProcessingStatus status)
    {
        return status == ProcessingService.ProcessingStatus.SUCCESSFUL
                || status == ProcessingService.ProcessingStatus.ERRORS
                || status == ProcessingService.ProcessingStatus.UNEXPECTED;
    }
}
