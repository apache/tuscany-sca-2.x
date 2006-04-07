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
package org.apache.tuscany.container.java.context;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.container.java.builder.JavaContextFactoryBuilder;
import org.apache.tuscany.container.java.builder.JavaTargetWireBuilder;
import org.apache.tuscany.container.java.mock.MockConfigContext;
import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.components.OtherTarget;
import org.apache.tuscany.container.java.mock.components.Source;
import org.apache.tuscany.container.java.mock.components.Target;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.AtomicContext;
import org.apache.tuscany.core.context.impl.CompositeContextImpl;
import org.apache.tuscany.core.invocation.jdk.JDKProxyFactoryFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Tests wires that have different interfaces on the source and target side
 * 
 * @version $Rev$ $Date$
 */
public class DifferentInterfaceWireTestCase extends TestCase {

    public void testMultiplicity() throws Exception {
        
        CompositeContext context = createContext();
        context.start();
        context.registerModelObject(MockFactory.createModuleWithWiredComponentsOfDifferentInterface(Scope.MODULE, Scope.MODULE));
        context.fireEvent(EventContext.MODULE_START, null);
        Source source = (Source) ((AtomicContext) context.getContext("source")).getImplementationInstance();
        Assert.assertNotNull(source);
        OtherTarget target = (OtherTarget) ((AtomicContext)context.getContext("target")).getImplementationInstance();
        Assert.assertNotNull(target);
        // test setter injection
        List<Target> targets = source.getTargets();
        Assert.assertEquals(1, targets.size());
        
        // test field injection
        targets = source.getTargetsThroughField();
        Assert.assertEquals(1, targets.size());
        targets.get(0).setString("foo");
        Assert.assertEquals("foo",target.getString());
    }

    private CompositeContext createContext() {
        CompositeContextImpl context = new CompositeContextImpl();
        context.setName("system.context");
        List<ContextFactoryBuilder>builders = MockFactory.createSystemBuilders();
        builders.add(new JavaContextFactoryBuilder(new JDKProxyFactoryFactory(), new MessageFactoryImpl()));
        List<WireBuilder> wireBuilders = new ArrayList<WireBuilder>();
        wireBuilders.add(new JavaTargetWireBuilder());
        context.setConfigurationContext(new MockConfigContext(builders,wireBuilders));
        return context;
    }
}
