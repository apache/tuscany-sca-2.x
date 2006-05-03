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
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.context.impl.CompositeContextImpl;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.DefaultScopeStrategy;
import org.apache.tuscany.core.context.event.ModuleStop;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.mock.MockConfigContext;
import org.apache.tuscany.core.mock.MockFactory;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponent;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponentImpl;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.impl.AssemblyContextImpl;
import org.apache.tuscany.spi.QualifiedName;
import org.osoa.sca.ModuleContext;
import org.osoa.sca.ServiceUnavailableException;

import java.util.List;

/**
 * Performs testing of various hierarchical scenarios
 * 
 * @version $Rev$ $Date$
 */
public class CompositeHierarchyTestCase extends AbstractCompositeHierarchyTests {

    /**
     * FIXME model Tests adding a component, accessing it and then exposing it as an entry point after the first access
     * 
     * @throws Exception
     */
    public void testChildContextIsolation() throws Exception {
        CompositeContext parent = createContextHierachy();
        CompositeContext child = (CompositeContext) parent.getContext("test.child");
        Component component = factory.createSystemComponent("TestService1", ModuleScopeSystemComponent.class, ModuleScopeSystemComponentImpl.class, Scope.MODULE);
        component.initialize(new AssemblyContextImpl(factory, null, null));
        child.registerModelObject(component);
        parent.publish(new ModuleStart(this));
        child.publish(new ModuleStart(this));
        Assert.assertNotNull(child.getContext("TestService1").getInstance(null));
        try {
            ((ModuleContext) parent).locateService("test.child/TestService1");
            fail("Expected " + ServiceUnavailableException.class.getName()
                    + " since [test.child/TestService1] is not an entry point");
        } catch (ServiceUnavailableException e) {
            // should throw an exception since it is not an entry point
        }

        // now expose the service as an entry point
        // FIXME hack to get around initialization of component - just create another one ;-)
        component = factory.createSystemComponent("TestService1", ModuleScopeSystemComponent.class, ModuleScopeSystemComponentImpl.class, Scope.MODULE);
        EntryPoint ep = MockFactory.createEPSystemBinding("TestService1EP", ModuleScopeSystemComponent.class, "TestService1",
                component);
        child.registerModelObject(ep);
        Assert.assertNotNull(child.getContext("TestService1EP").getInstance(null));
        Assert.assertNotNull(parent.getContext("test.child").getInstance(new QualifiedName("./TestService1EP")));

        // now expose the child entry point from the parent context
        EntryPoint parentEp = MockFactory.createEntryPointWithStringRef("TestService1EP", ModuleScopeSystemComponent.class,
                "TestService1", "test.child/TestService1EP");
        parent.registerModelObject(parentEp);
        Assert.assertNotNull(parent.getContext("TestService1EP").getInstance(null));

        parent.publish(new ModuleStop(this));
        child.publish(new ModuleStop(this));
        parent.stop();
    }

    protected CompositeContext createContextHierachy() throws Exception {
        List<ContextFactoryBuilder> systemBuilders = MockFactory.createSystemBuilders();
        CompositeContext parent = new CompositeContextImpl("test.parent", null, new DefaultScopeStrategy(),
                new EventContextImpl(), new MockConfigContext(systemBuilders));
        Component component = MockFactory.createCompositeComponent("test.child");
        parent.registerModelObject(component);
        parent.start();
        CompositeContext child = (CompositeContext) parent.getContext("test.child");
        Assert.assertNotNull(child);
        return parent;
    }

}
