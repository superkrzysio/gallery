package kw.tools.gallery.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import kw.tools.gallery.models.Gallery;
import kw.tools.gallery.models.Repository;
import kw.tools.gallery.services.GalleryService;
import kw.tools.gallery.services.RepositoryService;
import kw.tools.gallery.views.components.GalleryRow;
import kw.tools.gallery.views.gridplugins.GridPlugin;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

@Route("single-view")
public class RepositorySingleView extends VerticalLayout implements HasUrlParameter<String>
{
    private static final String REPOSITORY_PARAM_KEY = "repository";

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private GalleryService galleryService;

    private final List<GridPlugin> gridPlugins;

    private GalleryRow galleryRowRichComponent;
    private Gallery currentGallery;
    private ListIterator<Gallery> galleryIterator;

    private final Button nextButton;
    private final Button prevButton;

    // todo: bugs: deleting does not delete from iterator; rating does not refresh in current iterator until page refresh,
    //      in general retarded plugin handling
    public RepositorySingleView(@Autowired List<GridPlugin> gplugins)
    {
        this.gridPlugins = gplugins;
        setHeight(100, Unit.PERCENTAGE);
        setAlignItems(Alignment.CENTER);

        HorizontalLayout backLink = new HorizontalLayout(new Anchor("/", "Back"));
        backLink.setWidth(100, Unit.PERCENTAGE);
        add(backLink);

        prevButton = new Button("prev");
        prevButton.addClickListener(this::prevGallery);
        nextButton = new Button("next");
        nextButton.addClickListener(this::nextGallery);
        HorizontalLayout nav = new HorizontalLayout(prevButton, nextButton);
        add(nav);

        galleryRowRichComponent = GalleryRow.EMPTY;
        add(galleryRowRichComponent);

        // prototype, layout will come later
        gridPlugins.forEach(plugin -> plugin.getHeader().ifPresent(this::add));
    }

    private void nextGallery(ClickEvent<Button> buttonClickEvent)
    {
        if (galleryIterator.hasNext())
        {
            currentGallery = galleryIterator.next();
            refreshNavButtons();
            loadGallery();
        }
    }

    private void prevGallery(ClickEvent<Button> buttonClickEvent)
    {
        if (galleryIterator.hasPrevious())
        {
            currentGallery = galleryIterator.previous();
            refreshNavButtons();
            loadGallery();
        }
    }

    private void refreshNavButtons()
    {
        prevButton.setEnabled(galleryIterator.hasPrevious());
        nextButton.setEnabled(galleryIterator.hasNext());
    }

    private void loadGallery()
    {
        GalleryRow galleryRow = new GalleryRow(currentGallery, gridPlugins);
        replace(galleryRowRichComponent, galleryRow);
        galleryRowRichComponent = galleryRow;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String repo)
    {
        Map<String, List<String>> queryParams = beforeEvent.getLocation().getQueryParameters().getParameters();

        Repository repository = getRepoOrRedirect(queryParams);

        galleryIterator = galleryService.getAllFull(repository.getId()).listIterator();
        if (!galleryIterator.hasNext())     // do not honor empty repositories
        {
            redirectHome();
        }
        currentGallery = galleryIterator.next();
        refreshNavButtons();
        loadGallery();
    }

    private Repository getRepoOrRedirect(final Map<String, List<String>> queryParams)
    {
        List<String> repos = queryParams.get(REPOSITORY_PARAM_KEY);
        if (repos == null || repos.size() > 1)
        {
            redirectHome();
        }
        Optional<Repository> maybeRepo = repositoryService.get(repos.get(0));
        if (maybeRepo.isEmpty())
        {
            redirectHome();
        }
        return maybeRepo.get();
    }

    private void redirectHome()
    {
        UI.getCurrent().getPage().setLocation("/");
        throw new IllegalArgumentException("Redirect in progress");
    }

}
