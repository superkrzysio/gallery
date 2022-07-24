package kw.tools.gallery.views;

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
import kw.tools.gallery.persistence.GallerySearchCriteria;
import kw.tools.gallery.services.GalleryService;
import kw.tools.gallery.services.RepositoryService;
import kw.tools.gallery.views.components.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.function.Supplier;

@Route("single-view")
public class RepositorySingleView extends VerticalLayout implements HasUrlParameter<String>
{
    private static final Logger LOG = LoggerFactory.getLogger(RepositorySingleView.class);
    private static final String REPOSITORY_PARAM_KEY = "repository";
    private static final String RATED_PARAM_KEY = "rated";

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
//    private final FilterByRated filterByRated;

    public RepositorySingleView()
    {
        setHeight(100, Unit.PERCENTAGE);
        setAlignItems(Alignment.CENTER);

        HorizontalLayout backLink = new HorizontalLayout(new Anchor("/", "Back"));
        backLink.setWidth(100, Unit.PERCENTAGE);
        add(backLink);

        prevButton = new Button("prev");
        prevButton.addClickListener(evt -> switchPage(() -> galleryIterator.hasPrevious(), () -> galleryIterator.previous()));
        nextButton = new Button("next");
        nextButton.addClickListener(evt -> switchPage(() -> galleryIterator.hasNext(), () -> galleryIterator.next()));
        HorizontalLayout nav = new HorizontalLayout(prevButton, nextButton);
        add(nav);

//        filterByRated = new FilterByRated();
//        HorizontalLayout filters = new HorizontalLayout(filterByRated);
//        add(filters);

        galleryRowRichComponent = GalleryRow.EMPTY;
        add(galleryRowRichComponent);

        // controls row
        // attach an empty context, which will be modified later
        CurrentRowActions currentRowActions = new DefaultCurrentRowActions();
        HorizontalLayout controlsRow = new HorizontalLayout();
        controlsRow.setAlignItems(Alignment.CENTER);
        VerticalLayout galleryControlsVerticalWrapper = new VerticalLayout();
        rating = new Rating(currentRowActions);
        deleteButton = new DeleteButton(currentRowActions);
        galleryControlsVerticalWrapper.add(rating);
        galleryControlsVerticalWrapper.add(deleteButton);
        galleryControlsVerticalWrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        controlsRow.add(galleryControlsVerticalWrapper);
        add(controlsRow);
    }

    private void switchPage(Supplier<Boolean> checkNext, Supplier<Gallery> getNext)
    {
        if (checkNext.get())
        {
            Gallery old = currentGallery;
            currentGallery = getNext.get();
            LOG.debug("Switching from '{}' (id: {}) to '{}' (id: {})", old.getName(), old.hashCode(),
                    currentGallery.getName(), currentGallery.hashCode());
            if (currentGallery == old)      // fix for listIterator behaviour when alternating next() and previous()
            {
                switchPage(checkNext, getNext);
                return;
            }
            refreshNavButtons();
            loadGallery();
            afterPageChange();
        } else
        {
            refreshNavButtons();
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
    }

    private void afterPageChange()
    {
        OnPageChangeVisitor onPageChange = new OnPageChangeVisitor(currentGallery);
        rating.accept(onPageChange);
        deleteButton.accept(onPageChange);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String repo)
    {
        Map<String, List<String>> queryParams = beforeEvent.getLocation().getQueryParameters().getParameters();

        Repository repository = getRepoOrRedirect(queryParams);
        if (repository == null)
        {
            return;
        }

        GallerySearchCriteria.RatingSearchMode rated = getRatedFilter(queryParams);

        List<Gallery> galleries = galleryService.search(
                new GallerySearchCriteria().withRepository(repository.getId()).withRating(rated)
        );
        galleries.forEach(gal -> galleryService.setThumbs(gal));

        galleryIterator = galleries.listIterator();

        if (!galleryIterator.hasNext())     // do not honor empty repositories
        {
            redirectHome();
        }
        currentGallery = galleryIterator.next();
        refreshNavButtons();
        loadGallery();
        afterPageChange();
    }

    private GallerySearchCriteria.RatingSearchMode getRatedFilter(Map<String, List<String>> queryParams)
    {
        List<String> rated = queryParams.get(RATED_PARAM_KEY);
        if (rated == null || rated.isEmpty())
        {
            return GallerySearchCriteria.RatingSearchMode.NONE;
        }
        try
        {
            return GallerySearchCriteria.RatingSearchMode.valueOf(rated.get(0).toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e)
        {
            return GallerySearchCriteria.RatingSearchMode.NONE;
        }
    }

    private Repository getRepoOrRedirect(final Map<String, List<String>> queryParams)
    {
        List<String> repos = queryParams.get(REPOSITORY_PARAM_KEY);
        if (repos == null || repos.size() > 1)
        {
            redirectHome();
            return null;
        }
        Optional<Repository> maybeRepo = repositoryService.get(repos.get(0));
        if (maybeRepo.isEmpty())
        {
            redirectHome();
            return null;
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
            switchPage(() -> galleryIterator.hasNext(), () -> galleryIterator.next());
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
