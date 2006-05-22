package org.apache.tuscany.spi.extension;

import org.apache.tuscany.model.Binding;
import org.apache.tuscany.model.BoundReference;
import org.apache.tuscany.model.BoundService;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import org.osoa.sca.annotations.Init;

/**
 * @version $$Rev$$ $$Date$$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public abstract class BindingBuilderExtension<B extends Binding> implements BindingBuilder<B> {

    protected BuilderRegistry builderRegistry;
    protected PolicyBuilderRegistry policyBuilderRegistry;

    @Autowire
    public void setBuilderRegistry(BuilderRegistry registry) {
        this.builderRegistry = registry;
    }

    @Autowire
    public void setPolicyBuilderRegistry(PolicyBuilderRegistry registry) {
        this.policyBuilderRegistry = registry;
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(getBindingType(), this);
    }

    public Context build(CompositeContext parent, BoundService<B> boundService) {
        return null;
    }

    public Context build(CompositeContext parent, BoundReference<B> boundReference) {
        return null;
    }

    protected abstract Class<B> getBindingType();
}
