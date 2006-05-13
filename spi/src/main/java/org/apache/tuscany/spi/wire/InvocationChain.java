package org.apache.tuscany.spi.wire;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Contains a source- or target-side invocation pipeline for a service operation. The runtime framework
 * creates invocation chains on a per-operation, per-service basis. Further, invocation chains are further
 * distinguished by being part of the source or target sides of a wire. Chains are "bridged" together by the
 * runtime by a set of {@link org.apache.tuscany.spi.builder.WireBuilder}s with the source-side holding
 * references to the target.
 * <p/>
 * <code>InvocationChain</code>s are managed by {@link SourceWire}s and {@link
 * TargetWire}s, which are used by wire factories to
 * buildSource wires and proxies.
 * <p/>
 * Invocation configurations must contain at least one interceptor and may have 0 to N handlers. Handlers
 * process a wire request or response in a one-way fashion. A typical wire sequence where interceptors and
 * handlers are configured for both the source and target-side will proceed as follows:
 * <pre>
 * <ol>
 * <li>The first source interceptor will be called with a message, which will in
 *     turn invoke the next interceptor in the chain <li>The last source interceptor, which must be of type
 * {@link
 *     org.apache.tuscany.core.wire.impl.RequestResponseInterceptor} if there are handlers present, will be
 * invoked. The RR
 *     interceptor will in turn pass the message to a {@link MessageChannel} which
 * will invoke all source-side request handlers.
 * <li>The RR interceptor will then invoke the target-side request <code>MessageChannel</code>.
 * <li>The last source-side handler, an instance of {@link MessageDispatcher},
 * will invoke the
 *     first source-side interceptor, which in turn will pass the message down the target-side interceptor
 * chain.
 * <li>If the target is a component instance the last target-side interceptor, an instance of
 *     {@link InvokerInterceptor} will retrieve the {@link TargetInvoker} from the
 * message and
 *     call it to invoke the operation on a target instance. <tt>TargetInvoker</tt>s are help by
 *     the source proxy to enable optimizations such as caching of target instances. <li> The response is
 * returned up the wire
 * stack
 *     until it reaches the source-side <tt>RequestResponseInterceptor</tt>, which invokes the target and
 * source-side response
 *     channels respectively.
 * <li>The response is then passed back up the rest of the wire stack. </ol>
 * </pre>
 * <p/>
 * The source-to-target bridge may be constructed in any of the following ways:
 * <pre>
 * <ul>
 * <li>Source handler-to-target handler
 * <li>Source handler-to-target interceptor
 * <li>Source interceptor-to-target handler
 * <li>Source interceptor-to-target interceptor
 * </ul>
 * </pre>
 * <p/>
 * In some scenarios, a service proxy may only contain target-side invocaton chains, for example, when a
 * service is resolved through a locate operation by a non-component client. In this case, there will be no
 * source-side wire chains and the target invoker will be held by the target-side and passed down the
 * pipeline.
 *
 * @version $Rev: 396284 $ $Date: 2006-04-23 08:27:42 -0700 (Sun, 23 Apr 2006) $
 * @see org.apache.tuscany.spi.builder.WireBuilder
 * @see TargetInvoker
 */
public interface InvocationChain {
    /**
     * Returns the target operation for this invocation chain
     */
    Method getMethod();

    /**
     * Adds an request handler to the invocation chain
     */
    void addRequestHandler(MessageHandler handler);

    /**
     * Adds an response handler to the invocation chain
     */
    void addResponseHandler(MessageHandler handler);

    /**
     * Returns the request handler chain
     */
    List<MessageHandler> getRequestHandlers();

    /**
     * Returns the response handler chain
     */
    List<MessageHandler> getResponseHandlers();

    /**
     * Sets the target invoker to pass down the chain
     */
    void setTargetInvoker(TargetInvoker invoker);

    /**
     * Returns the target invoker that is passed down the chain
     */
    TargetInvoker getTargetInvoker();

    /**
     * Adds an interceptor to the chain
     */
    void addInterceptor(Interceptor interceptor);

    /**
     * Returns the last interceptor in the chain
     */
    Interceptor getTailInterceptor();

    /**
     * Returns the first interceptor in the chain
     */
    Interceptor getHeadInterceptor();

    /**
     * Signals to the chain that its configuration is complete. Implementations may use this callback to
     * prepare there invocation chains.
     */
    void build();
}
