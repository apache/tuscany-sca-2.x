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

package org.apache.tuscany.sca.host.embedded.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.host.management.ComponentListener;
import org.apache.tuscany.sca.host.management.ComponentManager;

public class ComponentManagerImpl implements ComponentManager {

    protected List<ComponentListener> listeners = new ArrayList<ComponentListener>();
    protected EmbeddedSCADomain domain;

    public ComponentManagerImpl(EmbeddedSCADomain domain) {
        this.domain = domain;
    }

    public void addComponentListener(ComponentListener listener) {
        this.listeners.add(listener);
    }

    public void removeComponentListener(ComponentListener listener) {
        this.listeners.remove(listener);
    }

    public Set<String> getComponentNames() {
        return domain.getDomainCompositeHelper().getComponentNames();
    }

    public Component getComponent(String componentName) {
        return domain.getDomainCompositeHelper().getComponent(componentName);
    }

    public void startComponent(String componentName) throws ActivationException {
        Component component = domain.getDomainCompositeHelper().getComponent(componentName);
        if (component == null) {
            throw new IllegalArgumentException("no component: " + componentName);
        }
        domain.getDomainCompositeHelper().startComponent(component);
    }

    public void stopComponent(String componentName) throws ActivationException {
        Component component = domain.getDomainCompositeHelper().getComponent(componentName);
        if (component == null) {
            throw new IllegalArgumentException("no component: " + componentName);
        }
        domain.getDomainCompositeHelper().stopComponent(component);
    }

    public void notifyComponentStarted(String componentName) {
        for (ComponentListener listener : listeners) {
            try {
                listener.componentStarted(componentName);
            } catch (Exception e) {
                e.printStackTrace(); // TODO: log
            }
        }
    }

    public void notifyComponentStopped(String componentName) {
        for (ComponentListener listener : listeners) {
            try {
                listener.componentStopped(componentName);
            } catch (Exception e) {
                e.printStackTrace(); // TODO: log
            }
        }
    }

}
