package org.apache.tuscany.core.context.event;

import org.apache.tuscany.spi.context.CompositeComponent;

/**
 * Propagated when a module starts
 *
 * @version $$Rev$$ $$Date$$
 */
public class ModuleStart extends AbstractEvent implements ModuleEvent {

    private CompositeComponent component;
    
    /**
     * Creates a module stop event
     * @param source the source of the event
     * @param ctx the composite component associated the module being stopped
     */
    public ModuleStart(Object source, CompositeComponent ctx) {
        super(source);
        component = ctx;
    }

    public CompositeComponent getContext(){
        return component;
    }

}
