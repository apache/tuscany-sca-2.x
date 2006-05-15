package org.apache.tuscany.spi.extension;

import org.apache.tuscany.model.Component;
import org.apache.tuscany.model.ComponentType;
import org.apache.tuscany.model.Implementation;
import org.apache.tuscany.model.Reference;
import org.apache.tuscany.model.Scope;
import org.apache.tuscany.model.Service;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.ComponentBuilder;
import org.apache.tuscany.spi.context.AtomicContext;
import org.apache.tuscany.spi.context.ComponentContext;
import org.apache.tuscany.spi.context.CompositeContext;
import org.apache.tuscany.spi.context.Context;
import org.apache.tuscany.spi.context.ScopeRegistry;
import org.apache.tuscany.spi.context.ScopeContext;
import org.apache.tuscany.spi.wire.SourceWire;
import org.apache.tuscany.spi.wire.TargetWire;
import org.apache.tuscany.spi.wire.WireService;
import org.osoa.sca.annotations.Init;

/**
 * @version $$Rev$$ $$Date$$
 */
@org.osoa.sca.annotations.Scope("MODULE")
public abstract class ComponentBuilderExtension<I extends Implementation<?>> implements ComponentBuilder<I> {

    protected BuilderRegistry builderRegistry;
    protected WireService wireService;
    protected ScopeRegistry scopeRegistry;

    @Autowire
    public void setBuilderRegistry(BuilderRegistry registry) {
        this.builderRegistry = registry;
    }

    @Autowire
    public void setWireService(WireService wireService) {
        this.wireService = wireService;
    }

    @Autowire
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    @Init(eager = true)
    public void init() {
        builderRegistry.register(this);
    }

    @SuppressWarnings("unchecked")
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
        //TODO this could be moved up to the runtime
        if (context instanceof AtomicContext) {
            AtomicContext ctx = (AtomicContext) context;
            Scope scope = ctx.getScope();
            if (scope == null) {
                scope = Scope.STATELESS;
            }
            ScopeContext scopeContext = scopeRegistry.getScopeContext(scope);
            if (scopeContext == null){
                throw new BuilderConfigException("Scope context not registered for scope "+ scope);
            }
            ctx.setScopeContext(scopeContext);
            scopeContext.register(ctx);

        }
        return context;
    }

    protected abstract ComponentContext createContext(CompositeContext parent, Component<I> component);
}
