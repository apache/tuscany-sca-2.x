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
package org.apache.tuscany.model.assembly.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.ModuleFragment;
import org.apache.tuscany.model.assembly.Part;
import org.apache.tuscany.model.assembly.ServiceURI;
import org.apache.tuscany.model.assembly.sdo.impl.ModuleFragmentElementImpl;

/**
 * An implementation of the model object '<em><b>Module</b></em>'.
 */
public class ModuleFragmentImpl extends ModuleFragmentElementImpl implements ModuleFragment {
    private Map<String, Component> componentsMap;
    private Map<String, EntryPoint> entryPointsMap;
    private Map<String, ExternalService> externalServicesMap;
    private List<Part> parts;

    /**
     * Constructor
     */
    protected ModuleFragmentImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.ModuleFragmentElementImpl#getName()
     */
    public String getName() {
        return super.getName();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.ModuleFragmentElementImpl#setName(java.lang.String)
     */
    public void setName(String newName) {
        super.setName(newName);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getComponents()
     */
    public List<Component> getComponents() {
        return super.getComponents();
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getComponent(java.lang.String)
     */
    public Component getComponent(String name) {
        return componentsMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getEntryPoints()
     */
    public List<EntryPoint> getEntryPoints() {
        return super.getEntryPoints();
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getEntryPoint(java.lang.String)
     */
    public EntryPoint getEntryPoint(String name) {
        return entryPointsMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getExternalServices()
     */
    public List<ExternalService> getExternalServices() {
        return super.getExternalServices();
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getExternalService(java.lang.String)
     */
    public ExternalService getExternalService(String name) {
        return externalServicesMap.get(name);
    }

    /**
     * @return Returns the componentsMap.
     */
    protected Map<String, Component> getComponentsMap() {
        return componentsMap;
    }

    /**
     * @return Returns the externalServicesMap.
     */
    protected Map<String, ExternalService> getExternalServicesMap() {
        return externalServicesMap;
    }

    /**
     * @return Returns the entryPointsMap.
     */
    protected Map<String, EntryPoint> getEntryPointsMap() {
        return entryPointsMap;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getParts()
     */
    public List<Part> getParts() {
        return parts;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getConfiguredService(org.apache.tuscany.model.assembly.ServiceURI)
     */
    public ConfiguredService getConfiguredService(ServiceURI address) {
        String partName = address.getPartName();
        String serviceName = address.getServiceName();
        Component component = getComponent(partName);
        if (component != null) {
            if (serviceName != null) {
                return component.getConfiguredService(serviceName);
            } else {
                if (!component.getConfiguredServices().isEmpty()) {
                    return component.getConfiguredServices().get(0);
                } else {
                    return null;
                }
            }

        } else {
            ExternalService externalService = getExternalService(partName);
            if (externalService != null) {
                return externalService.getConfiguredService();
            } else {
                return null;
            }
        }
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        // Populate maps of components, entry points and external services
        componentsMap = new HashMap<String, Component>();
        for (Iterator<Component> i = getComponents().iterator(); i.hasNext();) {
            Component component = i.next();
            componentsMap.put(component.getName(), component);
            component.initialize(modelContext);
        }
        entryPointsMap = new HashMap<String, EntryPoint>();
        for (Iterator<EntryPoint> i = getEntryPoints().iterator(); i.hasNext();) {
            EntryPoint entryPoint = i.next();
            entryPointsMap.put(entryPoint.getName(), entryPoint);
            entryPoint.initialize(modelContext);
        }
        externalServicesMap = new HashMap<String, ExternalService>();
        for (Iterator<ExternalService> i = getExternalServices().iterator(); i.hasNext();) {
            ExternalService externalService = i.next();
            externalServicesMap.put(externalService.getName(), externalService);
            externalService.initialize(modelContext);
        }
        parts = new ArrayList<Part>();
        parts.addAll(getEntryPoints());
        parts.addAll(getComponents());
        parts.addAll(getExternalServices());
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#freeze()
     */
    public void freeze() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        return AssemblyModelVisitorHelperImpl.accept(this, visitor);
    }

} //ModuleImpl
