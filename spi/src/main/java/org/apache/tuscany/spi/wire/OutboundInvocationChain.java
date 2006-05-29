package org.apache.tuscany.spi.wire;

/**
 * A set of interceptors and handlers (contained in request and response message channels) associated with the
 * outbound side of a wire for a service operation. Outbound invocation chains always start with an {@link
 * Interceptor} and may contain 0..n {@link MessageHandler}s. <code>MessageHandlers</code> are part of a
 * request or response chainnel, which are invoked prior to and after dispatching to a target instance
 * respectively.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface OutboundInvocationChain extends InvocationChain {

}
