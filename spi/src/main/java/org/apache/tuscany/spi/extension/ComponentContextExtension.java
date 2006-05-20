package org.apache.tuscany.spi.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetInvocationChain;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public abstract class ComponentContextExtension<T> extends AbstractContext<T> implements ComponentContext<T> {

    protected Map<String, TargetWire> targetWires = new HashMap<String, TargetWire>();
    protected List<SourceWire> sourceWires = new ArrayList<SourceWire>();

    protected ComponentContextExtension() {
    }

    public void addTargetWire(TargetWire wire) {
        targetWires.put(wire.getServiceName(), wire);
        onTargetWire(wire);
    }

    public TargetWire getTargetWire(String serviceName) {
        return targetWires.get(serviceName);
    }

    public Map<String, TargetWire> getTargetWires() {
        return targetWires;
    }

    public void addSourceWire(SourceWire wire) {
        sourceWires.add(wire);
        onSourceWire(wire);
    }

    public List<SourceWire> getSourceWires() {
        return sourceWires;
    }

    public void addSourceWires(Class<?> multiplicityClass, List<SourceWire> wires) {
        sourceWires.addAll(wires);
        onSourceWires(multiplicityClass, wires);
    }

    public void prepare() {
        for (TargetWire<T> targetWire : targetWires.values()) {
            for (TargetInvocationChain chain : targetWire.getInvocationChains().values()) {
                chain.setTargetInvoker(createTargetInvoker(targetWire.getServiceName(), chain.getMethod()));
            }
        }
    }

    public void onSourceWire(SourceWire wire){}

    public void onSourceWires(Class<?> multiplicityClass, List<SourceWire> wires){}

    public void onTargetWire(TargetWire wire){}

}
