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

import junit.framework.TestCase;

import org.apache.tuscany.core.bootstrap.DefaultBootstrapper;
import org.apache.tuscany.core.mock.component.BasicInterface;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.core.system.model.SystemBinding;
import org.apache.tuscany.core.system.model.SystemCompositeImplementation;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.BoundService;
import org.apache.tuscany.spi.model.Component;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Service;

/**
 * @version $Rev$ $Date$
 */
public class BootstrapDeployerTestCase extends TestCase {
    private DeployerImpl deployer;
    private DeploymentContext deploymentContext;
    private Component<SystemCompositeImplementation> component;
    private SystemCompositeImplementation implementation;

    public void testBoot1() throws LoaderException {
        URL scdl = BootstrapDeployerTestCase.class.getResource("boot1.scdl");
        implementation.setScdlLocation(scdl);
        deployer.load(component, deploymentContext);
        CompositeComponentType componentType = implementation.getComponentType();
        assertNotNull(componentType);
        assertEquals("simple", componentType.getName());
        Map<String, Service> services = componentType.getServices();
        assertEquals(1, services.size());
        BoundService service = (BoundService) services.get("service");
        assertNotNull(service);
        assertEquals("service", service.getName());
        assertEquals(BasicInterface.class, service.getServiceContract().getInterfaceClass());
        assertTrue(service.getBinding() instanceof SystemBinding);
    }

    protected void setUp() throws Exception {
        super.setUp();
        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
        DefaultBootstrapper bootstrapper = new DefaultBootstrapper(new NullMonitorFactory());
        deployer = (DeployerImpl) bootstrapper.createDeployer();
        deploymentContext = new DeploymentContext(getClass().getClassLoader(), xmlFactory, null);
        implementation = new SystemCompositeImplementation();
        component = new Component<SystemCompositeImplementation>(implementation);
    }
}
