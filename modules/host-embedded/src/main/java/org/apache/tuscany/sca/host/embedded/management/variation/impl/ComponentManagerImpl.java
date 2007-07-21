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

package org.apache.tuscany.sca.host.embedded.management.variation.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.core.runtime.ActivationException;
import org.apache.tuscany.sca.core.runtime.RuntimeComponentImpl;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;
import org.apache.tuscany.sca.host.embedded.management.variation.ComponentListener;
import org.apache.tuscany.sca.host.embedded.management.variation.ComponentManager;

public class ComponentManagerImpl implements ComponentManager {

    protected List<ComponentListener> listeners = new CopyOnWriteArrayList<ComponentListener>();
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

    public Component getComponent(String componentName) {
        return domain.getDomainCompositeHelper().getComponent(componentName);
    }
    
    public List<Component> getComponents() {
        return domain.getDomainCompositeHelper().getComponents();
    }

    public void startComponent(Component component) throws ActivationException {
        domain.getDomainCompositeHelper().startComponent(component);
    }

    public void stopComponent(Component component) throws ActivationException {
        domain.getDomainCompositeHelper().stopComponent(component);
    }

    public void notifyComponentStarted(Component component) {
        for (ComponentListener listener : listeners) {
            try {
                listener.componentStarted(component);
            } catch (Exception e) {
                e.printStackTrace(); // TODO: log
            }
        }
    }

    public void notifyComponentStopped(Component component) {
        for (ComponentListener listener : listeners) {
            try {
                listener.componentStopped(component);
            } catch (Exception e) {
                e.printStackTrace(); // TODO: log
            }
        }
    }

    public boolean isComponentStarted(Component component) {
        RuntimeComponentImpl runtimeComponent = (RuntimeComponentImpl)component;
        return runtimeComponent.isStarted();
    }

}
