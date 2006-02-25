/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.container.java.mock;

import org.apache.tuscany.container.java.assembly.JavaAssemblyFactory;
import org.apache.tuscany.container.java.assembly.JavaImplementation;
import org.apache.tuscany.container.java.assembly.impl.JavaAssemblyFactoryImpl;
import org.apache.tuscany.container.java.mock.binding.foo.FooBinding;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.SimpleComponent;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * Generates test components and module assemblies
 * 
 * @version $Rev$ $Date$
 */
public class MockAssemblyFactory {

    private static JavaAssemblyFactory factory = new JavaAssemblyFactoryImpl();

    private static SystemAssemblyFactory systemFactory = new SystemAssemblyFactoryImpl();

    public static SimpleComponent createComponent(String name, Class type, Scope scope) throws NoSuchMethodException {
        SimpleComponent sc = factory.createSimpleComponent();
        JavaImplementation impl = factory.createJavaImplementation();
        impl.setComponentType(factory.createComponentType());
        impl.setImplementationClass(type);
        sc.setComponentImplementation(impl);
        Service s = factory.createService();
        JavaServiceContract ji = factory.createJavaServiceContract();
        s.setServiceContract(ji);
        ji.setScope(scope);
        impl.getComponentType().getServices().add(s);
        sc.setName(name);
        sc.setComponentImplementation(impl);
        return sc;
    }

    public static Component createSystemComponent(String name, String type, Scope scope) throws NoSuchMethodException,
            ClassNotFoundException {
        Class claz = JavaIntrospectionHelper.loadClass(type);
        Component sc = null;
        if (AggregateContext.class.isAssignableFrom(claz)) {
            sc = systemFactory.createModuleComponent();
        } else {
            sc = systemFactory.createSimpleComponent();
        }
        SystemImplementation impl = systemFactory.createSystemImplementation();
        impl.setImplementationClass(claz);
        sc.setComponentImplementation(impl);
        Service s = systemFactory.createService();
        JavaServiceContract ji = systemFactory.createJavaServiceContract();
        s.setServiceContract(ji);
        ji.setScope(scope);
        impl.setComponentType(systemFactory.createComponentType());
        impl.getComponentType().getServices().add(s);
        sc.setName(name);
        sc.setComponentImplementation(impl);
        return sc;
    }

    /**
     * Creates an external service configured with the 'Foo' test binding
     */
    public static ExternalService createFooBindingExternalService(String name, Class interfaz) throws NoSuchMethodException,
            ClassNotFoundException {
        ExternalService es = factory.createExternalService();
        es.setName(name);
        Service s = factory.createService();
        JavaServiceContract ji = factory.createJavaServiceContract();
        ji.setScope(Scope.MODULE);
        ji.setInterface(interfaz);
        s.setServiceContract(ji);
        ConfiguredService configuredService = factory.createConfiguredService();
        es.setConfiguredService(configuredService);

        FooBinding binding = new FooBinding();
        es.getBindings().add(binding);
        return es;
    }

}
