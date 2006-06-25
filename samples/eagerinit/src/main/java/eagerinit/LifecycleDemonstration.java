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
import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;

/**
 * Temporary class to demonstrate component lifcycle
 *
 * @version $Rev$ $Date$
 */
public class LifecycleDemonstration {

    public static void main(String args[]) throws LoaderException {
        URL scdl = LifecycleDemonstration.class.getResource("/eagerinit.composite");
        assert scdl != null;

        Bootstrapper bootstrapper = new DefaultBootstrapper(new NullMonitorFactory(), XMLInputFactory.newInstance());
        Deployer deployer = bootstrapper.createDeployer();

        CompositeComponent parent = new CompositeComponentImpl("parent", null, null, new JDKWireService());

        SystemCompositeImplementation moduleImplementation = new SystemCompositeImplementation();
        moduleImplementation.setScdlLocation(scdl);
        moduleImplementation.setClassLoader(EagerInitImpl.class.getClassLoader());
        ComponentDefinition<SystemCompositeImplementation> moduleDefinition =
                new ComponentDefinition<SystemCompositeImplementation>("parent", moduleImplementation);
        deployer.deploy(parent, moduleDefinition);

/*

        Introspector introspector = bootstrapper.createIntrospector();
        ScopeRegistry scopeRegistry = bootstrapper.createScopeRegistry(new WorkContextImpl());

        // the following is done by the runtime bootstrapper
        CompositeComponent composite = new CompositeComponentImpl("composite", null, null, new JDKWireService());


        // setup builders
        JavaComponentBuilder builder = new JavaComponentBuilder();
        builder.setScopeRegistry(scopeRegistry);
        ScopeContainer moduleScope = scopeRegistry.getScopeContainer(Scope.MODULE);
        DeploymentContext context = new DeploymentContext(null, null, moduleScope);

        // mock reading SCDL
        PojoComponentType type = new PojoComponentType();
        type.setLifecycleScope(Scope.MODULE);
        JavaImplementation impl = new JavaImplementation();
        impl.setImplementationClass(EagerInitImpl.class);
        impl.setComponentType(type);
        ComponentDefinition<JavaImplementation> definition =
            new ComponentDefinition<JavaImplementation>("EagerInitComponent", impl);
        introspector.introspect(EagerInitImpl.class,type,context);

        // build component and register it with its scope container and composite
        AtomicComponent<?> component = builder.build(composite, definition, context);
        moduleScope.register(component);
        composite.register(component);

        // send start event to the scope, which will trigger the eager init
        moduleScope.onEvent(new CompositeStart(new Object(), composite));
        System.out.println("After composite start event");
        // locate service
        EagerInitService eager = (EagerInitService) composite.getChild("EagerInitComponent").getServiceInstance();
        System.out.println("Greeting: " +eager.getGreetings("Ciao"));
        // send stop event to the scope, which will trigger destroy
        moduleScope.onEvent(new CompositeStop(new Object(), composite));
*/
    }
}
