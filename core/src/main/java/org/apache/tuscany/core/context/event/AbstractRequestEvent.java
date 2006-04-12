package org.apache.tuscany.core.context.event;

/**
 * Base implementation of a request event
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class AbstractRequestEvent extends AbstractEvent implements RequestEvent {

    private Object id;

    /**
     * Creates a new event
     * @param source the source of the event
     * @param id the id of the request associated with the event
     */
     public AbstractRequestEvent(Object source, Object id) {
         super(source);
         assert (id !=null): "Request id was null";
         this.id = id;
     }

    public Object getId(){
        return id;
    }

}
