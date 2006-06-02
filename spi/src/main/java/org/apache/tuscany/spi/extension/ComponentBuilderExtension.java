package org.apache.tuscany.spi.extension;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.context.ScopeRegistry;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.policy.PolicyBuilderRegistry;
import org.apache.tuscany.spi.wire.WireService;

/**
 * An extension point for component builders. When adding support for new component types, implementations may
 * extend this class as a convenience.
 *
 * @version $$Rev$$ $$Date$$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public abstract class ComponentBuilderExtension<I extends Implementation<?>> implements ComponentBuilder<I> {
    /**
     * The builder registry that this builder should register with; usually set by injection.
     */
    protected BuilderRegistry builderRegistry;

    /**
     * The scope registry that this builder should use; usually set by injection.
     */
    protected ScopeRegistry scopeRegistry;

    /**
     * The policy builder that this builder should use; usually set by injection.
     */
    protected PolicyBuilderRegistry policyBuilderRegistry;
    protected WireService wireService;

    @Autowire
    public void setBuilderRegistry(BuilderRegistry registry) {
        this.builderRegistry = registry;
    }

    @Autowire
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
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
        builderRegistry.register(getImplementationType(), this);
    }

    @Destroy
    public void destroy() {
        builderRegistry.unregister(getImplementationType());
    }

    /**
     * Returns the Class of the implementation that this builder can handle.
     * @return the type of implementation that this builder can handle
     */
    protected abstract Class<I> getImplementationType();
}
