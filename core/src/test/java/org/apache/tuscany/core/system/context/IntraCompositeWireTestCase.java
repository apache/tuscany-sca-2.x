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

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.context.AtomicContext;
import org.apache.tuscany.core.context.SystemCompositeContext;
import org.apache.tuscany.core.context.event.ModuleStartEvent;
import org.apache.tuscany.core.context.event.ModuleStopEvent;
import org.apache.tuscany.core.mock.MockConfigContext;
import org.apache.tuscany.core.mock.MockFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.model.assembly.Scope;

import java.util.List;

/**
 * Tests intra-composite system component wiring scenarios
 * 
 * @version $Rev$ $Date$
 */
public class IntraCompositeWireTestCase extends TestCase {

    public void testModuleToModuleScope() throws Exception {
        SystemCompositeContext context = createContext();
        context.start();
        context.registerModelObject(MockFactory.createSystemModuleWithWiredComponents("system.module",Scope.MODULE, Scope.MODULE));
        context.publish(new ModuleStartEvent(this));
        Source source = (Source) ((AtomicContext) context.getContext("source")).getTargetInstance();
        Assert.assertNotNull(source);
        Target targetRef = source.getTarget();
        Assert.assertNotNull(targetRef);
        Target target = (Target) ((AtomicContext) context.getContext("target")).getTargetInstance();
        Assert.assertSame(target, targetRef);
        Assert.assertSame(target, source.getTarget());
        context.publish(new ModuleStopEvent(this));
        context.stop();
    }

    public void testStatelessToModuleScope() throws Exception {
        SystemCompositeContext context = createContext();
        context.start();
        context.registerModelObject(MockFactory.createSystemModuleWithWiredComponents("system.module",Scope.INSTANCE, Scope.MODULE));
        context.publish(new ModuleStartEvent(this));
        Source source = (Source) ((AtomicContext) context.getContext("source")).getTargetInstance();
        Assert.assertNotNull(source);
        Target targetRef = source.getTarget();
        Assert.assertNotNull(targetRef);
        source = (Source) ((AtomicContext) context.getContext("source")).getTargetInstance();
        Target target = (Target) ((AtomicContext) context.getContext("target")).getTargetInstance();
        Assert.assertSame(target, targetRef);
        Assert.assertSame(target, source.getTarget());
        context.publish(new ModuleStopEvent(this));
        context.stop();
    }

    public void testModuleToStatelessScope() throws Exception {
        SystemCompositeContext context = createContext();
        context.start();
        context.registerModelObject(MockFactory.createSystemModuleWithWiredComponents("system.module",Scope.MODULE, Scope.INSTANCE));
        context.publish(new ModuleStartEvent(this));
        Source source = (Source) ((AtomicContext) context.getContext("source")).getTargetInstance();
        Assert.assertNotNull(source);
        Target targetRef = source.getTarget();
        Assert.assertNotNull(targetRef);
        Target target = (Target) ((AtomicContext) context.getContext("target")).getTargetInstance();
        Assert.assertNotSame(target, targetRef);
        Source source2 = (Source) ((AtomicContext) context.getContext("source")).getTargetInstance();
        // should be the same since the module scope component was alreadyy created and the stateless
        // component will be "attached" to it
        Assert.assertSame(source.getTarget(), source2.getTarget());
        context.publish(new ModuleStopEvent(this));
        context.stop();
    }

    public void testMultiplicity() throws Exception {
        SystemCompositeContext context = createContext();
        context.start();
        context.registerModelObject(MockFactory.createSystemModuleWithWiredComponents("system.module",Scope.MODULE, Scope.MODULE));
        context.publish(new ModuleStartEvent(this));
        Source source = (Source) ((AtomicContext) context.getContext("source")).getTargetInstance();
        Assert.assertNotNull(source);
        Target target = (Target) ((AtomicContext) context.getContext("target")).getTargetInstance();
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

    private SystemCompositeContext createContext() {
        SystemCompositeContextImpl context = new SystemCompositeContextImpl();
        context.setName("system.context");
        context.setConfigurationContext(new MockConfigContext(MockFactory.createSystemBuilders()));
        return context;
    }



}
