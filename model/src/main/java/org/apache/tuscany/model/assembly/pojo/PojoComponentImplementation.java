package org.apache.tuscany.model.assembly.pojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelVisitor;
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.Property;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;

import commonj.sdo.Sequence;

public abstract class PojoComponentImplementation implements ComponentImplementation {

    private boolean frozen;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    public PojoComponentImplementation() {
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

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

    public Sequence getAny() {
        throw new UnsupportedOperationException();
    }

    // FIXME SDO reference
    public Sequence getAnyAttribute() {
        throw new UnsupportedOperationException();
    }

    private Object runtimeConfiguration;

    public void setRuntimeConfiguration(Object configuration) {
        check();
        runtimeConfiguration = configuration;
    }

    public Object getRuntimeConfiguration() {
        return runtimeConfiguration;
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

    protected void check() {
        if (frozen == true) {
            throw new IllegalStateException("Attempt to modify a frozen configuration");
        }
    }

}
