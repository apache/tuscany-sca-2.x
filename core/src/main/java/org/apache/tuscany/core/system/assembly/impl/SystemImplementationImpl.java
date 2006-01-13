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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;

import org.osoa.sca.ServiceRuntimeException;

import org.apache.tuscany.common.resource.loader.ResourceLoader;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.model.assembly.AssemblyLoader;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.impl.AssemblyModelVisitorHelperImpl;

/**
 * An implementation of the model object '<em><b>Java Implementation</b></em>'.
 */
public class SystemImplementationImpl extends org.apache.tuscany.core.system.assembly.sdo.impl.SystemImplementationImpl implements SystemImplementation {
    private ComponentType componentType;
    private Object runtimeConfiguration;

    public String getClass_() {
        return super.getClass_();
    }

    public void setClass(String value) {
        super.setClass(value);
    }

    public void initialize(AssemblyModelContext modelContext) {
        ResourceLoader resourceLoader = modelContext.getResourceLoader();
        String className = getClass_();

        // Load the component type
        AssemblyLoader assemblyLoader = modelContext.getAssemblyLoader();
        componentType = loadComponentType(assemblyLoader, resourceLoader, className);
        componentType.initialize(modelContext);
    }

    /**
     * Load the component implementation class
     *
     */
    private static ComponentType loadComponentType(final AssemblyLoader assemblyLoader, final ResourceLoader resourceLoader, final String className) {
        try {
            // SECURITY
            return (ComponentType) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    String componentTypeName = className.replace('.', '/') + ".componentType";
                    URL url = resourceLoader.getResource(componentTypeName);
                    if (url==null)
                        throw new FileNotFoundException(componentTypeName);
                    return assemblyLoader.getComponentType(url.toString());
                }
            });
        } catch (PrivilegedActionException e1) {
            throw new ServiceRuntimeException(e1.getException());
        }
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        return AssemblyModelVisitorHelperImpl.accept(this, visitor);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#freeze()
     */
    public void freeze() {
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredRuntimeObject#getRuntimeConfiguration()
     */
    public Object getRuntimeConfiguration() {
        return runtimeConfiguration;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ConfiguredRuntimeObject#setRuntimeConfiguration(java.lang.Object)
     */
    public void setRuntimeConfiguration(Object configuration) {
        this.runtimeConfiguration = configuration;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getProperties()
     */
    public List<org.apache.tuscany.model.assembly.Property> getProperties() {
        return componentType.getProperties();
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getProperty(java.lang.String)
     */
    public org.apache.tuscany.model.assembly.Property getProperty(String name) {
        return componentType.getProperty(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getReference(java.lang.String)
     */
    public Reference getReference(String name) {
        return componentType.getReference(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getReferences()
     */
    public List<Reference> getReferences() {
        return componentType.getReferences();
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getService(java.lang.String)
     */
    public Service getService(String name) {
        return componentType.getService(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.ComponentType#getServices()
     */
    public List<Service> getServices() {
        return componentType.getServices();
    }

} //TExtensionImplementationImpl
