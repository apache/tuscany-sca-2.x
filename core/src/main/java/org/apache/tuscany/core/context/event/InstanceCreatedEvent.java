package org.apache.tuscany.core.context.event;

import org.apache.tuscany.core.context.Context;

/**
 * Denotes a component instance being created in the runtime
 * 
 * @version $$Rev$$ $$Date$$
 */
public class InstanceCreatedEvent extends AbstractEvent {

    public InstanceCreatedEvent(Object source) {
        super(source);
        assert(source instanceof Context): "Source must be of type " + Context.class.getClass().getName();
    }
}
