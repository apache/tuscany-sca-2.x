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

import org.apache.tuscany.model.assembly.Aggregate;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;

/**
 * An implementation of the model object '<em><b>Module Component</b></em>'.
 */
public class ModuleComponentImpl extends org.apache.tuscany.model.assembly.sdo.impl.ModuleComponentImpl implements ModuleComponent {
    private List<ConfiguredReference> configuredReferences = new ArrayList<ConfiguredReference>();
    private Map<String, ConfiguredReference> configuredReferencesMap = new HashMap<String, ConfiguredReference>();
    private List<ConfiguredService> configuredServices = new ArrayList<ConfiguredService>();
    private Map<String, ConfiguredService> configuredServicesMap = new HashMap<String, ConfiguredService>();
    private List<ConfiguredProperty> configuredProperties = new ArrayList<ConfiguredProperty>();
    private Map<String, ConfiguredProperty> configuredPropertiesMap = new HashMap<String, ConfiguredProperty>();
    private ComponentImplementation configuredImplementation;

    /**
     * Constructor
     */
    protected ModuleComponentImpl() {
    }

    /**
     * @see org.apache.tuscany.model.assembly.Part#getAggregate()
     */
    public Aggregate getAggregate() {
        return (Aggregate) super.getContainer();
    }

    /**
     * @see org.apache.tuscany.model.assembly.Component#getComponentImplementation()
     */
    public ComponentImplementation getComponentImplementation() {
        return configuredImplementation;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Component#setComponentImplementation(org.apache.tuscany.model.assembly.ComponentImplementation)
     */
    public void setComponentImplementation(ComponentImplementation value) {
        configuredImplementation = value;
    }

    /**
     * @see org.apache.tuscany.model.assembly.ModuleComponent#getModuleImplementation()
     */
    public Module getModuleImplementation() {
        return (Module) getComponentImplementation();
    }

    /**
     * @see org.apache.tuscany.model.assembly.ModuleComponent#setModuleImplementation(org.apache.tuscany.model.assembly.Module)
     */
    public void setModuleImplementation(Module module) {
        setComponentImplementation(module);
    }

    /**
     * @see org.apache.tuscany.model.assembly.ModuleComponent#getURI()
     */
    public String getURI() {
        return super.getUri();
    }

    /**
     * @see org.apache.tuscany.model.assembly.ModuleComponent#setURI(java.lang.String)
     */
    public void setURI(String value) {
        super.setUri(value);
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
     * @see org.apache.tuscany.model.assembly.Component#getConfiguredServices()
     */
    public List<ConfiguredService> getConfiguredServices() {
        return configuredServices;
    }

    /**
     * @see org.apache.tuscany.model.assembly.Component#getConfiguredService(java.lang.String)
     */
    public ConfiguredService getConfiguredService(String name) {
        return configuredServicesMap.get(name);
    }

    /**
     * @see org.apache.tuscany.model.assembly.AssemblyModelObject#initialize(org.apache.tuscany.model.assembly.AssemblyModelContext)
     */
    public void initialize(AssemblyModelContext modelContext) {
        //FIXME Populate lists and maps
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

} //ModuleComponentImpl
