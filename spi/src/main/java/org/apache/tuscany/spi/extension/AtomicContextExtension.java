package org.apache.tuscany.spi.extension;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.WireService;
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
    protected Map<String, InboundWire> serviceWires = new HashMap<String, InboundWire>();
    protected Map<String, List<OutboundWire>> referenceWires = new HashMap<String,List<OutboundWire>>();
    protected WireService wireService;

    protected AtomicContextExtension(String name, CompositeContext<?> parent, ScopeContext scopeContext, WireService wireService) {
        super(name, parent);
        this.scopeContext = scopeContext;
        this.wireService = wireService;
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

    public void addInboundWire(InboundWire wire) {
        serviceWires.put(wire.getServiceName(), wire);
        onServiceWire(wire);
    }

    public InboundWire getInboundWire(String serviceName) {
        if (serviceName == null) {
            return serviceWires.values().iterator().next();
        } else {
            return serviceWires.get(serviceName);
        }
    }

    public void addOutboundWire(OutboundWire wire) {
        List<OutboundWire> list = new ArrayList<OutboundWire>();
        list.add(wire);
        referenceWires.put(wire.getReferenceName(), list);
        onReferenceWire(wire);
    }

    public Map<String,List<OutboundWire>> getOutboundWires() {
        return referenceWires;
    }

    public void addOutboundWires(Class<?> multiplicityClass, List<OutboundWire> wires) {
        assert(wires != null && wires.size() > 0);
        referenceWires.put(wires.get(0).getReferenceName(), wires);
        onReferenceWires(multiplicityClass, wires);
    }

    public void prepare() {
        for (InboundWire<T> inboundWire : serviceWires.values()) {
            for (InboundInvocationChain chain : inboundWire.getInvocationChains().values()) {
                chain.setTargetInvoker(createTargetInvoker(inboundWire.getServiceName(), chain.getMethod()));
                chain.build();
            }
        }
    }

    protected void onReferenceWire(OutboundWire wire){}

    protected void onReferenceWires(Class<?> multiplicityClass, List<OutboundWire> wires){}

    protected void onServiceWire(InboundWire wire){}

}
