package org.apache.tuscany.spi.wire;

/**
 * A set of interceptors and handlers (contained in request and response message channels) associated with the
 * target side of a wire for a service operation. A service will have one chain per operation.
 *
 * @version $$Rev$$ $$Date$$
 */
public interface TargetInvocationChain extends InvocationChain {

}
