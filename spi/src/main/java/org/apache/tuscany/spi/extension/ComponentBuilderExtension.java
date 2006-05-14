package org.apache.tuscany.spi.extension;

import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.model.Implementation;
import org.apache.tuscany.model.Component;
import org.apache.tuscany.model.Service;
import org.apache.tuscany.model.ComponentType;
import org.apache.tuscany.model.Reference;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Init;

/**
 * @version $$Rev$$ $$Date$$
 */
@Scope("MODULE")
public abstract class ComponentBuilderExtension<I extends Implementation<?>> implements ComponentBuilder<I> {

    protected BuilderRegistry registry;
    protected WireService wireService;

    @Autowire
    public void setRegistry(BuilderRegistry registry) {
        this.registry = registry;
    }

    @Init(eager = true)
    public void init(){
        registry.register(this);
    }

    public Context build(CompositeContext parent, Component<I> component) throws BuilderConfigException {
        ComponentType componentType = component.getImplementation().getComponentType();
        ComponentContext context = createContext(parent, component);
        // create target wires
        for (Service service : componentType.getServices().values()) {
            TargetWire wire = wireService.createTargetWire(service);
            context.addTargetWire(wire);
        }
        // create source wires
        for (Reference reference : componentType.getReferences().values()) {
            SourceWire wire = wireService.createSourceWire(reference);
            context.addSourceWire(wire);
        }
        return context;
    }

    protected abstract ComponentContext createContext(CompositeContext parent, Component<I> component);
}
