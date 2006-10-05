package org.apache.tuscany.core.implementation.composite;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.AbstractOutboundInvocationHandler;

public abstract class AbstractOperationOutboundInvocationHandler
    extends AbstractOutboundInvocationHandler {

    public abstract Object invoke(Operation operation, Object[] args) throws Throwable;
}
