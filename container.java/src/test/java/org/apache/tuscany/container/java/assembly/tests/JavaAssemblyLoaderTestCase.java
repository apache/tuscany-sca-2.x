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
package org.apache.tuscany.container.java.assembly.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.container.java.assembly.tests.bigbank.account.services.accountdata.AccountDataService;
import org.apache.tuscany.container.java.loader.JavaSCDLModelLoader;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.apache.tuscany.model.scdl.loader.impl.SCDLAssemblyModelLoaderImpl;

/**
 */
public class JavaAssemblyLoaderTestCase extends TestCase {
    ClassLoader origLoader;
    
    /**
     *
     */
    public JavaAssemblyLoaderTestCase() {
        super();
    }

    public void testLoader() {

        ResourceLoader resourceLoader=new ResourceLoaderImpl(Thread.currentThread().getContextClassLoader());
        JavaSCDLModelLoader javaLoader=new JavaSCDLModelLoader();
        List<SCDLModelLoader> scdlLoaders=new ArrayList<SCDLModelLoader>();
        scdlLoaders.add(javaLoader);
        AssemblyModelLoader assemblyLoader=new SCDLAssemblyModelLoaderImpl(scdlLoaders);
        AssemblyFactory assemblyFactory=new AssemblyFactoryImpl();
        AssemblyModelContext modelContext=new AssemblyModelContextImpl(assemblyFactory, assemblyLoader, resourceLoader);

        Module module = assemblyLoader.loadModule(getClass().getResource("sca.module").toString());
        module.initialize(modelContext);
        Assert.assertTrue(module.getName().equals("tuscany.container.java.assembly.tests.bigbank.account"));

        Component component=module.getComponent("AccountServiceComponent");
        Assert.assertTrue(component!= null);

        EntryPoint entryPoint=module.getEntryPoint("AccountService");
        Assert.assertTrue(entryPoint!= null);
        
      Object value = component.getConfiguredProperty("currency").getValue();
      Assert.assertTrue(value.equals("EURO"));

      //ConfiguredService configuredService = component.getConfiguredReference("accountDataService").getTargetConfiguredServices().get(0);
      //Assert.assertTrue(configuredService.getAggregatePart().getName().equals("AccountDataServiceComponent"));

      Class interfaceClass = component.getConfiguredReference("accountDataService").getReference().getServiceContract().getInterface();
      Assert.assertTrue(interfaceClass == AccountDataService.class);
        
    }

    protected void setUp() throws Exception {
        super.setUp();

        origLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    }
    protected void tearDown() throws Exception {
        Thread.currentThread().setContextClassLoader(origLoader);
        super.tearDown();
    }

}
