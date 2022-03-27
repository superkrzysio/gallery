package kw.tools.gallery.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import kw.tools.gallery.models.Gallery;
import kw.tools.gallery.models.Repository;
import kw.tools.gallery.services.GalleryService;
import kw.tools.gallery.services.RepositoryService;
import kw.tools.gallery.services.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Route("/regenerate")
public class RegenerateView extends VerticalLayout implements HasUrlParameter<String>
{
    private static final Logger LOG = LoggerFactory.getLogger(RegenerateView.class);
    final private TaskService taskService;
    final private RepositoryService repositoryService;
    final private GalleryService galleryService;

    final TextField targetRepositoryInput;
    final Checkbox addNewCheckbox;
    final Checkbox removeMissingCheckbox;
    final Checkbox regenerateThumbsCheckbox;

    final Button okButton;
    final Button cancelButton;

    String repoId;

    public RegenerateView(@Autowired TaskService taskService,
                          @Autowired RepositoryService repositoryService,
                          @Autowired GalleryService galleryService)
    {
        this.taskService = taskService;
        this.repositoryService = repositoryService;
        this.galleryService = galleryService;

        setHeight(100, Unit.PERCENTAGE);
        setWidth(50, Unit.PERCENTAGE);
        targetRepositoryInput = new TextField();
        targetRepositoryInput.setEnabled(false);
        targetRepositoryInput.setMaxWidth(100, Unit.PERCENTAGE);
        add(targetRepositoryInput);
        VerticalLayout checkboxGroup = new VerticalLayout();

        addNewCheckbox = new Checkbox("Scan for new galleries", true);
        removeMissingCheckbox = new Checkbox("Clean galleries missing on disk", true);
        regenerateThumbsCheckbox = new Checkbox("Regenerate all thumbs", false);
        checkboxGroup.add(addNewCheckbox, removeMissingCheckbox, regenerateThumbsCheckbox);
        this.add(checkboxGroup);

        HorizontalLayout controlButtonsGroup = new HorizontalLayout();
        VerticalLayout controlButtonsGroupAlignment = new VerticalLayout();
        controlButtonsGroupAlignment.setAlignItems(Alignment.END);

        okButton = new Button("OK");
        okButton.getElement().getThemeList().add("primary");
        okButton.addClickListener(this::ok);
        cancelButton = new Button("Cancel");
        cancelButton.addClickListener(this::cancel);
        controlButtonsGroup.add(cancelButton, okButton);
        controlButtonsGroupAlignment.add(controlButtonsGroup);
        this.add(controlButtonsGroupAlignment);
    }

    private void cancel(ClickEvent<Button> buttonClickEvent)
    {
        this.getUI().get().getPage().setLocation("/");
    }

    private void ok(ClickEvent<Button> buttonClickEvent)
    {
        Repository repo = repositoryService.get(repoId).orElseThrow();

        // it is recommended to call regenerating thumbs separately, because it:
        // either concurrently tries to regenerate freshly added galleries
        // or concurrently tries to regenerate galleries that were or will be removed
        // todo: to fix this I need to implement task dependencies in the task engine
        if (regenerateThumbsCheckbox.getValue())
        {
            for (Gallery g : galleryService.getAll(repoId))
                taskService.createThumbnailingTask(repoId, g.getPath(), galleryService.getThumbnailDir(repoId, g.getId()));
        }

        if (addNewCheckbox.getValue())
        {
            taskService.createScanningTask(repo.getId(), repo.getPath());
        }
        if (removeMissingCheckbox.getValue())
        {
            taskService.createGalleryClensingTask(repo.getId());
        }
        this.getUI().get().getPage().setLocation("/");
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String pathParam)
    {
        targetRepositoryInput.setValue(pathParam);
        this.repoId = pathParam;
        if (repositoryService.get(repoId).isEmpty())
        {
            throw new IllegalArgumentException("Repository does not exist");
        }
    }

}
