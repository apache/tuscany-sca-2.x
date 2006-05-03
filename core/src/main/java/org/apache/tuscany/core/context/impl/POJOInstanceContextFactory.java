/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.core.context.impl;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.InstanceContextFactory;
import org.apache.tuscany.spi.context.TargetException;
import org.apache.tuscany.core.injection.EventInvoker;

/**
 * @version $Rev$ $Date$
 */
public class POJOInstanceContextFactory<T> implements InstanceContextFactory {
    private final ObjectFactory<T> objectFactory;
    private EventInvoker<T> initInvoker;
    private EventInvoker<T> destroyInvoker;

    public POJOInstanceContextFactory(ObjectFactory<T> objectFactory,
                                      EventInvoker<T> initInvoker,
                                      EventInvoker<T> destroyInvoker) {
        this.objectFactory = objectFactory;
        this.initInvoker = initInvoker;
        this.destroyInvoker = destroyInvoker;
    }

    public InstanceContext createContext() throws TargetException {
        T instance = objectFactory.getInstance();
        return new POJOInstanceContext(instance);
    }

    protected class POJOInstanceContext extends AbstractLifecycle implements InstanceContext {
        private final T instance;

        public POJOInstanceContext(T instance) {
            this.instance = instance;
        }

        public T getInstance() {
            return instance;
        }

        public void start() throws CoreRuntimeException {
            setLifecycleState(INITIALIZING);
            if (initInvoker != null) {
                initInvoker.invokeEvent(instance);
            }
            setLifecycleState(STARTED);
        }

        public void stop() throws CoreRuntimeException {
            setLifecycleState(STOPPING);
            if (destroyInvoker != null) {
                destroyInvoker.invokeEvent(instance);
            }
            setLifecycleState(STOPPED);
        }
    }
}
