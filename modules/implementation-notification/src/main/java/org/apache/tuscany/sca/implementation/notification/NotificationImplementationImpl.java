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
package org.apache.tuscany.sca.implementation.notification;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.impl.ComponentTypeImpl;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.PolicySet;


/**
 * Model object for a Notification implementation.
 * 
 * @version $Rev$ $Date$
 */
public class NotificationImplementationImpl extends ComponentTypeImpl implements Implementation {

    private String componentTypeName;
    private String implementationType;
    private ComponentType componentType;

    
    public NotificationImplementationImpl() {
        // Without this, the loader's resolve is not called
        setUnresolved(true);
    }
    
    public String getComponentTypeName() {
        return componentTypeName;
    }
    
    public void setComponentTypeName(String componentTypeName) {
        this.componentTypeName = componentTypeName;
    }
    
    public String getImplementationType() {
        return implementationType;
    }
    
    public void setImplementationType(String implementationType) {
        this.implementationType = implementationType;
    }
    
    public ComponentType getComponentType() {
        return componentType;
    }
    
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public List<Service> getServices() {
        return componentType.getServices();
    }

    public List<Reference> getReferences() {
        return componentType.getReferences();
    }

    @Override
    public int hashCode() {
        return String.valueOf(getComponentTypeName()).hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        else if (obj instanceof NotificationImplementationImpl &&
            getComponentTypeName().equals(((NotificationImplementationImpl)obj).getComponentTypeName()))
             return true;
        else
            return false;
    }
}
