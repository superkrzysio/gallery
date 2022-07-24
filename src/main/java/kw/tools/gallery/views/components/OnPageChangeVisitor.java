package kw.tools.gallery.views.components;

import kw.tools.gallery.models.Gallery;

/**
 * Additional logic on components when a page changes (prev/next buttons)
 */
public class OnPageChangeVisitor
{
    private final Gallery ctx;

    public OnPageChangeVisitor(Gallery newCtx)
    {
        this.ctx = newCtx;
    }

    public void visit(DeleteButton deleteButton)
    {
        deleteButton.setEnabled(true);
    }

    public void visit(Rating rating)
    {
        rating.setGalleryCtx(ctx);
        rating.redrawRating();
    }
}
