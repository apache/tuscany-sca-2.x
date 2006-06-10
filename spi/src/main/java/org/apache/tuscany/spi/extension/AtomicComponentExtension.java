package org.apache.tuscany.spi.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.AbstractSCAObject;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

/**
 * An extension point for atomic component type, which new implementation types may extend
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class AtomicComponentExtension<T> extends AbstractSCAObject<T> implements AtomicComponent<T> {

    protected ScopeContainer scopeContainer;
    protected Scope scope;
    protected Map<String, InboundWire> serviceWires = new HashMap<String, InboundWire>();
    protected Map<String, List<OutboundWire>> referenceWires = new HashMap<String, List<OutboundWire>>();
    protected WireService wireService;

    protected AtomicComponentExtension(String name,
                                       CompositeComponent<?> parent,
                                       ScopeContainer scopeContainer,
                                       WireService wireService) {
        super(name, parent);
        this.scopeContainer = scopeContainer;
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
        scopeContainer.register(this);
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
            if (serviceWires.size() < 1) {
                return null;
            }
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

    public Map<String, List<OutboundWire>> getOutboundWires() {
        return referenceWires;
    }

    public void addOutboundWires(Class<?> multiplicityClass, List<OutboundWire> wires) {
        assert wires != null && wires.size() > 0;
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

    protected void onReferenceWire(OutboundWire wire) {
    }

    protected void onReferenceWires(Class<?> multiplicityClass, List<OutboundWire> wires) {
    }

    protected void onServiceWire(InboundWire wire) {
    }

}
