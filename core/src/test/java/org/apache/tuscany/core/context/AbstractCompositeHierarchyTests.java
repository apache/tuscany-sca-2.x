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

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.context.impl.CompositeContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.core.context.event.ModuleStartEvent;
import org.apache.tuscany.core.context.event.ModuleStopEvent;
import org.apache.tuscany.core.mock.MockConfigContext;
import org.apache.tuscany.core.mock.MockFactory;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponentImpl;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Scope;
import org.osoa.sca.ModuleContext;

import java.util.List;

/**
 * Performs testing of various hierarchical scenarios
 * 
 * @version $Rev$ $Date$
 */
public abstract class AbstractCompositeHierarchyTests extends TestCase {
    protected List<ContextFactoryBuilder> builders;
    protected SystemAssemblyFactory factory;

    public void testParentContextIsolation() throws Exception {
        CompositeContext parent = createContextHierachy();
        CompositeContext child = (CompositeContext) parent.getContext("test.child");
        Component component = factory.createSystemComponent("TestService1", ModuleScopeSystemComponent.class, ModuleScopeSystemComponentImpl.class, Scope.MODULE);
        parent.registerModelObject(component);
        EntryPoint ep = MockFactory.createEPSystemBinding("TestService1EP", ModuleScopeSystemComponent.class, "TestService1", component);
        parent.registerModelObject(ep);
        parent.publish(new ModuleStartEvent(this));
        child.publish(new ModuleStartEvent(this));
        Assert.assertNotNull(parent.getContext("TestService1EP").getInstance(null));
        try {
            ((ModuleContext) child).locateService("TestService1EP");
            fail("Expexcted " + ServiceNotFoundException.class.getName());
        } catch (ServiceNotFoundException e) {
            // expect exception to be thrown
        }
        parent.publish(new ModuleStopEvent(this));
        child.publish(new ModuleStopEvent(this));
        parent.stop();

    }

    /**
     * Checks that registration of duplicate named model objects before context start throws an exception
     */
    public void testRegisterSameName() throws Exception {
        CompositeContext parent = new CompositeContextImpl("test.parent", null, new DefaultScopeStrategy(),
                new EventContextImpl(), new MockConfigContext(builders));
        parent.registerModelObject(MockFactory.createSystemCompositeComponent("test.child"));
        try {
            parent.registerModelObject(MockFactory.createSystemCompositeComponent("test.child"));
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
        CompositeContext parent = new CompositeContextImpl("test.parent", null, new DefaultScopeStrategy(),
                new EventContextImpl(), new MockConfigContext(builders));
        parent.registerModelObject(MockFactory.createSystemCompositeComponent("test.child"));
        parent.start();
        CompositeContext child = (CompositeContext) parent.getContext("test.child");
        Assert.assertNotNull(child);
        try {
            parent.registerModelObject(MockFactory.createSystemCompositeComponent("test.child"));
            fail("Expected " + DuplicateNameException.class.getName());
        } catch (DuplicateNameException e) {
            // expected
        }
    }

    protected abstract CompositeContext createContextHierachy() throws Exception;

    protected void setUp() throws Exception {
        super.setUp();
        factory = new SystemAssemblyFactoryImpl();
        builders = MockFactory.createSystemBuilders();
    }
}
