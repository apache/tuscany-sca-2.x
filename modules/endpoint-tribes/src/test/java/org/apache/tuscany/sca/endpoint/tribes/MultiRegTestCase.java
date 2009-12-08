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

package org.apache.tuscany.sca.endpoint.tribes;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.impl.SCABindingFactoryImpl;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

// Ignore so its not run in the build yet till its working
//@Ignore
public class MultiRegTestCase {

//    @Test
//    public void testTwoNodesMultiCast() throws InterruptedException {
//        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
//        FactoryExtensionPoint factories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
//        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);
//
//        ReplicatedEndpointRegistry reg1 = new ReplicatedEndpointRegistry(extensionPoints, null, "foo", "bar");
//        reg1.start();
//
//        Endpoint ep1 = assemblyFactory.createEndpoint();
//        Component comp = assemblyFactory.createComponent();
//        ep1.setComponent(comp);
//        ep1.setService(assemblyFactory.createComponentService());
//        Binding b = new SCABindingFactoryImpl().createSCABinding();
//        ep1.setBinding(b);
//        ep1.setURI("ep1uri");
//        reg1.addEndpoint(ep1);
//
//        Endpoint ep1p = reg1.getEndpoint("ep1uri");
//        Assert.assertNotNull(ep1p);
//        Assert.assertEquals("ep1uri", ep1p.getURI());
//
//        ReplicatedEndpointRegistry reg2 = new ReplicatedEndpointRegistry(extensionPoints, null, "foo", "bar");
//        reg2.start();
//        Thread.sleep(5000);
//
//        Endpoint ep1p2 = reg2.getEndpoint("ep1uri");
//        Assert.assertNotNull(ep1p2);
//        Assert.assertEquals("ep1uri", ep1p2.getURI());
//
//        reg1.stop();
//        reg2.stop();
//    }

    @Test
    public void testTwoNodesStaticNoMultiCast() throws InterruptedException {
        DefaultExtensionPointRegistry extensionPoints = new DefaultExtensionPointRegistry();
        FactoryExtensionPoint factories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = factories.getFactory(AssemblyFactory.class);

        Map<String, String> attrs1 = new HashMap<String, String>();
        attrs1.put("nomcast", "true");
        attrs1.put("routes", "9.167.197.91:4001");
        ReplicatedEndpointRegistry reg1 = new ReplicatedEndpointRegistry(extensionPoints, attrs1, "foo", "bar");
        reg1.start();

        Endpoint ep1 = assemblyFactory.createEndpoint();
        Component comp = assemblyFactory.createComponent();
        ep1.setComponent(comp);
        ep1.setService(assemblyFactory.createComponentService());
        Binding b = new SCABindingFactoryImpl().createSCABinding();
        ep1.setBinding(b);
        ep1.setURI("ep1uri");
        reg1.addEndpoint(ep1);

        Endpoint ep1p = reg1.getEndpoint("ep1uri");
        Assert.assertNotNull(ep1p);
        Assert.assertEquals("ep1uri", ep1p.getURI());

        Map<String, String> attrs2 = new HashMap<String, String>();
        attrs2.put("nomcast", "true");
        attrs2.put("routes", "9.167.197.91:4000");
        ReplicatedEndpointRegistry reg2 = new ReplicatedEndpointRegistry(extensionPoints, attrs2, "foo", "bar");
        reg2.start();
        
        System.out.println("wait");
        Thread.sleep(5000);
        System.out.println("run");

        Endpoint ep1p2 = reg2.getEndpoint("ep1uri");
        Assert.assertNotNull(ep1p2);
        Assert.assertEquals("ep1uri", ep1p2.getURI());

        System.out.println("wait2");
        Thread.sleep(5000);
        System.out.println("end");
        reg1.stop();
        reg2.stop();
    }

}
