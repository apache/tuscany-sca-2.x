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
import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;

/**
 * An implementation of Component.
 */
public class ComponentImpl extends AggregatePartImpl implements Component {
    
    private List<ConfiguredReference> configuredReferences=new ArrayList<ConfiguredReference>();
    private Map<String, ConfiguredReference> configuredReferencesMap;
    private List<ConfiguredService> configuredServices=new ArrayList<ConfiguredService>();
    private Map<String, ConfiguredService> configuredServicesMap;
    private List<ConfiguredProperty> configuredProperties=new ArrayList<ConfiguredProperty>();
    private Map<String, ConfiguredProperty> configuredPropertiesMap;
    private ComponentImplementation implementation;

    /**
     * Constructor
     */
    protected ComponentImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.Component#getComponentImplementation()
     */
    public ComponentImplementation getComponentImplementation() {
        return implementation;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Component#setComponentImplementation(org.apache.tuscany.model.assembly.ComponentImplementation)
     */
    public void setComponentImplementation(ComponentImplementation value) {
        checkNotFrozen();
        implementation=value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Component#getConfiguredServices()
     */
    public List<ConfiguredService> getConfiguredServices() {
        return configuredServices;
    }

    public ConfiguredService getConfiguredService(String name) {
        checkInitialized();
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
        checkInitialized();
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
        checkInitialized();
        return configuredPropertiesMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);

        // Initialize the implementation
        ComponentImplementation implementation = getComponentImplementation();
        if (implementation != null) {
            implementation.initialize(modelContext);
        }

        // Derive the configured services, references and properties from the component implementation
        //FIXME we have two options here: either just index the configured services, references and properties
        // that we find in the corresponding lists, or derive them from the services, references and properties on
        // the component type, for now just check if the lists are empty or not to determine which option to go with
        configuredServicesMap = new HashMap<String, ConfiguredService>();
        configuredReferencesMap = new HashMap<String, ConfiguredReference>();
        configuredPropertiesMap = new HashMap<String, ConfiguredProperty>();
        if (configuredServices.isEmpty() && configuredReferences.isEmpty() && configuredProperties.isEmpty()) {
            if (implementation != null) {
                AssemblyFactory factory = modelContext.getAssemblyFactory();
                for (Service service : implementation.getComponentType().getServices()) {
                    ConfiguredService configuredService = factory.createConfiguredService();
                    configuredService.setPort(service);
                    configuredServices.add(configuredService);
                    configuredServicesMap.put(service.getName(), configuredService);
                    ((ConfiguredPortImpl)configuredService).setAggregatePart(this);
                    configuredService.initialize(modelContext);
                }
    
                for (Reference reference : implementation.getComponentType().getReferences()) {
                    ConfiguredReference configuredReference = factory.createConfiguredReference();
                    configuredReference.setPort(reference);
                    configuredReferences.add(configuredReference);
                    configuredReferencesMap.put(reference.getName(), configuredReference);
                    ((ConfiguredPortImpl)configuredReference).setAggregatePart(this);
                    configuredReference.initialize(modelContext);
                }
    
                // Derive configured properties from the properties on the component type 
                //FIXME
    //            if (super.getPropertyValues() != null) {
    //                Sequence sequence = super.getPropertyValues().getAny();
    //                for (int p = 0, n = sequence.size(); p < n; p++) {
    //
    //                    // Get each property value element
    //                    commonj.sdo.Property propertyElementDef = sequence.getProperty(p);
    //                    DataObject propertyElement = (DataObject) sequence.getValue(p);
    //
    //                    // Get the corresponding property definition
    //                    String propertyName = propertyElementDef.getName();
    //                    Property property = implementation.getProperty(propertyName);
    //                    if (property == null) {
    //                        throw new IllegalArgumentException("Undefined property " + propertyName);
    //                    }
    //
    //                    // Create a property value object
    //                    ConfiguredProperty propertyValue = factory.createConfiguredProperty();
    //                    propertyValue.setComponent(this);
    //                    propertyValue.setProperty(property);
    //
    //                    // Get the property value text and convert to the expected java type
    //                    Sequence text = propertyElement.getSequence(0);
    //                    if (text != null && text.size() != 0) {
    //                        Object rawValue = text.getValue(0);
    //                        propertyValue.setValue(rawValue);
    //                    }
    //
    //                    // Add the property value object to the map
    //                    configuredProperties.add(propertyValue);
    //                    configuredPropertiesMap.put(propertyName, propertyValue);
    //                }
    //            }
                
            }
        } else {
            
            // Just populate the maps of services, references and properties from the contents of
            // the corresponding lists
            for (ConfiguredService configuredService : configuredServices) {
                configuredServicesMap.put(configuredService.getService().getName(), configuredService);
                ((ConfiguredPortImpl)configuredService).setAggregatePart(this);
                configuredService.initialize(modelContext);
            }

            for (ConfiguredReference configuredReference : configuredReferences) {
                configuredReferencesMap.put(configuredReference.getReference().getName(), configuredReference);
                ((ConfiguredPortImpl)configuredReference).setAggregatePart(this);
                configuredReference.initialize(modelContext);
            }

            for (ConfiguredProperty configuredProperty : configuredProperties) {
                configuredPropertiesMap.put(configuredProperty.getProperty().getName(), configuredProperty);
                configuredProperty.initialize(modelContext);
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
        
        // Freeze configured services, references and properties
        configuredServices=Collections.unmodifiableList(configuredServices);
        freeze(configuredServices);
        configuredReferences=Collections.unmodifiableList(configuredReferences);
        freeze(configuredReferences);
        configuredProperties=Collections.unmodifiableList(configuredProperties);
        freeze(configuredProperties);
        if (implementation!=null)
            implementation.freeze();
    }
    
    /**
     * @see org.apache.tuscany.model.assembly.impl.AssemblyModelObjectImpl#accept(org.apache.tuscany.model.assembly.AssemblyModelVisitor)
     */
    public boolean accept(AssemblyModelVisitor visitor) {
        if (!super.accept(visitor))
            return false;
        
        if (!accept(configuredServices, visitor))
            return false;
        if (!accept(configuredReferences, visitor))
            return false;
        if (!accept(configuredProperties, visitor))
            return false;
        if (implementation!=null) {
            if (!implementation.accept(visitor))
                return false;
        }
        
        return true;
    }

}
