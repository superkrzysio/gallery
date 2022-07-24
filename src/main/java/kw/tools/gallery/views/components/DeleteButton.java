package kw.tools.gallery.views.components;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

public class DeleteButton extends Button implements ComponentVisitable
{
    private final CurrentRowActions ctx;

    public DeleteButton(CurrentRowActions ctx)
    {
        this.ctx = ctx;
        setIcon(VaadinIcon.TRASH.create());
        getElement().getThemeList().add("error");
        setDisableOnClick(false);
        addClickListener(e -> deleteGallery());
    }

    private void deleteGallery()
    {
        UI.getCurrent().getPage().executeJs("return confirm(\"Are you sure you want to DELETE THIS GALLERY FOLDER " +
                "PERMANENTLY FROM THE DISK?\")").then(decision -> {
            if (decision.asBoolean())
            {
                ctx.hardDelete();
                this.setEnabled(false);
            }
            ctx.nextPage();
        });

    }

    @Override
    public void accept(OnPageChangeVisitor onPageChangeVisitor)
    {
        onPageChangeVisitor.visit(this);
    }

}
