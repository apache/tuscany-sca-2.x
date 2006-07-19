package org.apache.tuscany.spi.extension;

import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.spi.services.work.WorkScheduler;

/**
 * An extension point for component builders. When adding support for new component types, implementations may extend
 * this class as a convenience.
 *
 * @version $$Rev$$ $$Date$$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public abstract class ComponentBuilderExtension<I extends Implementation<?>> implements ComponentBuilder<I> {

    protected BuilderRegistry builderRegistry;
    protected ScopeRegistry scopeRegistry;
    protected WireService wireService;
    protected WorkScheduler workScheduler;

    @Autowire
    public void setBuilderRegistry(BuilderRegistry registry) {
        this.builderRegistry = registry;
    }

    @Autowire
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    @Autowire
    public void setWireService(WireService wireService) {
        this.wireService = wireService;
    }

    @Autowire
    public void setWorkScheduler(WorkScheduler workScheduler) {
        this.workScheduler = workScheduler;
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(getImplementationType(), this);
    }

    protected abstract Class<I> getImplementationType();
}
