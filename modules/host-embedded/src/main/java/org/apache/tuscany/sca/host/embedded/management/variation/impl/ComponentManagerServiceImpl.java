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

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.host.embedded.management.variation.ComponentInfo;
import org.apache.tuscany.sca.host.embedded.management.variation.ComponentManager;
import org.apache.tuscany.sca.host.embedded.management.variation.ComponentManagerService;

public class ComponentManagerServiceImpl implements ComponentManagerService {
    
    private ComponentManager componentManager;

    public ComponentManagerServiceImpl(ComponentManager componentManager) {
        this.componentManager = componentManager;
    }

    public List<ComponentInfo> getComponentInfos() {
        List<ComponentInfo> componentInfos = new ArrayList<ComponentInfo>();
        for (Component component: componentManager.getComponents()) {
            ComponentInfo componentInfo = new ComponentInfoImpl();
            componentInfo.setName(component.getName());
            componentInfo.setStarted(componentManager.isComponentStarted(component));
            componentInfos.add(componentInfo);
        }
        return componentInfos;
    }
    
    public ComponentInfo getComponentInfo(String componentName) {
        Component component = componentManager.getComponent(componentName);
        ComponentInfo componentInfo = new ComponentInfoImpl();
        componentInfo.setName(component.getName());
        componentInfo.setStarted(componentManager.isComponentStarted(component));
        return componentInfo;
    }
    
    public void startComponent(String componentName) throws ActivationException {
        componentManager.startComponent(componentManager.getComponent(componentName));
    }

    public void stopComponent(String componentName) throws ActivationException {
        componentManager.stopComponent(componentManager.getComponent(componentName));
    }

}
