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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyInitializationException;
import org.apache.tuscany.model.assembly.AssemblyVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentType;
import org.apache.tuscany.model.assembly.ConfiguredPort;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Implementation;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.util.NotifyingList;

/**
 * An implementation of Component.
 */
public abstract class ComponentImpl<I extends Implementation> extends PartImpl implements Component<I> {

    /**
     * A list of configured ports synchronized with a map
     */
    private class ConfiguredPortList<E extends ConfiguredPort> extends NotifyingList<E>{
        
        Map<String, E> map;
        
        private ConfiguredPortList(Map<String, E> map) {
            this.map=map;
        }
        
        protected void added(E element) {
            String name=element.getPort()!=null? element.getPort().getName():element.getName();
            map.put(name, element);
            element.setPart(ComponentImpl.this);
        }
        protected void removed(E element) {
            String name=element.getPort()!=null? element.getPort().getName():element.getName();
            map.remove(name);
            element.setPart(null);
        }
    }
    
    private Map<String, ConfiguredReference> configuredReferencesMap = new HashMap<String, ConfiguredReference>();
    private List<ConfiguredReference> configuredReferences = new ConfiguredPortList<ConfiguredReference>(configuredReferencesMap);
    
    private Map<String, ConfiguredService> configuredServicesMap = new HashMap<String, ConfiguredService>();
    private List<ConfiguredService> configuredServices = new ConfiguredPortList<ConfiguredService>(configuredServicesMap);
    
    /**
     * A list of properties synchronized with a map
     */
    private class ConfiguredPropertyList<E extends ConfiguredProperty> extends NotifyingList<E>{
        
        protected void added(E element) {
            String name=element.getProperty()!=null? element.getProperty().getName():element.getName();
            configuredPropertiesMap.put(name, element);
        }
        protected void removed(E element) {
            String name=element.getProperty()!=null? element.getProperty().getName():element.getName();
            configuredPropertiesMap.remove(name);
        }
    }
    
    private List<ConfiguredProperty> configuredProperties = new ConfiguredPropertyList<ConfiguredProperty>();
    private Map<String, ConfiguredProperty> configuredPropertiesMap=new HashMap<String, ConfiguredProperty>();
    
    private I implementation;

    public I getImplementation() {
        return implementation;
    }

    public void setImplementation(I value) {
        checkNotFrozen();
        implementation = value;
    }

    public List<ConfiguredService> getConfiguredServices() {
        return configuredServices;
    }

    public ConfiguredService getConfiguredService(String name) {
        return configuredServicesMap.get(name);
    }

    public List<ConfiguredReference> getConfiguredReferences() {
        return configuredReferences;
    }

    public ConfiguredReference getConfiguredReference(String name) {
        return configuredReferencesMap.get(name);
    }

    public List<ConfiguredProperty> getConfiguredProperties() {
        return configuredProperties;
    }

    public ConfiguredProperty getConfiguredProperty(String name) {
        return configuredPropertiesMap.get(name);
    }

    public void initialize(AssemblyContext modelContext) {
        if (isInitialized())
            return;
        super.initialize(modelContext);

        // Initialize the implementation
        if (implementation == null) {
            throw new AssemblyInitializationException("No implementation for component [" + getName() + ']');
        }
        implementation.initialize(modelContext);

        // Derive the configured services from the component implementation
        ComponentType componentType=implementation.getComponentType();
        AssemblyFactory factory = modelContext.getAssemblyFactory();
        for (Service service : componentType.getServices()) {
            ConfiguredService configuredService = factory.createConfiguredService();
            configuredService.setPort(service);
            configuredServices.add(configuredService);
            configuredService.initialize(modelContext);
        }

        // Derive the configured references from the references on the component info
        for (Reference reference : componentType.getReferences()) {
            ConfiguredReference configuredReference = configuredReferencesMap.get(reference.getName());
            if (configuredReference==null) {
                configuredReference=factory.createConfiguredReference();
                configuredReference.setPort(reference);
                configuredReferences.add(configuredReference);
                configuredReference.initialize(modelContext);
            } else {
                configuredReference.setPort(reference);
                configuredReference.initialize(modelContext);
            }
        }
        for (ConfiguredReference configuredReference : configuredReferences) {
            if (configuredReference.getPort()==null) {
                throw new AssemblyInitializationException("Undefined reference ["+configuredReference.getName()+"]");
            }
        }

        // Derive the configured properties from the properties on the component info
        for (Property property : componentType.getProperties()) {
            ConfiguredProperty configuredProperty = configuredPropertiesMap.get(property.getName());
            if (configuredProperty != null) {
                configuredProperty.setProperty(property);
                configuredProperty.initialize(modelContext);
            }
        }
        for (ConfiguredProperty configuredProperty : configuredProperties) {
            if (configuredProperty.getProperty()==null) {
                throw new AssemblyInitializationException("Undefined property ["+configuredProperty.getName()+"]");
            }
        }

    }

    public void freeze() {
        if (isFrozen())
            return;
        super.freeze();

        // Freeze configured services, references and properties
        configuredServices = freeze(configuredServices);
        configuredReferences = freeze(configuredReferences);
        configuredProperties = freeze(configuredProperties);
        if (implementation != null)
            implementation.freeze();
    }

    public boolean accept(AssemblyVisitor visitor) {
        if (!super.accept(visitor))
            return false;

        if (!accept(configuredServices, visitor))
            return false;
        if (!accept(configuredReferences, visitor))
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
