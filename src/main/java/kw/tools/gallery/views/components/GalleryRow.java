package kw.tools.gallery.views.components;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import kw.tools.gallery.Utils;
import kw.tools.gallery.models.Gallery;

import java.util.ArrayList;
import java.util.List;

public class GalleryRow extends VerticalLayout
{
    private final Gallery gallery;
    private final H1 nameH1;
    private final Span path;
    private final Span id;
    private final Span pictureCount;
    private final List<Image> images;

    private final HorizontalLayout nameRow;
    private final HorizontalLayout imgRow;
    private final HorizontalLayout metaRow;


    public static final GalleryRow EMPTY = new GalleryRow(new Gallery());

    public GalleryRow(Gallery gal)
    {
        this.gallery = gal;

        // name row: gallery name and id
        nameRow = new HorizontalLayout();

        id = new Span(gallery.getId());
        id.addClassName("gallery-path");

        nameH1 = new H1(new Span(gallery.getName()), id);
        nameRow.add(nameH1);
        add(nameRow);

        // meta row: additional info like filesystem path, image count
        metaRow = new HorizontalLayout();

        path = new Span(gallery.getPath());
        path.addClassName("gallery-path");
        path.addClickListener(e -> Utils.openInFileBrowser(gallery.getPath()));
        metaRow.add(path);

        pictureCount = new Span("Images: " + gallery.getPictureCount());
        metaRow.add(pictureCount);
        add(metaRow);

        // img row: thumbnails
        imgRow = new HorizontalLayout();
        images = new ArrayList<>();
        for (String imgSrc : gallery.getThumbnails())
        {
            Image img = new Image(imgSrc, imgSrc);
            img.addClassName("thumb");
            imgRow.add(img);
            images.add(img);
        }
        add(imgRow);
    }
}
