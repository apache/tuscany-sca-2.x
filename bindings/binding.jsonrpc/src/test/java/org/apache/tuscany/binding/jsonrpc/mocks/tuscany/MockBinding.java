package org.apache.tuscany.binding.jsonrpc.mocks.tuscany;

import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyInitializationException;
import org.apache.tuscany.model.assembly.AssemblyVisitor;
import org.apache.tuscany.model.assembly.Binding;

public class MockBinding implements Binding {

    public String getURI() {

        return null;
    }

    public void setURI(String value) {

    }

    public void initialize(AssemblyContext modelContext) throws AssemblyInitializationException {

    }

    public void freeze() {

    }

    public boolean accept(AssemblyVisitor visitor) {

        return false;
    }

}
