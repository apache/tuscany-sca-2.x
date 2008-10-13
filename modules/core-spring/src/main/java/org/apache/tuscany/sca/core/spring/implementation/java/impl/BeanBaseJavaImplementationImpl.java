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
package org.apache.tuscany.sca.core.spring.implementation.java.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.ConfiguredOperation;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.implementation.java.BaseJavaImplementation;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * An implementation of the SCA assembly JavaImplementation interface backed by a Spring
 * Bean definition.
 *
 *  @version $Rev$ $Date$
 */
public class BeanBaseJavaImplementationImpl extends GenericBeanDefinition implements BaseJavaImplementation, Cloneable {
    private static final long serialVersionUID = 1L;

    private List<Service> services = new ArrayList<Service>();
    private ConstrainingType constrainingType;
    private List<Object> extensions = new ArrayList<Object>();
    private boolean unresolved;
    private BeanDefinitionRegistry beanRegistry;
    private String uri;

    private List<PolicySet> applicablePolicySets = new ArrayList<PolicySet>();
    private List<ConfiguredOperation> configuredOperations = new ArrayList<ConfiguredOperation>();
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private IntentAttachPointType type = null;

    protected BeanBaseJavaImplementationImpl(BeanDefinitionRegistry beanRegistry) {
        this.beanRegistry = beanRegistry;

        // Register this bean definition in the bean registry
        //TODO find a better name for bean definitions representing component types
        String name = String.valueOf(System.identityHashCode(this));
        this.beanRegistry.registerBeanDefinition(name, this);
    }

    @Override
    public AbstractBeanDefinition cloneBeanDefinition() {
        BeanBaseJavaImplementationImpl clone = (BeanBaseJavaImplementationImpl)super.cloneBeanDefinition();        
        clone.getServices().clear();
        try {
            for (Service service : getServices()) {
                clone.getServices().add((Service)service.clone());
            }
            clone.getReferences().clear();
            for (Reference reference : getReferences()) {
                clone.getReferences().add((Reference)reference.clone());
            }
            clone.getProperties().clear();
            for (Property property : getProperties()) {
                clone.getProperties().add((Property)property.clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            //throw new CloneNotSupportedException(e.getMessage());
        }
        return clone;
    }

    public Class<?> getJavaClass() {
        return super.getBeanClass();
    }

    public String getName() {
        return super.getBeanClassName();
    }

    public void setJavaClass(Class<?> javaClass) {
        super.setBeanClass(javaClass);
    }

    public void setName(String className) {
        super.setBeanClassName(className);
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public ConstrainingType getConstrainingType() {
        return constrainingType;
    }

    //TODO use a better list implementation
    private List<Property> properties = new ArrayList<Property>() {
        private static final long serialVersionUID = 1L;

        // Add a property
        @Override
        public boolean add(Property property) {

            // Add corresponding bean property value
            getPropertyValues().addPropertyValue(property.getName(), property.getValue());

            return super.add(property);
        }
    };

    public List<Property> getProperties() {
        return properties;
    }

    //TODO use a better list implementation
    private List<Reference> references = new ArrayList<Reference>() {
        private static final long serialVersionUID = 1L;

        // Add a reference
        @Override
        public boolean add(Reference reference) {

            // Add corresponding bean property value
            String target;
            if (!reference.getTargets().isEmpty()) {
                //TODO handle multiplicity
                target = reference.getTargets().get(0).getName();
                int i = target.indexOf('/');
                if (i != -1)
                    target = target.substring(0, i);
            } else {
                target = null;
            }
            getPropertyValues().addPropertyValue(reference.getName(), target);

            return super.add(reference);
        }
    };


    public List<Reference> getReferences() {
        return references;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setConstrainingType(ConstrainingType constrainingType) {
        this.constrainingType = constrainingType;
    }

    public List<Object> getExtensions() {
        return extensions;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean unresolved) {
        this.unresolved = unresolved;
    }

    public List<PolicySet> getApplicablePolicySets() {
        return applicablePolicySets;
    }

    public List<ConfiguredOperation> getConfiguredOperations() {
        return configuredOperations;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public IntentAttachPointType getType() {
        return type;
    }

    public void setType(IntentAttachPointType type) {
        this.type = type;
    }
}
