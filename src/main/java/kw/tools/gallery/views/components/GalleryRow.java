package kw.tools.gallery.views.components;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import kw.tools.gallery.Utils;
import kw.tools.gallery.models.Gallery;
import kw.tools.gallery.views.gridplugins.GridPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GalleryRow extends VerticalLayout
{
    private final Gallery gallery;
    private final H1 nameH1;
    private final Span path;
    private final Span id;
    private final Span pictureCount;
    private final List<Image> images;
    private final List<GridPlugin> plugins;


    private final HorizontalLayout nameRow;
    private final HorizontalLayout imgRow;
    private final HorizontalLayout metaRow;
    private final HorizontalLayout controlsRow;
    private final VerticalLayout galleryControlsVerticalWrapper;

    private Binder<Gallery> binder;

    public static final GalleryRow EMPTY = new GalleryRow(new Gallery(), Collections.emptyList());

    public GalleryRow(Gallery gal, List<GridPlugin> pls)
    {
        this.gallery = gal;
        this.plugins = pls;

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

        // controls row: plugins
        controlsRow = new HorizontalLayout();
        controlsRow.setAlignItems(Alignment.CENTER);
        galleryControlsVerticalWrapper = new VerticalLayout();
        plugins.forEach(plugin -> plugin.getComponent(gallery).ifPresent(galleryControlsVerticalWrapper::add));
        galleryControlsVerticalWrapper.setAlignItems(FlexComponent.Alignment.CENTER);
        imgRow.add(galleryControlsVerticalWrapper);
        controlsRow.add(galleryControlsVerticalWrapper);
        add(controlsRow);

//        binder.bin


    }
}
