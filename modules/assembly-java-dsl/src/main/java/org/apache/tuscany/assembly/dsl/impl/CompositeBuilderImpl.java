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

package org.apache.tuscany.assembly.dsl.impl;

import java.util.List;

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.assembly.Composite;
import org.apache.tuscany.assembly.dsl.ComponentBuilder;
import org.apache.tuscany.assembly.dsl.CompositeBuilder;
import org.apache.tuscany.assembly.impl.CompositeImpl;

public class CompositeBuilderImpl extends CompositeImpl implements CompositeBuilder {
	
	public CompositeBuilder contains(ComponentBuilder... componentBuilders) {
		List<Component> components = getComponents();
		for (ComponentBuilder componentBuilder: componentBuilders) {
			Component component = (Component)componentBuilder;
			components.add(component);

			for (ComponentService componentService: component.getServices()) {
				ComponentServiceBuilderImpl builder = (ComponentServiceBuilderImpl)componentService;
				if (builder.getCompositeService() != null)
					getServices().add(builder.getCompositeService());
			}
			for (ComponentReference componentReference: component.getReferences()) {
				ComponentReferenceBuilderImpl builder = (ComponentReferenceBuilderImpl)componentReference;
				if (builder.getCompositeReference() != null)
					getReferences().add(builder.getCompositeReference());
			}
		}
		return this;
	}
	
	public CompositeBuilder includes(CompositeBuilder... compositeBuilders) {
		List<Composite> list = getIncludes();
		for (CompositeBuilder composite: compositeBuilders) {
			list.add((Composite)composite);
		}
		return this;
	}

}
