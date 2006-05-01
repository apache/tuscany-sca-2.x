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
import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.model.assembly.Composite;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Implementation;
import org.apache.tuscany.model.assembly.ImportWSDL;
import org.apache.tuscany.model.assembly.Multiplicity;
import org.apache.tuscany.model.assembly.OverrideOption;
import org.apache.tuscany.model.assembly.Part;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.ServiceContract;
import org.apache.tuscany.model.assembly.ServiceURI;
import org.apache.tuscany.model.assembly.Wire;
import org.apache.tuscany.model.util.NotifyingList;

/**
 * An implementation of Composite.
 */
public abstract class CompositeImpl extends ExtensibleImpl implements Composite {

    private String name;
    private ComponentInfo componentInfo;
    private Class<?> implementationClass;

    /**
     * A list of parts synchronized with a map
     */
    private class PartList<E extends Part> extends NotifyingList<E> {
        protected void added(E element) {
            partsMap.put(element.getName(), element);
            element.setComposite(CompositeImpl.this);
        }

        protected void removed(E element) {
            partsMap.remove(element.getName());
            element.setComposite(null);
        }
    }

    private Map<String, Part> partsMap = new HashMap<String, Part>();

    private List<Component> components = new PartList<Component>();
    private List<EntryPoint> entryPoints = new PartList<EntryPoint>();
    private List<ExternalService> externalServices = new PartList<ExternalService>();

    private List<Wire> wires = new ArrayList<Wire>();

    /**
     * A list of WSDL imports synchronized with a map
     */
    private class ImportWSDLList extends NotifyingList<ImportWSDL> {
        protected void added(ImportWSDL element) {
            List<ImportWSDL> importList = wsdlImportsMap.get(element.getNamespace());
            if (importList == null) {
                importList = new ArrayList<ImportWSDL>();
                wsdlImportsMap.put(element.getNamespace(), importList);
            }
            importList.add(element);
        }

        protected void removed(ImportWSDL element) {
            List<ImportWSDL> importList = wsdlImportsMap.get(element.getNamespace());
            if (importList != null) {
                importList.remove(element);
                if (importList.isEmpty())
                    wsdlImportsMap.remove(element.getNamespace());
            }
        }
    }

    private Map<String, List<ImportWSDL>> wsdlImportsMap = new HashMap<String, List<ImportWSDL>>();

    private List<ImportWSDL> wsdlImports = new ImportWSDLList();

    protected CompositeImpl() {
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        checkNotFrozen();
        name = newName;
    }

    public List<Component> getComponents() {
        return components;
    }

    public List<EntryPoint> getEntryPoints() {
        return entryPoints;
    }

    public List<ExternalService> getExternalServices() {
        return externalServices;
    }

    public Part getPart(String name) {
        return partsMap.get(name);
    }

    public List<Wire> getWires() {
        return wires;
    }

    public List<ImportWSDL> getWSDLImports() {
        return wsdlImports;
    }

    public List<ImportWSDL> getWSDLImports(String namespace) {
        return wsdlImportsMap.get(namespace);
    }

    public ComponentInfo getComponentInfo() {
        return componentInfo;
    }

    public void setComponentInfo(ComponentInfo componentType) {
        checkNotFrozen();
        this.componentInfo = componentType;
    }

    public ConfiguredService getConfiguredService(ServiceURI address) {
        String partName = address.getPartName();
        String serviceName = address.getServiceName();
        Part part = getPart(partName);
        if (part instanceof Component) {
            Component<?> component = (Component<?>) part;
            if (serviceName != null) {
                return component.getConfiguredService(serviceName);
            } else {
                if (!component.getConfiguredServices().isEmpty()) {
                    return component.getConfiguredServices().get(0);
                } else {
                    return null;
                }
            }

        }
        if (part instanceof ExternalService) {
            ExternalService externalService = (ExternalService) part;
            return externalService.getConfiguredService();
        } else
            return null;
    }

    public void initialize(AssemblyContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);

        // Initialize WSDL imports
        for (ImportWSDL importWSDL : wsdlImports) {
            importWSDL.initialize(modelContext);
        }

        // Initialize parts
        for (Part part : externalServices) {
            part.initialize(modelContext);
        }
        for (Part part : components) {
            part.initialize(modelContext);
        }
        for (Part part : entryPoints) {
            part.initialize(modelContext);
        }

