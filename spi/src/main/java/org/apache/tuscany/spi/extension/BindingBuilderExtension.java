package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BoundReference;
import org.apache.tuscany.spi.model.BoundService;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import org.apache.tuscany.spi.wire.WireService;
import org.osoa.sca.annotations.Init;

/**
 * An extension point for binding builders. When adding support for new bindings, implementations may extend
 * this class as a convenience.
 *
 * @version $$Rev$$ $$Date$$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public abstract class BindingBuilderExtension<B extends Binding> implements BindingBuilder<B> {

    protected BuilderRegistry builderRegistry;
    protected PolicyBuilderRegistry policyBuilderRegistry;
    protected WireService wireService;

    @Autowire
    public void setBuilderRegistry(BuilderRegistry registry) {
        this.builderRegistry = registry;
    }

    @Autowire
    public void setPolicyBuilderRegistry(PolicyBuilderRegistry registry) {
        this.policyBuilderRegistry = registry;
    }

    @Autowire
    public void setWireService(WireService wireService) {
        this.wireService = wireService;
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(getBindingType(), this);
    }

    public Context build(CompositeContext parent, BoundService<B> boundService, DeploymentContext deploymentContext) {
        return null;
    }

    public Context build(CompositeContext parent, BoundReference<B> boundReference, DeploymentContext deploymentContext) {
        return null;
    }

    protected abstract Class<B> getBindingType();
}
