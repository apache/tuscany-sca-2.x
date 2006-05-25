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
package org.apache.tuscany.container.java;

import junit.framework.TestCase;


/**
 * Tests wires that are configured with a multiplicity
 * 
 * @version $Rev$ $Date$
 */
public class MultiplicityTestCase extends TestCase {

    public void testMultiplicity() throws Exception {
//        CompositeContext context = createContext();
//        context.start();
//        context.registerModelObject(MockContextFactory.createModuleWithWiredComponents(Scope.MODULE, Scope.MODULE));
//        context.publish(new ModuleStart(this));
//        Source source = (Source) ((AtomicContext) context.getContext("source")).getTargetInstance();
//        Assert.assertNotNull(source);
//        Target target = (Target) ((AtomicContext)context.getContext("target")).getTargetInstance();
//        Assert.assertNotNull(target);
//        // test setter injection
//        List<Target> targets = source.getTargets();
//        Assert.assertEquals(1, targets.size());
//
//        // test field injection
//        targets = source.getTargetsThroughField();
//        Assert.assertEquals(1, targets.size());
    }

//    private CompositeContext createContext() {
//        CompositeContextImpl context = new CompositeContextImpl();
//        context.setName("system.context");
//        List<ContextFactoryBuilder>builders = MockContextFactory.createSystemBuilders();
//        WireService wireService = new DefaultWireFactoryService(new MessageFactoryImpl(), new JDKWireFactoryService(), new DefaultPolicyBuilderRegistry());
//        builders.add(new JavaContextFactoryBuilder(wireService));
//        List<WireBuilder> wireBuilders = new ArrayList<WireBuilder>();
//        wireBuilders.add(new JavaTargetWireBuilder());
//        context.setConfigurationContext(new MockConfigContext(builders,wireBuilders));
//        return context;
//    }
}
