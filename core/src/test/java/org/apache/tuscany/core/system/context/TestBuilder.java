package org.apache.tuscany.core.system.context;

import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyObject;

@Scope("MODULE")
public class TestBuilder implements ContextFactoryBuilder {
    private ContextFactoryBuilderRegistry builderRegistry;

    private boolean invoked = false;

    public TestBuilder() {
        super();
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(this);
    }

    @Autowire
    public void setBuilderRegistry(ContextFactoryBuilderRegistry builderRegistry) {
        this.builderRegistry = builderRegistry;
    }

    public void build(AssemblyObject object) throws BuilderException {
        invoked = true;
    }

    public boolean invoked() {
        return invoked;
    }
}
