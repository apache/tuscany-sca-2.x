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
package org.apache.tuscany.container.java.config;

import java.net.URL;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.container.java.assembly.impl.JavaImplementationImpl;
import org.apache.tuscany.container.java.loader.JavaSCDLModelLoader;
import org.apache.tuscany.core.config.ConfigurationException;
import org.apache.tuscany.core.config.ModuleComponentConfigurationLoader;
import org.apache.tuscany.core.config.impl.ModuleComponentConfigurationLoaderImpl;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.ComponentImplementation;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.ModuleComponent;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;
import org.apache.tuscany.model.scdl.loader.impl.SCDLAssemblyModelLoaderImpl;

/**
 * @version $Rev: 379821 $ $Date: 2006-02-22 09:25:32 -0800 (Wed, 22 Feb 2006) $
 */
public class ModuleComponentConfigurationLoaderTestCase extends TestCase {
    private ModuleComponentConfigurationLoader loader;
    private AssemblyModelContext modelContext;

    public void testFoo() throws ConfigurationException {
        URL xml = ModuleComponentConfigurationLoaderTestCase.class.getResource("ModuleComponentLoaderTest1.module");
        ModuleComponent moduleComponent = loader.loadModuleComponent("test", "test", xml.toString());
        
        Assert.assertEquals("test", moduleComponent.getName());
        Module module = moduleComponent.getModuleImplementation();
        Assert.assertEquals("ModuleComponentLoaderTest1", module.getName());
        List<Component> components = module.getComponents();
        Assert.assertEquals(1, components.size());
        Component component = components.get(0);
        Assert.assertEquals("HelloWorldServiceComponent", component.getName());

        component = module.getComponent("HelloWorldServiceComponent");
        Assert.assertEquals("HelloWorldServiceComponent", component.getName());

        ComponentImplementation implementation = component.getComponentImplementation();
       Assert.assertTrue(implementation instanceof JavaImplementationImpl);
    }

    protected void setUp() throws Exception {
        super.setUp();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        AssemblyModelLoader modelLoader=new SCDLAssemblyModelLoaderImpl();
        JavaSCDLModelLoader javaLoader=new JavaSCDLModelLoader();
        modelLoader.getSCDLModelLoaders().add(javaLoader);
        modelContext=new AssemblyModelContextImpl(new AssemblyFactoryImpl(), modelLoader,
                new ResourceLoaderImpl(this.getClass().getClassLoader()));
          loader = new ModuleComponentConfigurationLoaderImpl(modelContext);
    }
}
