/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.sca.implementation.spring;

import java.util.Hashtable;
import java.util.List;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Implementation;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.impl.ComponentTypeImpl;
import org.apache.tuscany.sca.implementation.spring.xml.SpringBeanElement;
import org.springframework.core.io.Resource;

/**
 * Represents a Spring implementation.
 * 
 * @version $Rev: 511195 $ $Date: 2007-02-24 02:29:46 +0000 (Sat, 24 Feb 2007) $ 
 */
public class SpringImplementation extends ComponentTypeImpl implements Implementation {

    // The location attribute which points to the Spring application-context XML file
    private String springLocation;
    // The application-context file as a Spring Resource
    private Resource resource;
    private ComponentType componentType;
    // Mapping of Services to Beans
    private Hashtable<String, SpringBeanElement> serviceMap;
    // Mapping of property names to Java class
    private Hashtable<String, Class> propertyMap;

    protected SpringImplementation() {
        this.springLocation = null;
        this.resource = null;
        setUnresolved(true);
        serviceMap = new Hashtable<String, SpringBeanElement>();
        propertyMap = new Hashtable<String, Class>();
    } // end method SpringImplementation

    /* Returns the location attribute for this Spring implementation */
    public String getSpringLocation() {
        return springLocation;
    }

    /**
     * Sets the location attribute for this Spring implementation
     * location - a URI to the Spring application-context file
     */
    public void setSpringLocation(String location) {
        this.springLocation = location;
        return;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    /* 
     * Returns the componentType for this Spring implementation 
     */
    public ComponentType getComponentType() {
        return componentType;
    }

    /*
     * Sets the componentType for this Spring implementation
     */
    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    @Override
    public List<Service> getServices() {
        return componentType.getServices();
    }

    @Override
    public List<Reference> getReferences() {
        return componentType.getReferences();
    }

    @Override
    public List<Property> getProperties() {
        return componentType.getProperties();
    }

    /**
     * Returns the Spring Bean which implements a particular service
     * @param service the service
     * @return the bean which implements the service, as a SpringBeanElement
     */
    public SpringBeanElement getBeanFromService(Service service) {
        SpringBeanElement theBean = serviceMap.get(service.getName());
        return theBean;
    }

    /**
     * Sets the mapping from a service to the Spring Bean that implements the service
     * @param service the service
     * @param theBean a SpringBeanElement for the Bean implementing the service
     */
    public void setBeanForService(Service service, SpringBeanElement theBean) {
        serviceMap.put(service.getName(), theBean);
    }

    /**
     * Add a mapping from a SCA property name to a Java class for the property
     * @param propertyName
     * @param propertyClass
     */
    public void setPropertyClass(String propertyName, Class propertyClass) {
        if (propertyName == null || propertyClass == null)
            return;
        propertyMap.put(propertyName, propertyClass);
        return;
    } // end method setPropertyClass

    /**
     * Gets the Java Class for an SCA property 
     * @param propertyName - the property name
     * @return - a Class object for the type of the property
     */
    public Class getPropertyClass(String propertyName) {
        return propertyMap.get(propertyName);
    } // end method getPropertyClass
}
