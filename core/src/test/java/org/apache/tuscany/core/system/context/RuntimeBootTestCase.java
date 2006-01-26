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
package org.apache.tuscany.core.system.context;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.ContextConstants;
import org.apache.tuscany.core.mock.MockSystemAssemblyFactory;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;

/**
 * Tests runtime boot scenarios
 * 
 * @version $Rev$ $Date$
 */
public class RuntimeBootTestCase extends TestCase {

    public void testIncrementalBoot() throws Exception{

        List<RuntimeConfigurationBuilder> builders  = MockSystemAssemblyFactory.createBuilders();
        // start the runtime context
        RuntimeContext runtimeContext = new RuntimeContextImpl(new NullMonitorFactory(), builders);
        runtimeContext.start();

        // create the system context
        Component component = MockSystemAssemblyFactory.createComponent(RuntimeContext.SYSTEM,
                SystemAggregateContextImpl.class.getName(), ContextConstants.AGGREGATE_SCOPE_ENUM);
        runtimeContext.registerModelObject(component);

        AggregateContext system = runtimeContext.getSystemContext();
        Assert.assertNotNull(system);
        // register system components
        system.registerModelObject(MockSystemAssemblyFactory.createSystemModule());
        // start the module scope
        system.fireEvent(EventContext.MODULE_START, null);
        // register the first module

        // register the second module

        // start the modules

        system.fireEvent(EventContext.MODULE_STOP, null);
        runtimeContext.stop();
        Assert.assertEquals(Context.STOPPED,system.getLifecycleState());
    }
}

