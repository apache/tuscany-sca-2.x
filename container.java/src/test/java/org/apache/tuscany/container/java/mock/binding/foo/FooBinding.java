package org.apache.tuscany.container.java.mock.binding.foo;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.ModelInitException;

public class FooBinding implements Binding {

    public FooBinding() {
    }

    public String getURI() {
        return null;
    }

    public void setURI(String value) {
    }

    public void initialize(AssemblyModelContext modelContext) throws ModelInitException {
    }

    public void freeze() {
    }

    public boolean accept(AssemblyModelVisitor visitor) {
        return false;
    }

    private Object config;

    public void setRuntimeConfiguration(Object configuration) {
        config = configuration;
    }

    public Object getRuntimeConfiguration() {
        System.out.println("retting");
        return config;
    }

}
