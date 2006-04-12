package org.apache.tuscany.core.context.event;

/**
 * Represents the expiration of an HTTP-based session
 *
 * @version $$Rev$$ $$Date$$
 */
public class HttpSessionEndEvent extends HttpSessionEvent {

   public HttpSessionEndEvent(Object source, Object id) {
        super(source,id);
    }

 }
