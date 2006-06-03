package org.apache.tuscany.core.component.event;

import org.apache.tuscany.core.component.event.HttpSessionEvent;

/**
 * Propagated when an HTTP-based session has started
 *
 * @version $$Rev$$ $$Date$$
 */
public class HttpSessionStart extends HttpSessionEvent {

    /**
     * Creates a new event
     * @param source the source of the event
     * @param id the id of the HTTP session being ended
     */
    public HttpSessionStart(Object source, Object id) {
        super(source,id);
    }

 }
