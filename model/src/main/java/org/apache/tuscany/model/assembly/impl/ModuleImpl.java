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

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import org.eclipse.emf.ecore.sdo.EDataObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.osoa.sca.model.ModuleWire;
import org.osoa.sca.model.ReferenceValues;

import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Interface;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleFragment;
import org.apache.tuscany.model.assembly.Part;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceURI;

/**
 * An implementation of the model object '<em><b>Module</b></em>'.
 */
public class ModuleImpl extends org.apache.tuscany.model.assembly.sdo.impl.ModuleImpl implements Module {
    private List<ModuleFragment> moduleFragments = new ArrayList<ModuleFragment>();
    private Map<String, ModuleFragment> moduleFragmentsMap;
    private Map<String, Component> componentsMap;
    private Map<String, EntryPoint> entryPointsMap;
    private Map<String, ExternalService> externalServicesMap;
    private List<Part> parts;
    private ComponentType componentType;
    private Object runtimeConfiguration;

    /**
     * Constructor
     */
    protected ModuleImpl() {
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
     * @see org.apache.tuscany.model.assembly.Module#getModuleFragments()
     */
    public List<ModuleFragment> getModuleFragments() {
        return moduleFragments;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Module#getModuleFragment(java.lang.String)
     */
    public ModuleFragment getModuleFragment(String name) {
        return moduleFragmentsMap.get(name);
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
        moduleFragmentsMap = new HashMap<String, ModuleFragment>();
        for (Iterator<ModuleFragment> i = getModuleFragments().iterator(); i.hasNext();) {
            ModuleFragmentImpl moduleFragment = (ModuleFragmentImpl) i.next();
            moduleFragmentsMap.put(moduleFragment.getName(), moduleFragment);
            componentsMap.putAll(moduleFragment.getComponentsMap());
            entryPointsMap.putAll(moduleFragment.getEntryPointsMap());
            externalServicesMap.putAll(moduleFragment.getExternalServicesMap());
        }
        parts = new ArrayList<Part>();
        parts.addAll(getEntryPoints());
        parts.addAll(getComponents());
        parts.addAll(getExternalServices());

        //FIXME derive the module properties from the overridable properties of the components in the module

        // Derive the component type from the entry points and external services in the module
        if (componentType == null) {
            AssemblyFactory factory = modelContext.getAssemblyFactory();
            componentType = factory.createComponentType();
            for (Iterator<EntryPoint> i = getEntryPoints().iterator(); i.hasNext();) {
                EntryPoint entryPoint = i.next();
                Service service = factory.createService();
                service.setName(entryPoint.getName());
                Interface iface = entryPoint.getInterfaceContract();
                if (iface != null)
                    service.setInterfaceContract((Interface) EcoreUtil.copy((EDataObject) iface));
                componentType.getServices().add(service);
            }
            for (Iterator<ExternalService> i = getExternalServices().iterator(); i.hasNext();) {
                ExternalService externalService = i.next();
                Reference reference = factory.createReference();
                reference.setName(externalService.getName());
                Interface iface = externalService.getInterfaceContract();
                if (iface != null)
                    reference.setInterfaceContract((Interface) EcoreUtil.copy((EDataObject) iface));
                componentType.getReferences().add(reference);
            }
            componentType.initialize(modelContext);
        }

        // Resolve the references and the wires
        AssemblyFactory factory = modelContext.getAssemblyFactory();

        // Resolve entry point references
        for (Iterator<EntryPoint> i = entryPointsMap.values().iterator(); i.hasNext();) {
            EntryPoint entryPoint = i.next();
            ConfiguredReference configuredReference = entryPoint.getConfiguredReference();
            for (Iterator<DataObject> r = ((org.osoa.sca.model.EntryPoint) entryPoint).getReferences().iterator(); r.hasNext();) {
                DataObject targetURIElement = r.next();
                ConfiguredService configuredService = resolveURIElement(factory, targetURIElement);
                if (configuredService != null) {
                    if (!configuredReference.getReference().isMultiplicityN() && !configuredReference.getConfiguredServices().isEmpty()) {
                        // FIXME shouldn't we be throwing an exception here
                        // log.error("Attempting to wire multiple targets to reference " + configuredReference.getReference().getName());
                    } else {
                        configuredReference.getConfiguredServices().add(configuredService);
                    }
                }
            }
        }

        // Resolve component references
        for (Iterator<Component> i = componentsMap.values().iterator(); i.hasNext();) {
            Component component = i.next();
            ReferenceValues referenceValues = ((org.osoa.sca.model.Component) component).getReferenceValues();
            if (referenceValues == null)
                continue;
            Sequence sequence = referenceValues.getAny();
            for (int p = 0, n = sequence.size(); p < n; p++) {
                Property property = sequence.getProperty(p);
                DataObject targetURIElement = (DataObject) sequence.getValue(p);

                // Get the named reference
                ConfiguredReference configuredReference = component.getConfiguredReference(property.getName());
                if (configuredReference != null) {
                    ConfiguredService configuredService = resolveURIElement(factory, targetURIElement);
                    if (configuredService != null) {
                        if (!configuredReference.getReference().isMultiplicityN() && !configuredReference.getConfiguredServices().isEmpty()) {
                            // FIXME shouldn't we be throwing an exception here
                            // log.error("Attempting to wire multiple targets to reference " + configuredReference.getReference().getName());
                        } else {
                            configuredReference.getConfiguredServices().add(configuredService);
                        }
                    }
                } else {
                    // FIXME shouldn't we be throwing an exception here
                    // log.error("Undefined reference " + property.getName());
                }
            }
        }

        // Resolve wires from this module and its module fragments
        resolveWires(factory, getWires());
        for (Iterator<ModuleFragment> i = getModuleFragments().iterator(); i.hasNext();) {
            ModuleFragment moduleFragment = i.next();
            resolveWires(factory, ((org.osoa.sca.model.ModuleFragment) moduleFragment).getWires());
        }
    }

    /**
     * Resolve a target URI
     *
     * @param factory
     * @param targetURIElement
     */
    private ConfiguredService resolveURIElement(AssemblyFactory factory, DataObject targetURIElement) {
        Sequence sequence = targetURIElement.getSequence(0);
        String targetURI = (String) sequence.getValue(0);
        ServiceURI serviceURI = factory.createServiceURI(null, targetURI);
        ConfiguredService configuredService = getConfiguredService(serviceURI);
        if (configuredService == null) {
            // FIXME shouldn't we be throwing an exception here
            // log.error("Cannot find service for " + targetURI);
        }
        return configuredService;
    }

    /**
     * Resolve the given wires
     *
     * @param factory
     * @param wires
     */
    private void resolveWires(AssemblyFactory factory, List<ModuleWire> wires) {

        // Loop through the wires
        for (Iterator<ModuleWire> i = wires.iterator(); i.hasNext();) {
            ModuleWire wire = i.next();

            // Get the source reference
            ServiceURI sourceURI = factory.createServiceURI(null, wire.getSourceUri());
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
                // FIXME shouldn't we be throwing an exception here
                // log.error("Cannot find wire source " + sourceURI);
            } else {

                // Resolve the target service endpoint
                ServiceURI targetURI = factory.createServiceURI(null, wire.getTargetUri());
                ConfiguredService configuredService = getConfiguredService(targetURI);
                if (configuredService != null) {

                    // Wire the reference to the target
                    if (configuredReference.getReference().isMultiplicityN()) {
                        configuredReference.getConfiguredServices().add(configuredService);
                    } else {
                        configuredReference.getConfiguredServices().clear();
                        configuredReference.getConfiguredServices().add(configuredService);
                    }
                } else {
                    // FIXME shouldn't we be throwing an exception here
                    // log.error("Cannot find service for " + targetURI.getAddress());
                }
            }
        }
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

} //ModuleImpl
