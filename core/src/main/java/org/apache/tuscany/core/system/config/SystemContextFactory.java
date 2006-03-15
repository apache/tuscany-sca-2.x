package org.apache.tuscany.core.system.config;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.common.TuscanyRuntimeException;
import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.ContextFactory;
import org.apache.tuscany.core.builder.ContextResolver;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.system.context.SystemComponentContext;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Scope;

/**
 * A <code>ContextFactory</code> that handles system component implementation types, which may be either simple,
 * leaf types or an aggregates.
 * <p>
 * For aggregate types, this factory delegates to an {@link org.apache.tuscany.core.builder.ObjectFactory} to create an
 * instance of the aggregate implementation and perform injection of configuration and references. Once an aggregate instance is
 * created, the factory will register the aggregate's children. This process may be done recursively in a lazy fashion,
 * descending down an aggregate hierarchy as a child aggregate is instantiated.
 * 
 * @version $Rev$ $Date$
 */
public class SystemContextFactory implements ContextFactory<InstanceContext>, ContextResolver {

    // the component name as configured in the hosting module
    private String name;

    // if this factory produces aggregates, the module will be the logical model associated with its children
    private Module module;

    private AggregateContext parentContext;

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

    // if the component implementation is an aggregate context
    private boolean isAggregate;

    // ----------------------------------
    // Constructors
    // ----------------------------------

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
        this.isAggregate = AggregateContext.class.isAssignableFrom(ctr.getDeclaringClass());
        this.scope = scope;
        stateless = (scope == Scope.INSTANCE);
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public String getName() {
        return name;
    }

    public Scope getScope() {
        return scope;
    }

    public InstanceContext createContext() throws ContextCreationException {
        if (isAggregate) {
            try {
                // aggregate context types are themselves an instance context
                PojoObjectFactory objectFactory = new PojoObjectFactory(ctr, null, setters);
                AggregateContext ctx = (AggregateContext) objectFactory.getInstance();
                ctx.setName(name);
                // the aggregate has been created, now register its children
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
            return new SystemComponentContext(name, objectFactory, eagerInit, init, destroy, stateless);
        }
    }

    public void addTargetProxyFactory(String serviceName, ProxyFactory factory) {
        throw new UnsupportedOperationException();
    }

    public ProxyFactory getTargetProxyFactory(String serviceName) {
        return null;
    }

    public Map<String, ProxyFactory> getTargetProxyFactories() {
        return null;
    }

    public void addSourceProxyFactory(String referenceName, ProxyFactory factory) {
        throw new UnsupportedOperationException();
    }

    public List<ProxyFactory> getSourceProxyFactories() {
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

    public void prepare(AggregateContext parent) {
        parentContext = parent;
    }

    public AggregateContext getCurrentContext() {
        return parentContext;
    }

}
