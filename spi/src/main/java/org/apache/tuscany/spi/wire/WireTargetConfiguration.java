package org.apache.tuscany.spi.wire;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;

/**
 * Contains configuration for the target side of a wire
 *
 * @version $$Rev$$ $$Date$$
 */
public class WireTargetConfiguration extends WireConfiguration<TargetInvocationConfiguration> {

    /**
     * Creates the source side of a wire
     *
     * @param targetName        the qualified name of the target service specified by the wire
     * @param invocationConfigs a collection of target service operation-to-invocation chain mappings
     * @param proxyClassLoader  the classloader to use when creating a proxy
     */
    public WireTargetConfiguration(QualifiedName targetName, Map<Method, TargetInvocationConfiguration> invocationConfigs,
                                   ClassLoader proxyClassLoader) {
        super(targetName, proxyClassLoader);
        assert (invocationConfigs != null) : "No wire configuration map specified";
        configurations = invocationConfigs;

    }

}
