/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.core.implementation.composite;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;

/**
 * Base class for dispatching to a Composite Reference.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractCompositeReferenceTargetInvoker implements TargetInvoker {

    protected Operation operation;
    protected boolean cacheable;

    public AbstractCompositeReferenceTargetInvoker(Operation operation) {
        assert operation != null : "Operation method cannot be null";
        this.operation = operation;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public boolean isOptimizable() {
        return isCacheable(); // we only need to check if the scopes are correct
    }

    public Object invokeTarget(final Object payload) throws InvocationTargetException {
        throw new InvocationTargetException(new InvocationRuntimeException("Not allowed to invokeTarget with object"));
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            AbstractOperationOutboundInvocationHandler invocationHandler = getInvocationHandler();
            return invocationHandler.invoke(operation, msg);
        } catch (Throwable e) {
            Message faultMsg = new MessageImpl();
            faultMsg.setBodyWithFault(e);
            return faultMsg;
        }
}

    @Override
    public AbstractCompositeReferenceTargetInvoker clone() throws CloneNotSupportedException {
        return (AbstractCompositeReferenceTargetInvoker) super.clone();
    }

    protected abstract AbstractOperationOutboundInvocationHandler getInvocationHandler();
}
