/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.system.assembly.tests;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.core.addressing.impl.AddressingFactoryImpl;
import org.apache.tuscany.core.builder.SimpleComponentRuntimeConfiguration;
import org.apache.tuscany.core.builder.impl.SystemRuntimeConfigurationBuilderImpl;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.message.handler.MessageHandler;
import org.apache.tuscany.model.assembly.AssemblyLoader;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;

/**
 *
 */
public class SystemAssemblyRuntimeBuilderTestCase extends TestCase {
	
	private AssemblyModelContext modelContext;

	/**
	 * 
	 */
	public SystemAssemblyRuntimeBuilderTestCase() {
		super();
	}
	
	public void testBuilder() throws TargetException {
		
		AssemblyLoader loader=modelContext.getAssemblyLoader();
		Module module=loader.getModule(getClass().getResource("sca.module").toString());
		module.initialize(modelContext);
		
		Assert.assertTrue(module.getName().equals("tuscany.core.system.assembly.tests.testModule"));
		
		Component component=module.getComponent("org.apache.tuscany.core.pipeline.TuscanyCoreCreationPipeline");
		Assert.assertTrue(component!=null);

		SystemRuntimeConfigurationBuilderImpl builder=new SystemRuntimeConfigurationBuilderImpl(modelContext.getAssemblyFactory(), new AddressingFactoryImpl(), modelContext.getResourceLoader());
		builder.build(module);
		
		SimpleComponentRuntimeConfiguration runtimeConfiguration=(SimpleComponentRuntimeConfiguration)component.getComponentImplementation().getRuntimeConfiguration();
		MessageHandler tuscanyCoreCreationPipeline=(MessageHandler)runtimeConfiguration.createComponentContext().getInstance(null);
		System.out.println(tuscanyCoreCreationPipeline);
		
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
		modelContext=new AssemblyModelContextImpl();
	}

}
