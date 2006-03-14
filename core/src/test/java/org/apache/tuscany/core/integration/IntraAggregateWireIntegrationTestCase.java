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
package org.apache.tuscany.core.integration;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.context.EventContext;
import org.apache.tuscany.core.mock.MockFactory;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.runtime.RuntimeContext;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.core.system.context.SystemAggregateContextImpl;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * Tests intra-aggregate system wires are properly constructed in the runtime
 * 
 * @version $Rev$ $Date$
 */
public class IntraAggregateWireIntegrationTestCase extends TestCase {

  
    public void testWireConstruction2() throws Exception {
        RuntimeContext runtime = MockFactory.createCoreRuntime();
        ModuleComponent moduleComponent = createSystemAggregateComponent("test.system");
        Module module = MockFactory.createSystemModuleWithWiredComponents(Scope.MODULE, Scope.MODULE);
        moduleComponent.setModuleImplementation(module);
        runtime.getSystemContext().registerModelObject(moduleComponent);
        AggregateContext context = (AggregateContext) runtime.getSystemContext().getContext("test.system").getImplementationInstance();
        context.fireEvent(EventContext.MODULE_START, null);
        context.registerModelObject(module);
        Source source = (Source) context.getContext("source").getImplementationInstance();
        Assert.assertNotNull(source);
        Target targetRef = source.getTarget();
        Assert.assertNotNull(targetRef);
        Target target = (Target) context.getContext("target").getImplementationInstance();
        Assert.assertSame(target, targetRef);
        Source source2 = (Source) context.getContext("source").getImplementationInstance();
        Assert.assertSame(target, source2.getTarget());
        context.fireEvent(EventContext.MODULE_STOP, null);
        context.stop();
    }

    private static SystemAssemblyFactory systemFactory = new SystemAssemblyFactoryImpl();

    /**
     * Creates an aggregate component with the given name
     */
    public static ModuleComponent createSystemAggregateComponent(String name) {
        ModuleComponent sc = systemFactory.createModuleComponent();
        SystemImplementation impl = systemFactory.createSystemImplementation();
        impl.setImplementationClass(SystemAggregateContextImpl.class);
        sc.setComponentImplementation(impl);
        Service s = systemFactory.createService();
        JavaServiceContract ji = systemFactory.createJavaServiceContract();
        s.setServiceContract(ji);
        ji.setScope(Scope.AGGREGATE);
        impl.setComponentType(systemFactory.createComponentType());
        impl.getComponentType().getServices().add(s);
        sc.setName(name);
        sc.setComponentImplementation(impl);
        return sc;
    }
    
    
    public void testWireConstruction() throws ConfigurationException {
        RuntimeContext runtime = MockFactory.createCoreRuntime();
        runtime.getSystemContext().registerModelObject(MockFactory.createSystemAggregateComponent("test.system"));
        AggregateContext context = (AggregateContext) runtime.getSystemContext().getContext("test.system").getImplementationInstance();

        context.fireEvent(EventContext.MODULE_START, null);
        context.registerModelObject(MockFactory.createSystemModuleWithWiredComponents(Scope.MODULE,Scope.MODULE));
        Source source = (Source) context.getContext("source").getImplementationInstance();
        Assert.assertNotNull(source);
        Target targetRef = source.getTarget();
        Assert.assertNotNull(targetRef);
        Target target = (Target) context.getContext("target").getImplementationInstance();
        Assert.assertSame(target, targetRef);
        Source source2 = (Source) context.getContext("source").getImplementationInstance();
        Assert.assertSame(target, source2.getTarget());
        context.fireEvent(EventContext.MODULE_STOP, null);
        context.stop();
    }
}
