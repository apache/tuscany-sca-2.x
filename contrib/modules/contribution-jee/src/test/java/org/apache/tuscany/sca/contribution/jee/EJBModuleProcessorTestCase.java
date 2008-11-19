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
import java.util.Map;
import java.util.Set;

import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.EjbModule;
import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.assembly.Service;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class EJBModuleProcessorTestCase {

    EjbModule ejbModule;

    @Before
    public void setUp() throws Exception {
        String jarFilePath = "target/test-classes/ejb-injection-sample.jar";
        JavaEEModuleHelper jmh = new JavaEEModuleHelper();
        AppModule appModule = jmh.getMetadataCompleteModules(jarFilePath);
        ejbModule = appModule.getEjbModules().get(0);
    }

    @Test
    public void testEjbContribution() throws Exception {
        EJBModuleProcessor emp = new EJBModuleProcessor(ejbModule);
        Map<String, ComponentType> ejbComponentTypes = emp.getEjbComponentTypes();
        Assert.assertEquals(3, ejbComponentTypes.size());

        Assert.assertTrue(ejbComponentTypes.containsKey("DataStoreImpl"));
        Assert.assertTrue(ejbComponentTypes.containsKey("DataStoreStatefulImpl"));
        Assert.assertTrue(ejbComponentTypes.containsKey("DataReaderImpl"));

        ComponentType ct = ejbComponentTypes.get("DataStoreImpl");
        Assert.assertEquals(2, ct.getServices().size());
        Set<String> serviceNames = new HashSet<String>();
        for (Service s : ct.getServices()) {
            serviceNames.add(s.getName());
        }

        Assert.assertEquals(2, serviceNames.size());
        Assert.assertTrue(serviceNames.contains("DataStoreRemote"));
        Assert.assertTrue(serviceNames.contains("DataStoreLocal"));

        Assert.assertEquals(0, ct.getReferences().size());
    }
}
