package org.apache.tuscany.spi.extension;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
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
    protected Map<String, TargetWire> targetWires = new HashMap<String, TargetWire>();
    protected Map<String, List<SourceWire>> sourceWires = new HashMap<String,List<SourceWire>>();

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

    public void addTargetWire(TargetWire wire) {
        targetWires.put(wire.getServiceName(), wire);
        onTargetWire(wire);
    }

    public TargetWire getTargetWire(String serviceName) {
        if (serviceName == null) {
            return targetWires.values().iterator().next();
        } else {
            return targetWires.get(serviceName);
        }
    }

    public void addSourceWire(SourceWire wire) {
        List<SourceWire> list = new ArrayList<SourceWire>();
        list.add(wire);
        sourceWires.put(wire.getReferenceName(), list);
        onSourceWire(wire);
    }

    public Map<String,List<SourceWire>> getSourceWires() {
        return sourceWires;
    }

    public void addSourceWires(Class<?> multiplicityClass, List<SourceWire> wires) {
        assert(wires != null && wires.size() > 0);
        sourceWires.put(wires.get(0).getReferenceName(), wires);
        onSourceWires(multiplicityClass, wires);
    }

    public void prepare() {
        for (TargetWire<T> targetWire : targetWires.values()) {
            for (TargetInvocationChain chain : targetWire.getInvocationChains().values()) {
                chain.setTargetInvoker(createTargetInvoker(targetWire.getServiceName(), chain.getMethod()));
                chain.build();
            }
        }
    }

    protected void onSourceWire(SourceWire wire){}

    protected void onSourceWires(Class<?> multiplicityClass, List<SourceWire> wires){}

    protected void onTargetWire(TargetWire wire){}


}
