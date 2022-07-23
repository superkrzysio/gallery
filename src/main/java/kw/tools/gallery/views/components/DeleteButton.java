package kw.tools.gallery.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

public class DeleteButton extends Button
{
    private final GalleryRowContext ctx;

    public DeleteButton(GalleryRowContext ctx)
    {
        this.ctx = ctx;
        setIcon(VaadinIcon.TRASH.create());
        getElement().getThemeList().add("error");
        setDisableOnClick(false);
        addClickListener(e -> deleteGallery());
    }

    public void reenable()
    {
        this.setEnabled(true);
    }

    private void deleteGallery()
    {
        UI.getCurrent().getPage().executeJs("return confirm(\"Are you sure you want to DELETE THIS GALLERY FOLDER " +
                "PERMANENTLY FROM THE DISK?\")").then(decision -> {
            if (decision.asBoolean())
            {
                ctx.getActions().hardDelete();
                this.setEnabled(false);
            }
            ctx.getActions().nextPage();
        });

    }

}
