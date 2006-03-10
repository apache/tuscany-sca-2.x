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
package org.apache.tuscany.container.js.mock;

import org.apache.tuscany.container.js.assembly.JavaScriptAssemblyFactory;
import org.apache.tuscany.container.js.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.js.assembly.impl.JavaScriptAssemblyFactoryImpl;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.AggregateContext;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.SystemImplementation;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.SimpleComponent;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * Generates test components and module assemblies
 * 
 * @version $Rev: 377775 $ $Date: 2006-02-14 09:18:31 -0800 (Tue, 14 Feb 2006) $
 */
public class MockAssemblyFactory {

    private static JavaScriptAssemblyFactory factory = new JavaScriptAssemblyFactoryImpl();

    private static SystemAssemblyFactory systemFactory = new SystemAssemblyFactoryImpl();

    public static SimpleComponent createComponent(String name, String scriptFile, Class type, Scope scope) {
        SimpleComponent sc = factory.createSimpleComponent();
        JavaScriptImplementation impl = factory.createJavaScriptImplementation();
        impl.setComponentType(factory.createComponentType());
        impl.setScriptFile(scriptFile);
        sc.setComponentImplementation(impl);
        Service s = factory.createService();
        String serviceName = type.getName().substring(type.getName().lastIndexOf('.')+1);
        s.setName(serviceName);
        JavaServiceContract contract = factory.createJavaServiceContract();
        s.setServiceContract(contract);
        contract.setScope(scope);
        contract.setInterface(type);
        impl.getComponentType().getServices().add(s);
        ConfiguredService cService = factory.createConfiguredService();
        cService.setService(s);
        cService.initialize(new AssemblyModelContextImpl(null,null));
        sc.getConfiguredServices().add(cService);
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
}
