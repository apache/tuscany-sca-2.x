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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleFragment;
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.model.assembly.ServiceURI;
import org.apache.tuscany.model.assembly.Wire;

/**
 * An implementation of Module.
 */
public class ModuleImpl extends AggregateImpl implements Module {
    
    private List<ModuleFragment> moduleFragments = new ArrayList<ModuleFragment>();
    private Map<String, ModuleFragment> moduleFragmentsMap;
    private ComponentType componentType;
    private Object runtimeConfiguration;

    /**
     * Constructor
     */
    protected ModuleImpl() {
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.ComponentImplementation#getComponentType()
     */
    public ComponentType getComponentType() {
        return componentType;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.ComponentImplementation#setComponentType(org.apache.tuscany.model.assembly.ComponentType)
     */
    public void setComponentType(ComponentType componentType) {
        checkNotFrozen();
        this.componentType=componentType;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Module#getModuleFragments()
     */
    public List<ModuleFragment> getModuleFragments() {
        return moduleFragments;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Module#getModuleFragment(java.lang.String)
     */
    public ModuleFragment getModuleFragment(String name) {
        checkInitialized();
        return moduleFragmentsMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (isInitialized())
            return;
        
        // Populate map of module fragments
        moduleFragmentsMap = new HashMap<String, ModuleFragment>();
        for (ModuleFragment moduleFragment : moduleFragments) {
            moduleFragmentsMap.put(moduleFragment.getName(), moduleFragment);
            
            // Add all WSDL imports, components, entry points and external services from the module fragments
            getWSDLImports().addAll(moduleFragment.getWSDLImports());
            getComponents().addAll(moduleFragment.getComponents());
            getEntryPoints().addAll(moduleFragment.getEntryPoints());
            getExternalServices().addAll(moduleFragment.getExternalServices());
            
            // Add all the wires from the module fragments
            getWires().addAll(moduleFragment.getWires());
            
            moduleFragment.initialize(modelContext);
        }
        
        // Initialize the aggregate
        super.initialize(modelContext);

        // Derive the component type from the entry points and external services in the module
        if (componentType==null) {
            AssemblyFactory factory = modelContext.getAssemblyFactory();
            componentType = factory.createComponentType();
            for (EntryPoint entryPoint : getEntryPoints()) {
                Service service = factory.createService();
                service.setName(entryPoint.getName());
                ServiceContract serviceContract = entryPoint.getConfiguredService().getService().getServiceContract();
                if (serviceContract != null)
                    service.setServiceContract(serviceContract);
                componentType.getServices().add(service);
            }
            for (Iterator<ExternalService> i = getExternalServices().iterator(); i.hasNext();) {
                ExternalService externalService = i.next();
                Reference reference = factory.createReference();
                reference.setName(externalService.getName());
                ServiceContract serviceContract = externalService.getConfiguredService().getService().getServiceContract();
                if (serviceContract != null)
                    reference.setServiceContract(serviceContract);
                componentType.getReferences().add(reference);
            }
        }
        componentType.initialize(modelContext);

        //FIXME derive the module properties from the overridable properties of the components in the module

        // Wire the module parts
        for (Wire wire : getWires()) {

            // Get the source reference
            ServiceURI sourceURI=wire.getSource();
            ConfiguredReference configuredReference = null;
            String partName = sourceURI.getPartName();
            String referenceName = sourceURI.getServiceName();
            if (referenceName != null) {
                Component component = getComponent(partName);
                if (component != null) {
                    configuredReference = component.getConfiguredReference(referenceName);
                }
            } else {
                EntryPoint entryPoint = getEntryPoint(partName);
                if (entryPoint != null) {
                    configuredReference = entryPoint.getConfiguredReference();
                }
            }
            if (configuredReference == null) {
                throw new IllegalArgumentException("Cannot find wire source " + sourceURI.getAddress());
            } else {

                // Resolve the target service endpoint
                ServiceURI targetURI = wire.getTarget();
                ConfiguredService configuredService = getConfiguredService(targetURI);
                if (configuredService != null) {

                    // Wire the reference to the target
                    Multiplicity multiplicity=configuredReference.getReference().getMultiplicity();
                    if (multiplicity==Multiplicity.ZERO_N || multiplicity==Multiplicity.ONE_N) {
                        configuredReference.getTargetConfiguredServices().add(configuredService);
                    } else {
                        configuredReference.getTargetConfiguredServices().clear();
                        configuredReference.getTargetConfiguredServices().add(configuredService);
                    }
                } else {
                    throw new IllegalArgumentException("Cannot find service for " + targetURI.getAddress());
                }
            }
        }
    }
        
    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#freeze()
     */
    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();
        
        // Freeze component type and module fragments
        if (componentType!=null)
            componentType.freeze();
        moduleFragments=Collections.unmodifiableList(moduleFragments);
        freeze(moduleFragments);
    }

    /**
     * @see org.apache.tuscany.model.assembly.RuntimeConfigurationHolder#getRuntimeConfiguration()
     */
    public Object getRuntimeConfiguration() {
        return runtimeConfiguration;
    }

    /**
     * @see org.apache.tuscany.model.assembly.RuntimeConfigurationHolder#setRuntimeConfiguration(java.lang.Object)
     */
    public void setRuntimeConfiguration(Object configuration) {
        checkNotFrozen();
        this.runtimeConfiguration = configuration;
    }

    /**
     * @see org.apache.tuscany.model.assembly.impl.AggregateImpl#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        if (!super.accept(visitor))
            return false;
        
        if (componentType!=null) {
            if (!componentType.accept(visitor))
                return false;
        }
        
        if (!accept(moduleFragments, visitor))
            return false;
        
        return true;
    }
    
}
