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

import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.sca.assembly.AbstractProperty;
import org.apache.tuscany.sca.assembly.AbstractReference;
import org.apache.tuscany.sca.assembly.AbstractService;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Callback;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentProperty;
import org.apache.tuscany.sca.assembly.ComponentReference;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.ConstrainingType;
import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.Property;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.Service;
import org.apache.tuscany.sca.assembly.Wire;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * An alternate implementation of the SCA assembly model factory that creates SCA
 * assembly model objects backed by Spring bean definitions.
 *
 *  @version $Rev$ $Date$
 */
public class BeanAssemblyFactory implements AssemblyFactory {
	
	private AssemblyFactory defaultFactory;
	private BeanDefinitionRegistry beanRegistry;
	
	public BeanAssemblyFactory(AssemblyFactory defaultFactory, BeanDefinitionRegistry beanRegistry) {
		this.defaultFactory = defaultFactory;
		this.beanRegistry = beanRegistry;
	}

        public BeanAssemblyFactory(BeanDefinitionRegistry beanRegistry) {
            this(new DefaultAssemblyFactory(), beanRegistry);
        }

	public AbstractProperty createAbstractProperty() {
		return defaultFactory.createAbstractProperty();
	}

	public AbstractReference createAbstractReference() {
		return defaultFactory.createAbstractReference();
	}

	public AbstractService createAbstractService() {
		return defaultFactory.createAbstractService();
	}

	public Callback createCallback() {
		return defaultFactory.createCallback();
	}

	public Component createComponent() {
		return new BeanComponentImpl(beanRegistry);
	}

	public ComponentProperty createComponentProperty() {
		return defaultFactory.createComponentProperty();
	}

	public ComponentReference createComponentReference() {
		return defaultFactory.createComponentReference();
	}

	public ComponentService createComponentService() {
		return defaultFactory.createComponentService();
	}

	public ComponentType createComponentType() {
		return defaultFactory.createComponentType();
	}

	public Composite createComposite() {
		return defaultFactory.createComposite();
	}

	public CompositeReference createCompositeReference() {
		return defaultFactory.createCompositeReference();
	}

	public CompositeService createCompositeService() {
		return defaultFactory.createCompositeService();
	}

	public ConstrainingType createConstrainingType() {
		return defaultFactory.createConstrainingType();
	}

	public Property createProperty() {
		return defaultFactory.createProperty();
	}

	public Reference createReference() {
		return defaultFactory.createReference();
	}

	public Service createService() {
		return defaultFactory.createService();
	}

	public Wire createWire() {
		return defaultFactory.createWire();
	}

	public SCABinding createSCABinding() {
		return defaultFactory.createSCABinding();
	}
	
	public Operation createOperation() {
		return defaultFactory.createOperation();
	}
}
