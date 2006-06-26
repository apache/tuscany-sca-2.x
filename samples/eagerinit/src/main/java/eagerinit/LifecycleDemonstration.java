/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
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
package eagerinit;

import java.net.URL;
import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.core.bootstrap.Bootstrapper;
import org.apache.tuscany.core.bootstrap.DefaultBootstrapper;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.bootstrap.RuntimeComponent;

/**
 * Temporary class to demonstrate component lifcycle
 *
 * @version $Rev$ $Date$
 */
public class LifecycleDemonstration {

    public static void main(String args[]) throws LoaderException {
        // locate the SCDL for the composite that we want to run
        URL scdl = LifecycleDemonstration.class.getResource("/eagerinit.composite");
        assert scdl != null;

        Bootstrapper bootstrapper = new DefaultBootstrapper(new NullMonitorFactory(), XMLInputFactory.newInstance());
        Deployer deployer = bootstrapper.createDeployer();

        // create and start the core runtime
        RuntimeComponent runtime = bootstrapper.createRuntime();
        runtime.start();

        // create a ComponentDefinition to represent the component we are going to deploy
        SystemCompositeImplementation moduleImplementation = new SystemCompositeImplementation();
        moduleImplementation.setScdlLocation(scdl);
        moduleImplementation.setClassLoader(EagerInitImpl.class.getClassLoader());
        ComponentDefinition<SystemCompositeImplementation> moduleDefinition =
                new ComponentDefinition<SystemCompositeImplementation>("eagerinit", moduleImplementation);

        // deploy the component into the system under the application root
        System.out.println("Deploying composite component");
        CompositeComponent root = runtime.getRootComponent();
        CompositeComponent<?> composite = (CompositeComponent<?>) deployer.deploy(root, moduleDefinition);

        // start the composite (which will fire the init method)
        System.out.println("Starting composite component");
        composite.start();

        // locate and invoke the service
        System.out.println("Locating and invoking service");
        EagerInitService eager = (EagerInitService) composite.getChild("EagerInitComponent").getServiceInstance();
        System.out.println("Greeting returned: " + eager.getGreetings("Ciao"));

        // stop the composite
        System.out.println("Stopping composite component");
        composite.stop();
    }
}
