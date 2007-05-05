package org.apache.tuscany.core.runtime;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.assembly.impl.ComponentServiceImpl;
import org.apache.tuscany.core.RuntimeComponentService;
import org.apache.tuscany.core.RuntimeWire;

public class RuntimeComponentServiceImpl extends ComponentServiceImpl implements RuntimeComponentService {
    private List<RuntimeWire> wires = new ArrayList<RuntimeWire>();
    private List<RuntimeWire> callbackWires = new ArrayList<RuntimeWire>();

    public void addRuntimeWire(RuntimeWire wire) {
        wires.add(wire);
    }

    public List<RuntimeWire> getRuntimeWires() {
        return wires;
    }
    
    public RuntimeWire getRuntimeWire(Binding binding) {
        for (RuntimeWire wire : wires) {
            if (wire.getTarget().getBinding() == binding) {
                return wire;
            }
        }
        return null;
    }    

    public List<RuntimeWire> getCallbackWires() {
        return callbackWires;
    }

    public void addCallbackWire(RuntimeWire callbackWire) {
        this.callbackWires.add(callbackWire);
    }
}
