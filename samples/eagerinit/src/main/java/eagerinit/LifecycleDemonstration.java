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

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Scope;

import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.component.scope.HttpSessionScopeObjectFactory;
import org.apache.tuscany.core.component.scope.ModuleScopeObjectFactory;
import org.apache.tuscany.core.component.scope.RequestScopeObjectFactory;
import org.apache.tuscany.core.component.scope.ScopeRegistryImpl;
import org.apache.tuscany.core.component.scope.StatelessScopeObjectFactory;
import org.apache.tuscany.core.implementation.IntrospectionRegistryImpl;
import org.apache.tuscany.core.implementation.PojoComponentType;
import org.apache.tuscany.core.implementation.ProcessingException;
import org.apache.tuscany.core.implementation.processor.DestroyProcessor;
import org.apache.tuscany.core.implementation.processor.InitProcessor;
import org.apache.tuscany.core.implementation.processor.ScopeProcessor;
import org.apache.tuscany.core.implementation.processor.PropertyProcessor;
import org.apache.tuscany.core.implementation.processor.ReferenceProcessor;
import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;
import org.apache.tuscany.core.implementation.java.JavaComponentBuilder;
import org.apache.tuscany.core.implementation.java.JavaImplementation;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.apache.tuscany.core.wire.jdk.JDKWireService;

/**
 * Temporary class to demonstrate component lifecyclPojoAtomicComponente
 *
 * @version $Rev$ $Date$
 */
public class LifecycleDemonstration {

    public static void main(String args[]) throws ProcessingException {

        // the following is done by the runtime bootstrapper
        CompositeComponent composite = new CompositeComponentImpl("composite", null, null, new JDKWireService());

        ScopeRegistry scopeRegistry = new ScopeRegistryImpl(new WorkContextImpl());
        scopeRegistry.registerFactory(Scope.MODULE, new ModuleScopeObjectFactory());
        scopeRegistry.registerFactory(Scope.SESSION, new HttpSessionScopeObjectFactory());
        scopeRegistry.registerFactory(Scope.REQUEST, new RequestScopeObjectFactory());
        scopeRegistry.registerFactory(Scope.STATELESS, new StatelessScopeObjectFactory());

        IntrospectionRegistryImpl introspectionRegistry = new IntrospectionRegistryImpl();
        introspectionRegistry
            .setMonitor(new NullMonitorFactory().getMonitor(IntrospectionRegistryImpl.IntrospectionMonitor.class));
        introspectionRegistry.registerProcessor(new DestroyProcessor());
        introspectionRegistry.registerProcessor(new InitProcessor());
        introspectionRegistry.registerProcessor(new ScopeProcessor());
        introspectionRegistry.registerProcessor(new PropertyProcessor());
        introspectionRegistry.registerProcessor(new ReferenceProcessor());

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
        introspectionRegistry.introspect(EagerInitImpl.class,type,context);

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
    }
}
