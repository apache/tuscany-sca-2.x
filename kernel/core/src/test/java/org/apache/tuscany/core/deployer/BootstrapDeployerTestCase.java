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

import java.net.URL;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.spi.bootstrap.ComponentNames;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.deployer.DeploymentContext;
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
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.mock.component.BasicInterface;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.core.component.ComponentManager;
import org.apache.tuscany.core.component.ComponentManagerImpl;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import org.easymock.EasyMock;

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

    @SuppressWarnings("unchecked")
    public void testBoot1Load() throws LoaderException {
        CompositeComponent parent = createNiceMock(CompositeComponent.class);
        URI uri = URI.create("parent");
        EasyMock.expect(parent.getUri()).andReturn(uri).atLeastOnce();
        EasyMock.replay(parent);
        URL scdl = BootstrapDeployerTestCase.class.getResource("boot1.scdl");
        implementation.setScdlLocation(scdl);
        deployer.load(parent, componentDefinition, deploymentContext);
        CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> composite =
            implementation.getComponentType();
        assertNotNull(composite);
        assertEquals("boot1", composite.getName());

        // check parse of <service>
        Map<String, ServiceDefinition> services = composite.getDeclaredServices();
        assertEquals(1, services.size()); // included doesn't count
        services = composite.getServices();
        assertEquals(2, services.size()); // included counts
        ServiceDefinition serviceDefinition = services.get("service");
        assertNotNull(serviceDefinition);
        assertEquals("parent#service", serviceDefinition.getUri().toString());
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
        CompositeComponent parent = createNiceMock(CompositeComponent.class);
        URI uri = URI.create("parent");
        EasyMock.expect(parent.getUri()).andReturn(uri).atLeastOnce();
        parent.register(isA(SCAObject.class));
        replay(parent);
        // load the boot1 file using the bootstrap deployer
        componentDefinition.setName(new URI("simple"));
        Component component = deployer.deploy(parent, componentDefinition);
        assertNotNull(component);
        verify(parent);
    }

    public void testBoot2Deployment() throws Exception {
        URL scdl = BootstrapDeployerTestCase.class.getResource("boot2.scdl");
        implementation.setScdlLocation(scdl);
        CompositeComponent parent = createNiceMock(CompositeComponent.class);
        URI uri = URI.create("parent");
        EasyMock.expect(parent.getUri()).andReturn(uri).atLeastOnce();
        parent.register(isA(SCAObject.class));
        replay(parent);

        // load the boot2 file using the bootstrap deployer
        componentDefinition.setName(new URI("newDeployer"));
        CompositeComponent component = (CompositeComponent) deployer.deploy(parent, componentDefinition);
        assertNotNull(component);
        verify(parent);
        component.start();
        SCAObject newDeployer = component.getSystemChild(ComponentNames.TUSCANY_DEPLOYER);
        assertNotNull(newDeployer);
        SCAObject wireService = component.getSystemChild(ComponentNames.TUSCANY_WIRE_SERVICE);
        assertNotNull(wireService);

/*      // FIXME
        // load the boot2 file using the newly loaded deployer
        parent.reset();
        parent.expects(once()).method("register").withAnyArguments();
        componentDefinition.setName("newDeployer2");
        component = newDeployer.deploy((CompositeComponent) parent.proxy(), componentDefinition);
        assertNotNull(component);
        parent.verify();
        component.start();
        Deployer newDeployer2 = (Deployer) component.getServiceInstance("deployer");
        assertNotNull(newDeployer2);
*/
    }

    protected void setUp() throws Exception {
        super.setUp();
        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
        ComponentManager manager = new ComponentManagerImpl();
        Bootstrapper bootstrapper = new DefaultBootstrapper(new NullMonitorFactory(), xmlFactory, manager, null);
        deployer = (DeployerImpl) bootstrapper.createDeployer();
        deploymentContext = new RootDeploymentContext(null, xmlFactory, null, null);
        implementation = new SystemCompositeImplementation();
        implementation.setClassLoader(getClass().getClassLoader());
        componentDefinition = new ComponentDefinition<SystemCompositeImplementation>(implementation);
    }
}
