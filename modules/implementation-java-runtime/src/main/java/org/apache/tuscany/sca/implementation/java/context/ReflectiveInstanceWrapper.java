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
package org.apache.tuscany.sca.implementation.java.context;

import org.apache.tuscany.sca.implementation.java.injection.EventInvoker;
import org.apache.tuscany.sca.scope.InstanceWrapper;
import org.apache.tuscany.sca.scope.TargetDestructionException;
import org.apache.tuscany.sca.scope.TargetInitializationException;

/**
 * @version $Rev$ $Date$
 */
public class ReflectiveInstanceWrapper<T> implements InstanceWrapper<T> {
    private final EventInvoker<T> initInvoker;
    private final EventInvoker<T> destroyInvoker;
    private final T instance;

    public ReflectiveInstanceWrapper(T instance, EventInvoker<T> initInvoker, EventInvoker<T> destroyInvoker) {
        this.instance = instance;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
    }
    
    public T getInstance() {
        return instance;
    }

    public void start() throws TargetInitializationException {
        if (initInvoker != null) {
            initInvoker.invokeEvent(instance);
        }
    }


    public void stop() throws TargetDestructionException {
        if (destroyInvoker != null) {
            destroyInvoker.invokeEvent(instance);
        }
    }
}
