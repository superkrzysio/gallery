package kw.tools.gallery.processing;

public interface ThumbnailSelectionFactory
{
    ThumbnailSelector get(ThumbnailSelector.Strategy strategy, int count);
}
