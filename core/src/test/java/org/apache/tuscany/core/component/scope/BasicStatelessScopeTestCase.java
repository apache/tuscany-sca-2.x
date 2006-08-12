/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.component.WorkContext;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponent;
import org.apache.tuscany.core.mock.component.StatelessComponent;
import org.apache.tuscany.core.mock.component.StatelessComponentImpl;
import org.apache.tuscany.core.mock.factories.MockFactory;

/**
 * Unit tests for the module scope container
 *
 * @version $Rev$ $Date$
 */
public class BasicStatelessScopeTestCase extends TestCase {

    /**
     * Tests instance identity is properly maintained
     */
    public void testInstanceManagement() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        StatelessScopeContainer scope = new StatelessScopeContainer(ctx);
        scope.start();
        SystemAtomicComponent context1 =
            MockFactory.createAtomicComponent("comp1", scope, StatelessComponentImpl.class);
        scope.register(context1);
        SystemAtomicComponent context2 =
            MockFactory.createAtomicComponent("comp2", scope, StatelessComponentImpl.class);
        scope.register(context2);
        StatelessComponentImpl comp1 = (StatelessComponentImpl) scope.getInstance(context1);
        Assert.assertNotNull(comp1);
        StatelessComponentImpl comp2 = (StatelessComponentImpl) scope.getInstance(context2);
        Assert.assertNotNull(comp2);
        Assert.assertNotSame(comp1, comp2);
        scope.stop();
    }

    public void testRegisterContextAfterRequest() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        StatelessScopeContainer scope = new StatelessScopeContainer(ctx);

        scope.start();
        SystemAtomicComponent context1 =
            MockFactory.createAtomicComponent("comp1", scope, StatelessComponentImpl.class);
        scope.register(context1);
        StatelessComponent comp1 = (StatelessComponentImpl) scope.getInstance(context1);
        Assert.assertNotNull(comp1);
        SystemAtomicComponent context2 =
            MockFactory.createAtomicComponent("comp2", scope, StatelessComponentImpl.class);
        scope.register(context2);
        StatelessComponentImpl comp2 = (StatelessComponentImpl) scope.getInstance(context2);
        Assert.assertNotNull(comp2);
        scope.stop();
    }


    /**
     * Tests setting no components in the scope
     */
    public void testSetNullComponents() throws Exception {
        WorkContext ctx = new WorkContextImpl();
        StatelessScopeContainer scope = new StatelessScopeContainer(ctx);
        scope.start();
        scope.stop();
    }


}
