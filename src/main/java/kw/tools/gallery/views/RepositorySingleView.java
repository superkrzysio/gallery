package kw.tools.gallery.views;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
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
import kw.tools.gallery.views.components.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Route("single-view")
public class RepositorySingleView extends VerticalLayout implements HasUrlParameter<String>
{
    private static final Logger LOG = LoggerFactory.getLogger(RepositorySingleView.class);
    private static final String REPOSITORY_PARAM_KEY = "repository";

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private GalleryService galleryService;

    private GalleryRow galleryRowRichComponent;
    private Gallery currentGallery;
    private ListIterator<Gallery> galleryIterator;

    private final Button nextButton;
    private final Button prevButton;
    private final Rating rating;
    private final DeleteButton deleteButton;
    private final HorizontalLayout controlsRow;
    private final VerticalLayout galleryControlsVerticalWrapper;
    private final ModifiableGalleryRowContext currentRowCtx;

    // todo: bugs: when setting a rating, all next galleries with lower rating will appear to have this rating
    public RepositorySingleView()
    {
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

        // controls row
        // attach an empty context, which will be modified later
        currentRowCtx = new ModifiableGalleryRowContext();
        currentRowCtx.setActions(new DefaultCurrentRowActions());
        controlsRow = new HorizontalLayout();
        controlsRow.setAlignItems(Alignment.CENTER);
        galleryControlsVerticalWrapper = new VerticalLayout();
        rating = new Rating(currentRowCtx);
        deleteButton = new DeleteButton(currentRowCtx);
        galleryControlsVerticalWrapper.add(rating);
        galleryControlsVerticalWrapper.add(deleteButton);
        galleryControlsVerticalWrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        controlsRow.add(galleryControlsVerticalWrapper);
        add(controlsRow);
    }

    private void nextGallery(ClickEvent<Button> buttonClickEvent)
    {
        if (galleryIterator.hasNext())
        {
            Gallery old = currentGallery;
            currentGallery = galleryIterator.next();
            LOG.debug("NextGallery: Switching from '{}' (id: {}) to '{}' (id: {})", old.getName(), old.hashCode(),
                    currentGallery.getName(), currentGallery.hashCode());
            if (currentGallery == old)      // fix for listIterator behaviour when alternating next() and previous()
            {
                nextGallery(buttonClickEvent);
                return;
            }
            refreshNavButtons();
            loadGallery();
        }
    }

    private void prevGallery(ClickEvent<Button> buttonClickEvent)
    {
        if (galleryIterator.hasPrevious())
        {
            Gallery old = currentGallery;
            currentGallery = galleryIterator.previous();
            LOG.debug("PrevGallery: Switching from '{}' (id: {}) to '{}' (id: {})", old.getName(), old.hashCode(),
                    currentGallery.getName(), currentGallery.hashCode());
            if (currentGallery == old)    // fix for listIterator behaviour when alternating next() and previous()
            {
                prevGallery(buttonClickEvent);
                return;
            }
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
        GalleryRow galleryRow = new GalleryRow(currentGallery);
        replace(galleryRowRichComponent, galleryRow);
        galleryRowRichComponent = galleryRow;
        currentRowCtx.setGallery(currentGallery);
        rating.loadRating();
        deleteButton.reenable();
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

    private class DefaultCurrentRowActions implements CurrentRowActions
    {
        @Override
        public void nextPage()
        {
            nextGallery(null);
        }

        @Override
        public void filter()
        {

        }

        @Override
        public void hardDelete()
        {
            galleryIterator.remove();
            galleryService.hardDelete(currentGallery.getId());
        }

        @Override
        public void setRating(int rating)
        {
            currentGallery.setRating(rating);
            galleryService.setRating(currentGallery.getId(), rating);
        }
    }
}
