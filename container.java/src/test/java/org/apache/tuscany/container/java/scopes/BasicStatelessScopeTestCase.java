/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.container.java.scopes;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.container.java.builder.JavaComponentContextBuilder;
import org.apache.tuscany.container.java.mock.MockFactory;
import org.apache.tuscany.container.java.mock.components.StatelessComponent;
import org.apache.tuscany.container.java.mock.components.StatelessComponentImpl;
import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.RuntimeConfiguration;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.context.InstanceContext;
import org.apache.tuscany.core.context.impl.EventContextImpl;
import org.apache.tuscany.core.context.scope.StatelessScopeContext;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.SimpleComponent;

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
        EventContext ctx = new EventContextImpl();
        StatelessScopeContext scope = new StatelessScopeContext(ctx);
        scope.registerConfigurations(createConfigurations());
        scope.start();
        // first request
        StatelessComponentImpl comp1 = (StatelessComponentImpl) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        // second request
        StatelessComponentImpl comp2 = (StatelessComponentImpl) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp2);
        Assert.assertNotSame(comp1, comp2);
        scope.stop();
    }

    public void testRegisterContextBeforeRequest() throws Exception {
        EventContext ctx = new EventContextImpl();
        StatelessScopeContext scope = new StatelessScopeContext(ctx);
        scope.registerConfigurations(createConfigurations());
        scope.registerConfiguration(createConfiguration("NewTestService"));
        scope.start();
        StatelessComponent comp1 = (StatelessComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        StatelessComponent comp2 = (StatelessComponent) scope.getContext("NewTestService").getInstance(null);
        Assert.assertNotNull(comp2);
        scope.stop();
    }

    public void testRegisterContextAfterRequest() throws Exception {
        EventContext ctx = new EventContextImpl();
        StatelessScopeContext scope = new StatelessScopeContext(ctx);
        scope.registerConfigurations(createConfigurations());
        scope.start();
        StatelessComponent comp1 = (StatelessComponent) scope.getContext("TestService1").getInstance(null);
        Assert.assertNotNull(comp1);
        scope.registerConfiguration(createConfiguration("NewTestService"));
        StatelessComponent comp2 = (StatelessComponent) scope.getContext("NewTestService").getInstance(null);
        Assert.assertNotNull(comp2);
        scope.stop();
    }

    
    /**
     * Tests setting no components in the scope
     */
    public void testSetNullComponents() throws Exception {
        EventContext ctx = new EventContextImpl();
        StatelessScopeContext scope = new StatelessScopeContext(ctx);
        scope.registerConfigurations(createConfigurations());
        scope.start();
        scope.stop();
    }

    // ----------------------------------
    // Private methods
    // ----------------------------------

    JavaComponentContextBuilder builder = new JavaComponentContextBuilder();

    private List<RuntimeConfiguration<InstanceContext>> createConfigurations()
            throws NoSuchMethodException, BuilderException {
        SimpleComponent component = MockFactory.createComponent("TestService1", StatelessComponentImpl.class,
                Scope.INSTANCE);
        builder.build(component, null);
        List<RuntimeConfiguration<InstanceContext>> configs = new ArrayList();
        configs.add((RuntimeConfiguration<InstanceContext>) component.getComponentImplementation().getRuntimeConfiguration());
        return configs;
    }

    private RuntimeConfiguration<InstanceContext> createConfiguration(String name)
            throws NoSuchMethodException, BuilderException {
        SimpleComponent component = MockFactory.createComponent(name, StatelessComponentImpl.class,
                Scope.INSTANCE);
        builder.build(component, null);
        return (RuntimeConfiguration<InstanceContext>) component.getComponentImplementation().getRuntimeConfiguration();
    }

}
