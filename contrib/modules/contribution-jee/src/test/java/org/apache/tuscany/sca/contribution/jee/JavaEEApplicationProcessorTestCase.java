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

package org.apache.tuscany.sca.contribution.jee;

import java.util.HashSet;
import java.util.Set;

import org.apache.openejb.config.AppModule;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Reference;
import org.apache.tuscany.sca.assembly.Service;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class JavaEEApplicationProcessorTestCase {

    AppModule appModule;

    @Before
    public void setUp() throws Exception {
        String jarFilePath = "target/test-classes/ejb-injection-sample.ear";
        JavaEEModuleHelper jmh = new JavaEEModuleHelper();
        appModule = jmh.getMetadataCompleteModules(jarFilePath);
    }

    @Test
    public void testJavaEEAppContribution() throws Exception {
        JavaEEApplicationProcessor jap = new JavaEEApplicationProcessor(appModule);

        ComponentType ct = jap.getJavaEEAppComponentType();
        
        // Check the services
        Assert.assertEquals(6, ct.getServices().size());

        Set<String> expectedServiceNames = new HashSet<String>();
        expectedServiceNames.add("BankBean_Bank");
        expectedServiceNames.add("ConverterBean_Converter");
        expectedServiceNames.add("ConverterBean_ConverterLocal");
        expectedServiceNames.add("Converter2Bean_ConverterLocal");
        expectedServiceNames.add("InvoiceBean_Invoice");
        expectedServiceNames.add("PurchaseOrderBean_PurchaseOrder");

        Set<String> serviceNames = new HashSet<String>();
        for(Service service : ct.getServices()) {
            serviceNames.add(service.getName());
        }
        
        Assert.assertEquals(expectedServiceNames, serviceNames);

        // Check the references
        Assert.assertEquals(1, ct.getReferences().size());
        
        Set<String> expectedReferenceNames = new HashSet<String>();
        expectedReferenceNames.add("BankBean_simple.BankBean_converter");
        
        Set<String> referenceNames = new HashSet<String>();
        for (Reference r : ct.getReferences()) {
            referenceNames.add(r.getName());
        }

        Assert.assertEquals(expectedReferenceNames, referenceNames);
    }
}
