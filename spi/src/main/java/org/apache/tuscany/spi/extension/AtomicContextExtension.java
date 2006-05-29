package org.apache.tuscany.spi.extension;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.wire.ServiceWire;
import org.apache.tuscany.spi.wire.ReferenceWire;
import org.apache.tuscany.spi.wire.ServiceInvocationChain;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.model.Scope;

/**
 * An extension point for atomic contexts. When adding support for new component types, implementations may
 * extend this class as a convenience.
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class AtomicContextExtension<T> extends AbstractContext<T> implements AtomicContext<T> {

    protected ScopeContext scopeContext;
    protected Scope scope;
    protected Map<String, ServiceWire> serviceWires = new HashMap<String, ServiceWire>();
    protected Map<String, List<ReferenceWire>> referenceWires = new HashMap<String,List<ReferenceWire>>();

    protected AtomicContextExtension(String name, CompositeContext<?> parent, ScopeContext scopeContext) {
        super(name, parent);
        this.scopeContext = scopeContext;
    }

    public Scope getScope() {
        return scope;
    }

    public boolean isEagerInit() {
        return false;
    }

    public void start() throws CoreRuntimeException {
        super.start();
        scopeContext.register(this);
    }

    public void init(Object instance) throws TargetException {

    }

    public void destroy(Object instance) throws TargetException {

    }

    public void addServiceWire(ServiceWire wire) {
        serviceWires.put(wire.getServiceName(), wire);
        onServiceWire(wire);
    }

    public ServiceWire getServiceWire(String serviceName) {
        if (serviceName == null) {
            return serviceWires.values().iterator().next();
        } else {
            return serviceWires.get(serviceName);
        }
    }

    public void addReferenceWire(ReferenceWire wire) {
        List<ReferenceWire> list = new ArrayList<ReferenceWire>();
        list.add(wire);
        referenceWires.put(wire.getReferenceName(), list);
        onReferenceWire(wire);
    }

    public Map<String,List<ReferenceWire>> getReferenceWires() {
        return referenceWires;
    }

    public void addReferenceWires(Class<?> multiplicityClass, List<ReferenceWire> wires) {
        assert(wires != null && wires.size() > 0);
        referenceWires.put(wires.get(0).getReferenceName(), wires);
        onReferenceWires(multiplicityClass, wires);
    }

    public void prepare() {
        for (ServiceWire<T> serviceWire : serviceWires.values()) {
            for (ServiceInvocationChain chain : serviceWire.getInvocationChains().values()) {
                chain.setTargetInvoker(createTargetInvoker(serviceWire.getServiceName(), chain.getMethod()));
                chain.build();
            }
        }
    }

    protected void onReferenceWire(ReferenceWire wire){}

    protected void onReferenceWires(Class<?> multiplicityClass, List<ReferenceWire> wires){}

    protected void onServiceWire(ServiceWire wire){}

}
