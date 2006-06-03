/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.component;

import org.apache.tuscany.spi.AbstractLifecycle;
import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.InstanceWrapper;

/**
 * Manages a Java class-based implementation instance
 *
 * @version $$Rev$$ $$Date$$
 */
public class PojoInstanceWrapper extends AbstractLifecycle implements InstanceWrapper {

    private Object instance;
    private AtomicComponent component;

    public PojoInstanceWrapper(AtomicComponent component, Object instance) {
        assert(component != null);
        assert(instance != null);
        this.component = component;
        this.instance = instance;
    }

    public Object getInstance() {
        checkInit();
        return instance;
    }

    public void start() throws CoreRuntimeException {
        try {
            component.init(instance);
            lifecycleState = RUNNING;
        } catch (RuntimeException e) {
            lifecycleState = ERROR;
            throw e;
        }
    }

    public void stop() throws CoreRuntimeException {
        checkInit();
        component.destroy(instance);
    }

    protected void checkInit() {
        if (getLifecycleState() != RUNNING) {
            throw new IllegalStateException("Scope not running [" + getLifecycleState() + "]");
        }
    }

}
