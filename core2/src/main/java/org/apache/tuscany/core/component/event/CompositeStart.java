package org.apache.tuscany.core.component.event;

import org.apache.tuscany.spi.context.CompositeComponent;
import org.apache.tuscany.core.component.event.AbstractEvent;
import org.apache.tuscany.core.component.event.CompositeEvent;

/**
 * Propagated when a composite starts
 *
 * @version $$Rev$$ $$Date$$
 */
public class CompositeStart extends AbstractEvent implements CompositeEvent {

    private CompositeComponent component;

    /**
     * Creates a module stop event
     * @param source the source of the event
     * @param ctx the composite component associated the module being stopped
     */
    public CompositeStart(Object source, CompositeComponent ctx) {
        super(source);
        component = ctx;
    }

    public CompositeComponent getComposite(){
        return component;
    }

}