        // Derive the component info from the entry points and external services in the composite
        // Also derive properties from the overridable properties of the components in the composite
        if (componentInfo == null) {
            AssemblyFactory factory = modelContext.getAssemblyFactory();
            componentInfo = factory.createComponentInfo();
            for (EntryPoint entryPoint : getEntryPoints()) {
                Service service = factory.createService();
                service.setName(entryPoint.getName());
                ServiceContract serviceContract = entryPoint.getConfiguredService().getPort().getServiceContract();
                if (serviceContract != null)
                    service.setServiceContract(serviceContract);
                componentInfo.getServices().add(service);

                ConfiguredReference configuredReference = entryPoint.getConfiguredReference();
                ServiceURI sourceURI = factory.createServiceURI(null, entryPoint, configuredReference);
                for (String target : configuredReference.getTargets()) {
                    ServiceURI targetURI = factory.createServiceURI(null, target);
                    Wire wire = factory.createWire();
                    wire.setSource(sourceURI);
                    wire.setTarget(targetURI);
                    getWires().add(wire);
                }
            }
            for (ExternalService externalService : getExternalServices()) {
                if (externalService.getOverrideOption() == null || externalService.getOverrideOption() == OverrideOption.NO)
                    continue;
                Reference reference = factory.createReference();
                reference.setName(externalService.getName());
                ServiceContract serviceContract = externalService.getConfiguredService().getPort().getServiceContract();
                if (serviceContract != null)
                    reference.setServiceContract(serviceContract);
                componentInfo.getReferences().add(reference);
            }
            for (Component<Implementation> component : getComponents()) {
                for (ConfiguredProperty configuredProperty : component.getConfiguredProperties()) {
                    if (configuredProperty.getOverrideOption() == null || configuredProperty.getOverrideOption() == OverrideOption.NO)
                        continue;
                    componentInfo.getProperties().add(configuredProperty.getProperty());
                }

                for (ConfiguredReference configuredReference : component.getConfiguredReferences()) {
                    // Create a wire
                    ServiceURI sourceURI = factory.createServiceURI(null, component, configuredReference);
                    for (String target : configuredReference.getTargets()) {
                        ServiceURI targetURI = factory.createServiceURI(null, target);
                        Wire wire = factory.createWire();
                        wire.setSource(sourceURI);
                        wire.setTarget(targetURI);
                        getWires().add(wire);
                    }
                }
            }
        }
        componentInfo.initialize(modelContext);

        // Wire the module parts
        for (Wire wire : getWires()) {

            // Get the source reference
            ServiceURI sourceURI = wire.getSource();
            ConfiguredReference configuredReference = null;
            String partName = sourceURI.getPartName();
            String referenceName = sourceURI.getServiceName();
            if (referenceName != null) {
                //Component<?> component = (Component<?>)getPart(partName);
//                if (component != null) {
                Part part = getPart(partName);
                if (part instanceof Component) {
                    configuredReference = ((Component) part).getConfiguredReference(referenceName);
                } else if (part instanceof EntryPoint) {
                    configuredReference = ((EntryPoint) part).getConfiguredReference();
                }
            } else {
                EntryPoint entryPoint = (EntryPoint) getPart(partName);
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
                    Multiplicity multiplicity = configuredReference.getPort().getMultiplicity();
                    if (multiplicity == Multiplicity.ZERO_N || multiplicity == Multiplicity.ONE_N) {
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

    public Class<?> getImplementationClass() {
        return implementationClass;
    }

    public void setImplementationClass(Class<?> clazz) {
        checkNotFrozen();
        this.implementationClass = clazz;
    }

    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();

        // Freeze component info
        if (componentInfo != null)
            componentInfo.freeze();

        // Freeze lists
        wsdlImports = freeze(wsdlImports);
        components = freeze(components);
        entryPoints = freeze(entryPoints);
        externalServices = freeze(externalServices);
        wires = freeze(wires);
    }

    public boolean accept(AssemblyVisitor visitor) {
        if (!super.accept(visitor))
            return false;

        if (!accept(wsdlImports, visitor))
            return false;

        if (!accept(partsMap.values(), visitor))
            return false;

        if (!accept(wires, visitor))
            return false;

        if (componentInfo != null) {
            if (!componentInfo.accept(visitor))
                return false;
        }

        return true;
    }

}
