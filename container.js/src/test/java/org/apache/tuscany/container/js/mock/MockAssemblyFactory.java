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
        cService.initialize(new AssemblyModelContextImpl(null, null,null));
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

    // public static SimpleComponent createComponent(String name, String scriptFile, String serviceName, ScopeEnum
    // scope)
    // throws NoSuchMethodException, ClassNotFoundException {
    // SimpleComponent sc = new PojoSimpleComponent();
    // PojoJavaScriptImplementation impl = new PojoJavaScriptImplementation();
    // impl.setScriptFile(scriptFile);
    // impl.initialize(new AssemblyModelContextImpl());
    //
    // sc.setComponentImplementation(impl);
    // Service s = new PojoService();
    // s.setName(serviceName.substring(serviceName.lastIndexOf('.') + 1));
    // PojoJavaInterface ji = new PojoJavaInterface();
    // ji.setInterface(serviceName);
    // Class claz = JavaIntrospectionHelper.loadClass(serviceName);
    // PojoInterfaceType iType = new PojoInterfaceType();
    // iType.setInstanceClass(claz);
    // for (Method m : claz.getMethods()) {
    // // assume no method overloading
    // PojoOperationType type = new PojoOperationType();
    // type.setName(m.getName());
    // for (Class inputType : m.getParameterTypes()) {
    // type.setOutputType(new SDOType(null,null,inputType,Collections.EMPTY_LIST));
    // }
    // iType.addOperationType(type);
    // }
    // ji.setInterfaceType(iType);
    //
    //        
    // s.setInterfaceContract(ji);
    // ji.setScope(scope);
    // impl.getServices().add(s);
    // sc.setName(name);
    // sc.setComponentImplementation(impl);
    // PojoConfiguredService cService = new PojoConfiguredService();
    // cService.setService(s);
    // sc.getConfiguredServices().add(cService);
    // return sc;
    // }
    //
    // public static Component createSystemComponent(String name, String type, ScopeEnum scope) throws
    // NoSuchMethodException,
    // ClassNotFoundException {
    //
    // Class claz = JavaIntrospectionHelper.loadClass(type);
    // PojoComponent sc = null;
    // if (AggregateContext.class.isAssignableFrom(claz)) {
    // sc = new PojoAggregateComponent();
    // } else {
    // sc = new PojoSimpleComponent();
    // }
    // SystemImplementation impl = new PojoSystemImplementation();
    // impl.setClass(type);
    // sc.setComponentImplementation(impl);
    // Service s = new PojoService();
    // JavaInterface ji = new PojoJavaInterface();
    // s.setInterfaceContract(ji);
    // ji.setScope(scope);
    // impl.getServices().add(s);
    // sc.setName(name);
    // sc.setComponentImplementation(impl);
    // return sc;
    // }

}
