package kw.tools.gallery.views.gridplugins;

import kw.tools.gallery.models.Gallery;

import java.util.function.Consumer;

/**
 * Mark the plugin (and trust it) that it will call the defined callbacks after executing its main action. <br />
 * Best suited for single-action plugins like delete button.
 */
public interface AfterActionObservable
{
    void addAfterActionCallback(Consumer<Gallery> callback);
}
