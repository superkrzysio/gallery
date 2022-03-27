package kw.tools.gallery.views.gridplugins;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.Label;
import kw.tools.gallery.models.Gallery;
import kw.tools.gallery.services.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * 5-star rating component <br />
 * Requires @CssImport("styles/rating.css") in the parent view. <br />
 * Requires <tt>rating</tt> field in Gallery entity.
 */
@org.springframework.stereotype.Component
public class Rating implements GridPlugin
{
    public static final int MAX_RATING = 5;

    @Autowired
    private GalleryService galleryService;

    @Override
    public Optional<Component> getComponent(Gallery ctx)
    {
        // https://codepen.io/melnik909/pen/OvaxVY snippet migrated to Vaadin and somehow simplified
        Div div = new Div();
        div.addClassName("rating");
        div.getElement().setProperty("data-id", ctx.getId());
        for (int x = 1; x <= MAX_RATING; x++)
        {
            div.add(prepareRadio(ctx, x));
        }
        for (int x = 1; x <= MAX_RATING; x++)
        {
            div.add(prepareLabel(ctx, x));
        }
        return Optional.of(div);
    }

    @Override
    public Optional<Component> getHeader()
    {
        return Optional.empty();
    }

    private void setRating(String galId, int rating)
    {
        galleryService.setRating(galId, rating);
    }

    private static String formatRadioId(String galId, int i)
    {
        return String.format("%s-rc%d", galId, i);
    }

    private Input prepareRadio(final Gallery ctx, final int index)
    {
        Input radio = new Input();
        radio.setType("radio");
        radio.getElement().setProperty("name", ctx.getId());
        radio.addClassNames("rating__control", "screen-reader");
        radio.setId(formatRadioId(ctx.getId(), index));
        if (ctx.getRating() == index)
            radio.getElement().setProperty("checked", "checked");
        return radio;
    }

    private Label prepareLabel(final Gallery ctx, final int index)
    {
        Label label = new Label(String.format("%s-rc%d", ctx.getId(), index));
        label.setFor(formatRadioId(ctx.getId(), index));
        label.setText("");
        label.addClassName("rating__item");
        Image star = new Image("/star64.png", "");
        star.addClassName("rating__star");
        star.addClickListener(evt -> setRating(ctx.getId(), index));
        label.add(star);
        return label;
    }
}
