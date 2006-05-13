package org.apache.tuscany.spi.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.Scope;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.EventFilter;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public abstract class AtomicContextExtension implements AtomicContext {

    protected ScopeContext scopeContext;
    protected String name;
    protected CompositeContext parent;
    protected int lifecycleState = UNINITIALIZED;
    protected Map<String, TargetWire> targetWires = new HashMap<String, TargetWire>();
    protected List<SourceWire> sourceWires = new ArrayList<SourceWire>();

    public List getServiceInterfaces() {
        return null;
    }

    public Scope getScope() {
        if (scopeContext != null) {
            return scopeContext.getScope();
        } else {
            return null;
        }
    }

    public void setScopeContext(ScopeContext context) {
        scopeContext = context;
    }

    public Object getService() throws TargetException {
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CompositeContext getParent() {
        return parent;
    }

    public void setParent(CompositeContext parent) {
        this.parent = parent;
    }

    public void publish(Event object) {

    }

    public void addListener(RuntimeEventListener listener) {

    }

    public void addListener(EventFilter filter, RuntimeEventListener listener) {

    }

    public void removeListener(RuntimeEventListener listener) {

    }

    public int getLifecycleState() {
        return lifecycleState;
    }

    public void start() throws CoreRuntimeException {
        lifecycleState = RUNNING;
    }

    public void stop() throws CoreRuntimeException {
        lifecycleState = STOPPED;
    }

    public void addTargetWire(TargetWire wire) {
        targetWires.put(wire.getServiceName(), wire);
    }

    public TargetWire getTargetWire(String serviceName) {
        return targetWires.get(serviceName);
    }

    public Map<String, TargetWire> getTargetWires() {
        return targetWires;
    }

    public void addSourceWire(SourceWire wire) {
        sourceWires.add(wire);
    }

    public List<SourceWire> getSourceWires() {
        return sourceWires;
    }

    public void prepare() {

    }

    public void addSourceWires(Class multiplicityClass, List wires) {
        // TODO implement
    }


}
