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

import java.io.InputStream;
import java.io.IOException;

import org.apache.tuscany.container.js.assembly.JavaScriptAssemblyFactory;
import org.apache.tuscany.container.js.assembly.JavaScriptImplementation;
import org.apache.tuscany.container.js.assembly.impl.JavaScriptAssemblyFactoryImpl;
import org.apache.tuscany.core.config.JavaIntrospectionHelper;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.AtomicComponent;
import org.apache.tuscany.model.assembly.impl.AssemblyContextImpl;
import org.apache.tuscany.model.types.java.JavaServiceContract;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;

/**
 * Generates test components and module assemblies
 * 
 * @version $Rev: 377775 $ $Date: 2006-02-14 09:18:31 -0800 (Tue, 14 Feb 2006) $
 */
public class MockAssemblyFactory {

    private static JavaScriptAssemblyFactory factory = new JavaScriptAssemblyFactoryImpl();

    private static SystemAssemblyFactory systemFactory = new SystemAssemblyFactoryImpl();

    public static AtomicComponent createComponent(String name, String scriptFile, Class type, Scope scope) {
        AtomicComponent sc = factory.createSimpleComponent();
        JavaScriptImplementation impl = factory.createJavaScriptImplementation();
        impl.setComponentInfo(factory.createComponentInfo());
        impl.setScriptFile(scriptFile);
        impl.setScript(readScript(type.getClassLoader().getResourceAsStream(scriptFile)));
        impl.setResourceLoader(new ResourceLoaderImpl(type.getClassLoader()));
        sc.setImplementation(impl);
        Service s = factory.createService();
        String serviceName = type.getName().substring(type.getName().lastIndexOf('.')+1);
        s.setName(serviceName);
        JavaServiceContract contract = factory.createJavaServiceContract();
        s.setServiceContract(contract);
        contract.setScope(scope);
        contract.setInterface(type);
        impl.getComponentInfo().getServices().add(s);
        ConfiguredService cService = factory.createConfiguredService();
        cService.setPort(s);
        cService.initialize(new AssemblyContextImpl(null,null));
        sc.getConfiguredServices().add(cService);
        sc.setName(name);
        sc.setImplementation(impl);
        return sc;
    }

    public static Component createSystemComponent(String name, String type, Scope scope) throws NoSuchMethodException,
            ClassNotFoundException {
        Class claz = JavaIntrospectionHelper.loadClass(type);
        Component sc = null;
        if (CompositeContext.class.isAssignableFrom(claz)) {
            sc = systemFactory.createModuleComponent();
        } else {
            sc = systemFactory.createSimpleComponent();
        }
        Module impl = systemFactory.createModule();
        impl.setName(name);
        sc.setImplementation(impl);
        Service s = systemFactory.createService();
        JavaServiceContract ji = systemFactory.createJavaServiceContract();
        s.setServiceContract(ji);
        ji.setScope(scope);
        impl.setComponentInfo(systemFactory.createComponentInfo());
        impl.getComponentInfo().getServices().add(s);
        sc.setName(name);
        sc.setImplementation(impl);
        return sc;
    }

    private static String readScript(InputStream is) {
        try {
            StringBuilder sb = new StringBuilder(1024);
            int n;
            while ((n = is.read()) != -1) {
                sb.append((char) n);
            }
            is.close();
            return sb.toString();
        } catch (IOException e) {
            throw new AssertionError();
        }
    }
}
