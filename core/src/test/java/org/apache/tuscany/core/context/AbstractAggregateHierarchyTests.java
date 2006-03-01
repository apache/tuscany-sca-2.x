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
import junit.framework.TestCase;

import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.context.impl.AggregateContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.core.mock.MockConfigContext;
import org.apache.tuscany.core.mock.MockFactory;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponentImpl;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Scope;
import org.osoa.sca.ModuleContext;

/**
 * Performs testing of various hierarchical scenarios
 * 
 * @version $Rev$ $Date$
 */
public abstract class AbstractAggregateHierarchyTests extends TestCase {
    protected List<RuntimeConfigurationBuilder> builders;

    public void testParentContextIsolation() throws Exception {
        AggregateContext parent = createContextHierachy();
        AggregateContext child = (AggregateContext) parent.getContext("test.child");
        Component component = MockFactory.createSystemComponent("TestService1", ModuleScopeSystemComponentImpl.class,
                Scope.MODULE);
        parent.registerModelObject(component);
        EntryPoint ep = MockFactory.createEPSystemBinding("TestService1EP", ModuleScopeSystemComponent.class, "TestService1",
                component);
        parent.registerModelObject(ep);
        parent.fireEvent(EventContext.MODULE_START, null);
        child.fireEvent(EventContext.MODULE_START, null);
        Assert.assertNotNull(parent.locateInstance("TestService1EP"));
        try {
            ((ModuleContext) child).locateService("TestService1EP");
            fail("Expexcted " + ServiceNotFoundException.class.getName());
        } catch (ServiceNotFoundException e) {
            // expect exception to be thrown
        }
        parent.fireEvent(EventContext.MODULE_STOP, null);
        child.fireEvent(EventContext.MODULE_STOP, null);
        parent.stop();

    }

    /**
     * Checks that registration of duplicate named model objects before context start throws an exception
     */
    public void testRegisterSameName() throws Exception {
        AggregateContext parent = new AggregateContextImpl("test.parent", null, new DefaultScopeStrategy(),
                new EventContextImpl(), new MockConfigContext(builders), new NullMonitorFactory());
        parent.registerModelObject(MockFactory.createSystemAggregateComponent("test.child"));
        try {
            parent.registerModelObject(MockFactory.createSystemAggregateComponent("test.child"));
            parent.start();
            fail("Expected " + DuplicateNameException.class.getName());
        } catch (DuplicateNameException e) {
            // expected
        }
    }

    /**
     * Checks that registration of duplicate named model objects after context start throws an exception
     */
    public void testRegisterSameNameAfterStart() throws Exception {
        AggregateContext parent = new AggregateContextImpl("test.parent", null, new DefaultScopeStrategy(),
                new EventContextImpl(), new MockConfigContext(builders), new NullMonitorFactory());
        parent.registerModelObject(MockFactory.createSystemAggregateComponent("test.child"));
        parent.start();
        AggregateContext child = (AggregateContext) parent.getContext("test.child");
        Assert.assertNotNull(child);
        try {
            parent.registerModelObject(MockFactory.createSystemAggregateComponent("test.child"));
            fail("Expected " + DuplicateNameException.class.getName());
        } catch (DuplicateNameException e) {
            // expected
        }
    }

    protected abstract AggregateContext createContextHierachy() throws Exception;

    protected void setUp() throws Exception {
        super.setUp();
        builders = MockFactory.createSystemBuilders();
    }
}
