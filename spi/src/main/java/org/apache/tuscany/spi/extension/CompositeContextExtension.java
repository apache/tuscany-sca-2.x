package org.apache.tuscany.spi.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.ContextNotFoundException;
import org.apache.tuscany.spi.context.IllegalTargetException;
import org.apache.tuscany.spi.context.InvalidContextTypeException;
import org.apache.tuscany.spi.context.ReferenceContext;
import org.apache.tuscany.spi.context.ServiceContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.TargetNotFoundException;
import org.apache.tuscany.spi.event.Event;

/**
 * @version $$Rev$$ $$Date$$
 */
public abstract class CompositeContextExtension<T> extends ComponentContextExtension<T> implements CompositeContext<T> {

    protected final Map<String, Context> children = new ConcurrentHashMap<String, Context>();
    protected final List<ServiceContext> services = new ArrayList<ServiceContext>();
    protected final List<ReferenceContext> references = new ArrayList<ReferenceContext>();

    public void registerContext(Context context) {
        assert(context != null): "Context was null";
        if (context instanceof ServiceContext) {
        } else if (context instanceof ReferenceContext) {
        } else {
            InvalidContextTypeException e = new InvalidContextTypeException(context.getClass().getName());
            e.setIdentifier(context.getName());
            e.addContextName(getName());
            throw e;
        }
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public Context getContext(String name) {
        assert (name != null) : "Name was null";
        return children.get(name);
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

    public void onEvent(Event event) {

    }


}