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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.tuscany.container.java.assembly.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.config.Java5ComponentTypeIntrospector;
import org.apache.tuscany.core.config.ComponentTypeIntrospector;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.impl.AssemblyModelVisitorHelperImpl;
import org.apache.tuscany.model.assembly.impl.ComponentTypeImpl;
import org.apache.tuscany.model.util.XMLResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.osoa.sca.model.DocumentRoot;

/**
 * An implementation of the model object '<em><b>Java Implementation</b></em>'.
 */
public class JavaImplementationImpl extends org.apache.tuscany.container.java.assembly.sdo.impl.JavaImplementationImpl implements JavaImplementation {

    private ComponentType componentType;
    private Object runtimeConfiguration;

    /**
     * Constructor
     */
    protected JavaImplementationImpl() {
        componentType = new ComponentTypeImpl();
    }

    /**
     * @see org.apache.tuscany.container.java.assembly.JavaImplementation#getClass_()
     */
    public String getClass_() {
        return super.getClass_();
    }

    /**
     * @see org.apache.tuscany.container.java.assembly.JavaImplementation#setClass(java.lang.String)
     */
    public void setClass(String value) {
        super.setClass(value);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (componentType.getServices().isEmpty()) {
            Class implClass;
            try {
                implClass = JavaIntrospectionHelper.loadClass(getClass_());
            } catch (ClassNotFoundException e) {
                // todo anything better to throw?
                throw new IllegalArgumentException("Component implementation class not found: " + getClass_());
            }
            createComponentType(modelContext, implClass);
        }
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        return AssemblyModelVisitorHelperImpl.accept(this, visitor);
    }

    /**
     * @see org.apache.tuscany.model.assembly.RuntimeManagedModelObject#getRuntimeConfiguration()
     */
    public Object getRuntimeConfiguration() {
        return runtimeConfiguration;
    }

    /**
     * @see org.apache.tuscany.model.assembly.RuntimeManagedModelObject#setRuntimeConfiguration(java.lang.Object)
     */
    public void setRuntimeConfiguration(Object configuration) {
        this.runtimeConfiguration = configuration;
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#freeze()
     */
    public void freeze() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.TImplementation#setComponentType(org.apache.tuscany.model.assembly.ComponentType)
     */
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    /**
     * @see org.apache.tuscany.model.assembly.TImplementation#getComponentType()
     */
    public ComponentType getComponentType() {
        return componentType;
    }

    private void createComponentType(AssemblyModelContext modelContext, Class implClass) {
        String baseName = JavaIntrospectionHelper.getBaseName(implClass);
        InputStream sideFile = implClass.getResourceAsStream(baseName + ".componentType");
        if (sideFile != null) {
            try {
                // todo load the sidefile using EMF - should this be using SDO?
                Resource res = new XMLResourceFactoryImpl().createResource(URI.createURI(implClass.getName()));
                res.load(sideFile, null);
                DocumentRoot root = (DocumentRoot) res.getContents().get(0);
                componentType = (ComponentType) root.getComponentType();
                componentType.initialize(modelContext);
            } catch (IOException e) {
                throw new WrappedException(e);
            } finally {
                try {
                    sideFile.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        } else {
            JavaAssemblyFactory factory = new JavaAssemblyFactoryImpl();
            ComponentTypeIntrospector introspector = new Java5ComponentTypeIntrospector(factory);
            try {
                componentType = introspector.introspect(implClass);
                componentType.initialize(modelContext);
            } catch (ConfigurationException e) {
                throw new IllegalArgumentException("Unable to introspect implementation class: " + implClass.getName(), e);
            }
        }
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

} //JavaImplementationImpl
