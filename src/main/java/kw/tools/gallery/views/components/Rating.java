package kw.tools.gallery.views.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 5-star rating component <br />
 * Requires @CssImport("styles/rating.css") in the parent view. <br />
 * Requires <tt>rating</tt> field in Gallery entity.
 */
public class Rating extends Div
{
    private static final Logger LOG = LoggerFactory.getLogger(Rating.class);
    public static final int MAX_RATING = 5;
    private static final String RADIO_ID = "rating-component-";
    private final Input[] radios;

    private final GalleryRowContext ctx;

    public Rating(GalleryRowContext ctx)
    {
        this.ctx = ctx;
        this.radios = new Input[MAX_RATING];

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
        radio.getElement().setProperty("name", ctx.getGalleryId());
        radio.addClassNames("rating__control", "screen-reader");
        radio.setId(formatRadioId(index));
        return radio;
    }

    private Label prepareLabel(final int index)
    {
        Label label = new Label(String.format("%s-rc%d", ctx.getGalleryId(), index));
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

    public void loadRating()
    {
        int rating = ctx.getGalleryRating();
        LOG.debug("Loading rating {} for gallery '{}'", rating, ctx.getGalleryName());
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
        LOG.debug("Setting rating {} for gallery {}", rating, ctx.getGalleryName());
        ctx.getActions().setRating(rating);
        loadRating();
        ctx.getActions().nextPage();
    }
}
