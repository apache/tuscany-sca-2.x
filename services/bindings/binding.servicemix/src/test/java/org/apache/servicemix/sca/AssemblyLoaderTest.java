/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.servicemix.sca;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.servicemix.sca.assembly.JbiBinding;
import org.apache.servicemix.sca.tuscany.TuscanyRuntime;
import org.apache.tuscany.common.monitor.impl.NullMonitorFactory;
import org.apache.tuscany.model.assembly.Binding;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.EntryPoint;
import org.apache.tuscany.model.assembly.ExternalService;
import org.apache.tuscany.model.assembly.Module;

/**
 * @author delfinoj
 */
public class AssemblyLoaderTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    }

    public void testLoader() throws Exception {
        String name = "bigbank";
        String uri  = getClass().getResource("bigbank/sca.module").toString();
        
        URL url = getClass().getResource("bigbank/sca.module");
        URL parentUrl = new File(url.toURI()).getParentFile().toURL();
        ClassLoader cl = new URLClassLoader(new URL[] { parentUrl }, getClass().getClassLoader());
        
        TuscanyRuntime rt = new TuscanyRuntime(name, uri, cl, new NullMonitorFactory());
        assertNotNull(rt);
        
        Module module = rt.getModuleComponent().getModuleImplementation();

        Assert.assertTrue(module.getName().equals("org.apache.servicemix.sca.bigbank"));

        Component component = module.getComponent("AccountServiceComponent");
        Assert.assertTrue(component != null);

        EntryPoint entryPoint = module.getEntryPoint("AccountService");
        Assert.assertTrue(entryPoint != null);

        ExternalService externalService = module.getExternalService("StockQuoteService");
        Assert.assertTrue(externalService != null);

        Binding binding = externalService.getBindings().get(0);
        Assert.assertTrue(binding instanceof JbiBinding);
    }
}
