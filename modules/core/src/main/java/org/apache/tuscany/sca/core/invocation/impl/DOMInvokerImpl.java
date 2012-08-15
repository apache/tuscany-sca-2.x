package org.apache.tuscany.sca.core.invocation.impl;

import org.apache.tuscany.sca.runtime.DOMInvoker;
import org.oasisopen.sca.ServiceRuntimeException;
import org.w3c.dom.Node;

public class DOMInvokerImpl implements DOMInvoker {

    AsyncJDKInvocationHandler handler;
    
    public DOMInvokerImpl(AsyncJDKInvocationHandler handler) {
        this.handler = handler;
    }

    @Override
    public Node invoke(String opName, Node arg) {
        try {
            return (Node)handler.invoke(opName, new Object[]{arg}, handler.source, null);
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }
    }

}
