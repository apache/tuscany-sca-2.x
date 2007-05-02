package org.apache.tuscany.core.runtime;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.assembly.impl.ComponentReferenceImpl;
import org.apache.tuscany.core.RuntimeComponentReference;
import org.apache.tuscany.core.RuntimeWire;

public class RuntimeComponentReferenceImpl extends ComponentReferenceImpl implements RuntimeComponentReference {
    private List<RuntimeWire> wires = new ArrayList<RuntimeWire>();
    public void addRuntimeWire(RuntimeWire wire) {
        wires.add(wire);
    }

    public List<RuntimeWire> getRuntimeWires() {
        return wires;
    }


}
