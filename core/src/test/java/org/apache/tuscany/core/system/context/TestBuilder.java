package org.apache.tuscany.core.system.context;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.system.annotation.Autowire;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;

@Scope("MODULE")
public class TestBuilder implements RuntimeConfigurationBuilder<AggregateContext> {

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

    public void build(AssemblyModelObject object, AggregateContext context) throws BuilderException {
        invoked = true;
    }

    public boolean invoked() {
        return invoked;
    }
}
