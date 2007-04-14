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

package org.apache.tuscany.assembly.builder.impl;

import java.util.List;

import org.apache.tuscany.assembly.ComponentProperty;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.builder.ComponentBuilder;
import org.apache.tuscany.assembly.builder.ComponentPropertyBuilder;
import org.apache.tuscany.assembly.builder.ComponentReferenceBuilder;
import org.apache.tuscany.assembly.builder.ComponentServiceBuilder;
import org.apache.tuscany.assembly.builder.CompositeBuilder;
import org.apache.tuscany.assembly.impl.ComponentImpl;

public class ComponentBuilderImpl extends ComponentImpl implements ComponentBuilder {
	
	public ComponentBuilderImpl() {
	}
	
	public ComponentBuilder implementedBy(Class clazz) {
		//FIXME support Java implementations
		return this;
	}
	
	public ComponentBuilder implementedBy(CompositeBuilder composite) {
		setImplementation((Composite)composite);
		return this;
	}
	
	public ComponentBuilder uses(ComponentReferenceBuilder... componentReferences) {
		List<ComponentReference> references = getReferences();
		for (ComponentReferenceBuilder componentReference: componentReferences) {
			references.add((ComponentReference)componentReference);
		}
		return this;
	}

	public ComponentBuilder provides(ComponentServiceBuilder... componentServices) {
		List<ComponentService> services = getServices();
		for (ComponentServiceBuilder componentService: componentServices) {
			services.add((ComponentService)componentService);
		}
		return this;
	}
	
	public ComponentBuilder declares(ComponentPropertyBuilder...componentProperties) {
		List<ComponentProperty> properties = getProperties();
		for (ComponentPropertyBuilder componentProperty: componentProperties) {
			properties.add((ComponentProperty)componentProperty);
		}
		return this;
	}

}
