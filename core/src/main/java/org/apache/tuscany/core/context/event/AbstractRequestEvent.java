package org.apache.tuscany.core.context.event;

/**
 * Represents a general request event in the runtime
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class AbstractRequestEvent extends AbstractEvent implements RequestEvent {

    private Object id;

     public AbstractRequestEvent(Object source, Object id) {
         super(source);
         assert (id !=null): "Request id was null";
         this.id = id;
     }

    /**
     * Returns the session id associated with the request
     */
    public Object getId(){
        return id;
    }

}
