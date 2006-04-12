package org.apache.tuscany.core.context.event;

/**
 * Propagated when a module stops
 *
 * @version $$Rev$$ $$Date$$
 */
public class ModuleStopEvent extends AbstractEvent implements ModuleEvent{

    public ModuleStopEvent(Object source) {
        super(source);
    }
}
