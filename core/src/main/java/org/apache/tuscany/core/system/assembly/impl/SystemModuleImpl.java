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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.system.assembly.SystemModule;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Implementation;
import org.apache.tuscany.model.assembly.ModuleFragment;
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.OverrideOption;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.model.assembly.ServiceURI;
import org.apache.tuscany.model.assembly.Wire;
import org.apache.tuscany.model.assembly.impl.CompositeImpl;

/**
 * An implementation of Module.
 */
public class SystemModuleImpl extends CompositeImpl implements SystemModule {
    
    private List<ModuleFragment> moduleFragments = new ArrayList<ModuleFragment>();
    private Map<String, ModuleFragment> moduleFragmentsMap;
    private ComponentInfo componentType;
    private Object contextFactory;
    private Class<?> implementationClass;

    /**
     * Constructor
     */
    protected SystemModuleImpl() {
    }
    
    public Class<?> getImplementationClass() {
        return implementationClass;
    }

    public void setImplementationClass(Class<?> value) {
        checkNotFrozen();
        implementationClass = value;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.Implementation#getComponentInfo()
     */
    public ComponentInfo getComponentInfo() {
        return componentType;
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.Implementation#setComponentInfo(org.apache.tuscany.model.assembly.ComponentInfo)
     */
    public void setComponentInfo(ComponentInfo componentType) {
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
     * @see org.apache.tuscany.model.assembly.AssemblyObject#initialize(org.apache.tuscany.model.assembly.AssemblyContext)
     */
    public void initialize(AssemblyContext modelContext) {
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
        // Also derive properties from the overridable properties of the components in the module
        if (componentType==null) {
            AssemblyFactory factory = modelContext.getAssemblyFactory();
            componentType = factory.createComponentInfo();
            for (EntryPoint entryPoint : getEntryPoints()) {
                Service service = factory.createService();
                service.setName(entryPoint.getName());
                ServiceContract serviceContract = entryPoint.getConfiguredService().getPort().getServiceContract();
                if (serviceContract != null)
                    service.setServiceContract(serviceContract);
                componentType.getServices().add(service);

                ConfiguredReference configuredReference = entryPoint.getConfiguredReference();
                ServiceURI sourceURI = factory.createServiceURI(null, entryPoint, configuredReference);
                for (String target : configuredReference.getTargets()) {
                    ServiceURI targetURI =factory.createServiceURI(null, target);
                    Wire wire=factory.createWire();
                    wire.setSource(sourceURI);
                    wire.setTarget(targetURI);
                    getWires().add(wire);
                }
            }
            for (ExternalService externalService : getExternalServices()) {
                if (externalService.getOverrideOption()==null || externalService.getOverrideOption()== OverrideOption.NO)
                    continue;
                Reference reference = factory.createReference();
                reference.setName(externalService.getName());
                ServiceContract serviceContract = externalService.getConfiguredService().getPort().getServiceContract();
                if (serviceContract != null)
                    reference.setServiceContract(serviceContract);
                componentType.getReferences().add(reference);
            }
            for (Component<Implementation> component : getComponents()) {
                for (ConfiguredProperty configuredProperty : component.getConfiguredProperties()) {
                    if (configuredProperty.getOverrideOption()==null || configuredProperty.getOverrideOption()==OverrideOption.NO)
                        continue;
                    componentType.getProperties().add(configuredProperty.getProperty());
                }

                for (ConfiguredReference configuredReference : component.getConfiguredReferences()) {
                    // Create a wire
                    ServiceURI sourceURI =factory.createServiceURI(null, component, configuredReference);
                    for (String target : configuredReference.getTargets()) {
                        ServiceURI targetURI =factory.createServiceURI(null, target);
                        Wire wire=factory.createWire();
                        wire.setSource(sourceURI);
                        wire.setTarget(targetURI);
                        getWires().add(wire);
                    }
                }
            }
        }
        componentType.initialize(modelContext);


        // Wire the module parts
        for (Wire wire : getWires()) {

            // Get the source reference
            ServiceURI sourceURI=wire.getSource();
            ConfiguredReference configuredReference = null;
            String partName = sourceURI.getPartName();
            String referenceName = sourceURI.getServiceName();
            if (referenceName != null) {
                Component component = (Component)getPart(partName);
                if (component != null) {
                    configuredReference = component.getConfiguredReference(referenceName);
                }
            } else {
                EntryPoint entryPoint = (EntryPoint)getPart(partName);
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
                    Multiplicity multiplicity=configuredReference.getPort().getMultiplicity();
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
     * @see org.apache.tuscany.model.assembly.AssemblyObject#freeze()
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
     * @see org.apache.tuscany.model.assembly.ContextFactoryHolder#getContextFactory()
     */
    public Object getContextFactory() {
        return contextFactory;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ContextFactoryHolder#setContextFactory(java.lang.Object)
     */
    public void setContextFactory(Object configuration) {
        checkNotFrozen();
        this.contextFactory = configuration;
    }

    /**
     * @see org.apache.tuscany.model.assembly.impl.CompositeImpl#accept(org.apache.tuscany.model.assembly.AssemblyVisitor)
     */
    public boolean accept(AssemblyVisitor visitor) {
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
