/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.system.assembly.impl;

import java.net.URL;

import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.impl.ComponentImplementationImpl;

/**
 * An implementation of the SystemImplementation.
 */
public class SystemImplementationImpl extends ComponentImplementationImpl implements SystemImplementation {
    
    Class implementationClass;
    
    /**
     * Constructs a new SystemImplementationImpl.
     */
    protected SystemImplementationImpl() {
    }

    /**
     * @see org.apache.tuscany.core.system.assembly.SystemImplementation#getImplementationClass()
     */
    public Class getImplementationClass() {
        return implementationClass;
    }
    
    /**
     * @see org.apache.tuscany.core.system.assembly.SystemImplementation#setImplementationClass(java.lang.Class)
     */
    public void setImplementationClass(Class value) {
        checkNotFrozen();
        implementationClass=value;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (isInitialized())
            return;

        // Initialize the component type
        ComponentType componentType=getComponentType();
        if (componentType==null) {
            componentType=createComponentType(modelContext, implementationClass);
            setComponentType(componentType);
        }
        
        super.initialize(modelContext);
    }

    /**
     * Create the component type
     * @param modelContext
     * @param implementationClass
     */
    private ComponentType createComponentType(AssemblyModelContext modelContext, Class implementationClass) {
        String baseName = getBaseName(implementationClass);
        URL componentTypeFile = implementationClass.getResource(baseName + ".componentType");
        if (componentTypeFile != null) {
            return modelContext.getAssemblyLoader().getComponentType(componentTypeFile.toString());
        } else
            return null;
    }

    /**
     * Returns the simple name of a class - i.e. the class name devoid of its package qualifier
     * @param implClass
     * @return
     */
    private String getBaseName(Class implClass) {
        String baseName = implClass.getName();
        int lastDot = baseName.lastIndexOf('.');
        if (lastDot != -1) {
            baseName = baseName.substring(lastDot + 1);
        }
        return baseName;
    }

}
