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
package org.apache.tuscany.core.implementation.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import org.osoa.sca.NoRegisteredCallbackException;

import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.component.InvalidConversationSequenceException;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.ExecutionMonitor;
import org.apache.tuscany.spi.extension.TargetInvokerExtension;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.InboundWire;

import static org.apache.tuscany.core.util.JavaIntrospectionHelper.findClosestMatchingMethod;
import static org.apache.tuscany.core.util.JavaIntrospectionHelper.getAllUniquePublicProtectedMethods;

/**
 * Responsible for synchronously dispatching an invocation to a Java component implementation instance
 *
 * @version $Rev$ $Date$
 */
public class JavaTargetInvoker extends TargetInvokerExtension {
    protected Method operation;
    protected JavaAtomicComponent component;
    protected Object target;
    protected Class callbackClass;
    protected boolean stateless;

    public JavaTargetInvoker(Method operation,
                             JavaAtomicComponent component,
                             InboundWire wire,
                             Class callbackClass,
                             WorkContext context,
                             ExecutionMonitor monitor) {
        super(wire, context, monitor);
        assert operation != null : "Operation method cannot be null";
        this.operation = operation;
        this.component = component;
        stateless = Scope.STATELESS == component.getScope();
        this.callbackClass = callbackClass;
    }

    public JavaTargetInvoker(Method operation,
                             JavaAtomicComponent component,
                             InboundWire callbackWire,
                             WorkContext context,
                             ExecutionMonitor monitor) {
        this(operation, component, callbackWire, null, context, monitor);
    }

    public Object invokeTarget(final Object payload, final short sequence) throws InvocationTargetException {
        try {
            Object instance = getInstance(sequence);
            if (callbackClass != null && !callbackClass.isInstance(instance)) {
                throw new InvocationTargetException(
                    new NoRegisteredCallbackException("Instance is does not implement callback ["
                        + callbackClass.toString() + "]"));
            }
            if (!operation.getDeclaringClass().isInstance(instance)) {
                Set<Method> methods = getAllUniquePublicProtectedMethods(instance.getClass());
                Method newOperation = findClosestMatchingMethod(operation.getName(),
                    operation.getParameterTypes(), methods);
                if (newOperation != null) {
                    operation = newOperation;
                }
            }
            Object ret;
            if (payload != null && !payload.getClass().isArray()) {
                ret = operation.invoke(instance, payload);
            } else {
                ret = operation.invoke(instance, (Object[]) payload);
            }
            if (stateless) {
                // notify a stateless instance of a destruction event after the invoke
                component.destroy(instance);
            } else if (sequence == END) {
                component.destroy(instance);
                // if end conversation, remove resource
                component.removeInstance();
            }
            return ret;
        } catch (IllegalAccessException e) {
            throw new InvocationTargetException(e);
        } catch (ComponentException e) {
            throw new InvocationTargetException(e);
        }
    }

    @Override
    public JavaTargetInvoker clone() throws CloneNotSupportedException {
        try {
            JavaTargetInvoker invoker = (JavaTargetInvoker) super.clone();
            invoker.target = null;
            return invoker;
        } catch (CloneNotSupportedException e) {
            return null; // will not happen
        }
    }

    /**
     * Resolves the target service instance or returns a cached one
     */
    protected Object getInstance(short sequence) throws TargetException {
        if (!cacheable) {
            if (sequence == START || sequence == NONE) {
                return component.getTargetInstance();
            } else if (sequence == CONTINUE || sequence == END) {
                return component.getAssociatedTargetInstance();
            } else {
                throw new InvalidConversationSequenceException("Unknown sequence type", String.valueOf(sequence));
            }
        } else {
            assert sequence == NONE;  // conversations are not cacheable
            if (target == null) {
                target = component.getTargetInstance();
            }
            return target;
        }
    }


}
