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
package org.apache.tuscany.core.deployer;

import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.ComponentManager;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Include;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.bootstrap.Bootstrapper;
import org.apache.tuscany.core.bootstrap.DefaultBootstrapper;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.component.ComponentManagerImpl;
import org.apache.tuscany.core.component.scope.ScopeRegistryImpl;
import org.apache.tuscany.core.component.scope.CompositeScopeContainer;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.mock.component.BasicInterface;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.core.resolver.DefaultAutowireResolver;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * Verifies the default boostrap deployer
 *
 * @version $Rev$ $Date$
 */
public class BootstrapDeployerTestCase extends TestCase {
    private DeployerImpl deployer;
    private DeploymentContext deploymentContext;
    private ComponentDefinition<SystemCompositeImplementation> componentDefinition;
    private SystemCompositeImplementation implementation;
    private URI componentId;

    @SuppressWarnings("unchecked")
    public void testBoot1Load() throws LoaderException {
        Component parent = createNiceMock(Component.class);
        URI uri = URI.create("sca://parent");
        EasyMock.expect(parent.getUri()).andReturn(uri).atLeastOnce();
        EasyMock.replay(parent);
        URL scdl = BootstrapDeployerTestCase.class.getResource("boot1.scdl");
        implementation.setScdlLocation(scdl);
        deployer.load(parent, componentDefinition, deploymentContext);
        CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> composite =
            implementation.getComponentType();
        assertNotNull(composite);
        assertEquals(new QName("http://example.com", "boot1"), composite.getName());

        // check parse of <service>
        Map<String, ServiceDefinition> services = composite.getDeclaredServices();
        assertEquals(1, services.size()); // included doesn't count
        services = composite.getServices();
        assertEquals(2, services.size()); // included counts
        ServiceDefinition serviceDefinition = services.get("service");
        assertNotNull(serviceDefinition);
        assertEquals(componentId.resolve("#service"), serviceDefinition.getUri());
        assertEquals(BasicInterface.class, serviceDefinition.getServiceContract().getInterfaceClass());
        Collection<BindingDefinition> bindings = serviceDefinition.getBindings();
        assertTrue(bindings.isEmpty());

        // check parse of <component>
        Map<String, ComponentDefinition<? extends Implementation<?>>> components = composite.getDeclaredComponents();
        assertEquals(1, components.size()); // included doesn't count
        components = composite.getComponents();
        assertEquals(2, components.size()); // included counts        
        ComponentDefinition<? extends Implementation<?>> component = components.get("component");
        assertNotNull(component);
        PropertyValue<?> propVal = component.getPropertyValues().get("publicProperty");
        assertEquals("propval", propVal.getValueFactory().getInstance());

        // check introspection of implementation
        ComponentType<?, ?, ?> componentType = component.getImplementation().getComponentType();
        assertNotNull(componentType); // details checked in SystemComponentTypeLoaderTestCase

        // check included component
        Map<String, Include> includes = composite.getIncludes();
        assertEquals(1, includes.size());
        Include include = includes.get("boot1-include");
        assertNotNull(include);
        CompositeComponentType included = include.getIncluded();
        assertNotNull(included);
        assertEquals(1, included.getComponents().size());
    }

    public void testBoot1Deployment() throws Exception {
        URL scdl = BootstrapDeployerTestCase.class.getResource("boot1.scdl");
        implementation.setScdlLocation(scdl);
        Component parent = EasyMock.createMock(Component.class);
        replay(parent);
        // load the boot1 file using the bootstrap deployer
        componentDefinition.setUri(URI.create("sca://parent/simple"));
        Collection<Component> components = deployer.deploy(parent, componentDefinition);
        assertFalse(components.isEmpty());
        verify(parent);
    }

    public void testBoot2Deployment() throws Exception {
        URL scdl = BootstrapDeployerTestCase.class.getResource("boot2.scdl");
        implementation.setScdlLocation(scdl);
        Component parent = createNiceMock(Component.class);
        replay(parent);
        // load the boot2 file using the bootstrap deployer
        componentDefinition.setUri(URI.create("newDeployer"));
        Collection<Component> components = deployer.deploy(parent, componentDefinition);
        assertFalse(components.isEmpty());
        verify(parent);
        for (Component component : components) {
            component.start();
        }
    }

    @SuppressWarnings({"unchecked"})
    protected void setUp() throws Exception {
        super.setUp();
        componentId = URI.create("sca://localhost/parent");
        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
        DefaultAutowireResolver resolver = new DefaultAutowireResolver();
        ComponentManager manager = new ComponentManagerImpl(null, resolver);
        Connector connector = new ConnectorImpl(manager);
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        JavaServiceContract<ComponentManager> contract = registry.introspect(ComponentManager.class);
        manager.registerJavaObject(URI.create("ComponentManager"), contract, manager);
        NullMonitorFactory monitorFactory = new NullMonitorFactory();

        ScopeRegistry scopeRegistry = new ScopeRegistryImpl();
        CompositeScopeContainer scopeContainer =
            new CompositeScopeContainer(monitorFactory.getMonitor(ScopeContainerMonitor.class));
        scopeContainer.start();
        scopeRegistry.register(scopeContainer);

        Bootstrapper bootstrapper =
            new DefaultBootstrapper(monitorFactory, xmlFactory, manager, resolver, connector, scopeRegistry);
        deployer = (DeployerImpl) bootstrapper.createDeployer();
        deploymentContext = new RootDeploymentContext(null, null, componentId, xmlFactory, null, false);
        implementation = new SystemCompositeImplementation();
        implementation.setClassLoader(getClass().getClassLoader());
        componentDefinition = new ComponentDefinition<SystemCompositeImplementation>(implementation);
    }
}
