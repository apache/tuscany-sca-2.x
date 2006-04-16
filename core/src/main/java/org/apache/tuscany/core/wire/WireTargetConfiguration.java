package org.apache.tuscany.core.wire;

import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.context.QualifiedName;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Represents the target side of a wire, including all invocation chains for operations on a service
 *
 * @version $$Rev$$ $$Date$$
 */
public class WireTargetConfiguration extends WireConfiguration {

    public WireTargetConfiguration(QualifiedName targetName, Map<Method, InvocationConfiguration> invocationConfigs,
                                   ClassLoader proxyClassLoader, MessageFactory messageFactory) {
        super(targetName, invocationConfigs, proxyClassLoader, messageFactory);
    }


}
