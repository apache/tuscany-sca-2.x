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
package org.apache.tuscany.container.groovy;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.ExecutionMonitor;
import org.apache.tuscany.spi.extension.TargetInvokerExtension;
import org.apache.tuscany.spi.wire.InboundWire;

import groovy.lang.GroovyObject;

/**
 * Dispatches to a Groovy implementation instance
 *
 * @version $$Rev$$ $$Date$$
 */
public class GroovyInvoker extends TargetInvokerExtension {

    protected GroovyAtomicComponent component;
    protected String operation;
    protected boolean cacheable;

    public GroovyInvoker(String operation,
                         GroovyAtomicComponent component,
                         InboundWire wire,
                         WorkContext context,
                         ExecutionMonitor monitor) {
        super(wire, context, monitor);
        this.component = component;
        this.operation = operation;
    }

    public boolean isCacheable() {
        return cacheable;
    }

    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    public boolean isOptimizable() {
        return false;
    }

    /**
     * Dispatches to the the target. TODO support conversational dispatch
     */
    public Object invokeTarget(final Object payload, final short sequence) throws InvocationTargetException {
        GroovyObject target = null;
        try {
            target = component.getTargetInstance();
        } catch (TargetException e) {
            throw new InvocationTargetException(e);
        }
        Object[] args = (Object[]) payload;
        try {
            return target.invokeMethod(operation, args);
        } catch (Exception ex) {
            throw new InvocationTargetException(ex);
        }
    }

    public GroovyInvoker clone() throws CloneNotSupportedException {
        return (GroovyInvoker) super.clone();
    }


}
