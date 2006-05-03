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

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.core.builder.ContextFactoryBuilder;
import org.apache.tuscany.core.builder.impl.AssemblyVisitorImpl;
import org.apache.tuscany.core.mock.component.ModuleScopeSystemComponent;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.SystemBinding;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyObject;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentInfo;
import org.apache.tuscany.model.assembly.ConfiguredPort;
import org.apache.tuscany.model.assembly.ConfiguredReference;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ContextFactoryHolder;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Reference;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.impl.AssemblyContextImpl;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * Tests decorating a logical configuration model
 * 
 * @version $Rev$ $Date$
 */
public class AssemblyVisitorTestCase extends TestCase {

    private static final Object MARKER = new Object();

    private SystemAssemblyFactory factory = new SystemAssemblyFactoryImpl();
    private AssemblyContext assemblyContext = new AssemblyContextImpl(factory, null, null);

    public void testModelVisit() throws Exception {
        ComponentInfo componentType;
        Service service;
        SystemImplementation impl;
        Component component;

        Module module = factory.createModule();

        // create target component
        componentType = factory.createComponentInfo();
        service = factory.createService();
        service.setName("target");
        componentType.getServices().add(service);
        impl = factory.createSystemImplementation();
        impl.setComponentInfo(componentType);
        component = factory.createSimpleComponent();
        component.setName("target");
        component.setImplementation(impl);
        component.initialize(assemblyContext);
        module.getComponents().add(component);

        // create source component
        componentType = factory.createComponentInfo();
        Reference ref = factory.createReference();
        ref.setName("ref");
        componentType.getReferences().add(ref);
        impl = factory.createSystemImplementation();
        impl.setComponentInfo(componentType);
        component = factory.createSimpleComponent();
        component.setName("source");
        component.setImplementation(impl);
        ConfiguredReference cRef = factory.createConfiguredReference("ref", "target");
        component.getConfiguredReferences().add(cRef);
        component.initialize(assemblyContext);
        module.getComponents().add(component);

        EntryPoint ep = factory.createEntryPoint();
        JavaServiceContract contract = factory.createJavaServiceContract();
        contract.setInterface(ModuleScopeSystemComponent.class);
        service = factory.createService();
        service.setServiceContract(contract);
        ConfiguredService cService = factory.createConfiguredService();
        cService.setPort(service);
        cService.initialize(assemblyContext);
        ep.setConfiguredService(cService);
        SystemBinding binding = factory.createSystemBinding();
        ep.getBindings().add(binding);
        ConfiguredReference cEpRef = factory.createConfiguredReference();
        Reference epRef = factory.createReference();
        cEpRef.setPort(epRef);
        ep.setConfiguredReference(cEpRef);
        ep.initialize(assemblyContext);
        module.getEntryPoints().add(ep);

        List<ContextFactoryBuilder> builders = new ArrayList<ContextFactoryBuilder>();
        builders.add(new TestBuilder());
        AssemblyVisitorImpl visitor = new AssemblyVisitorImpl(builders);
        module.initialize(assemblyContext);
        visitor.start(module);

        Assert.assertSame(MARKER, component.getContextFactory());
        Assert.assertSame(MARKER, cRef.getProxyFactory());
        Assert.assertSame(MARKER, ep.getContextFactory());
        Assert.assertSame(MARKER, cEpRef.getProxyFactory());

    }

    private static class TestBuilder implements ContextFactoryBuilder {
        public void build(AssemblyObject model) throws BuilderException {
            if (model instanceof ConfiguredPort) {
                ((ConfiguredPort) model).setProxyFactory(MARKER);
            }
            if (model instanceof ContextFactoryHolder) {
                ((ContextFactoryHolder) model).setContextFactory(MARKER);
            }
        }

    }

}
