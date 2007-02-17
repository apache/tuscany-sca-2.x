package org.apache.tuscany.spi.wire;

/**
 * A holder used to associate an wire chain with a local copy of a target invoker that was previously cloned from the
 * chain master
 *
 * @version $Rev$ $Date$
 */
public class ChainHolder implements Cloneable {
    InvocationChain chain;
    TargetInvoker cachedInvoker;

    public ChainHolder(InvocationChain config) {
        this.chain = config;
    }

    public InvocationChain getChain() {
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
    public ChainHolder clone() {
        try {
            return (ChainHolder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
