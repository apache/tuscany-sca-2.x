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
package org.apache.tuscany.bean.context;

import java.io.IOException;

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.CompositeReference;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.assembly.Property;
import org.apache.tuscany.assembly.Reference;
import org.apache.tuscany.assembly.SCABinding;
import org.apache.tuscany.assembly.Service;
import org.apache.tuscany.implementation.java.JavaImplementation;
import org.apache.tuscany.interfacedef.java.JavaInterface;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.AbstractRefreshableApplicationContext;

/**
 * An implementation of a Spring ApplicationContext that turns an SCA Composite
 * into an assembly of Spring BeanDefinitions 
 *
 *  @version $Rev$ $Date$
 */
public class CompositeApplicationContext extends AbstractRefreshableApplicationContext {
	
	private Composite composite;

	public CompositeApplicationContext(Composite composite) {
		this.composite = composite; 
	}
	
	private RuntimeBeanReference getBeanReference(Contract contract) {
		SCABinding binding = contract.getBinding(SCABinding.class);
		String uri = binding.getURI();
		int i = uri.indexOf('/');
		if (i != -1) {
			uri = uri.substring(0, i);
		}
		return new RuntimeBeanReference(uri);
	}

	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws IOException, BeansException {
		DefaultBeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();	

		// Create bean definitions for the composite services
		for (Service service: composite.getServices()) {
			CompositeService compositeService = (CompositeService)service;
			RootBeanDefinition bean = new RootBeanDefinition(DummyCompositeServiceBean.class);
			MutablePropertyValues propertyValues = bean.getPropertyValues();
			propertyValues.addPropertyValue("name", compositeService.getName());		
			propertyValues.addPropertyValue("interface", ((JavaInterface)compositeService.getInterface()).getName());
			ComponentService promotedService = compositeService.getPromotedService();
			propertyValues.addPropertyValue("promote", getBeanReference(promotedService));
			beanFactory.registerBeanDefinition(beanNameGenerator.generateBeanName(bean, beanFactory), bean);        	
		}

		// Create bean definitions for the composite references
		for (Reference reference: composite.getReferences()) {
			CompositeReference compositeReference = (CompositeReference)reference;
			RootBeanDefinition bean = new RootBeanDefinition(DummyCompositeReferenceBean.class);
			MutablePropertyValues propertyValues = bean.getPropertyValues();
			propertyValues.addPropertyValue("name", compositeReference.getName());		
			propertyValues.addPropertyValue("interface", ((JavaInterface)compositeReference.getInterface()).getName());
			// TODO handle multiple promoted references
			Reference promotedReference = compositeReference.getPromotedReferences().get(0);
			propertyValues.addPropertyValue("promote", getBeanReference(promotedReference));
			beanFactory.registerBeanDefinition(beanNameGenerator.generateBeanName(bean, beanFactory), bean);        	
		}

		// Create bean definitions for the components
		for (Component component: composite.getComponents()) {
			RootBeanDefinition bean = new RootBeanDefinition();
			JavaImplementation javaImplementation = (JavaImplementation)component.getImplementation();
			bean.setBeanClassName(javaImplementation.getName());
			MutablePropertyValues propertyValues = bean.getPropertyValues();
			
			for (ComponentReference componentReference: component.getReferences()) {
				//TODO handle multiplicity
				ComponentService targetService = componentReference.getTargets().get(0);
				propertyValues.addPropertyValue(componentReference.getName(),getBeanReference(targetService));
			}
			
			for (Property property: component.getProperties()) {
				propertyValues.addPropertyValue(property.getName(), property.getDefaultValue());
			}
			
			beanFactory.registerBeanDefinition(component.getName(), bean);       	
		}			
			
	}	
	
}
