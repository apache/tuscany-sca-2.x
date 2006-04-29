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
package org.apache.tuscany.core.assembly.impl;

import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.core.assembly.JavaExtensibilityElement;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.Injector;

/**
 * The default implementation of <code>JavaExtensibilityElement</code>
 *
 * @version $$Rev$$ $$Date$$
 */
public class JavaExtensibilityElementImpl implements JavaExtensibilityElement {

    private EventInvoker init;

    private List<EventInvoker> invokers;

    private List<Injector> injectors;

    public JavaExtensibilityElementImpl() {
        invokers = new ArrayList<EventInvoker>();
        injectors = new ArrayList<Injector>();
    }

    public EventInvoker getInit() {
        return init;
    }

    public void setInit(EventInvoker init) {
        this.init = init;
    }

    private boolean eager;

    public boolean isEagerInit() {
        return eager;
    }

    public boolean setEagerInit(boolean val) {
        return eager;
    }

    private EventInvoker destroy;

    public EventInvoker getDestroy() {
        return destroy;
    }

    public void setDestroy(EventInvoker destroy) {
        this.destroy = destroy;
    }

    private AccessibleObject componentName;

    public AccessibleObject getComponentName() {
        return componentName;
    }

    public void setComponentName(AccessibleObject componentName) {
        this.componentName = componentName;
    }

    private AccessibleObject context;

    public AccessibleObject getContext() {
        return context;
    }

    public void setContext(AccessibleObject context) {
        this.context = context;
    }

    public List<EventInvoker> getInvokers() {
        return invokers;
    }

    public List<Injector> getInjectors() {
        return injectors;
    }


}
