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

package org.apache.tuscany.extension.script;

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
public class ScriptInvoker extends TargetInvokerExtension {

    protected Object clazz;
    protected String operationName;

    private final AtomicComponent component;
    private final ScopeContainer<?, ?> scopeContainer;
    protected InstanceWrapper<?> target;
    protected boolean stateless;

    public ScriptInvoker(String operationName,
                         AtomicComponent component,
                         ScopeContainer scopeContainer,
                         WorkContext workContext) {
        super(workContext);
        this.component = component;
        this.scopeContainer = scopeContainer;
        stateless = Scope.STATELESS == scopeContainer.getScope();

        this.operationName = operationName;
        // TODO: support script classes
    }

    public Object invokeTarget(Object instance, Object args) throws InvocationTargetException {
        try {

            Invocable scriptEngine = (Invocable)instance;

            if (clazz == null) {
                return scriptEngine.invokeFunction(operationName, (Object[])args);
            } else {
                return scriptEngine.invokeMethod(clazz, operationName, (Object[])args);
            }

        } catch (ScriptException e) {
            throw new InvocationTargetException(e.getCause() != null ? e.getCause() : e);
        } catch (Exception e) {
            throw new InvocationTargetException(e);
        }
    }

    public Object invokeTarget(final Object payload, final short sequence) throws InvocationTargetException {
        try {
            InstanceWrapper<?> wrapper = getInstance(sequence);
            Object instance = wrapper.getInstance();

            Object ret = invokeTarget(instance, payload);

            scopeContainer.returnWrapper(component, wrapper);
            if (sequence == END) {
                // if end conversation, remove resource
                scopeContainer.remove(component);
            }
            return ret;
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
    protected InstanceWrapper<?> getInstance(short sequence) throws TargetException {
        switch (sequence) {
            case NONE:
                if (cacheable) {
                    if (target == null) {
                        target = scopeContainer.getWrapper(component);
                    }
                    return target;
                } else {
                    return scopeContainer.getWrapper(component);
                }
            case START:
                assert !cacheable;
                return scopeContainer.getWrapper(component);
            case CONTINUE:
            case END:
                assert !cacheable;
                return scopeContainer.getAssociatedWrapper(component);
            default:
                throw new InvalidConversationSequenceException("Unknown sequence type", String.valueOf(sequence));
        }
    }
}
