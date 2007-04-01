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
package org.apache.tuscany.implementation.java.impl;

/**
 * A factory for the Java model.
 */
import org.apache.tuscany.assembly.model.AssemblyFactory;
import org.apache.tuscany.assembly.model.Service;
import org.apache.tuscany.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.implementation.java.JavaImplementation;

public class DefaultJavaImplementationFactory implements JavaImplementationFactory {
	
	private AssemblyFactory assemblyFactory;
	
	public DefaultJavaImplementationFactory(AssemblyFactory assemblyFactory) {
		this.assemblyFactory = assemblyFactory; 
	}

	public JavaImplementation createJavaImplementation() {
		JavaImplementation javaImplementation = new JavaImplementationImpl();
		
		//TODO temporary, services should be created by introspecting
		// the implementation
		Service service = assemblyFactory.createService();
		javaImplementation.getServices().add(service);
		return javaImplementation;
	}

}
