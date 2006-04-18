package org.apache.tuscany.core.wire;

import org.apache.tuscany.core.wire.impl.MessageChannelImpl;
import org.apache.tuscany.core.wire.impl.RequestResponseInterceptor;
import org.apache.tuscany.core.wire.impl.MessageDispatcher;

import java.lang.reflect.Method;

/**
 * Contains a source- or target-side wire pipeline for a service operation. Source and target wire pipelines
 * are "bridged" together by a set of wire builders with the source-side holding references to the target.
 * <p>
 * A set of wire configurations are used by a {@link ProxyFactory} to
 * create service proxies.
 * <p>
 * Invocation configurations must contain at least one interceptor and may have 0 to N handlers. Handlers process an
 * wire request or response in a one-way fashion. A typical wire sequence where interceptors and handlers
 * are configured for both the source and target-side will proceed as follows:
 * <ol>
 * <li>The first source interceptor will be called with a message, which will in turn invoke the next interceptor in
 * the chain
 * <li>The last source interceptor, which must be of type
 * {@link org.apache.tuscany.core.wire.impl.RequestResponseInterceptor} if there are handlers present, will be
 * invoked. The RR interceptor will in turn pass the message to a
 * {@link MessageChannel} which will invoke all source-side request handlers.
 * <li> The RR interceptor will then invoke the target-side request <tt>MessageChannel</tt>.
 * <li> The last source-side handler, an instance of
 * {@link org.apache.tuscany.core.wire.impl.MessageDispatcher}, will invoke the first source-side
 * interceptor, which in turn will pass the message down the target-side interceptor chain.
 * <li> If the target is a component instance the last target-side interceptor, an instance of
 * {@link org.apache.tuscany.core.wire.impl.InvokerInterceptor} will retrieve the
 * {@link TargetInvoker} from the message and call it to invoke the operation on a
 * target instance. <tt>TargetInvoker</tt>s are help by the source proxy to enable optimizations such as caching of
 * target instances.
 * <li> The response is returned up the wire stack until it reaches the source-side
 * <tt>RequestResponseInterceptor</tt>, which invokes the target and source-side response channels respectively.
 * <li> The response is then passed back up the rest of the wire stack.
 * </ol>
 * <p>
 * The source-to-target bridge may be constructed in any of the following ways:
 * <ul>
 * <li>Source handler-to-target handler
 * <li>Source handler-to-target interceptor
 * <li>Source interceptor-to-target handler
 * <li>Source interceptor-to-target interceptor
 * </ul>
 * <p>
 * In some scenarios, a service proxy may only contain target-side invocaton chains, for example, when a service is
 * resolved through a locate operation by a non-component client. In this case, there will be no source-side wire
 * chains and the target invoker will be held by the target-side and passed down the pipeline.
 *
 * @see org.apache.tuscany.core.builder.WireBuilder
 * @see ProxyFactory
 * @see TargetInvoker
 * @see org.apache.tuscany.core.wire.impl.MessageDispatcher
 *
 * @version $Rev: 394379 $ $Date: 2006-04-15 15:01:36 -0700 (Sat, 15 Apr 2006) $
 */
public class SourceInvocationConfiguration extends InvocationConfiguration{

    private Interceptor targetInterceptorChainHead;

    // a source-side pointer to target request handlers, if the exist
    private MessageChannel targetRequestChannel;

    // a source-side pointer to target response handlers, if the exist
    private MessageChannel targetResponseChannel;

    /**
     * Creates an new wire configuration for the given target operation
     */
    public SourceInvocationConfiguration(Method operation) {
        super(operation);
    }

    public void setTargetInterceptor(Interceptor interceptor) {
        targetInterceptorChainHead = interceptor;
    }

    /**
     * Returns the head target-side interceptor. This will be the head interceptor of the
     * "bridged" target configuration.
     */
    public Interceptor getTargetInterceptor() {
        return targetInterceptorChainHead;
    }

    /**
     * Used by source-side configurations, sets a pointer to the target-side request channel. This may be null when no
     * target request handlers exist.
     */
    public void setTargetRequestChannel(MessageChannel channel) {
        targetRequestChannel = channel;
    }

    /**
     * Used by source-side configurations, sets a pointer to the target-side response channel. This may be null when no
     * target response handlers exist.
     */
    public void setTargetResponseChannel(MessageChannel channel) {
        targetResponseChannel = channel;
    }

    /**
     * Prepares the configuration by linking interceptors and handlers
     */
    @Override
    public void build() {

        if (requestHandlers != null && targetInterceptorChainHead != null) {
              // on target-side, connect existing handlers and interceptors
              MessageHandler messageDispatcher = new MessageDispatcher(targetInterceptorChainHead);
              requestHandlers.add(messageDispatcher);
          }

        if (requestHandlers != null) {
            MessageChannel requestChannel = new MessageChannelImpl(requestHandlers);
            MessageChannel responseChannel = new MessageChannelImpl(responseHandlers);
            Interceptor channelInterceptor = new RequestResponseInterceptor(requestChannel, targetRequestChannel,
                    responseChannel, targetResponseChannel);

            if (interceptorChainHead != null) {
                interceptorChainTail.setNext(channelInterceptor);
            } else {
                interceptorChainHead = channelInterceptor;
            }

        } else {
            // no request handlers
            if (interceptorChainHead != null) {
                if (targetInterceptorChainHead != null) {
                    // Connect source interceptor chain directly to target interceptor chain
                    interceptorChainTail.setNext(targetInterceptorChainHead);
                    // interceptorChainTail = targetInterceptorChainHead;
                } else {
                    // Connect source interceptor chain to the target request channel
                    Interceptor channelInterceptor = new RequestResponseInterceptor(null, targetRequestChannel, null,
                            targetResponseChannel);
                    interceptorChainTail.setNext(channelInterceptor);
                }
            } else {
                // no source interceptor chain or source handlers, conntect to target interceptor chain or channel
                if (targetInterceptorChainHead != null) {
                    interceptorChainHead = targetInterceptorChainHead;
                    interceptorChainTail = targetInterceptorChainHead;
                } else {
                    Interceptor channelInterceptor = new RequestResponseInterceptor(null, targetRequestChannel, null,
                            targetResponseChannel);
                    interceptorChainHead = channelInterceptor;
                    interceptorChainTail = channelInterceptor;
                }
            }
        }
    }

}
