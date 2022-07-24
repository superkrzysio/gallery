package kw.tools.gallery.views.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Label;
import kw.tools.gallery.models.Gallery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 5-star rating component <br />
 * Requires @CssImport("styles/rating.css") in the parent view. <br />
 * Requires <tt>rating</tt> field in Gallery entity.
 */
public class Rating extends Div implements ComponentVisitable
{
    private static final Logger LOG = LoggerFactory.getLogger(Rating.class);
    public static final int MAX_RATING = 5;
    private static final String RADIO_ID = "rating-component-";
    private final Input[] radios;

    private final CurrentRowActions ctx;
    private Gallery galleryCtx;

    public Rating(CurrentRowActions ctx)
    {
        this.ctx = ctx;
        this.radios = new Input[MAX_RATING];

        galleryCtx = new EmptyGallery();

        // https://codepen.io/melnik909/pen/OvaxVY snippet migrated to Vaadin and somehow simplified
        this.addClassName("rating");
//        div.getElement().setProperty("data-id", ctx.getId());
        for (int x = 1; x <= MAX_RATING; x++)
        {
            Input radio = prepareRadio(x);
            this.add(radio);
            radios[x-1] = radio;
        }
        for (int x = 1; x <= MAX_RATING; x++)
        {
            this.add(prepareLabel(x));
        }
    }

    private Input prepareRadio(final int index)
    {
        Input radio = new Input();
        radio.setType("radio");
        radio.getElement().setProperty("name", galleryCtx.getId());
        radio.addClassNames("rating__control", "screen-reader");
        radio.setId(formatRadioId(index));
        return radio;
    }

    private Label prepareLabel(final int index)
    {
        Label label = new Label(String.format("%s-rc%d", galleryCtx.getId(), index));
        label.setFor(formatRadioId(index));
        label.setText("");
        label.addClassName("rating__item");
        Image star = new Image("/star64.png", "");
        star.addClassName("rating__star");
        star.addClickListener(evt -> saveRating(index));
        label.add(star);
        return label;
    }

    private static String formatRadioId(int i)
    {
        return String.format("%s-rc%d", RADIO_ID, i);
    }

    public void redrawRating()
    {
        int rating = galleryCtx.getRating();
        LOG.debug("Loading rating {} for gallery '{}'", rating, galleryCtx.getName());
        for (int i = 0; i < MAX_RATING; i++)
        {
            radios[i].getElement().removeProperty("checked");
        }
        if (rating > 0)
        {
            radios[rating-1].getElement().setProperty("checked", "checked");
        }
    }

    private void saveRating(int rating)
    {
        LOG.debug("Setting rating {} for gallery {}", rating, galleryCtx.getName());
        ctx.setRating(rating);
        ctx.nextPage();
        redrawRating();
    }

    @Override
    public void accept(OnPageChangeVisitor onPageChangeVisitor)
    {
        onPageChangeVisitor.visit(this);
    }

    public void setGalleryCtx(Gallery galleryCtx)
    {
        this.galleryCtx = galleryCtx;
    }

    private static class EmptyGallery extends Gallery
    {
        private EmptyGallery()
        {
            this.setId("");
            this.setName("");
            this.setRating(0);
        }
    }
}
