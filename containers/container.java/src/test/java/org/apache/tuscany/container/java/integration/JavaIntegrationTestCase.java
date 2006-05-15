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
package org.apache.tuscany.container.java.integration;

import junit.framework.TestCase;

import org.apache.tuscany.common.monitor.MonitorFactory;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.container.java.assembly.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.impl.JavaAssemblyFactoryImpl;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.client.BootstrapHelper;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.runtime.RuntimeContextImpl;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;

/**
 * Integration test that verifies container.java can be used to host components.
 *
 * @version $Rev$ $Date$
 */
public class JavaIntegrationTestCase extends TestCase {
    private JavaAssemblyFactory factory;
    private RuntimeContext runtime;

    public void testModuleWithOneComponent() throws Exception {
        Module module = factory.createModule();
        ModuleComponent moduleComponent = factory.createModuleComponent();
        moduleComponent.setImplementation(module);

//        runtime.registerModelObject(moduleComponent);
    }

    protected void setUp() throws Exception {
        super.setUp();

        // Create a factory for model objects
        factory = new JavaAssemblyFactoryImpl();

        // Create and bootstrap an empty Tuscany runtime
        MonitorFactory monitorFactory = new NullMonitorFactory();
        ContextFactoryBuilderRegistry builderRegistry = BootstrapHelper.bootstrapContextFactoryBuilders(monitorFactory);
        DefaultWireBuilder wireBuilder = new DefaultWireBuilder();
        runtime = new RuntimeContextImpl(monitorFactory, builderRegistry, wireBuilder);
        runtime.start();
    }

    protected void tearDown() throws Exception {
        runtime.stop();
        super.tearDown();
    }
}
