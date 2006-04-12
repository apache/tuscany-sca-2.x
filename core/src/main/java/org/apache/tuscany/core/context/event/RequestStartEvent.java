package org.apache.tuscany.core.context.event;

/**
 * Represents the start of a request in the runtime
 *
 * @version $$Rev$$ $$Date$$
 */
public class RequestStartEvent extends AbstractRequestEvent {

     public RequestStartEvent(Object source, Object id) {
         super(source,id);
     }

}
