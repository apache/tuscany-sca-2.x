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
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.model.assembly.impl.ComponentImplementationImpl;

/**
 * The default implementation of the system implementation assembly artifact
 * 
 * @version $Rev$ $Date$
 */
public class SystemImplementationImpl extends ComponentImplementationImpl implements SystemImplementation {

    private Class implementationClass;
    
    private AssemblyModelContext modelContext;
    
    protected SystemImplementationImpl() {
    }

    public Class getImplementationClass() {
        return implementationClass;
    }

    public void setImplementationClass(Class value) {
        checkNotFrozen();
        implementationClass = value;
    }

    public void initialize(AssemblyModelContext context) {
        if (isInitialized())
            return;
        this.modelContext = context;
        // Initialize the component type
        ComponentType componentType = getComponentType();
        if (componentType == null) {
            componentType = createComponentType(implementationClass);
            setComponentType(componentType);
        }
        super.initialize(modelContext);
    }

    /**
     * Creates the component type
     */
    private ComponentType createComponentType(Class implClass) {
        String baseName = getBaseName(implClass);
        URL componentTypeFile = implClass.getResource(baseName + ".componentType");
        if (componentTypeFile != null) {
            return modelContext.getAssemblyLoader().getComponentType(componentTypeFile.toString());
        } else {
            //FIXME Return a made-up component type for now
            // We need to introspect the component implementation class, support a subset of what
            // we support for java components.
            AssemblyFactory factory=modelContext.getAssemblyFactory();
            ComponentType componentType=factory.createComponentType();
            Service service=factory.createService();
            ServiceContract serviceContract=factory.createJavaServiceContract();
            serviceContract.setScope(Scope.MODULE);
            service.setServiceContract(serviceContract);
            componentType.getServices().add(service);
            return componentType;
        }
    }

    /**
     * Returns the simple name of a class - i.e. the class name devoid of its package qualifier
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
