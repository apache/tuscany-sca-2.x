package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import org.osoa.sca.annotations.Init;

/**
 * @version $$Rev$$ $$Date$$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public abstract class ComponentBuilderExtension<I extends Implementation<?>> implements ComponentBuilder<I> {

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
        builderRegistry.register(getImplementationType(),this);
    }

    protected abstract Class<I> getImplementationType();
}
