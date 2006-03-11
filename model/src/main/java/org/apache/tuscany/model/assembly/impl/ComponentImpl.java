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
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;

/**
 * An implementation of Component.
 */
public abstract class ComponentImpl extends AggregatePartImpl implements Component {

    private Map<String, ConfiguredReference> configuredReferencesMap = new HashMap<String, ConfiguredReference>();
    private List<ConfiguredService> configuredServices = new ArrayList<ConfiguredService>();
    private Map<String, ConfiguredService> configuredServicesMap;
    private List<ConfiguredProperty> configuredProperties = new ArrayList<ConfiguredProperty>();
    private Map<String, ConfiguredProperty> configuredPropertiesMap;
    private ComponentImplementation implementation;

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
        implementation = value;
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

    public Map<String, ConfiguredReference> getConfiguredReferences() {
        return configuredReferencesMap;
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
        if (implementation == null) {
            throw new IllegalStateException("No implementation for component [" + getName() + ']');
        }
        implementation.initialize(modelContext);
        ComponentType componentType = implementation.getComponentType();

        // Derive the configured services, references and properties from the component implementation
        //FIXME we have two options here: either just index the configured services, references and properties
        // that we find in the corresponding lists, or derive them from the services, references and properties on
        // the component type, for now just check if the lists are empty or not to determine which option to go with
        configuredServicesMap = new HashMap<String, ConfiguredService>();
        if (configuredServices.isEmpty()) {
            AssemblyFactory factory = modelContext.getAssemblyFactory();
            for (Service service : componentType.getServices()) {
                ConfiguredService configuredService = factory.createConfiguredService();
                configuredService.setPort(service);
                configuredServices.add(configuredService);
                configuredServicesMap.put(service.getName(), configuredService);
                ((ConfiguredPortImpl) configuredService).setAggregatePart(this);
                configuredService.initialize(modelContext);
            }
        } else {

            // Just populate the maps of services, references and properties from the contents of
            // the corresponding lists
            for (ConfiguredService configuredService : configuredServices) {
                configuredServicesMap.put(configuredService.getService().getName(), configuredService);
                ((ConfiguredPortImpl) configuredService).setAggregatePart(this);
                configuredService.initialize(modelContext);
            }

        }

        // Match configured properties to the properties on the component type
        configuredPropertiesMap = new HashMap<String, ConfiguredProperty>(configuredProperties.size());
        for (ConfiguredProperty configuredProperty : configuredProperties) {
            String name = configuredProperty.getName();
            configuredProperty.setProperty(componentType.getProperty(name));
            configuredProperty.initialize(modelContext);
            configuredPropertiesMap.put(name, configuredProperty);
        }

        // Match configured references to the references on the component type
        for (ConfiguredReference configuredReference : configuredReferencesMap.values()) {
            String name = configuredReference.getName();
            ((ConfiguredPortImpl) configuredReference).setAggregatePart(this);
            Reference reference = componentType.getReference(name);
            configuredReference.setReference(reference);
            configuredReference.initialize(modelContext);
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
        configuredServices = Collections.unmodifiableList(configuredServices);
        freeze(configuredServices);
        configuredReferencesMap = Collections.unmodifiableMap(configuredReferencesMap);
        freeze(configuredReferencesMap.values());
        configuredProperties = Collections.unmodifiableList(configuredProperties);
        freeze(configuredProperties);
        if (implementation != null)
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
        if (!accept(configuredReferencesMap.values(), visitor))
            return false;
        if (!accept(configuredProperties, visitor))
            return false;
        if (implementation != null) {
            if (!implementation.accept(visitor))
                return false;
        }

        return true;
    }

}
