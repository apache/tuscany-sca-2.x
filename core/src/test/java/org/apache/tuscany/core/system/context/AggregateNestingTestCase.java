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

import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.mock.MockFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.Scope;

/**
 * Tests registering of arbirarily deep child aggregate contexts
 * 
 * @version $Rev$ $Date$
 */
public class AggregateNestingTestCase extends TestCase {

    /**
     * Tests registration of a 3-level deep hierarchy under the top-level system aggregate context 
     */
    public void testSystemContext() throws Exception {
        RuntimeContext runtime = MockFactory.createCoreRuntime();
        ModuleComponent child1 = createHierarchy();
        runtime.getSystemContext().registerModelObject(child1);
        AggregateContext child1Ctx = (AggregateContext) runtime.getSystemContext().getContext("child1");
        Assert.assertNotNull(child1Ctx);
        child1Ctx.fireEvent(EventContext.MODULE_START, null);
        analyzeLeafComponents(child1Ctx);
        AggregateContext child2Ctx = (AggregateContext) child1Ctx.getContext("child2");
        Assert.assertNotNull(child2Ctx);
        child2Ctx.fireEvent(EventContext.MODULE_START, null);
        analyzeLeafComponents(child2Ctx);
        AggregateContext child3Ctx = (AggregateContext) child2Ctx.getContext("child3");
        Assert.assertNotNull(child3Ctx);
        child3Ctx.fireEvent(EventContext.MODULE_START, null);
        analyzeLeafComponents(child3Ctx);
        
        Assert.assertNull(child1Ctx.getContext("child3")); // sanity check
    }

    /**
     * Tests registration of a 3-level deep hierarchy under the root application aggregate context 
     */
    public void testRootContext() throws Exception {
        RuntimeContext runtime = MockFactory.createCoreRuntime();
        ModuleComponent child1 = createHierarchy();
        runtime.getRootContext().registerModelObject(child1);
        AggregateContext child1Ctx = (AggregateContext) runtime.getRootContext().getContext("child1");
        Assert.assertNotNull(child1Ctx);
        child1Ctx.fireEvent(EventContext.MODULE_START, null);
        analyzeLeafComponents(child1Ctx);
        AggregateContext child2Ctx = (AggregateContext) child1Ctx.getContext("child2");
        Assert.assertNotNull(child2Ctx);
        child2Ctx.fireEvent(EventContext.MODULE_START, null);
        analyzeLeafComponents(child2Ctx);
        AggregateContext child3Ctx = (AggregateContext) child2Ctx.getContext("child3");
        Assert.assertNotNull(child3Ctx);
        child3Ctx.fireEvent(EventContext.MODULE_START, null);
        analyzeLeafComponents(child3Ctx);
        
        Assert.assertNull(child1Ctx.getContext("child3")); // sanity check
    }

    private ModuleComponent createHierarchy(){
        ModuleComponent child3 = MockFactory.createSystemModuleComponentWithWiredComponents("child3", Scope.MODULE, Scope.MODULE);
        ModuleComponent child2 = MockFactory.createSystemModuleComponentWithWiredComponents("child2", Scope.MODULE, Scope.MODULE);
        child2.getModuleImplementation().getComponents().add(child3);
        ModuleComponent child1 = MockFactory.createSystemModuleComponentWithWiredComponents("child1", Scope.MODULE, Scope.MODULE);
        child1.getModuleImplementation().getComponents().add(child2);
        return child1;
    }
    
    private void analyzeLeafComponents(AggregateContext ctx) throws Exception {
        Source source = (Source) ctx.locateInstance("source");
        Assert.assertNotNull(source);
        Target target = source.getTarget();
        Assert.assertNotNull(target);
    }
}
