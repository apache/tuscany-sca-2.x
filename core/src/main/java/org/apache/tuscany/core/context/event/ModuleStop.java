package org.apache.tuscany.core.context.event;

/**
 * Propagated when a module stops
 *
 * @version $$Rev$$ $$Date$$
 */
public class ModuleStop extends AbstractEvent implements ModuleEvent{

    public ModuleStop(Object source) {
        super(source);
    }
}
