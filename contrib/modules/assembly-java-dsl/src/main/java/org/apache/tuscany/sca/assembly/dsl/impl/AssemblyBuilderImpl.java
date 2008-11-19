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
package org.apache.tuscany.sca.assembly.dsl.impl;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.assembly.DefaultAssemblyFactory;
import org.apache.tuscany.sca.assembly.dsl.AssemblyBuilder;
import org.apache.tuscany.sca.assembly.dsl.ComponentBuilder;
import org.apache.tuscany.sca.assembly.dsl.ComponentPropertyBuilder;
import org.apache.tuscany.sca.assembly.dsl.ComponentReferenceBuilder;
import org.apache.tuscany.sca.assembly.dsl.ComponentServiceBuilder;
import org.apache.tuscany.sca.assembly.dsl.CompositeBuilder;

public class AssemblyBuilderImpl extends DefaultAssemblyFactory implements AssemblyBuilder {

	public ComponentBuilder component(String name) {
		ComponentBuilderImpl component = new ComponentBuilderImpl();
		component.setName(name);
		return component;
	}

	public CompositeBuilder composite(String name) {
		CompositeBuilderImpl composite = new CompositeBuilderImpl();
		//TODO handle namespace
		composite.setName(new QName("", name));
		return composite;
	}

	public CompositeBuilder domain(String uri) {
		CompositeBuilderImpl composite = new CompositeBuilderImpl();
		composite.setName(new QName(uri, ""));
		return composite;
	}

	public ComponentPropertyBuilder property(String name) {
		ComponentPropertyBuilderImpl property = new ComponentPropertyBuilderImpl();
		property.setName(name);
		return property;
	}

	public ComponentReferenceBuilder reference(String name) {
		ComponentReferenceBuilderImpl reference = new ComponentReferenceBuilderImpl(this);
		reference.setName(name);
		return reference;
	}

	public ComponentServiceBuilder service(String name) {
		ComponentServiceBuilderImpl service = new ComponentServiceBuilderImpl(this);
		service.setName(name);
		return service;
	}

}
