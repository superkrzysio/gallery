package kw.tools.gallery.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import kw.tools.gallery.models.Gallery;
import kw.tools.gallery.models.Repository;
import kw.tools.gallery.services.GalleryService;
import kw.tools.gallery.services.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;

@Route("repository")
@CssImport("styles/styles.css")
public class RepositoryView extends VerticalLayout implements HasUrlParameter<String>
{
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private GalleryService galleryService;

    private String repoId;

    private final Grid<Gallery> masterGrid = new Grid<>();

    private final H2 repositoryTitle = new H2();

    public RepositoryView()
    {
        add(new Anchor("/", "Back"));
        setHeight(100, Unit.PERCENTAGE);
        masterGrid.setHeight(100, Unit.PERCENTAGE);
        masterGrid.setSelectionMode(Grid.SelectionMode.NONE);
        masterGrid.addComponentColumn(gallery -> {
            HorizontalLayout headersRow = new HorizontalLayout();
            headersRow.add(new Span(gallery.getName()));
            Span pathSpan = new Span(gallery.getPath());
            pathSpan.addClassName("gallery-path");
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
        }).setKey("gallery").setAutoWidth(true).setFlexGrow(1).setComparator(Gallery::getId).setSortable(true);

        masterGrid.addComponentColumn(gallery -> {
            HorizontalLayout wrapper = new HorizontalLayout();
            Button delete = new Button();
            delete.setIcon(VaadinIcon.TRASH.create());
            delete.getElement().getThemeList().add("error");
            delete.setDisableOnClick(false);
            delete.addClickListener(e -> deleteGallery(gallery.getId(), delete));
            wrapper.add(delete);
            wrapper.setAlignItems(Alignment.CENTER);
            return wrapper;
        }).setKey("actions").setAutoWidth(false).setFlexGrow(0);
        add(repositoryTitle);
        add(masterGrid);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String param)
    {
        this.repoId = param;
        Repository repository = repositoryService.getFullForId(repoId);
        repositoryTitle.setText(repository.getId());
        masterGrid.setItems(repository.getGalleries());
    }

    private void deleteGallery(String id, Button buttonHandle)
    {
        UI.getCurrent().getPage().executeJs("return confirm(\"Are you sure you want to delete?\")").then(decision -> {
            if (decision.asBoolean())
            {
                galleryService.delete(id);
                buttonHandle.setEnabled(false);
            }
        });
    }
}
