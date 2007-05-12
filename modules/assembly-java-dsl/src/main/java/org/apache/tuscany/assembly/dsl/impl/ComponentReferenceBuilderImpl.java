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

import org.apache.tuscany.assembly.dsl.ComponentReferenceBuilder;
import org.apache.tuscany.assembly.dsl.ComponentServiceBuilder;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.CompositeReference;
import org.apache.tuscany.sca.assembly.impl.ComponentReferenceImpl;
import org.apache.tuscany.sca.assembly.impl.ComponentServiceImpl;

public class ComponentReferenceBuilderImpl extends ComponentReferenceImpl implements ComponentReferenceBuilder {
	
	private CompositeReference compositeReference;
        private AssemblyFactory assemblyFactory;
        
        protected ComponentReferenceBuilderImpl(AssemblyFactory assemblyFactory) {
            this.assemblyFactory = assemblyFactory;
        }
	
	public ComponentReferenceBuilder wiredTo(String target) {
		ComponentService componentService = assemblyFactory.createComponentService();
		componentService.setUnresolved(true);
		componentService.setName(target);
		getTargets().add(componentService);
		return this;
	}
	
	public ComponentReferenceBuilder wiredTo(ComponentServiceBuilder target) {
		getTargets().add((ComponentServiceImpl)target);
		return this;
	}
	
	public ComponentReferenceBuilderImpl typedBy(Class interfaceClass) {
		//FIXME support for Java interfaces 
		return this;
	}
	
	public ComponentReferenceBuilderImpl promotedAs(String promoted) {
		compositeReference = assemblyFactory.createCompositeReference();
		compositeReference.setName(promoted);
		return this;
	}

	public ComponentReferenceBuilderImpl promoted() {
		compositeReference = assemblyFactory.createCompositeReference();
		compositeReference.setName(getName());
		return this;
	}

	public ComponentReferenceBuilder boundTo(String uri) {
		//TODO support bindings
		return this;
	}

	CompositeReference getCompositeReference() {
		return compositeReference;
	}
}
