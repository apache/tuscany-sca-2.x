package org.apache.tuscany.core.component.event;

/**
 * Propagated when an HTTP-based session has started
 *
 * @version $$Rev: 411441 $$ $$Date: 2006-06-03 07:52:56 -0700 (Sat, 03 Jun 2006) $$
 */
public class HttpSessionStart extends HttpSessionEvent {

    /**
     * Creates a new event
     *
     * @param source the source of the event
     * @param id     the id of the HTTP session being ended
     */
    public HttpSessionStart(Object source, Object id) {
        super(source, id);
    }

}
