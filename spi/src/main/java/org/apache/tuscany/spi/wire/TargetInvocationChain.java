package org.apache.tuscany.spi.wire;

/**
 * A set of interceptors and handlers (contained in request and response message channels) associated with the
 * target side of a wire for a service operation. Target invocation chains may start with request {@link
 * MessageHandler}s and contain at least one {@link Interceptor} processed after the handlers prior to
 * dipatching to the target instance. Target invocation chains may also contain a set of response
 * <code>MessageHandler</code>s which are processed after dispatching to the target instance.
 * <p/>
 * Two target chains may be bridged together. The exact connection points (e.g. interceptors, request
 * handlers, response handlers) will vary depending on the contains of each chain.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface TargetInvocationChain extends InvocationChain {


}
