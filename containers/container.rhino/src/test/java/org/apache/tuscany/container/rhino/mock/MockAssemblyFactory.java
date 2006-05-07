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
package org.apache.tuscany.container.rhino.mock;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.container.rhino.assembly.JavaScriptImplementation;
import org.apache.tuscany.core.config.ComponentTypeIntrospector;
import org.apache.tuscany.core.config.ConfigurationLoadException;
import org.apache.tuscany.core.config.processor.ProcessorUtils;
import org.apache.tuscany.core.context.CompositeContext;
import org.apache.tuscany.core.system.assembly.SystemAssemblyFactory;
import org.apache.tuscany.core.system.assembly.impl.SystemAssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.AtomicComponent;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ConfiguredService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.Service;
import org.apache.tuscany.model.assembly.impl.AssemblyContextImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.types.java.JavaServiceContract;

/**
 * Generates test components and module assemblies
 *
 * @version $Rev: 377775 $ $Date: 2006-02-14 09:18:31 -0800 (Tue, 14 Feb 2006) $
 */
public class MockAssemblyFactory {

    private static AssemblyFactoryImpl factory = new AssemblyFactoryImpl();

    private static SystemAssemblyFactory systemFactory = new SystemAssemblyFactoryImpl();
    private static ComponentTypeIntrospector introspector;

    public static ComponentTypeIntrospector getIntrospector() {
        if (introspector == null) {
            introspector = ProcessorUtils.createCoreIntrospector(systemFactory);
        }
        return introspector;
    }

    public static AtomicComponent createComponent(String name, String scriptFile, Class type, Scope scope) {
        AtomicComponent sc = factory.createSimpleComponent();
        JavaScriptImplementation impl = new JavaScriptImplementation();
        impl.setComponentType(factory.createComponentType());
        impl.setScriptFile(scriptFile);
        impl.setScript(readScript(type.getClassLoader().getResourceAsStream(scriptFile)));
        impl.setResourceLoader(new ResourceLoaderImpl(type.getClassLoader()));
        sc.setImplementation(impl);
        Service s = factory.createService();
        String serviceName = type.getName().substring(type.getName().lastIndexOf('.') + 1);
        s.setName(serviceName);
        JavaServiceContract contract = factory.createJavaServiceContract();
        s.setServiceContract(contract);
        contract.setScope(scope);
        contract.setInterface(type);
        impl.getComponentType().getServices().add(s);
        ConfiguredService cService = factory.createConfiguredService();
        cService.setPort(s);
        cService.initialize(new AssemblyContextImpl(null, null));
        sc.getConfiguredServices().add(cService);
        sc.setName(name);
        sc.setImplementation(impl);
        return sc;
    }

    public static Component createSystemComponent(String name, Class claz, Scope scope) throws ConfigurationLoadException {
        Component sc;
        if (CompositeContext.class.isAssignableFrom(claz)) {
            sc = systemFactory.createModuleComponent();
        } else {
            sc = systemFactory.createSimpleComponent();
        }
        Module impl = systemFactory.createModule();
        impl.setName(name);
        sc.setImplementation(impl);
        impl.setComponentType(getIntrospector().introspect(claz));
        Service s = systemFactory.createService();
        JavaServiceContract ji = systemFactory.createJavaServiceContract();
        s.setServiceContract(ji);
        ji.setScope(scope);
        impl.getComponentType().getServices().add(s);
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
