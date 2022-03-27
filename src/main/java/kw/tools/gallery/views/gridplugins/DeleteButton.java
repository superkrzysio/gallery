package kw.tools.gallery.views.gridplugins;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import kw.tools.gallery.models.Gallery;
import kw.tools.gallery.services.GalleryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@org.springframework.stereotype.Component
public class DeleteButton implements GridPlugin
{
    @Autowired
    private GalleryService galleryService;

    @Override
    public Optional<Component> getComponent(Gallery ctx)
    {
        Button delete = new Button();
        delete.setIcon(VaadinIcon.TRASH.create());
        delete.getElement().getThemeList().add("error");
        delete.setDisableOnClick(false);
        delete.addClickListener(e -> deleteGallery(ctx.getId(), delete));
        return Optional.of(delete);
    }

    @Override
    public Optional<Component> getHeader()
    {
        return Optional.empty();
    }

    private void deleteGallery(String id, Button buttonHandle)
    {
        UI.getCurrent().getPage().executeJs("return confirm(\"Are you sure you want to DELETE THIS GALLERY FOLDER " +
                "PERMANENTLY FROM THE DISK?\")").then(decision -> {
            if (decision.asBoolean())
            {
                galleryService.hardDelete(id);
                buttonHandle.setEnabled(false);
            }
        });
    }
}
