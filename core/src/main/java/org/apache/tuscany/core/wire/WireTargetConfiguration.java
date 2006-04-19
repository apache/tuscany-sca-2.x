package org.apache.tuscany.core.wire;

import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.context.QualifiedName;

import java.lang.reflect.Method;
import java.util.Map;

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
     * @param messageFactory    the factory used to create wire messages
     */
    public WireTargetConfiguration(QualifiedName targetName, Map<Method, TargetInvocationConfiguration> invocationConfigs,
                                   ClassLoader proxyClassLoader, MessageFactory messageFactory) {
        super(targetName, proxyClassLoader, messageFactory);
        assert (invocationConfigs != null) : "No wire configuration map specified";
        configurations = invocationConfigs;

    }

}
