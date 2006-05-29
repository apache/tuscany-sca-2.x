package org.apache.tuscany.spi.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.ContextNotFoundException;
import org.apache.tuscany.spi.context.DuplicateNameException;
import org.apache.tuscany.spi.context.IllegalTargetException;
import org.apache.tuscany.spi.context.InvalidContextTypeException;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.TargetNotFoundException;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.ReferenceWire;
import org.apache.tuscany.spi.wire.ServiceWire;

/**
 * An extension point for composite contexts. When adding support for new composite component types,
 * implementations may extend this class as a convenience.
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class CompositeContextExtension<T> extends AbstractContext<T> implements CompositeContext<T> {

    protected final Map<String, Context> children = new ConcurrentHashMap<String, Context>();
    protected final List<ServiceContext> services = new ArrayList<ServiceContext>();
    protected final List<ReferenceContext> references = new ArrayList<ReferenceContext>();

    protected CompositeContextExtension(String name, CompositeContext<?> parent) {
        super(name, parent);
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public void onEvent(Event event) {
        publish(event);
    }

    public void registerContext(Context child) {
        assert(child != null): "Context was null";
        if (children.get(child.getName()) != null) {
            DuplicateNameException e = new DuplicateNameException("A context is already registered with name");
            e.setIdentifier(child.getName());
            e.addContextName(getName());
            throw e;
        }
        children.put(child.getName(), child);
        if (child instanceof ServiceContext) {
            ServiceContext serviceContext = (ServiceContext) child;
            synchronized (services) {
                services.add(serviceContext);
            }
        } else if (child instanceof ReferenceContext) {
            ReferenceContext context = (ReferenceContext) child;
            synchronized (references) {
                references.add(context);
            }
        } else {
            InvalidContextTypeException e = new InvalidContextTypeException(child.getClass().getName());
            e.setIdentifier(child.getName());
            e.addContextName(getName());
            throw e;
        }
    }

    public Context getContext(String name) {
        assert (name != null) : "Name was null";
        return children.get(name);
    }

    public List<Context> getContexts() {
        return Collections.unmodifiableList(new ArrayList<Context>(children.values()));
    }

    public List<ServiceContext> getServiceContexts() {
        return Collections.unmodifiableList(services);
    }

    public ServiceContext getServiceContext(String name) {
        Context ctx = children.get(name);
        if (ctx == null) {
            ContextNotFoundException e = new ContextNotFoundException("Service context not found");
            e.setIdentifier(name);
            e.addContextName(getName());
            throw e;
        } else if (!(ctx instanceof ServiceContext)) {
            ContextNotFoundException e = new ContextNotFoundException("Context not a service context");
            e.setIdentifier(name);
            e.addContextName(getName());
            throw e;
        }
        return (ServiceContext) ctx;
    }

    public List<ReferenceContext> getReferenceContexts() {
        return Collections.unmodifiableList(references);
    }

    public T getService() throws TargetException {
        return null;  //TODO implement
    }

    public Object getService(String name) throws TargetException {
        Context context = children.get(name);
        if (context == null) {
            TargetNotFoundException e = new TargetNotFoundException(name);
            e.addContextName(getName());
            throw e;
        } else if (context instanceof ServiceContext) {
            return context.getService();
        } else {
            IllegalTargetException e = new IllegalTargetException("Target must be a service");
            e.setIdentifier(name);
            e.addContextName(getName());
            throw e;
        }
    }

    public List<Class<?>> getServiceInterfaces() {
        List<Class<?>> serviceInterfaces = new ArrayList<Class<?>>(services.size());
        synchronized (services) {
            for (ServiceContext serviceContext : services) {
                serviceInterfaces.add(serviceContext.getInterface());
            }
        }
        return serviceInterfaces;
    }

    public void addReferenceWire(ReferenceWire wire) {

    }

    public void addReferenceWires(Class<?> multiplicityClass, List<ReferenceWire> wires) {

    }

    public Map<String, List<ReferenceWire>> getReferenceWires() {
        return null;
    }

    public void addServiceWire(ServiceWire wire) {
        //TODO implement
    }

    public ServiceWire getServiceWire(String serviceName) {
        Context context = children.get(serviceName);
        if (context == null || !(context instanceof ServiceContext)) {
            throw new ContextNotFoundException(serviceName);
        }
        return ((ServiceContext) context).getWire();
    }

    public void prepare(){
        for (Context context : children.values()) {
            context.prepare();
        }
    }


}