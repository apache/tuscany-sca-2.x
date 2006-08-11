package org.apache.tuscany.spi.extension;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Document;

import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.ComponentNotFoundException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.DuplicateNameException;
import org.apache.tuscany.spi.component.IllegalTargetException;
import org.apache.tuscany.spi.component.InvalidComponentTypeException;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.TargetNotFoundException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * An extension point for composite components, which new types may extend
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class CompositeComponentExtension<T> extends AbstractSCAObject<T> implements CompositeComponent<T> {

    protected final Map<String, SCAObject> children = new ConcurrentHashMap<String, SCAObject>();
    protected final List<Service> services = new ArrayList<Service>();
    protected final List<Reference> references = new ArrayList<Reference>();
    protected final Map<String, Document> propertyValues;

    protected CompositeComponentExtension(String name, CompositeComponent<?> parent,
                                          Map<String, Document> propertyValues) {
        super(name, parent);
        this.propertyValues = propertyValues;
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public void onEvent(Event event) {
        publish(event);
    }

    public void register(SCAObject child) {
        assert child != null : "SCAObject was null";
        if (children.get(child.getName()) != null) {
            DuplicateNameException e = new DuplicateNameException("A context is already registered with name");
            e.setIdentifier(child.getName());
            e.addContextName(getName());
            throw e;
        }
        children.put(child.getName(), child);
        if (child instanceof Service) {
            Service service = (Service) child;
            synchronized (services) {
                services.add(service);
            }
        } else if (child instanceof Reference) {
            Reference context = (Reference) child;
            synchronized (references) {
                references.add(context);
            }
        } else {
            InvalidComponentTypeException e = new InvalidComponentTypeException(child.getClass().getName());
            e.setIdentifier(child.getName());
            e.addContextName(getName());
            throw e;
        }
    }

    public Document getPropertyValue(String name) {
        return propertyValues.get(name);
    }

    public SCAObject getChild(String name) {
        assert name != null : "Name was null";
        return children.get(name);
    }

    public List<SCAObject> getChildren() {
        return Collections.unmodifiableList(new ArrayList<SCAObject>(children.values()));
    }

    public List<Service> getServices() {
        return Collections.unmodifiableList(services);
    }

    public Service getService(String name) {
        SCAObject ctx = children.get(name);
        if (ctx == null) {
            ComponentNotFoundException e = new ComponentNotFoundException("Service not found");
            e.setIdentifier(name);
            e.addContextName(getName());
            throw e;
        } else if (!(ctx instanceof Service)) {
            ComponentNotFoundException e = new ComponentNotFoundException("SCAObject not a service");
            e.setIdentifier(name);
            e.addContextName(getName());
            throw e;
        }
        return (Service) ctx;
    }

    public List<Reference> getReferences() {
        return Collections.unmodifiableList(references);
    }

    public T getServiceInstance() throws TargetException {
        //TODO implement
        return null;
    }

    public Object getServiceInstance(String name) throws TargetException {
        SCAObject context = children.get(name);
        if (context == null) {
            TargetNotFoundException e = new TargetNotFoundException(name);
            e.addContextName(getName());
            throw e;
        } else if (context instanceof Service) {
            return context.getServiceInstance();
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
            for (Service service : services) {
                serviceInterfaces.add(service.getInterface());
            }
        }
        return serviceInterfaces;
    }

    public void addOutboundWire(OutboundWire wire) {

    }

    public void addOutboundWires(Class<?> multiplicityClass, List<OutboundWire> wires) {

    }

    public Map<String, List<OutboundWire>> getOutboundWires() {
        return null;
    }

    public void addInboundWire(InboundWire wire) {
        //TODO implement
    }

    public InboundWire getInboundWire(String serviceName) {
        SCAObject object = children.get(serviceName);
        if (!(object instanceof Service)) {
            throw new ComponentNotFoundException(serviceName);
        }
        return ((Service) object).getInboundWire();
    }

    public Map<String, InboundWire> getInboundWires() {
        synchronized (services) {
            Map<String, InboundWire> map = new HashMap<String, InboundWire>();
            for (Service service : services) {
                map.put(service.getName(), service.getInboundWire());
            }
            return map;
        }
    }


    public void prepare() {
        for (SCAObject object : children.values()) {
            object.prepare();
        }
    }

    public TargetInvoker createAsyncTargetInvoker(String serviceName, Method operation, OutboundWire wire) {
        throw new UnsupportedOperationException();
    }

}
