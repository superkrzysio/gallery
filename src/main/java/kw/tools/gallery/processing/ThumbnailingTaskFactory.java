package kw.tools.gallery.processing;

public interface ThumbnailingTaskFactory
{
    /**
     * Creates a task that will generate thumbs from the <tt>source</tt> folder
     * into <tt>target</tt> folder.
     *
     * @param repoId
     * @param source
     * @param target
     * @return
     */
    Task create(String repoId, String source, String target);
}
