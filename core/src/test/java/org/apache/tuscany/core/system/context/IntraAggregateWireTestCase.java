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

import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.SystemAggregateContext;
import org.apache.tuscany.core.mock.MockConfigContext;
import org.apache.tuscany.core.mock.MockFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Tests intra-aggregate system component wiring scenarios
 * 
 * @version $Rev$ $Date$
 */
public class IntraAggregateWireTestCase extends TestCase {

    public void testModuleToModuleScope() throws Exception {
        SystemAggregateContext context = createContext();
        context.start();
        context.registerModelObject(MockFactory.createSystemModuleWithWiredComponents("system.module",Scope.MODULE, Scope.MODULE));
        context.fireEvent(EventContext.MODULE_START, null);
        Source source = (Source) context.getContext("source").getImplementationInstance();
        Assert.assertNotNull(source);
        Target targetRef = source.getTarget();
        Assert.assertNotNull(targetRef);
        Target target = (Target) context.getContext("target").getImplementationInstance();
        Assert.assertSame(target, targetRef);
        Assert.assertSame(target, source.getTarget());
        context.fireEvent(EventContext.MODULE_STOP, null);
        context.stop();
    }

    public void testStatelessToModuleScope() throws Exception {
        SystemAggregateContext context = createContext();
        context.start();
        context.registerModelObject(MockFactory.createSystemModuleWithWiredComponents("system.module",Scope.INSTANCE, Scope.MODULE));
        context.fireEvent(EventContext.MODULE_START, null);
        Source source = (Source) context.getContext("source").getImplementationInstance();
        Assert.assertNotNull(source);
        Target targetRef = source.getTarget();
        Assert.assertNotNull(targetRef);
        Target target = (Target) context.getContext("target").getImplementationInstance();
        Assert.assertSame(target, targetRef);
        Assert.assertSame(target, source.getTarget());
        context.fireEvent(EventContext.MODULE_STOP, null);
        context.stop();
    }

    public void testModuleToStatelessScope() throws Exception {
        SystemAggregateContext context = createContext();
        context.start();
        context.registerModelObject(MockFactory.createSystemModuleWithWiredComponents("system.module",Scope.MODULE, Scope.INSTANCE));
        context.fireEvent(EventContext.MODULE_START, null);
        Source source = (Source) context.getContext("source").getImplementationInstance();
        Assert.assertNotNull(source);
        Target targetRef = source.getTarget();
        Assert.assertNotNull(targetRef);
        Target target = (Target) context.getContext("target").getImplementationInstance();
        Assert.assertNotSame(target, targetRef);
        Source source2 = (Source) context.getContext("source").getImplementationInstance();
        // should be the same since the module scope component was alreadyy created and the stateless 
        // component will be "attached" to it
        Assert.assertSame(source.getTarget(), source2.getTarget());
        context.fireEvent(EventContext.MODULE_STOP, null);
        context.stop();
    }

    public void testMultiplicity() throws Exception {
        SystemAggregateContext context = createContext();
        context.start();
        context.registerModelObject(MockFactory.createSystemModuleWithWiredComponents("system.module",Scope.MODULE, Scope.MODULE));
        context.fireEvent(EventContext.MODULE_START, null);
        Source source = (Source) context.getContext("source").getImplementationInstance();
        Assert.assertNotNull(source);
        Target target = (Target) context.getContext("target").getImplementationInstance();
        Assert.assertNotNull(target);
        // test setter injection
        List<Target> targets = source.getTargets();
        Assert.assertEquals(1,targets.size());
        assertSame(target,targets.get(0));
        
        // test field injection
        targets = source.getTargetsThroughField();
        Assert.assertEquals(1,targets.size());
        assertSame(target,targets.get(0));

        // test array injection
        Target[] targetArray = source.getArrayOfTargets();
        Assert.assertEquals(1,targetArray.length);
        assertSame(target,targetArray[0]);

    
    }

    private SystemAggregateContext createContext() {
        SystemAggregateContextImpl context = new SystemAggregateContextImpl();
        context.setName("system.context");
        context.setConfigurationContext(new MockConfigContext(MockFactory.createSystemBuilders()));
        return context;
    }



}
