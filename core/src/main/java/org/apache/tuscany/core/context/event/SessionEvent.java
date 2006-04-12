package org.apache.tuscany.core.context.event;

/**
 * @version $$Rev$$ $$Date$$
 */
public interface SessionEvent extends Event {

     public Object getSessionTypeIdentifier();

     /**
      * Returns the session id associated with the event
      */
     public Object getId();
}
