package org.apache.tuscany.core.builder;

import java.util.Map;

import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Implementations create instance contexts based on a compiled runtime
 * configuration
 *
 * @version $Rev$ $Date$
 */
public interface RuntimeConfiguration<T extends Context> {

    /**
     * Creates an instance context based on a set of runtime configuration
     * information
     *
     * @return a new instance context
     * @throws ContextCreationException if an error occurs creating the context
     */
    public T createInstanceContext() throws ContextCreationException;
    
    public Scope getScope();
    
    public String getName();
    
    /////////////
    public void prepare();
    
    /**
     * Adds a poxy factory for the given service name
     */
    public void addTargetProxyFactory(String serviceName, ProxyFactory factory);

    public ProxyFactory getTargetProxyFactory(String serviceName);
    
    public Map<String, ProxyFactory> getTargetProxyFactories();

    /**
     * Adds a poxy factory for the given reference
     */
    public void addSourceProxyFactory(String referenceName, ProxyFactory factory);

    public ProxyFactory getSourceProxyFactory(String referenceName);
    
    public Map<String,ProxyFactory> getSourceProxyFactories();

}
