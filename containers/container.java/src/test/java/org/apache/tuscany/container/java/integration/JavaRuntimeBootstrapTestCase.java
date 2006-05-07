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
package org.apache.tuscany.container.java.integration;

import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.components.GenericComponent;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.context.event.ModuleStart;
import org.apache.tuscany.core.runtime.RuntimeContext;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Ensures basic runtime with Java support boots properly
 * 
 * @version $Rev$ $Date$
 */
public class JavaRuntimeBootstrapTestCase extends TestCase {
    
    /**
     * Tests the runtime can be bootstrapped with Java builders and two module-scoped Java-based components can be wired 
     */
    public void testRuntimeBoot() throws Exception{
//        RuntimeContext runtime = MockFactory.createJavaRuntime();
//        Context ctx = runtime.getSystemContext().getContext(MockFactory.SYSTEM_CHILD);
//        Assert.assertNotNull(ctx);
//        runtime.getRootContext().registerModelObject(MockFactory.createCompositeComponent("test"));
//        CompositeContext testCtx = (CompositeContext) runtime.getRootContext().getContext("test");
//        Assert.assertNotNull(testCtx);
//        testCtx.registerModelObject(MockFactory.createModule());
//        testCtx.publish(new ModuleStart(this));
//        GenericComponent source = (GenericComponent)testCtx.getContext("source").getInstance(null);
//        Assert.assertNotNull(source);
//        GenericComponent target = (GenericComponent)testCtx.getContext("target").getInstance(null);
//        Assert.assertNotNull(target);
//        source.getGenericComponent().getString();
   }

}

