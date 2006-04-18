package org.apache.tuscany.core.wire;

import org.apache.tuscany.core.wire.impl.MessageDispatcher;

import java.lang.reflect.Method;

/**
 *
 * @see org.apache.tuscany.core.builder.WireBuilder
 * @see ProxyFactory
 * @see TargetInvoker
 * @see org.apache.tuscany.core.wire.impl.MessageDispatcher
 *
 * @version $Rev: 394379 $ $Date: 2006-04-15 15:01:36 -0700 (Sat, 15 Apr 2006) $
 */
public class TargetInvocationConfiguration extends InvocationConfiguration {

    /**
     * Creates an new target-side wire configuration for the given operation
     */
    public TargetInvocationConfiguration(Method operation) {
       super(operation);
    }

    /**
     * Prepares the configuration by linking interceptors and handlers
     */
    @Override
    public void build() {
        if (requestHandlers != null && interceptorChainHead != null) {
            // on target-side, connect existing handlers and interceptors
            MessageHandler messageDispatcher = new MessageDispatcher(interceptorChainHead);
            requestHandlers.add(messageDispatcher);
        }
    }

}
