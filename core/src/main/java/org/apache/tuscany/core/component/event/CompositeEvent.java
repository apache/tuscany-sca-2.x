package org.apache.tuscany.core.component.event;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.event.Event;

/**
 * Implemented by runtime events associated with a composite, e.g. lifecycle events
 *
 * @version $$Rev: 415032 $$ $$Date: 2006-06-17 10:28:07 -0700 (Sat, 17 Jun 2006) $$
 */
public interface CompositeEvent extends Event {

    CompositeComponent getComposite();

}
