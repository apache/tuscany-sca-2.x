package org.apache.tuscany.core.builder;

import java.util.Map;

import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Implementations create instances of {@link org.apache.tuscany.core.context.Context} based on a compiled
 * configuration, such as a logical assembly model. For example, implementations of
 * {@link org.apache.tuscany.core.builder.RuntimeConfigurationBuilder} analyze an
 * {@link org.apache.tuscany.model.assembly.AssemblyModelObject} to create implementations of
 * <tt>RuntimeConfiguration</tt>.
 * 
 * @version $Rev$ $Date$
 */
public interface RuntimeConfiguration<T extends Context> {

    /**
     * Creates an instance context based on the current runtime configuration
     * 
     * @return a new instance context
     * @throws ContextCreationException if an error occurs creating the context
     */
    public T createInstanceContext() throws ContextCreationException;

    /**
     * Returns the scope identifier associated with the type of contexts produced by the current configuration
     */
    public Scope getScope();

    /**
     * Returns the name of the contexts produced by the current configuration
     */
    public String getName();

    public void prepare();

    /**
     * Adds a target-side proxy factory for the given service name to the configuration. Target-side proxy factories
     * contain the invocation chains associated with the destination service of a wire and are responsible for
     * generating proxies
     */
    public void addTargetProxyFactory(String serviceName, ProxyFactory factory);

    /**
     * Returns the target-side proxy factory associated with the given service name
     */
    public ProxyFactory getTargetProxyFactory(String serviceName);

    /**
     * Returns a collection of target-side proxy factories for the configuration keyed by service name
     */
    public Map<String, ProxyFactory> getTargetProxyFactories();

    /**
     * Adds a source-side proxy factory for the given reference. Source-side proxy factories contain the invocation
     * chains for a reference in the component implementation associated with the instance context created by this
     * configuration. Source-side proxy factories also produce proxies that are injected on a reference in a component
     * implementation.
     */
    public void addSourceProxyFactory(String referenceName, ProxyFactory factory);

    public ProxyFactory getSourceProxyFactory(String referenceName);

    public Map<String, ProxyFactory> getSourceProxyFactories();

}
