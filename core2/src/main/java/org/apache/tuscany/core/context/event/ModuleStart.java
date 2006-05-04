package org.apache.tuscany.core.context.event;

import org.apache.tuscany.spi.context.CompositeContext;

/**
 * Propagated when a module starts
 *
 * @version $$Rev$$ $$Date$$
 */
public class ModuleStart extends AbstractEvent implements ModuleEvent {

    private CompositeContext context;
    
    /**
     * Creates a module stop event
     * @param source the source of the event
     * @param ctx the composite context associated the module being stopped
     */
    public ModuleStart(Object source, CompositeContext ctx) {
        super(source);
        assert(ctx != null): "Module composite context was null";
        context = ctx;
    }

    public CompositeContext getContext(){
        return context;
    }

}
