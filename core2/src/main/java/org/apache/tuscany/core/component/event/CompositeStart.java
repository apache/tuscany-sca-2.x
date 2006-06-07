package org.apache.tuscany.core.component.event;

import org.apache.tuscany.spi.component.CompositeComponent;

/**
 * Propagated when a composite starts
 *
 * @version $$Rev$$ $$Date$$
 */
public class CompositeStart extends AbstractEvent implements CompositeEvent {

    private CompositeComponent component;

    /**
     * Creates a module stop event
     *
     * @param source the source of the event
     * @param component    the composite component associated the module being stopped
     */
    public CompositeStart(Object source, CompositeComponent component) {
        super(source);
        this.component = component;
    }

    public CompositeComponent getComposite() {
        return component;
    }

}
