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
package org.apache.tuscany.implementation.java.invocation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.scope.InstanceWrapper;
import org.apache.tuscany.scope.Scope;
import org.apache.tuscany.scope.ScopeContainer;
import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.component.InvalidConversationSequenceException;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;

/**
 * Responsible for synchronously dispatching an invocation to a Java component
 * implementation instance
 * 
 * @version $Rev$ $Date$
 */
public class JavaTargetInvoker extends TargetInvokerExtension {
    protected Method operation;
    protected boolean stateless;
    protected InstanceWrapper target;
    private final RuntimeComponent component;
    private final ScopeContainer scopeContainer;

    public JavaTargetInvoker(Method operation, RuntimeComponent component) {
        assert operation != null : "Operation method cannot be null";
        this.operation = operation;
        this.component = component;
        this.scopeContainer = component.getScopeContainer();
        stateless = Scope.STATELESS == this.scopeContainer.getScope();
    }

    @Override
    public JavaTargetInvoker clone() throws CloneNotSupportedException {
        try {
            JavaTargetInvoker invoker = (JavaTargetInvoker)super.clone();
            invoker.target = null;
            return invoker;
        } catch (CloneNotSupportedException e) {
            return null; // will not happen
        }
    }

    /**
     * Resolves the target service instance or returns a cached one
     */
    protected InstanceWrapper getInstance(short sequence, Object contextId) throws TargetException {
        switch (sequence) {
            case NONE:
                if (cacheable) {
                    if (target == null) {
                        target = scopeContainer.getWrapper(contextId);
                    }
                    return target;
                } else {
                    return scopeContainer.getWrapper(contextId);
                }
            case START:
                assert !cacheable;
                return scopeContainer.getWrapper(contextId);
            case CONTINUE:
            case END:
                assert !cacheable;
                return scopeContainer.getAssociatedWrapper(contextId);
            default:
                throw new InvalidConversationSequenceException("Unknown sequence type: " + String.valueOf(sequence));
        }
    }

    public Object invokeTarget(final Object payload, final short sequence, WorkContext workContext)
        throws InvocationTargetException {
        Object contextId = workContext.getIdentifier(scopeContainer.getScope());
        try {
            InstanceWrapper wrapper = getInstance(sequence, contextId);
            Object instance = wrapper.getInstance();
            Object ret;
            if (payload != null && !payload.getClass().isArray()) {
                ret = operation.invoke(instance, payload);
            } else {
                ret = operation.invoke(instance, (Object[])payload);
            }
            scopeContainer.returnWrapper(wrapper, contextId);
            if (sequence == END) {
                // if end conversation, remove resource
                scopeContainer.remove();
            }
            return ret;
        } catch (IllegalAccessException e) {
            throw new InvocationTargetException(e);
        } catch (ComponentException e) {
            throw new InvocationTargetException(e);
        }
    }

}
