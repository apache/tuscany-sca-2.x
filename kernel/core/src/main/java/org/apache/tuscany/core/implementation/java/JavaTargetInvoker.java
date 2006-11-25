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

import org.apache.tuscany.spi.component.InvalidConversationSequenceException;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.ExecutionMonitor;
import org.apache.tuscany.spi.extension.TargetInvokerExtension;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;

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

    public JavaTargetInvoker(Method operation,
                             JavaAtomicComponent component,
                             InboundWire wire,
                             WorkContext context,
                             ExecutionMonitor monitor) {
        super(wire, context, monitor);
        assert operation != null : "Operation method cannot be null";
        this.operation = operation;
        this.component = component;
    }

    public Object invokeTarget(final Object payload, final short sequence) throws InvocationTargetException {
        try {
            Object instance = getInstance(sequence);
            if (!operation.getDeclaringClass().isInstance(instance)) {
                Set<Method> methods = getAllUniquePublicProtectedMethods(instance.getClass());
                Method newOperation = findClosestMatchingMethod(operation.getName(),
                    operation.getParameterTypes(), methods);
                if (newOperation != null) {
                    operation = newOperation;
                }
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
                throw new InvalidConversationSequenceException(String.valueOf(sequence));
            }
        } else {
            if (target == null) {
                target = component.getTargetInstance();
            }
            return target;
        }
    }


}
