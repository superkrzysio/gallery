package kw.tools.gallery.views;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import kw.tools.gallery.models.Gallery;
import kw.tools.gallery.models.Repository;
import kw.tools.gallery.services.GalleryService;
import kw.tools.gallery.services.RepositoryService;
import kw.tools.gallery.views.gridplugins.GridPlugin;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Route("repository")
@CssImport("styles/styles.css")
@CssImport("styles/rating.css")
public class RepositoryView extends VerticalLayout implements HasUrlParameter<String>
{
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private GalleryService galleryService;

    private final List<GridPlugin> gridPlugins;

    private String repoId;

    private final Grid<Gallery> masterGrid = new Grid<>();

    private final H2 repositoryTitle = new H2();

    public RepositoryView(@Autowired List<GridPlugin> gplugins)
    {
        this.gridPlugins = gplugins;
        setHeight(100, Unit.PERCENTAGE);

        add(new Anchor("/", "Back"));
        add(repositoryTitle);
        gridPlugins.forEach(plugin -> plugin.getHeader().ifPresent(this::add));

        masterGrid.setHeight(100, Unit.PERCENTAGE);
        masterGrid.setSelectionMode(Grid.SelectionMode.NONE);
        masterGrid.addComponentColumn(gallery -> {
            HorizontalLayout headersRow = new HorizontalLayout();
            headersRow.add(new Span(gallery.getName()));
            Span pathSpan = new Span(gallery.getPath());
            pathSpan.addClassName("gallery-path");
            pathSpan.addClickListener(e -> galleryService.openInFileBrowser(gallery.getPath()));
            headersRow.add(pathSpan);
            headersRow.add(new Span("Images: " + gallery.getPictureCount()));
            HorizontalLayout imgRow = new HorizontalLayout();
            for (String imgSrc : gallery.getThumbnails())
            {
                Image img = new Image(imgSrc, imgSrc);
                img.addClassName("thumb");
                imgRow.add(img);
            }
            return new VerticalLayout(headersRow, imgRow);
        }).setKey("gallery").setAutoWidth(true).setComparator(Gallery::getId).setSortable(true);

        masterGrid.addComponentColumn(gallery -> {
            VerticalLayout verticalWrapper = new VerticalLayout();
            gridPlugins.forEach(plugin -> plugin.getComponent(gallery).ifPresent(verticalWrapper::add));
            verticalWrapper.setAlignItems(Alignment.CENTER);
            return verticalWrapper;
        }).setKey("actions").setAutoWidth(false).setFlexGrow(1);
        add(masterGrid);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String param)
    {
        this.repoId = param;
        Optional<Repository> repo = repositoryService.get(repoId);
        repo.ifPresent(r -> {
            List<Gallery> gals = galleryService.getAllFull(repoId);
            repositoryTitle.setText(r.getId());
            masterGrid.setItems(gals);
        });
    }
}
