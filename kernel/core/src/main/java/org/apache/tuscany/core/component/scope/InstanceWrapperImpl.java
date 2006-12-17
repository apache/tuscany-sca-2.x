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
package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.TargetException;

/**
 * Default implementation of an <code>InstanceWrapper</code>
 *
 * @version $$Rev$$ $$Date$$
 */
public class InstanceWrapperImpl implements InstanceWrapper {
    private Object instance;
    private AtomicComponent component;
    private boolean started;

    public InstanceWrapperImpl(AtomicComponent component, Object instance) {
        assert component != null;
        assert instance != null;
        this.component = component;
        this.instance = instance;
    }

    public boolean isStarted() {
        return started;
    }

    public Object getInstance() {
        if (!started) {
            throw new IllegalStateException("Instance not started");
        }
        return instance;
    }

    public void start() throws TargetException {
        component.init(instance);
        started = true;
    }

    public void stop() throws TargetException {
        component.destroy(instance);
        started = false;
    }

}
