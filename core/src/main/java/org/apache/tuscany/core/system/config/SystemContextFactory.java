package org.apache.tuscany.core.system.config;

import org.apache.tuscany.common.TuscanyRuntimeException;
import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.spi.wire.SourceWireFactory;
import org.apache.tuscany.spi.wire.TargetWireFactory;
import org.apache.tuscany.core.system.context.SystemAtomicContext;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Scope;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * A <code>ContextFactory</code> that handles system component implementation types, which may be either simple, leaf
 * types or an composites.
 * <p>
 * For composite types, this factory delegates to an {@link org.apache.tuscany.spi.ObjectFactory} to create an
 * instance of the composite implementation and perform injection of configuration and references. Once an composite
 * instance is created, the factory will register the composite's children. This process may be done recursively in a
 * lazy fashion, descending down an composite hierarchy as a child composite is instantiated.
 *
 * @version $Rev$ $Date$
 */
public class SystemContextFactory implements ContextFactory<Context>, ContextResolver {

    // the component name as configured in the hosting module
    private String name;

    // if this factory produces composites, the module will be the logical model associated with its children
    private Module module;

    private CompositeContext parentContext;

    // the implementation type constructor
    private Constructor ctr;

    // injectors for properties, references and other metadata values such as @Context
    private List<Injector> setters;

    // an invoker for a method decorated with @Init
    private EventInvoker init;

    // whether the component should be eagerly initialized when its scope starts
    private boolean eagerInit;

    // an invoker for a method decorated with @Destroy
    private EventInvoker destroy;

    // the scope of the implementation instance
    private Scope scope;

    // if the component implementation scope is stateless
    private boolean stateless;

    // if the component implementation is an composite context
    private boolean isComposite;

    /**
     * Creates the runtime configuration
     * 
     * @param name the SCDL name of the component the context refers to
     * @param ctr the implementation type constructor
     * @param scope the scope of the component implementation type
     */
    public SystemContextFactory(String name, Constructor ctr, Scope scope) {
        this(name, null, ctr, scope);
    }

    /**
     * Creates the runtime configuration
     * 
     * @param name the SCDL name of the component the context refers to
     * @param module if this factory produces aggregagtes, the logical model associated with its children; otherwise
     *        null
     * @param ctr the implementation type constructor
     * @param scope the scope of the component implementation type
     */
    public SystemContextFactory(String name, Module module, Constructor ctr, Scope scope) {
        assert (name != null) : "Name was null";
        assert (ctr != null) : "Constructor was null";
        this.name = name;
        this.module = module;
        this.ctr = ctr;
        this.isComposite = CompositeContext.class.isAssignableFrom(ctr.getDeclaringClass());
        this.scope = scope;
        if (isComposite) {
            scope = Scope.AGGREGATE;
        } else {
            stateless = (scope == Scope.INSTANCE);
        }
    }

    public String getName() {
        return name;
    }

    public void addProperty(String propertyName, Object value) {

    }

    public Scope getScope() {
        return scope;
    }

    public Context createContext() throws ContextCreationException {
        if (isComposite) {
            try {
                // composite context types are themselves an instance context
                PojoObjectFactory<CompositeContext> objectFactory = new PojoObjectFactory<CompositeContext>(ctr, null, setters);
                CompositeContext ctx = objectFactory.getInstance();
                ctx.setName(name);
                // the composite has been created, now register its children
                if (module != null) {
                    try {
                        ctx.registerModelObject(module);
                    } catch (ConfigurationException e) {
                        ContextCreationException cce = new ContextCreationException("Error creating context", e);
                        cce.setIdentifier(getName());
                        throw cce;
                    }

                }
                return ctx;
            } catch (TuscanyRuntimeException e) {
                e.addContextName(name);
                throw e;
            }
        } else {
            PojoObjectFactory objectFactory = new PojoObjectFactory(ctr, null, setters);
            return new SystemAtomicContext(name, objectFactory, eagerInit, init, destroy, stateless);
        }
    }

    public void addTargetWireFactory(String serviceName, TargetWireFactory factory) {
        throw new UnsupportedOperationException();
    }

    public TargetWireFactory getTargetWireFactory(String serviceName) {
        return null;
    }

    public Map<String, TargetWireFactory> getTargetWireFactories() {
        return null;
    }

    public void addSourceWireFactory(String referenceName, SourceWireFactory factory) {
        throw new UnsupportedOperationException();
    }

    public void addSourceWireFactories(String referenceName, Class referenceInterface, List<SourceWireFactory> factory, boolean multiplicity) {

    }

    public List<SourceWireFactory> getSourceWireFactories() {
        return null;
    }

    public void setSetters(List<Injector> setters) {
        this.setters = setters;
    }

    public void setEagerInit(boolean val) {
        eagerInit = val;
    }

    public void setInitInvoker(EventInvoker invoker) {
        init = invoker;
    }

    public void setDestroyInvoker(EventInvoker invoker) {
        destroy = invoker;
    }

    public void prepare(CompositeContext parent) {
        parentContext = parent;
    }

    public CompositeContext getCurrentContext() {
        return parentContext;
    }

}
