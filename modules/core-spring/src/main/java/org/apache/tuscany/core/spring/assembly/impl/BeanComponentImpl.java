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
package org.apache.tuscany.core.spring.assembly.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentProperty;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.ConstrainingType;
import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.assembly.util.Visitor;
import org.apache.tuscany.policy.Intent;
import org.apache.tuscany.policy.PolicySet;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ChildBeanDefinition;

/**
 * An implementation of the SCA assembly Component interface backed by a Spring
 * Bean definition.
 *
 *  @version $Rev$ $Date$
 */
public class BeanComponentImpl extends ChildBeanDefinition implements Component, Cloneable {
	private static final long serialVersionUID = 1L;
	
	private ConstrainingType constrainingType;
	private Implementation implementation;
	private String name;
        private String uri;
	private List<ComponentService> services = new ArrayList<ComponentService>();
	private List<Intent> requiredIntents = new ArrayList<Intent>();
	private List<PolicySet> policySets = new ArrayList<PolicySet>();
	private List<Object> extensions = new ArrayList<Object>();
	private boolean unresolved = false;
	private BeanDefinitionRegistry beanRegistry;
	
	protected BeanComponentImpl(BeanDefinitionRegistry beanRegistry) {
		super((String)"");
		this.beanRegistry = beanRegistry;
	}
        
        @Override
        public Object clone() throws CloneNotSupportedException {
            BeanComponentImpl clone = (BeanComponentImpl)super.clone();

            clone.getProperties().clear();
            for (ComponentProperty property: getProperties()) {
                clone.getProperties().add((ComponentProperty)property.clone());
            }
            clone.getReferences().clear();
            for (ComponentReference reference: getReferences()) {
                clone.getReferences().add((ComponentReference)reference.clone());
            }
            clone.getServices().clear();
            for (ComponentService service: getServices()) {
                clone.getServices().add((ComponentService)service.clone());
            }
            return clone;
        }
	
	public String getParentName() {
		//TODO find a better name for bean definitions representing component types
		return String.valueOf(System.identityHashCode(implementation));
	}

	public ConstrainingType getConstrainingType() {
		return constrainingType;
	}

	public Implementation getImplementation() {
		return implementation;
	}

        public String getURI() {
            return uri;
        }
        
        public void setURI(String uri) {
            this.uri = uri;
                
            // Register this bean definition in the bean registry
            this.beanRegistry.registerBeanDefinition(uri, this);
        }

	public String getName() {
		return name;
	}

	//TODO use a better list implementation
	private List<ComponentProperty> properties = new ArrayList<ComponentProperty>() {
		private static final long serialVersionUID = 1L;
		
		// Add a property
		public boolean add(ComponentProperty property) {
			
			// Add corresponding bean property value
			getPropertyValues().addPropertyValue(property.getName(), property.getValue());

			return super.add(property);
		}
	};
	
	public List<ComponentProperty> getProperties() {
		return properties;
	}

	//TODO use a better list implementation
	private List<ComponentReference> references = new ArrayList<ComponentReference>() {
		private static final long serialVersionUID = 1L;

		// Add a reference
		public boolean add(ComponentReference reference) {
			
			// Add corresponding bean property value
                    if (!reference.getName().startsWith("$self$.")) {
			BeanReferenceImpl beanReference = new BeanReferenceImpl(reference);
			getPropertyValues().addPropertyValue(reference.getName(), beanReference);
                    }
                    return super.add(reference);
		}
	};
	
	public List<ComponentReference> getReferences() {
		return references;
	}

	public List<ComponentService> getServices() {
		return services;
	}

	public void setConstrainingType(ConstrainingType constrainingType) {
		this.constrainingType = constrainingType;
	}

	public void setImplementation(Implementation implementation) {
		this.implementation = implementation;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Intent> getRequiredIntents() {
		return requiredIntents;
	}

	public List<PolicySet> getPolicySets() {
		return policySets;
	}

	public boolean isAutowire() {
		return super.getAutowireMode() == AUTOWIRE_BY_TYPE;
	}
	
	public void setAutowire(boolean autowire) {
		super.setAutowireMode(autowire? AUTOWIRE_BY_TYPE:AUTOWIRE_NO);
	}

	public List<Object> getExtensions() {
		return extensions;
	}

	public boolean isUnresolved() {
		return unresolved;
	}

	public void setUnresolved(boolean undefined) {
		this.unresolved = undefined;
	}

            public boolean accept(Visitor visitor) {
                if (!visitor.visit(this)) {
                    return false;
                }
                for (ComponentProperty property : properties) {
                    if (!visitor.visit(property)) {
                        return false;
                    }
                }
                for (ComponentReference reference : references) {
                    if (!visitor.visit(reference)) {
                        return false;
                    }
                }
                for (ComponentService service : services) {
                    if (!visitor.visit(service)) {
                        return false;
                    }
                }
                return true;
            }
}
