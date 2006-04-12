package org.apache.tuscany.core.context.event;


/**
 * Represents the start of a session
 * @version $$Rev$$ $$Date$$
 */
public class HttpSessionBoundEvent extends HttpSessionEvent {

    public HttpSessionBoundEvent(Object source, Object id) {
        super(source,id);
    }

}
