package org.apache.tuscany.spi.wire;

/**
 * A holder used to associate an wire chain with a local copy of a target invoker that was previously cloned from the
 * chain master
 *
 * @version $Rev$ $Date$
 */
public class OutboundChainHolder implements Cloneable {
    OutboundInvocationChain chain;
    TargetInvoker cachedInvoker;

    public OutboundChainHolder(OutboundInvocationChain config) {
        this.chain = config;
    }

    public OutboundInvocationChain getChain() {
        return chain;
    }

    public TargetInvoker getCachedInvoker() {
        return cachedInvoker;
    }

    public void setCachedInvoker(TargetInvoker invoker) {
        this.cachedInvoker = invoker;
    }

    @SuppressWarnings({"CloneDoesntDeclareCloneNotSupportedException"})
    @Override
    public OutboundChainHolder clone() {
        try {
            return (OutboundChainHolder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
