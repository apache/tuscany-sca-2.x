/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.runtime;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.core.builder.ContextFactoryBuilderRegistry;
import org.apache.tuscany.core.builder.impl.DefaultWireBuilder;
import org.apache.tuscany.core.client.BootstrapHelper;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.Lifecycle;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.mock.MockFactory;

/**
 * Tests runtime boot scenarios
 * 
 * @version $Rev$ $Date$
 */
public class RuntimeBootTestCase extends TestCase {
    private RuntimeContext runtime;
    private NullMonitorFactory monitorFactory;
    private ContextFactoryBuilderRegistry builderRegistry;
    private DefaultWireBuilder wireBuilder;

    public void testContextParents() {
        CompositeContext rootContext = runtime.getRootContext();
        assertNotNull(rootContext);
        assertEquals("tuscany.root", rootContext.getName());
        assertSame(runtime, rootContext.getParent());
        assertSame(rootContext, runtime.getContext("tuscany.root"));

        CompositeContext systemContext = runtime.getSystemContext();
        assertNotNull(systemContext);
        assertEquals("tuscany.system", systemContext.getName());
        assertSame(runtime, systemContext.getParent());
        assertSame(systemContext, runtime.getContext("tuscany.system"));
    }

    public void testRuntimeLifecycle() {
        assertEquals(Lifecycle.RUNNING, runtime.getLifecycleState());
        runtime.stop();

        assertEquals(Lifecycle.STOPPED, runtime.getLifecycleState());
    }

    public void testIncrementalBoot() throws Exception{

        // start the runtime context
        RuntimeContext runtimeContext = new RuntimeContextImpl(monitorFactory, builderRegistry, wireBuilder);
        runtimeContext.start();

        CompositeContext system = runtimeContext.getSystemContext();
        Assert.assertNotNull(system);
        // register system components
        system.registerModelObject(MockFactory.createSystemModule());
        // start the module scope
        system.publish(new ModuleStart(this));
        // register the first module
        
        // register the second module

        // start the modules

        system.publish(new ModuleStop(this));
        runtimeContext.stop();
        Assert.assertEquals(Lifecycle.STOPPED,system.getLifecycleState());
    }

    protected void setUp() throws Exception {
        super.setUp();

        monitorFactory = new NullMonitorFactory();
        builderRegistry = BootstrapHelper.bootstrapContextFactoryBuilders(monitorFactory);
        wireBuilder = new DefaultWireBuilder();
        runtime = new RuntimeContextImpl(monitorFactory, builderRegistry, wireBuilder);
        runtime.start();
    }

    protected void tearDown() throws Exception {
        runtime.stop();
        super.tearDown();
    }
}

