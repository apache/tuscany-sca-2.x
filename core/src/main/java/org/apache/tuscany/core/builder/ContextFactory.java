package org.apache.tuscany.core.builder;

import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.invocation.ProxyFactory;
import org.apache.tuscany.model.assembly.Scope;

import java.util.List;
import java.util.Map;

/**
 * Implementations serve the dual purpose of creating instances of {@link org.apache.tuscany.core.context.Context} based
 * on a compiled configuration such as a logical assembly model and holding a
 * {@link org.apache.tuscany.core.invocation.ProxyFactory} for the instance type associated with the context.
 * <p>
 * Context factories are created or "built" in two phases. {@link org.apache.tuscany.core.builder.ContextFactoryBuilder}s
 * are responsible for analyzing a logical model assembly and producing the appropriate <code>ContextFactory</code>
 * for the runtime. {@link org.apache.tuscany.core.builder.WireBuilder}s update the proxy configuration associated with
 * the <code>ProxyFactory</code> attached to the <code>ContextFactory</code>.
 * <p>
 * <code>ContextFactory</code> implementations also contain the source and target invocations chains associated with
 * all instances of a given <code>Context</code> type. For example, two contexts associated with separate sessions for
 * a component will refer back to the same invocation chains held in the <code>ProxyFactory</code> attached to the
 * <code>ContextFactory</code>.
 * 
 * @version $Rev: 385747 $ $Date: 2006-03-13 22:12:53 -0800 (Mon, 13 Mar 2006) $
 */
public interface ContextFactory<T extends Context> {

    /**
     * Creates a <code>Context</code> based on configuration supplied by a logical model assembly
     * 
     * @return a new instance context
     * @throws ContextCreationException if an error occurs creating the context
     */
    public T createContext() throws ContextCreationException;

    /**
     * Returns the scope identifier associated with the type of contexts produced by the current factory
     */
    public Scope getScope();

    /**
     * Returns the name of the contexts produced by the current factory
     */
    public String getName();

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

    /**
     * Returns a collection of source side-proxy factories for component references. There may 1..n proxy factories per
     * reference.
     */
    public List<ProxyFactory> getSourceProxyFactories();

    /**
     * Called to signal to the configuration that its parent context has been activated and that it shoud perform any
     * required initialization steps
     * 
     * @param parent the parent context's configuration
     */
    public void prepare(CompositeContext parent);

}
