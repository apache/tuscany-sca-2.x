package org.apache.tuscany.core.context.event;

/**
 * Represents the a module end event
 *
 * @version $$Rev$$ $$Date$$
 */
public class ModuleStopEvent extends AbstractEvent implements ModuleEvent{

    public ModuleStopEvent(Object source) {
        super(source);
    }
}
