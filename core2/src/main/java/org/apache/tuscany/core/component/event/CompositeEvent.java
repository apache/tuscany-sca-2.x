package org.apache.tuscany.core.component.event;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.event.Event;

/**
 * Implemented by runtime events associated with a composite, e.g. lifecycle events
 *
 * @version $$Rev$$ $$Date$$
 */
public interface CompositeEvent extends Event {

    public CompositeComponent getComposite();

}
