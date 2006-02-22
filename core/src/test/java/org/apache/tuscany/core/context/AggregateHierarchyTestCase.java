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
package org.apache.tuscany.core.context;

import java.util.List;

import junit.framework.Assert;

import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.core.mock.MockConfigContext;
import org.apache.tuscany.core.mock.MockSystemAssemblyFactory;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponentImpl;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Scope;
import org.osoa.sca.ModuleContext;
import org.osoa.sca.ServiceUnavailableException;

/**
 * Performs testing of various hierarchical scenarios
 * 
 * @version $Rev$ $Date$
 */
public class AggregateHierarchyTestCase extends AbstractAggregateHierarchyTests {

    /**
     * FIXME model 
     * Tests adding a component, accessing it and then exposing it as an entry point after the first access
     * @throws Exception
     */
    public void testChildContextIsolation() throws Exception {
        AggregateContext parent = createContextHierachy();
        AggregateContext child = (AggregateContext) parent.getContext("test.child");
        Component component = MockSystemAssemblyFactory.createInitializedComponent("TestService1", ModuleScopeSystemComponentImpl.class
                .getName(), Scope.MODULE);
        child.registerModelObject(component);
        parent.fireEvent(EventContext.MODULE_START, null);
        child.fireEvent(EventContext.MODULE_START, null);
        Assert.assertNotNull(child.locateInstance("TestService1"));
        try {
            ((ModuleContext) parent).locateService("test.child/TestService1");
            fail("Expected " + ServiceUnavailableException.class.getName()
                    + " since [test.child/TestService1] is not an entry point");
        } catch (ServiceUnavailableException e) {
            // should throw an exception since it is not an entry point
        }

        // now expose the service as an entry point
        //FIXME hack to get around initialization of component - just create another one ;-)
        component = MockSystemAssemblyFactory.createComponent("TestService1", ModuleScopeSystemComponentImpl.class
                .getName(), Scope.MODULE);
        EntryPoint ep = MockSystemAssemblyFactory.createEntryPoint("TestService1EP", ModuleScopeSystemComponent.class,
                "TestService1", component);
        child.registerModelObject(ep);
        Assert.assertNotNull(child.locateInstance("TestService1EP"));
        Assert.assertNotNull(parent.locateInstance("test.child/TestService1EP"));

        // now expose the child entry point from the parent context
        EntryPoint parentEp = MockSystemAssemblyFactory.createEntryPointWithStringRef("TestService1EP",
                ModuleScopeSystemComponent.class, "TestService1", "test.child/TestService1EP");
        parent.registerModelObject(parentEp);
        Assert.assertNotNull(parent.locateInstance("TestService1EP"));

        parent.fireEvent(EventContext.MODULE_STOP, null);
        child.fireEvent(EventContext.MODULE_STOP, null);
        parent.stop();
    }

    protected AggregateContext createContextHierachy() throws Exception {
        List<RuntimeConfigurationBuilder> systemBuilders = MockSystemAssemblyFactory.createBuilders();
        AggregateContext parent = new AggregateContextImpl(
                "test.parent",
                null,
                new DefaultScopeStrategy(),
                new EventContextImpl(),
                new MockConfigContext(systemBuilders),
                new NullMonitorFactory());
        Component component = MockSystemAssemblyFactory.createComponent("test.child", AggregateContextImpl.class.getName(), Scope.AGGREGATE);
        parent.registerModelObject(component);
        parent.start();
        AggregateContext child = (AggregateContext) parent.getContext("test.child");
        Assert.assertNotNull(child);
        return parent;
    }

}
