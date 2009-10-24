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

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.LifeCycleListener;
import org.apache.tuscany.sca.extensibility.test.Test2Impl;
import org.apache.tuscany.sca.extensibility.test.TestImpl;
import org.apache.tuscany.sca.extensibility.test.TestInterface;
import org.junit.Assert;
import org.junit.Test;

public class ServiceHelperTestCase {
    @Test
    public void testNewInstance() throws Exception {
        Test2Impl implA = ServiceHelper.newInstance(Test2Impl.class);
        Assert.assertNull(implA.getRegistry());

        TestImpl impl1 = ServiceHelper.newInstance(TestImpl.class);
        Assert.assertNotNull(impl1);
    }

    @Test
    public void testNewInstance2() throws Exception {
        Collection<ServiceDeclaration> sds =
            ServiceDiscovery.getInstance().getServiceDeclarations(TestInterface.class, true);
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        Iterator<ServiceDeclaration> iterator = sds.iterator();
        Test2Impl implA = ServiceHelper.newInstance(registry, iterator.next());
        Assert.assertSame(registry, implA.getRegistry());

        TestImpl impl1 = ServiceHelper.newInstance(registry, iterator.next());
        Assert.assertNotNull(impl1);
    }

    @Test
    public void testNewLazyInstance() throws Exception {
        Collection<ServiceDeclaration> sds =
            ServiceDiscovery.getInstance().getServiceDeclarations(TestInterface.class, true);
        ExtensionPointRegistry registry = new DefaultExtensionPointRegistry();
        Iterator<ServiceDeclaration> iterator = sds.iterator();
        TestInterface ti = ServiceHelper.newLazyInstance(registry, iterator.next(), TestInterface.class);
        Assert.assertTrue(Proxy.isProxyClass(ti.getClass()));
        Assert.assertTrue(ti instanceof LifeCycleListener);
        Assert.assertTrue(ti.toString().startsWith("Proxy"));
        Assert.assertEquals(System.identityHashCode(ti), ti.hashCode());
        ServiceHelper.start(ti);
        ServiceHelper.stop(ti);
        QName name = ti.getArtifactType();
        Assert.assertEquals(new QName("http://sample", "Test2"), name);
        String str = ti.test("ABC");
        Assert.assertEquals("Test 2: ABC", str);
        ServiceHelper.stop(ti);
    }
}
