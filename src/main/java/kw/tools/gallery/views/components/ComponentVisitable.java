package kw.tools.gallery.views.components;

public interface ComponentVisitable
{
    void accept(OnPageChangeVisitor onPageChangeVisitor);
}
