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
package org.apache.tuscany.container.java.assembly.impl;

import java.net.URL;

import org.apache.tuscany.container.java.assembly.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.config.Java5ComponentTypeIntrospector;
import org.apache.tuscany.core.config.ComponentTypeIntrospector;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.impl.ComponentImplementationImpl;

/**
 * An implementation of JavaImplementation.
 */
public class JavaImplementationImpl extends ComponentImplementationImpl implements JavaImplementation {

    private Class implementationClass;

    /**
     * Constructor
     */
    protected JavaImplementationImpl() {
    }

    /**
     * @see org.apache.tuscany.container.java.assembly.JavaImplementation#getImplementationClass()
     */
    public Class getImplementationClass() {
        return implementationClass;
    }

    /**
     * @see org.apache.tuscany.container.java.assembly.JavaImplementation#setImplementationClass(java.lang.Class)
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
    private ComponentType createComponentType(AssemblyModelContext modelContext, Class implClass) {
        String baseName = JavaIntrospectionHelper.getBaseName(implClass);
        URL componentTypeFile = implClass.getResource(baseName + ".componentType");
        if (componentTypeFile != null) {
            return modelContext.getAssemblyLoader().getComponentType(componentTypeFile.toString());
        } else {
            JavaAssemblyFactory factory = new JavaAssemblyFactoryImpl();
            ComponentTypeIntrospector introspector = new Java5ComponentTypeIntrospector(factory);
            try {
                return introspector.introspect(implClass);
            } catch (ConfigurationException e) {
                throw new IllegalArgumentException("Unable to introspect implementation class: " + implClass.getName(), e);
            }
        }
    }

 }
