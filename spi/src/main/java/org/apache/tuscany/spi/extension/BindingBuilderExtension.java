package org.apache.tuscany.spi.extension;

import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BindingBuilder;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.wire.WireService;

/**
 * An extension point for binding builders. When adding support for new bindings, implementations may extend this class
 * as a convenience.
 *
 * @version $$Rev$$ $$Date$$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public abstract class BindingBuilderExtension<B extends Binding> implements BindingBuilder<B> {

    protected BuilderRegistry builderRegistry;
    protected WireService wireService;

    @Autowire
    public void setBuilderRegistry(BuilderRegistry registry) {
        this.builderRegistry = registry;
    }

    @Autowire
    public void setWireService(WireService wireService) {
        this.wireService = wireService;
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(getBindingType(), this);
    }

    public SCAObject build(CompositeComponent parent,
                           BoundServiceDefinition<B> boundServiceDefinition,
                           DeploymentContext deploymentContext) {
        return null;
    }

    public SCAObject build(CompositeComponent parent,
                           BoundReferenceDefinition<B> boundReferenceDefinition,
                           DeploymentContext deploymentContext) {
        return null;
    }

    protected abstract Class<B> getBindingType();
}
