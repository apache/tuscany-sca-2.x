package org.apache.tuscany.core.implementation.composite;

import org.apache.tuscany.core.wire.AbstractOutboundInvocationHandler;
import org.apache.tuscany.spi.model.Operation;

public abstract class AbstractOperationOutboundInvocationHandler
    extends AbstractOutboundInvocationHandler {

    public abstract Object invoke(Operation operation, Object[] args) throws Throwable;
}
