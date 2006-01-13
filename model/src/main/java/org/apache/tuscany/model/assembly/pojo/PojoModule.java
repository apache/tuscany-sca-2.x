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

import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleFragment;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;

/**
 * 
 * 
 * @version $Rev$ $Date$
 */
public class PojoModule extends PojoAggregate implements Module {

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public PojoModule() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    private List<ModuleFragment> moduleFragments = new ArrayList();

    private Map<String, ModuleFragment> moduleFragmentMap = new HashMap();

    public List<ModuleFragment> getModuleFragments() {
        return moduleFragments;
    }

    public ModuleFragment getModuleFragment(String name) {
        return moduleFragmentMap.get(name);
    }

    public void addModuleFragment(ModuleFragment fragment) {
        check();
        moduleFragments.add(fragment);
        moduleFragmentMap.put(fragment.getName(), fragment);
    }

    private List<Service> services = new ArrayList();

    private List<Service> unModifiableServices;

    private Map<String, Service> serviceMap = new HashMap();

    public List<Service> getServices() {
        if (frozen) {
            if (unModifiableServices == null) {
                unModifiableServices = Collections.unmodifiableList(services);
            }
            return unModifiableServices;
        } else {
            return services;
        }
    }

    public Service getService(String name) {
        return serviceMap.get(name);
    }

    public void addService(Service service) {
        check();
        services.add(service);
        serviceMap.put(service.getName(), service);
    }

    private List<Reference> references = new ArrayList();

    private List<Reference> unModifiableRefererences;

    private Map<String, Reference> referenceMap = new HashMap();

    public List<Reference> getReferences() {
        if (frozen) {
            if (unModifiableRefererences == null) {
                unModifiableRefererences = Collections.unmodifiableList(references);
            }
            return unModifiableRefererences;
        } else {
            return references;
        }
    }

    public Reference getReference(String name) {
        return referenceMap.get(name);
    }

    public void addReference(Reference reference) {
        check();
        references.add(reference);
        referenceMap.put(reference.getName(), reference);
    }

    private List<Property> properties = new ArrayList();

    private List<Property> unModifiableProperties;

    private Map<String, Property> propertyMap = new HashMap();

    public List<Property> getProperties() {
        if (frozen) {
            if (unModifiableProperties == null) {
                unModifiableProperties = Collections.unmodifiableList(properties);
            }
            return unModifiableProperties;
        } else {
            return properties;
        }
    }

    public Property getProperty(String name) {
        return propertyMap.get(name);
    }

    public void addProperty(Property property) {
        check();
        properties.add(property);
        propertyMap.put(property.getName(), property);
    }

    private Object runtimeConfiguration;

    public void setRuntimeConfiguration(Object configuration) {
        check();
        runtimeConfiguration = configuration;
    }

    public Object getRuntimeConfiguration() {
        return runtimeConfiguration;
    }

    public boolean accept(AssemblyModelVisitor visitor) {
        if (!super.accept(visitor)) {
            return false;
        }
        for (ModuleFragment fragment : moduleFragments) {
            if(!fragment.accept(visitor)){
                return false;
            }
        }
        for (Service service : services) {
            if (!service.accept(visitor)) {
                return false;
            }
        }
        for (Reference reference : references) {
            if (!reference.accept(visitor)) {
                return false;
            }
        }
        for (Property property : properties) {
            if (!property.accept(visitor)) {
                return false;
            }
        }
        return true;
    }

}
