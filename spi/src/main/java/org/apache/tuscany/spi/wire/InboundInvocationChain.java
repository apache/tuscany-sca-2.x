package org.apache.tuscany.spi.wire;

/**
 * A set of interceptors and handlers (contained in request and response message channels) associated with the
 * inbound side of a wire for an operation. Inbound chains may start with request {@link
 * MessageHandler}s and contain at least one {@link Interceptor} processed after the handlers prior to
 * dipatching to the target instance. Inbound invocation chains may also contain a set of response
 * <code>MessageHandler</code>s which are processed after dispatching to the target instance.
 * <p/>
 *
 * @version $$Rev$$ $$Date$$
 */
public interface InboundInvocationChain extends InvocationChain {


}
