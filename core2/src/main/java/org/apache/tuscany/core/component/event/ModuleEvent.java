package org.apache.tuscany.core.component.event;

import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.context.CompositeComponent;

/**
 * Implemented by runtime events associated with a module, e.g. lifecycle events
 *
 * @version $$Rev$$ $$Date$$
 */
public interface ModuleEvent extends Event {

    public CompositeComponent getContext();

}
