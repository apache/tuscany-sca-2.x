package org.apache.tuscany.core.context.event;

/**
 * Represents a generic HTTP-based session event in the runtime
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class HttpSessionEvent implements SessionEvent {

    // FIXME this needs to be made private and not directly referenced in the runtime
    public static final Object HTTP_IDENTIFIER = new Object();

    private Object id;
    protected transient Object  source;

    public HttpSessionEvent(Object source, Object id) {
        assert (source !=null): "Source id was null";
        assert (id !=null): "Session id was null";
        this.source = source;
        this.id = id;
    }

    public Object getSource() {
        return source;
    }

    public Object getId(){
        return id;
    }

    public Object getSessionTypeIdentifier(){
        return HTTP_IDENTIFIER;
    }
}
