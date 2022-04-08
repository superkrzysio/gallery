package kw.tools.gallery.views.gridplugins;

import com.vaadin.flow.component.Component;
import kw.tools.gallery.models.Gallery;

import java.util.Optional;

/**
 * Controls for each gallery row are implemented as "plugin", which provides fully featured {@link Component}
 * including event listeners.<br />
 * Any additional Gallery fields used by plugin (e.g. <tt>rating</tt> column) must be added separately to Gallery entity.<br/>
 * Plugin objects are stateless, they generate data on demand (components, callbacks, etc).
 */
public interface GridPlugin
{
    /**
     * Create a component that will be displayed on the gallery grid.
     */
    Optional<Component> getComponent(Gallery ctx);

    /**
     * Create a component that will be displayed only once, above the grid
     * and can be used for filtering, addtional scripts, etc.
     */
    Optional<Component> getHeader();
}
