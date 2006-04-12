package org.apache.tuscany.core.context.event;

/**
 * Represents the end of a request in the runtime
 *
 * @version $$Rev$$ $$Date$$
 */
public class RequestEndEvent extends AbstractRequestEvent{

     public RequestEndEvent(Object source, Object id) {
         super(source,id);
     }


}
