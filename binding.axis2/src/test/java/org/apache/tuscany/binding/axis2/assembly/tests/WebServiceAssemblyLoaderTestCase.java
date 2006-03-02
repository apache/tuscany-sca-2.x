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
package org.apache.tuscany.binding.axis2.assembly.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.binding.axis2.assembly.WebServiceBinding;
import org.apache.tuscany.binding.axis2.loader.WebServiceSCDLModelLoader;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;
import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.AssemblyModelContext;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Module;
import org.apache.tuscany.model.assembly.impl.AssemblyFactoryImpl;
import org.apache.tuscany.model.assembly.impl.AssemblyModelContextImpl;
import org.apache.tuscany.model.assembly.loader.AssemblyModelLoader;
import org.apache.tuscany.model.scdl.loader.SCDLModelLoader;
import org.apache.tuscany.model.scdl.loader.impl.SCDLAssemblyModelLoaderImpl;

/**
 */
public class WebServiceAssemblyLoaderTestCase extends TestCase {

    /**
     *
     */
    public WebServiceAssemblyLoaderTestCase() {
        super();
    }

    public void testLoader() {

        ResourceLoader resourceLoader=new ResourceLoaderImpl(Thread.currentThread().getContextClassLoader());
        WebServiceSCDLModelLoader wsLoader=new WebServiceSCDLModelLoader();
        List<SCDLModelLoader> scdlLoaders=new ArrayList<SCDLModelLoader>();
        scdlLoaders.add(wsLoader);
        AssemblyModelLoader assemblyLoader=new SCDLAssemblyModelLoaderImpl(scdlLoaders);
        AssemblyFactory assemblyFactory=new AssemblyFactoryImpl();
        AssemblyModelContext modelContext=new AssemblyModelContextImpl(assemblyFactory, assemblyLoader, resourceLoader);

        Module module = assemblyLoader.loadModule(getClass().getResource("sca.module").toString());
        module.initialize(modelContext);

        Assert.assertTrue(module.getName().equals("tuscany.binding.axis2.assembly.tests.bigbank.account"));

        Component component = module.getComponent("AccountServiceComponent");
        Assert.assertTrue(component != null);

        EntryPoint entryPoint = module.getEntryPoint("AccountService");
        Assert.assertTrue(entryPoint != null);

        ExternalService externalService = module.getExternalService("StockQuoteService");
        Assert.assertTrue(externalService != null);

        Binding binding = externalService.getBindings().get(0);
        Assert.assertTrue(binding instanceof WebServiceBinding);
    }

    protected void setUp() throws Exception {
        super.setUp();

        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    }

}
