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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Import;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.model.assembly.Aggregate;
import org.apache.tuscany.model.assembly.AggregatePart;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.ServiceURI;
import org.apache.tuscany.model.assembly.Wire;
import org.apache.tuscany.sdo.util.SDOUtil;

import commonj.sdo.helper.XSDHelper;

/**
 * An implementation of Aggregate.
 */
public abstract class AggregateImpl extends ExtensibleImpl implements Aggregate {
    
    private String name;
    private List<Component> components=new ArrayList<Component>();
    private Map<String, Component> componentsMap;
    private List<EntryPoint> entryPoints=new ArrayList<EntryPoint>();
    private Map<String, EntryPoint> entryPointsMap;
    private List<ExternalService> externalServices=new ArrayList<ExternalService>();
    private Map<String, ExternalService> externalServicesMap;
    private List<AggregatePart> aggregateParts;
    private List<Wire> wires=new ArrayList<Wire>();
    private List<Import> wsdlImports=new ArrayList<Import>();
    private Map<String, List<Import>> wsdlImportsMap;
    private AssemblyModelContext modelContext;

    /**
     * Constructor
     */
    protected AggregateImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#setName(java.lang.String)
     */
    public void setName(String newName) {
        checkNotFrozen();
        name=newName;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getComponents()
     */
    public List<Component> getComponents() {
        return components;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getComponent(java.lang.String)
     */
    public Component getComponent(String name) {
        checkInitialized();
        return componentsMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getEntryPoints()
     */
    public List<EntryPoint> getEntryPoints() {
        return entryPoints;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getEntryPoint(java.lang.String)
     */
    public EntryPoint getEntryPoint(String name) {
        checkInitialized();
        return entryPointsMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getExternalServices()
     */
    public List<ExternalService> getExternalServices() {
        return externalServices;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getExternalService(java.lang.String)
     */
    public ExternalService getExternalService(String name) {
        checkInitialized();
        return externalServicesMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getAggregateParts()
     */
    public List<AggregatePart> getAggregateParts() {
        checkInitialized();
        return aggregateParts;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getWires()
     */
    public List<Wire> getWires() {
        return wires;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getWSDLImports()
     */
    public List<Import> getWSDLImports() {
        return wsdlImports;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getWSDLImports(java.lang.String)
     */
    public List<Import> getWSDLImports(String namespace) {
        checkInitialized();
        return wsdlImportsMap.get(namespace);
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.Aggregate#getAssemblyModelContext()
     */
    public AssemblyModelContext getAssemblyModelContext() {
        checkInitialized();
        return modelContext;
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
        if (isInitialized())
            return;
        super.initialize(modelContext);
        
        // Save the model context
        this.modelContext=modelContext;
        
        // Populate map of WSDL imports
        ResourceLoader resourceLoader=modelContext.getApplicationResourceLoader();
        wsdlImportsMap = new HashMap<String, List<Import>>();
        for (Import wsdlImport : wsdlImports) {
            String namespace=wsdlImport.getNamespaceURI();
            List<Import> list=wsdlImportsMap.get(namespace);
            if (list==null) {
                list=new ArrayList<Import>();
                wsdlImportsMap.put(namespace, list);
            }
            list.add(wsdlImport);
            
            // Load the WSDL definition if necessary
            if (wsdlImport.getDefinition()==null) {
                String location=wsdlImport.getLocationURI();
                Definition definition;
                ClassLoader ccl=Thread.currentThread().getContextClassLoader();
                try {
                    URL url=resourceLoader.getResource(location);
                    if (url==null)
                        throw new IllegalArgumentException("Cannot find "+location);
                    definition = modelContext.getAssemblyLoader().loadDefinition(url.toString());
                    Thread.currentThread().setContextClassLoader(modelContext.getApplicationResourceLoader().getClassLoader());
                    XSDHelper xsdHelper=SDOUtil.createXSDHelper(modelContext.getTypeHelper());
                    xsdHelper.define (url.openStream(), null);
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                } finally {
                    Thread.currentThread().setContextClassLoader(ccl);
                }
                wsdlImport.setDefinition(definition);
            }
        }

        // Populate maps of components, entry points and external services
        aggregateParts = new ArrayList<AggregatePart>();
        componentsMap = new HashMap<String, Component>();
        for (Component component : components) {
            componentsMap.put(component.getName(), component);
            aggregateParts.add(component);
            component.initialize(modelContext);
            ((AggregatePartImpl)component).setAggregate(this);
        }
        entryPointsMap = new HashMap<String, EntryPoint>();
        for (EntryPoint entryPoint : entryPoints) {
            entryPointsMap.put(entryPoint.getName(), entryPoint);
            aggregateParts.add(entryPoint);
            entryPoint.initialize(modelContext);
            ((AggregatePartImpl)entryPoint).setAggregate(this);
        }
        externalServicesMap = new HashMap<String, ExternalService>();
        for (ExternalService externalService : externalServices) {
            externalServicesMap.put(externalService.getName(), externalService);
            aggregateParts.add(externalService);
            externalService.initialize(modelContext);
            ((AggregatePartImpl)externalService).setAggregate(this);
        }
        for (Wire wire : wires) {
            wire.initialize(modelContext);
        }
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#freeze()
     */
    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();
        
        // Freeze lists
        wsdlImports=Collections.unmodifiableList(wsdlImports);
        freeze(wsdlImports);
        components=Collections.unmodifiableList(components);
        freeze(components);
        entryPoints=Collections.unmodifiableList(entryPoints);
        freeze(entryPoints);
        externalServices=Collections.unmodifiableList(externalServices);
        freeze(externalServices);
        wires=Collections.unmodifiableList(wires);
        freeze(wires);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        if (!super.accept(visitor))
            return false;
        
        if (!accept(aggregateParts, visitor))
            return false;
        
        if (!accept(wires, visitor))
            return false;
        
        return true;
    }

} //ModuleImpl
