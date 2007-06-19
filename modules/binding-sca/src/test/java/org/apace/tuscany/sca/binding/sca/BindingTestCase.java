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
package org.apace.tuscany.sca.binding.sca;

import junit.framework.Assert;

import org.apache.tuscany.sca.assembly.SCABinding;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.binding.sca.SCABindingFactoryImpl;
import org.apache.tuscany.sca.binding.sca.SCABindingModuleActivator;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.distributed.assembly.DistributedSCABinding;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.DefaultProviderFactoryExtensionPoint;
import org.apache.tuscany.sca.provider.ProviderFactoryExtensionPoint;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BindingTestCase {

    @BeforeClass
    public static void init() throws Exception {            
    }
    
    @AfterClass
    public static void destroy() throws Exception {
    }

    @Test
    public void testModuleActivator() { 
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        ModelFactoryExtensionPoint factories = new DefaultModelFactoryExtensionPoint();
        registry.addExtensionPoint(factories);
        ProviderFactoryExtensionPoint providerFactories = new DefaultProviderFactoryExtensionPoint();
        registry.addExtensionPoint(providerFactories);        
        
        SCABindingModuleActivator moduleActivator = new SCABindingModuleActivator(); 
        moduleActivator.start(registry);
        
        Assert.assertNotNull(factories.getFactory(SCABindingFactory.class));
        Assert.assertNotNull(providerFactories.getProviderFactory(DistributedSCABinding.class));
    }
    
    @Test
    public void testBindingFactory() {
        /*
         * TODO - extend this test when we decide what SCA Binding is
         *        going to do 
         */
        SCABindingFactory factory = new SCABindingFactoryImpl(null, null);
        Assert.assertNotNull(factory.createSCABinding());
    }
}
