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
import commonj.sdo.Sequence;
import org.osoa.sca.model.Implementation;

import org.apache.tuscany.model.assembly.Aggregate;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.SimpleComponent;
import org.apache.tuscany.model.assembly.sdo.impl.ComponentImpl;

/**
 * An implementation of the model object '<em><b>Component</b></em>'.
 */
public class SimpleComponentImpl extends ComponentImpl implements SimpleComponent {
    private List<ConfiguredReference> configuredReferences;
    private Map<String, ConfiguredReference> configuredReferencesMap;
    private List<ConfiguredService> configuredServices;
    private Map<String, ConfiguredService> configuredServicesMap;
    private List<ConfiguredProperty> configuredProperties;
    private Map<String, ConfiguredProperty> configuredPropertiesMap;

    /**
     * Constructor
     */
    protected SimpleComponentImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.ComponentImpl#getName()
     */
    public String getName() {
        return super.getName();
    }

    /**
     * @see org.apache.tuscany.model.assembly.sdo.impl.ComponentImpl#setName(java.lang.String)
     */
    public void setName(String newName) {
        super.setName(newName);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Component#getComponentImplementation()
     */
    public ComponentImplementation getComponentImplementation() {
        return (ComponentImplementation) super.getImplementation();
    }

    /**
     * @see org.apache.tuscany.model.assembly.Component#setComponentImplementation(org.apache.tuscany.model.assembly.ComponentImplementation)
     */
    public void setComponentImplementation(ComponentImplementation value) {
        super.setImplementation((Implementation) value);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Part#getAggregate()
     */
    public Aggregate getAggregate() {
        return (Aggregate) super.getContainer();
    }

    /**
     * @see org.apache.tuscany.model.assembly.Component#getConfiguredServices()
     */
    public List<ConfiguredService> getConfiguredServices() {
        return configuredServices;
    }

    public ConfiguredService getConfiguredService(String name) {
        return configuredServicesMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Component#getConfiguredReferences()
     */
    public List<ConfiguredReference> getConfiguredReferences() {
        return configuredReferences;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Component#getConfiguredReference(java.lang.String)
     */
    public ConfiguredReference getConfiguredReference(String name) {
        return configuredReferencesMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.Component#getConfiguredProperties()
     */
    public List<ConfiguredProperty> getConfiguredProperties() {
        return configuredProperties;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Component#getConfiguredProperty(java.lang.String)
     */
    public ConfiguredProperty getConfiguredProperty(String name) {
        return configuredPropertiesMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        AssemblyFactory factory = modelContext.getAssemblyFactory();

        // Initialize the implementation
        ComponentImplementation implementation = getComponentImplementation();
        if (implementation != null) {
            implementation.initialize(modelContext);
        }

        // Derive the service endpoints from the services on the component type
        configuredServices = new ArrayList<ConfiguredService>();
        configuredServicesMap = new HashMap<String, ConfiguredService>();
        configuredReferences = new ArrayList<ConfiguredReference>();
        configuredReferencesMap = new HashMap<String, ConfiguredReference>();
        configuredProperties = new ArrayList<ConfiguredProperty>();
        configuredPropertiesMap = new HashMap<String, ConfiguredProperty>();
        if (implementation != null) {
            for (Iterator<Service> i = implementation.getServices().iterator(); i.hasNext();) {
                Service service = i.next();
                ConfiguredService serviceEndpoint = factory.createConfiguredService();
                serviceEndpoint.setPort(service);
                serviceEndpoint.setPart(this);
                configuredServices.add(serviceEndpoint);
                configuredServicesMap.put(service.getName(), serviceEndpoint);
                serviceEndpoint.initialize(modelContext);
            }

            // Derive the reference values from the references on the component type
            for (Iterator<Reference> i = implementation.getReferences().iterator(); i.hasNext();) {
                Reference reference = i.next();
                ConfiguredReference referenceValue = factory.createConfiguredReference();
                referenceValue.setPort(reference);
                referenceValue.setPart(this);
                configuredReferences.add(referenceValue);
                configuredReferencesMap.put(reference.getName(), referenceValue);
                referenceValue.initialize(modelContext);
            }

            // Populate property values map
            if (super.getPropertyValues() != null) {
                Sequence sequence = super.getPropertyValues().getAny();
                for (int p = 0, n = sequence.size(); p < n; p++) {

                    // Get each property value element
                    commonj.sdo.Property propertyElementDef = sequence.getProperty(p);
                    DataObject propertyElement = (DataObject) sequence.getValue(p);

                    // Get the corresponding property definition
                    String propertyName = propertyElementDef.getName();
                    Property property = implementation.getProperty(propertyName);
                    if (property == null) {
                        throw new IllegalArgumentException("Undefined property " + propertyName);
                    }

                    // Create a property value object
                    ConfiguredProperty propertyValue = factory.createConfiguredProperty();
                    propertyValue.setComponent(this);
                    propertyValue.setProperty(property);

                    // Get the property value text and convert to the expected java type
                    Sequence text = propertyElement.getSequence(0);
                    if (text != null && text.size() != 0) {
                        Object rawValue = text.getValue(0);
                        propertyValue.setValue(rawValue);
                    }

                    // Add the property value object to the map
                    configuredProperties.add(propertyValue);
                    configuredPropertiesMap.put(propertyName, propertyValue);
                }
            }
        } else {
            // FIXME shouldn't we be throwing an exception here
            // log.error("Component " + getName() + " has no implementation");
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

} //ComponentImpl
