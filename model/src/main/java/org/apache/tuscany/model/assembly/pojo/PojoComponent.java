/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.tuscany.model.assembly.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.assembly.Aggregate;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.ConfiguredProperty;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ScopeEnum;

import commonj.sdo.Sequence;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public abstract class PojoComponent implements Component {

    protected boolean frozen;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public PojoComponent() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        check();
        this.name = name;
    }

    private ScopeEnum scope = ScopeEnum.INSTANCE_LITERAL;

    public ScopeEnum getScope() {
        return scope;
    }

    public void setScope(ScopeEnum scope) {
        check();
        this.scope = scope;
    }

    private ComponentImplementation impl;

    public ComponentImplementation getComponentImplementation() {
        return impl;
    }

    public void setComponentImplementation(ComponentImplementation impl) {
        check();
        this.impl = impl;
    }

    private List<ConfiguredProperty> properties = new ArrayList();

    private List<ConfiguredProperty> unModifiableProperties;

    private Map<String, ConfiguredProperty> propertiesMap = new HashMap();

    public List<ConfiguredProperty> getConfiguredProperties() {
        if (frozen) {
            if (unModifiableProperties == null) {
                unModifiableProperties = Collections.unmodifiableList(properties);
            }
            return unModifiableProperties;
        } else {
            return properties;
        }
    }

    public ConfiguredProperty getConfiguredProperty(String propName) {
        return propertiesMap.get(propName);
    }

    public void addConfiguredProperty(ConfiguredProperty prop) {
        check();
        properties.add(prop);
        propertiesMap.put(prop.getProperty().getName(), prop);
    }

    private List<ConfiguredReference> references = new ArrayList();

    private List<ConfiguredReference> unModifiableReferences;

    private Map<String, ConfiguredReference> referencesMap = new HashMap();

    public List<ConfiguredReference> getConfiguredReferences() {
        if (frozen) {
            if (unModifiableReferences == null) {
                unModifiableReferences = Collections.unmodifiableList(references);
            }
            return unModifiableReferences;
        } else {
            return references;
        }
    }

    public void addConfiguredReference(ConfiguredReference ref) {
        check();
        references.add(ref);
        referencesMap.put(ref.getReference().getName(), ref);
    }

    public ConfiguredReference getConfiguredReference(String refName) {
        return referencesMap.get(refName);
    }

    private List<ConfiguredService> configuredServices = new ArrayList();

    private List<ConfiguredService> unModifiableConfigurableServices;

    private Map<String, ConfiguredService> configuredServicesMap = new HashMap();

    public List<ConfiguredService> getConfiguredServices() {
        if (frozen) {
            if (unModifiableConfigurableServices == null) {
                unModifiableConfigurableServices = Collections.unmodifiableList(configuredServices);
            }
            return unModifiableConfigurableServices;
        } else {
            return configuredServices;
        }
    }

    public ConfiguredService getConfiguredService(String serviceName) {
        return configuredServicesMap.get(serviceName);
    }

    public void addConfiguredService(ConfiguredService service) {
        check();
        configuredServices.add(service);
        configuredServicesMap.put(service.getService().getName(), service);
    }

    // FIXME SDO reference
    public Sequence getAny() {
        throw new UnsupportedOperationException();
    }

    // FIXME SDO reference
    public Sequence getAnyAttribute() {
        throw new UnsupportedOperationException();
    }

    private Aggregate aggregate;

    public Aggregate getAggregate() {
        return aggregate;
    }

    public void setAggregate(Aggregate aggregate) {
        check();
        this.aggregate = aggregate;
    }

    public void initialize(AssemblyModelContext modelContext) {
        check();
    }

    public void freeze() {
        frozen = true;
    }

    public boolean accept(AssemblyModelVisitor visitor) {
        if (!visitor.visit(this)) {
            return false;
        }
        if (!impl.accept(visitor)) {
            return false;
        }
        for (ConfiguredProperty property : properties) {
            if (!property.accept(visitor)) {
                return false;
            }
        }
        for (ConfiguredReference reference : references) {
            if (!reference.accept(visitor)) {
                return false;
            }
        }
        for (ConfiguredService service : configuredServices) {
            if (!service.accept(visitor)) {
                return false;
            }
        }
        if (aggregate != null && !aggregate.accept(visitor)) {
            return false;
        }
        return true;
    }

    protected void check() {
        if (frozen == true) {
            throw new IllegalStateException("Attempt to modify a frozen configuration");
        }
    }
}
