package kw.tools.gallery.views.components;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import kw.tools.gallery.persistence.GallerySearchCriteria;
import kw.tools.gallery.views.RepositorySingleView;

public class FilterByRated extends Div implements ClickNotifier<Div>
{
    public static final String NAME = "rated";

    private final CurrentRowActions ctx;

    public FilterByRated(CurrentRowActions ctx)
    {
        super();
        this.ctx = ctx;
        createButton(GallerySearchCriteria.RatingSearchMode.ZERO);
        createButton(GallerySearchCriteria.RatingSearchMode.POSITIVE);
        createButton(GallerySearchCriteria.RatingSearchMode.NONE);
    }

    private void createButton(GallerySearchCriteria.RatingSearchMode option)
    {
        Button button = new Button();
        button.setText(option.getDescription());
        button.addClassName("rating-filter-button");
        button.addClickListener(evt -> filter(option.name()));
        this.add(button);
    }

    private void filter(String value)
    {
        ctx.filter(RepositorySingleView.RATED_PARAM_KEY, value);
    }
}
