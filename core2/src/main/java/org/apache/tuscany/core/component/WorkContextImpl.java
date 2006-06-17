package org.apache.tuscany.core.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.WorkContext;

/**
 * An implementation of an {@link org.apache.tuscany.spi.component.WorkContext} that handles event-to-thread
 * associations using an <code>InheritableThreadLocal</code>
 *
 * @version $Rev$ $Date$
 */
public class WorkContextImpl implements WorkContext {

    private static final Object REMOTE_CONTEXT = new Object();

    // TODO implement propagation strategy for creating new threads

    //A map ( associated with the current thread) of scope identifiers keyed on the event context id type.
    //The scope identifier may be a {@link ScopeIdentifier} or an opaque id
    private ThreadLocal<Map<Object, Object>> workContext = new InheritableThreadLocal<Map<Object, Object>>();

    public WorkContextImpl() {
        super();
    }

    public CompositeComponent getRemoteComponent() {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            return null;
        }
        return (CompositeComponent) map.get(REMOTE_CONTEXT);
    }


    public void setRemoteComponent(CompositeComponent component) {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            map = new HashMap<Object, Object>();
            workContext.set(map);
        }
        map.put(REMOTE_CONTEXT, component);
    }

    public Object getIdentifier(Object type) {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            return null;
        }
        Object currentId = map.get(type);
        if (currentId instanceof ScopeIdentifier) {
            currentId = ((ScopeIdentifier) currentId).getIdentifier();
            // once we have accessed the id, replace the lazy wrapper
            map.put(type, currentId);
        }
        return currentId;
    }

    public void setIdentifier(Object type, Object identifier) {
        Map<Object, Object> map = workContext.get();
        if (map == null) {
            map = new HashMap<Object, Object>();
            workContext.set(map);
        }
        map.put(type, identifier);
    }

    public void clearIdentifier(Object type) {
        if (type == null) {
            return;
        }
        Map map = workContext.get();
        if (map != null) {
            map.remove(type);
        }
    }

    public void clearIdentifiers() {
        workContext.remove();
    }

}
