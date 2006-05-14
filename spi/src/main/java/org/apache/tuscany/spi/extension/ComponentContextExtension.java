package org.apache.tuscany.spi.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.context.AbstractContext;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;

/**
 * @version $$Rev$$ $$Date$$
 */
public abstract class ComponentContextExtension<T> extends AbstractContext<T> implements ComponentContext<T> {

    protected Map<String, TargetWire> targetWires = new HashMap<String, TargetWire>();
    protected List<SourceWire> sourceWires = new ArrayList<SourceWire>();

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

    public void addSourceWires(Class multiplicityClass, List wires) {
        // TODO implement
    }


    public void prepare() {

    }
    

}
