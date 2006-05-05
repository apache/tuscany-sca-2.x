package org.apache.tuscany.spi.wire;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;

/**
 * Contains configuration for the target side of a wire
 *
 * @version $$Rev$$ $$Date$$
 */
public class WireTargetConfiguration {

    protected Map<Method, TargetInvocationConfiguration> configurations;

    protected ClassLoader proxyClassLoader;

    protected QualifiedName targetName;


    /**
     * Creates the source side of a wire
     *
     * @param targetName        the qualified name of the target service specified by the wire
     * @param invocationConfigs a collection of target service operation-to-invocation chain mappings
     * @param proxyClassLoader  the classloader to use when creating a proxy
     */
    public WireTargetConfiguration(QualifiedName targetName, Map<Method, TargetInvocationConfiguration> invocationConfigs,
                                   ClassLoader proxyClassLoader) {
        this.targetName = targetName;
        if (proxyClassLoader == null) {
            this.proxyClassLoader = Thread.currentThread().getContextClassLoader();
        } else {
            this.proxyClassLoader = proxyClassLoader;
        }
        assert (invocationConfigs != null) : "No wire configuration map specified";
        configurations = invocationConfigs;

    }

    /**
     * Returns the qualified name of the target service specified by the wire
     */
    public QualifiedName getTargetName() {
        return targetName;
    }

    /**
     * Returns the classloader used for creating proxies
     */
    public ClassLoader getProxyClassLoader() {
        return proxyClassLoader;
    }

    /**
     * Returns the invocation configuration for each operation on a service specified by a reference or a
     * target service.
     */
    public Map<Method, TargetInvocationConfiguration> getInvocationConfigurations() {
        return configurations;
    }




}
