package org.apache.tuscany.core.context;

import java.util.Map;
import java.util.HashMap;

import org.apache.tuscany.spi.context.WorkContext;
import org.apache.tuscany.spi.context.CompositeContext;

/**
 * An implementation of an {@link org.apache.tuscany.spi.context.WorkContext} that handles event-to-thread associations using an
 * <code>InheritableThreadLocal</code>
 *
 * @version $Rev: 393567 $ $Date: 2006-04-12 11:28:58 -0700 (Wed, 12 Apr 2006) $
 */
public class WorkContextImpl implements WorkContext {

    private static final Object CURRENT_MODULE = new Object();

    // @TODO design a proper propagation strategy for creating new threads
    /*
     * a map ( associated with the current thread) of scope identifiers keyed on the event context id type. the scope identifier
     * may be a {@link ScopeIdentifier} or an opaque id
     */
    private ThreadLocal<Map<Object,Object>> eventContext = new InheritableThreadLocal<Map<Object,Object>>();

    public CompositeContext getCurrentModule() {
        return (CompositeContext)eventContext.get().get(CURRENT_MODULE);
    }


    public void setCurrentModule(CompositeContext context) {
        eventContext.get().put(CURRENT_MODULE,context);
    }

    public Object getIdentifier(Object type) {
        Map<Object,Object> map = eventContext.get();
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
        Map<Object,Object> map = eventContext.get();
        if (map == null) {
            map = new HashMap<Object,Object>();
            eventContext.set(map);
        }
        map.put(type, identifier);
    }

    public void clearIdentifier(Object type) {
        if (type == null) {
            return;
        }
        Map map = eventContext.get();
        if (map != null) {
            map.remove(type);
        }
    }

    public void clearIdentifiers() {
        eventContext.remove();
    }

    public WorkContextImpl() {
        super();
    }

}
