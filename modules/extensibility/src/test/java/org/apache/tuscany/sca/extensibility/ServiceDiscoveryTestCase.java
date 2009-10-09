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

package org.apache.tuscany.sca.extensibility;

import java.util.Collection;
import java.util.Iterator;

import org.apache.tuscany.sca.extensibility.test.TestInterface;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 */
public class ServiceDiscoveryTestCase {
    private static final String NAME = "org.apache.tuscany.sca.extensibility.test.TestInterface";

    @Test
    public void testRanking() throws Exception {
        ServiceDeclaration sd = ServiceDiscovery.getInstance().getServiceDeclaration(NAME);
        Assert.assertEquals("org.apache.tuscany.sca.extensibility.test.Test2Impl", sd.getClassName());

        Collection<ServiceDeclaration> sds = ServiceDiscovery.getInstance().getServiceDeclarations(NAME);
        Assert.assertEquals(3, sds.size());

        sds = ServiceDiscovery.getInstance().getServiceDeclarations(NAME, true);
        Assert.assertEquals(3, sds.size());
        Iterator<ServiceDeclaration> it = sds.iterator();
        ServiceDeclaration sd1 = it.next();
        ServiceDeclaration sd2 = it.next();
        Assert.assertEquals("org.apache.tuscany.sca.extensibility.test.Test2Impl", sd1.getClassName());
        Assert.assertEquals("org.apache.tuscany.sca.extensibility.test.TestImpl", sd2.getClassName());
    }

    @Test
    public void testServiceType() throws Exception {
        ServiceDeclaration sd = ServiceDiscovery.getInstance().getServiceDeclaration(TestInterface.class);
        Assert.assertEquals("org.apache.tuscany.sca.extensibility.test.Test2Impl", sd.getClassName());

        Collection<ServiceDeclaration> sds = ServiceDiscovery.getInstance().getServiceDeclarations(TestInterface.class);
        Assert.assertEquals(2, sds.size());

        sds = ServiceDiscovery.getInstance().getServiceDeclarations(TestInterface.class, true);
        Assert.assertEquals(2, sds.size());
        Iterator<ServiceDeclaration> it = sds.iterator();
        ServiceDeclaration sd1 = it.next();
        ServiceDeclaration sd2 = it.next();
        Assert.assertEquals("org.apache.tuscany.sca.extensibility.test.Test2Impl", sd1.getClassName());
        Assert.assertEquals("org.apache.tuscany.sca.extensibility.test.TestImpl", sd2.getClassName());
    }

    @Test
    public void testFilter() throws Exception {
        Collection<ServiceDeclaration> sds =
            ServiceDiscovery.getInstance().getServiceDeclarations(TestInterface.class, "(attr=abc)");

        Assert.assertEquals(1, sds.size());

        Iterator<ServiceDeclaration> it = sds.iterator();
        ServiceDeclaration sd1 = it.next();
        Assert.assertEquals("org.apache.tuscany.sca.extensibility.test.Test2Impl", sd1.getClassName());

        sds = ServiceDiscovery.getInstance().getServiceDeclarations(TestInterface.class, "(attr=1*)");
        Assert.assertEquals(1, sds.size());
        it = sds.iterator();
        sd1 = it.next();
        Assert.assertEquals("org.apache.tuscany.sca.extensibility.test.TestImpl", sd1.getClassName());
    }

}
