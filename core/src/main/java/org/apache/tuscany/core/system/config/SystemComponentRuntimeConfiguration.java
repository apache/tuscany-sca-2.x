package org.apache.tuscany.core.system.config;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.builder.ContextCreationException;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.system.context.SystemComponentContext;
import org.apache.tuscany.model.assembly.Scope;

/**
 * A RuntimeConfiguration that handles system component implementation types
 * 
 * @version $Rev$ $Date$
 */
public class SystemComponentRuntimeConfiguration implements RuntimeConfiguration<InstanceContext> {

    // the component name as configured in the hosting module
    private String name;

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
     * @param setters a collection of <code>Injectors</code> used to configure properties, references and other meta
     *        data values on implementation instances
     * @param eagerInit whether the component should be eagerly initialized
     * @param init an <code>Invoker</code> pointing to a method on the implementation type decorated with
     *        <code>@Init</code>
     * @param destroy an <code>Invoker</code> pointing to a method on the implementation type decorated with
     *        <code>@Destroy</code>
     * @param scope the scope of the component implementation type
     */
    public SystemComponentRuntimeConfiguration(String name, Constructor ctr, List<Injector> setters, boolean eagerInit,
            EventInvoker init, EventInvoker destroy, Scope scope) {
        assert (name != null) : "Name was null";
        assert (ctr != null) : "Constructor was null";
        assert (setters != null) : "Setters were null";
        this.name = name;
        this.ctr = ctr;
        this.isAggregate = AggregateContext.class.isAssignableFrom(ctr.getDeclaringClass());
        this.setters = setters;
        this.eagerInit = eagerInit;
        this.init = init;
        this.destroy = destroy;
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

    public InstanceContext createInstanceContext() throws ContextCreationException {
        if (isAggregate) {
            // aggregate context types are themselves an instance context
            PojoObjectFactory objectFactory = new PojoObjectFactory(ctr, null, setters);
            AggregateContext ctx = (AggregateContext) objectFactory.getInstance();
            ctx.setName(name);
            return ctx;
        } else {
            PojoObjectFactory objectFactory = new PojoObjectFactory(ctr, null, setters);
            return new SystemComponentContext(name, objectFactory, eagerInit, init, destroy, stateless);
        }
    }
    
    // -- Proxy

    public void prepare() {
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

    public ProxyFactory getSourceProxyFactory(String referenceName) {
        return null;
    }

    public Map<String, ProxyFactory> getSourceProxyFactories() {
        return null;
    }

}
