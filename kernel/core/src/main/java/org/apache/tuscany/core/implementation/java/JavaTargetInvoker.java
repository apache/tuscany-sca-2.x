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

import java.lang.reflect.Method;

import org.apache.tuscany.spi.component.TargetException;

import org.apache.tuscany.core.wire.PojoTargetInvoker;

/**
 * Uses a component to resolve an implementation instance based on the current thread component
 *
 * @version $Rev$ $Date$
 */
public class JavaTargetInvoker extends PojoTargetInvoker {

    private JavaAtomicComponent component;
    private Object target;

    /**
     * Creates a new invoker
     *
     * @param operation the operation the invoker is associated with
     * @param component the target component
     */
    public JavaTargetInvoker(Method operation, JavaAtomicComponent component) {
        super(operation);
        assert component != null : "No atomic component specified";
        this.component = component;
    }

    /**
     * Resolves the target service instance or returns a cached one
     */
    protected Object getInstance() throws TargetException {
        if (!cacheable) {
            return component.getTargetInstance();
        } else {
            if (target == null) {
                target = component.getTargetInstance();
            }
            return target;
        }
    }

    public JavaTargetInvoker clone() throws CloneNotSupportedException {
        JavaTargetInvoker invoker = (JavaTargetInvoker) super.clone();
        invoker.target = null;
        invoker.cacheable = this.cacheable;
        invoker.component = this.component;
        return invoker;
    }
}
