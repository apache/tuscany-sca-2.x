package org.apache.tuscany.spi.wire;

import java.lang.reflect.Method;
import java.util.List;

/**
 * A source- or target-side invocation pipeline for a service operation. Invocation chains are associated with
 * the source or target side of a wire and are bridged when an assembly is processed.
 * <p/>
 * Invocation configurations contain at least one {@link Interceptor} and may have 0 to N {@link
 * MessageHandler}s. <code>Interceptors>/code> process invocations in a synchronous, around style manner while
 * <code>MessageHandler</code>s do so in a one-way manner.
 * <p/>
 * Source-side chains may only connect to target-side chains. Target-side chains may connect to other
 * target-side chains, for example, when invoking from a {@link org.apache.tuscany.spi.context.Service}
 * to an {@link org.apache.tuscany.spi.context.AtomicComponent}.
 * <p/>
 * In some scenarios, a service proxy may only contain target-side invocaton chains, for example, when a
 * service is resolved through a locate operation by a non-component client. In this case, there will be no
 * source-side wire chains and the target invoker will be held by the target-side and passed down the
 * pipeline.
 * <p/>
 * A {@link Message} is used to pass data associated with an invocation through the chain.
 * <code>Message</code>s contain a {@link TargetInvoker} responsible for dispatching to a target instance and
 * may be cached on the client-side. Caching allows various optimizations such as avoiding target instance
 * resolution when the client-side lifecycle scope is a shorter duration than the target.
 *
 * @version $Rev: 396284 $ $Date: 2006-04-23 08:27:42 -0700 (Sun, 23 Apr 2006) $
 */
public interface InvocationChain {
    /**
     * Returns the target operation for this invocation chain
     */
    Method getMethod();

    /**
     * Adds a request handler to the invocation chain
     */
    void addRequestHandler(MessageHandler handler);

    /**
     * Adds a response handler to the invocation chain
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
     * Returns the request channel for the chain
     */
    public MessageChannel getRequestChannel();

    /**
     * Returns the response channel for the chain
     */
    public MessageChannel getResponseChannel();

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
     * Returns the first interceptor in the chain
     */
    Interceptor getHeadInterceptor();

    /**
     * Returns the last interceptor in the chain
     */
    Interceptor getTailInterceptor();

    /**
     * Sets the head interceptor of the bridged target-side chain
     */
    void setTargetInterceptor(Interceptor interceptor);

    /**
     * Returns the head interceptor of the birdged target-side chain
     */
    Interceptor getTargetInterceptor();

    /**
     * Sets the target-side request channel when two chains are bidged
     */
    void setTargetRequestChannel(MessageChannel channel);

    /**
     * Sets the target-side response channel when two chains are bridged
     */
    void setTargetResponseChannel(MessageChannel channel);

    /**
     * Returns the target-side request channel when two chains are bridged
     */
    public MessageChannel getTargetRequestChannel();

    /**
     * Returns the target-side response channel when two chains are bridged
     */
    public MessageChannel getTargetResponseChannel();

    /**
     * Signals to the chain that its configuration is complete. Implementations may use this callback to
     * prepare there invocation chains.
     */
    void build();
}
