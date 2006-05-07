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
package org.apache.tuscany.container.java.invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.apache.tuscany.core.util.JavaIntrospectionHelper;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.context.TargetException;

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
            Object instance = getInstance();
            if (!operation.getDeclaringClass().isInstance(instance)) {
                Set<Method> methods = JavaIntrospectionHelper.getAllUniqueMethods(instance.getClass());
                Method newOperation = JavaIntrospectionHelper.findClosestMatchingMethod(operation.getName(), operation
                        .getParameterTypes(), methods);
                if (newOperation != null)
                    operation = newOperation;
            }
            if (payload != null && !payload.getClass().isArray()) {
                return operation.invoke(instance, payload);
            } else {
                return operation.invoke(instance, (Object[]) payload);
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            AbstractJavaComponentInvoker clone = (AbstractJavaComponentInvoker) super.clone();
            clone.operation = this.operation;
            return clone;
        } catch (CloneNotSupportedException e) {
            return null; // will not happen
        }
    }
}
