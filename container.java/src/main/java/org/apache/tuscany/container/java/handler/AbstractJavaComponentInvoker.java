/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.java.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.invocation.Interceptor;
import org.apache.tuscany.core.invocation.InvocationRuntimeException;
import org.apache.tuscany.core.invocation.TargetInvoker;
import org.apache.tuscany.core.message.Message;

/**
 * Base class for dispatching to a Java based component implementation. Subclasses implement a strategy for resolving
 * implementation instances.
 * 
 * @version $Rev$ $Date$
 */
public abstract class AbstractJavaComponentInvoker implements TargetInvoker {

    protected Method operation;

    public AbstractJavaComponentInvoker(Method operation) {
        assert (operation != null) : "Operation method cannot be null";
        this.operation = operation;
    }

    public Object invokeTarget(Object payload) throws InvocationTargetException {
        try {
            if (payload != null && !payload.getClass().isArray()) {
                return operation.invoke(getInstance(), payload);
            } else {
                return operation.invoke(getInstance(), (Object[]) payload);
            }
        } catch (IllegalAccessException e) {
            throw new InvocationRuntimeException(e);
        }
    }

    public Message invoke(Message msg) {
        try {
            Object resp = invokeTarget(msg.getBody());
            msg.setBody(resp);
        } catch (InvocationTargetException e) {
            msg.setBody(e.getCause());
        } catch (Throwable e) {
            msg.setBody(e);
        }
        return msg;
    }

    protected abstract Object getInstance() throws TargetException;

    public void setNext(Interceptor next) {
        throw new IllegalStateException("This interceptor must be the last interceptor in an interceptor chain");
    }

}
