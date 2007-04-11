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

package org.apache.tuscany.implementation.script;

import java.lang.reflect.InvocationTargetException;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.component.InstanceWrapper;
import org.apache.tuscany.spi.component.InvalidConversationSequenceException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.TargetInvokerExtension;
import org.apache.tuscany.spi.model.Scope;

/**
 * Perform the actual script invocation
 * TODO: move vertually all of this to SPI TargetInvokerExtension
 */
@SuppressWarnings("deprecation")
public class ScriptInvoker<T> extends TargetInvokerExtension {

    protected Object clazz;
    protected String operationName;

    private final AtomicComponent<T> component;
    private final ScopeContainer scopeContainer;
    protected InstanceWrapper<T> target;
    protected boolean stateless;

    public ScriptInvoker(String operationName,
                         AtomicComponent component,
                         ScopeContainer scopeContainer,
                         WorkContext workContext) {

        this.operationName = operationName;
        this.component = component;
        this.scopeContainer = scopeContainer;
        stateless = Scope.STATELESS == scopeContainer.getScope();

        // TODO: support script classes
    }

    public Object invokeTarget(Object payload, short sequence, WorkContext workContext) throws InvocationTargetException {
        Object contextId = workContext.getIdentifier(scopeContainer.getScope());
        try {

            InstanceWrapper<T> wrapper = getInstance(sequence, contextId);
            Invocable scriptEngine = (Invocable)wrapper.getInstance();

            Object ret;
            if (clazz == null) {
                ret = scriptEngine.invokeFunction(operationName, (Object[])payload);
            } else {
                ret =  scriptEngine.invokeMethod(clazz, operationName, (Object[])payload);
            }

            scopeContainer.returnWrapper(component, wrapper, contextId);
            if (sequence == END) {
                // if end conversation, remove resource
                scopeContainer.remove(component);
            }

            return ret;

        } catch (ScriptException e) {
            throw new InvocationTargetException(e);
        } catch (ComponentException e) {
            throw new InvocationTargetException(e);
        }
    }

    @Override
    public ScriptInvoker clone() throws CloneNotSupportedException {
        try {
            ScriptInvoker invoker = (ScriptInvoker)super.clone();
            invoker.target = null;
            return invoker;
        } catch (CloneNotSupportedException e) {
            return null; // will not happen
        }
    }

    /**
     * Resolves the target service instance or returns a cached one
     */
    protected InstanceWrapper<T> getInstance(short sequence, Object contextId) throws TargetException {
        switch (sequence) {
        case NONE:
            if (cacheable) {
                if (target == null) {
                    target = scopeContainer.getWrapper(component, contextId);
                }
                return target;
            } else {
                return scopeContainer.getWrapper(component, contextId);
            }
        case START:
            assert !cacheable;
            return scopeContainer.getWrapper(component, contextId);
        case CONTINUE:
        case END:
            assert !cacheable;
            return scopeContainer.getAssociatedWrapper(component, contextId);
        default:
            throw new InvalidConversationSequenceException("Unknown sequence type", String.valueOf(sequence));
        }
    }
}
