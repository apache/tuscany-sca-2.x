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

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.CompositeService;
import org.apache.tuscany.assembly.dsl.ComponentServiceBuilder;
import org.apache.tuscany.assembly.impl.ComponentServiceImpl;

public class ComponentServiceBuilderImpl extends ComponentServiceImpl implements ComponentServiceBuilder {
	
	private CompositeService compositeService;
        private AssemblyFactory assemblyFactory;
        
        protected ComponentServiceBuilderImpl(AssemblyFactory assemblyFactory) {
            this.assemblyFactory = assemblyFactory;
        }
	
	public ComponentServiceBuilderImpl typedBy(Class interfaceClass) {
		//FIXME support for Java interfaces 
		return this;
	}
	
	public ComponentServiceBuilderImpl promotedAs(String promoted) {
		compositeService = assemblyFactory.createCompositeService();
		compositeService.setName(promoted);
		return this;
	}

	public ComponentServiceBuilderImpl promoted() {
		compositeService = assemblyFactory.createCompositeService();
		compositeService.setName(getName());
		return this;
	}
	
	public ComponentServiceBuilder boundTo(String uri) {
		// TODO support bindings
		return this;
	}
	
	CompositeService getCompositeService() {
		return compositeService;
	}
	
}
