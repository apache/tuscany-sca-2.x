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
package org.apache.tuscany.container.js.assembly.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.common.resource.loader.ResourceLoader;
import org.apache.tuscany.container.js.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.js.rhino.RhinoInvoker;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.impl.AssemblyModelVisitorHelperImpl;
import org.apache.tuscany.model.assembly.impl.ComponentTypeImpl;
import org.apache.tuscany.model.util.XMLResourceFactoryImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.osoa.sca.model.DocumentRoot;

import commonj.sdo.DataObject;

public class JavaScriptImplementationImpl extends org.apache.tuscany.container.js.assembly.sdo.impl.JavaScriptImplementationImpl implements
        JavaScriptImplementation {

    private static final long serialVersionUID = 1L;

    private RhinoInvoker rhinoInvoker;

    private ComponentType componentType;

    private Object runtimeConfiguration;

    private ResourceLoader resourceLoader;

    public JavaScriptImplementationImpl() {
        this.componentType = new ComponentTypeImpl();
    }

    public String getScriptFile() {
        return super.getScriptFile();
    }

    public void setScriptFile(String fn) {
        super.setScriptFile(fn);
    }

    public RhinoInvoker getRhinoInvoker() {
        return rhinoInvoker;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    public void initialize(AssemblyModelContext modelContext) {
        if (getComponentType().getServices().isEmpty()) {
            try {

                this.resourceLoader = modelContext.getResourceLoader();
                this.componentType = createComponentType(modelContext);
                this.rhinoInvoker = createRhinoInvoker();

            } catch (Exception e) {
                throw new IllegalArgumentException("Exception initializing component: " + getComponentName(), e);
                // TODO: throw better exception, should ConfigurationException be unchecked?
            }
        }
    }

    private ComponentType createComponentType(AssemblyModelContext modelContext) throws ConfigurationException {
        //TODO: make .componentType side file optional for JavaScript components
        String scriptFile = getScriptFile();
        String baseName = scriptFile.substring(0, scriptFile.lastIndexOf('.'));
        String ctName = baseName + ".componentType";
        try {
            URL url = modelContext.getResourceLoader().getResource(ctName);
            if (url == null) {
                throw new ConfigurationException("'.componentType' file not found: " + ctName);
            }

            InputStream is = url.openStream();
            try {
                Resource resource = new XMLResourceFactoryImpl().createResource(URI.createURI(getScriptFile()));
                resource.load(is, null);
                DocumentRoot root = (DocumentRoot) resource.getContents().get(0);
                ComponentType componentType = (ComponentType) root.getComponentType();

                componentType.initialize(modelContext);

                return componentType;

            } finally {
                is.close();
            }
        } catch (Exception e) {
            // TODO: .ct file bad interface class name gets WrappedException that never gets unwrapped
            throw new ConfigurationException("Exception initializing '.componentType' file: " + ctName, e);
        }
    }

    private RhinoInvoker createRhinoInvoker() throws ConfigurationException {
        String script = readScript();
        Map<String, Object> context = getPropertyDefaults();
        String name = getComponentName() + ":" + getScriptFile();
        RhinoInvoker ri = new RhinoInvoker(name, script, context);
        return ri;
    }

    private String readScript() throws ConfigurationException {
        try {
            URL url = resourceLoader.getResource(getScriptFile());
            if (url == null) {
                throw new ConfigurationException("script not found: " + getScriptFile());
            }

            InputStream inputStream = url.openStream();
            try {

                StringBuffer sb = new StringBuffer();
                int n = 0;
                while ((n = inputStream.read()) != -1) {
                    sb.append((char) n);
                }
                String s = sb.toString();

                return s;

            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new ConfigurationException("IOException reading script file : " + getScriptFile());
        }
    }

    private String getComponentName() {
        String componentName;
        DataObject o = getContainer();
        if (o == null) {
            componentName = "null";
        } else {
            componentName = ((Component) o).getName();
        }
        return componentName;
    }

    private Map<String, Object> getPropertyDefaults() {
        Map<String, Object> context = new HashMap<String, Object>();
        List<Property> ps = getProperties();
        for (Property p : ps) {
            context.put(p.getName(), p.getDefault());
        }
        return context;
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

}
