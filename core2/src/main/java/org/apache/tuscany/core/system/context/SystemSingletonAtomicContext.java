package org.apache.tuscany.core.system.context;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.InstanceWrapper;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.InboundWire;

/**
 * An {@link org.apache.tuscany.spi.context.AtomicContext} used when registering objects directly into a
 * composite
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemSingletonAtomicContext<S, T extends S> extends AbstractContext<S> implements SystemAtomicContext<S> {

    private T instance;
    private List<Class<?>> serviceInterfaces;

    public SystemSingletonAtomicContext(String name, CompositeContext<?> parent, Class<S> serviceInterface, T instance) {
        super(name, parent);
        this.instance = instance;
        serviceInterfaces = new ArrayList<Class<?>>(1);
        serviceInterfaces.add(serviceInterface);
    }

    public List<Class<?>> getServiceInterfaces() {
        return serviceInterfaces;
    }

    public Scope getScope() {
        return Scope.MODULE;
    }

    public boolean isEagerInit() {
        return false;
    }

    public T getTargetInstance() throws TargetException {
        return instance;
    }

    public void prepare() {
    }

    public Object getService(String name) throws TargetException {
        return getTargetInstance();
    }

    public S getService() throws TargetException {
        return getTargetInstance();
    }

    public void init(Object instance) throws TargetException {

    }

    public void destroy(Object instance) throws TargetException {

    }

    public InstanceWrapper createInstance() throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    public void addInboundWire(InboundWire wire) {
        throw new UnsupportedOperationException();
    }

    public InboundWire getInboundWire(String serviceName) {
        throw new UnsupportedOperationException();
    }

    public void addOutboundWire(OutboundWire wire) {
        throw new UnsupportedOperationException();
    }

    public void addOutboundWires(Class<?> multiplicityClass, List<OutboundWire> wires) {
        throw new UnsupportedOperationException();
    }

    public Map<String,List<OutboundWire>> getOutboundWires() {
        throw new UnsupportedOperationException();
    }


    public TargetInvoker createTargetInvoker(String serviceName, Method operation) {
        throw new UnsupportedOperationException();
    }
}
