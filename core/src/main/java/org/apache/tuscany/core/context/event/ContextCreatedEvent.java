package org.apache.tuscany.core.context.event;

/**
 * Represents the creation of a {@link org.apache.tuscany.core.context.Context} in the runtime
 * @version $$Rev$$ $$Date$$
 */
public class ContextCreatedEvent extends AbstractEvent {

    public ContextCreatedEvent(Object source) {
        super(source);
    }

}
