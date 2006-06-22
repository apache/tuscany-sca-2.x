/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.core.deployer;

import java.net.URL;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.bootstrap.DefaultBootstrapper;
import org.apache.tuscany.core.mock.component.BasicInterface;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.core.system.model.SystemBinding;
import org.apache.tuscany.core.system.model.SystemCompositeImplementation;

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

    public void testBoot1Load() throws LoaderException {
        URL scdl = BootstrapDeployerTestCase.class.getResource("boot1.scdl");
        implementation.setScdlLocation(scdl);
        deployer.load(componentDefinition, deploymentContext);
        CompositeComponentType composite = implementation.getComponentType();
        assertNotNull(composite);
        assertEquals("simple", composite.getName());

        // check parse of <service>
        Map<String, ServiceDefinition> services = composite.getServices();
        assertEquals(1, services.size());
        BoundServiceDefinition serviceDefinition = (BoundServiceDefinition) services.get("service");
        assertNotNull(serviceDefinition);
        assertEquals("service", serviceDefinition.getName());
        assertEquals(BasicInterface.class, serviceDefinition.getServiceContract().getInterfaceClass());
        assertTrue(serviceDefinition.getBinding() instanceof SystemBinding);

        // check parse of <component>
        Map<String, ComponentDefinition<? extends Implementation<?>>> components = composite.getComponents();
        assertEquals(1, components.size());
        ComponentDefinition<? extends Implementation<?>> component = components.get("component");
        assertNotNull(component);
        PropertyValue<?> propVal = component.getPropertyValues().get("publicProperty");
        assertEquals("propval", propVal.getValueFactory().getInstance());

        // check introspection of implementation
        ComponentType<?,?,?> componentType = component.getImplementation().getComponentType();
        ServiceDefinition service = componentType.getServices().get(BasicInterface.class.getName());
        assertEquals(BasicInterface.class, service.getServiceContract().getInterfaceClass());
        Property<?> property = componentType.getProperties().get("publicProperty");
        assertEquals(String.class, property.getJavaType());
        ReferenceDefinition referenceDefinition = componentType.getReferences().get("publicReference");
        assertEquals(BasicInterface.class, referenceDefinition.getServiceContract().getInterfaceClass());
    }

    protected void setUp() throws Exception {
        super.setUp();
        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
        DefaultBootstrapper bootstrapper = new DefaultBootstrapper(new NullMonitorFactory());
        deployer = (DeployerImpl) bootstrapper.createDeployer();
        deploymentContext = new DeploymentContext(getClass().getClassLoader(), xmlFactory, null);
        implementation = new SystemCompositeImplementation();
        componentDefinition = new ComponentDefinition<SystemCompositeImplementation>(implementation);
    }
}
