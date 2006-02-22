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
package org.apache.tuscany.core.system.builder.impl;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.core.builder.BuilderException;
import org.apache.tuscany.core.builder.RuntimeConfigurationBuilder;
import org.apache.tuscany.core.builder.impl.AssemblyVisitor;
import org.apache.tuscany.core.context.Context;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponent;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.SystemBinding;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.AssemblyModelObject;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredPort;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.RuntimeConfigurationHolder;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * Tests decorating a logical configuration model
 * 
 * @version $Rev$ $Date$
 */
public class AssemblyVisitorTestCase extends TestCase {

    private static final Object MARKER = new Object();

    private SystemAssemblyFactory factory = new SystemAssemblyFactoryImpl();
    private AssemblyModelContext assemblyContext = new AssemblyModelContextImpl(null,null);
     
    public void testModelVisit() throws Exception {
        Component component = factory.createSimpleComponent();
        SystemImplementation impl = factory.createSystemImplementation();
        impl.setComponentType(factory.createComponentType());
        component.setComponentImplementation(impl);
        ConfiguredReference cRef = factory.createConfiguredReference();
        Reference ref = factory.createReference();
        cRef.setReference(ref);
        component.getConfiguredReferences().add(cRef);

        EntryPoint ep = factory.createEntryPoint();
        JavaServiceContract contract = factory.createJavaServiceContract();
        contract.setInterface(ModuleScopeSystemComponent.class);
        Service service = factory.createService();
        service.setServiceContract(contract);
        ConfiguredService cService = factory.createConfiguredService();
        cService.setService(service);
        cService.initialize(assemblyContext);
        ep.setConfiguredService(cService);
        SystemBinding binding = factory.createSystemBinding();
        ep.getBindings().add(binding);
        ConfiguredReference cEpRef = factory.createConfiguredReference();
        Reference epRef = factory.createReference();
        cEpRef.setReference(epRef);
        ep.setConfiguredReference(cEpRef);
        
        ep.initialize(assemblyContext);
        Module module = factory.createModule();
        module.getComponents().add(component);
        module.getEntryPoints().add(ep);

        List<RuntimeConfigurationBuilder> builders = new ArrayList();
        builders.add(new TestBuilder());
        AssemblyVisitor visitor = new AssemblyVisitor(null, builders);
        module.initialize(assemblyContext);
        visitor.start(module);

        Assert.assertSame(MARKER, impl.getRuntimeConfiguration());
        Assert.assertSame(MARKER, cRef.getRuntimeConfiguration());
        Assert.assertSame(MARKER, cRef.getProxyFactory());
        Assert.assertSame(MARKER, binding.getRuntimeConfiguration());
        Assert.assertSame(MARKER, cEpRef.getRuntimeConfiguration());
        Assert.assertSame(MARKER, cEpRef.getProxyFactory());
        Assert.assertSame(MARKER, module.getRuntimeConfiguration());

    }

    private static class TestBuilder implements RuntimeConfigurationBuilder {
        public void build(AssemblyModelObject model, Context context) throws BuilderException {
            if (model instanceof ConfiguredPort) {
                ((ConfiguredPort) model).setProxyFactory(MARKER);
            }
            if (model instanceof RuntimeConfigurationHolder) {
                ((RuntimeConfigurationHolder) model).setRuntimeConfiguration(MARKER);
            }
        }

    }

}
