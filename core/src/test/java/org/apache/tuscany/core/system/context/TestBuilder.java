package org.apache.tuscany.core.system.context;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

@Scope("MODULE")
public class TestBuilder implements ContextFactoryBuilder {

    @Autowire
    private RuntimeContext runtime;

    private boolean invoked = false;

    public TestBuilder() {
        super();
    }

    @Init(eager = true)
    public void init() {
        runtime.addBuilder(this);
    }

    public void build(AssemblyModelObject object) throws BuilderException {
        invoked = true;
    }

    public boolean invoked() {
        return invoked;
    }
}
