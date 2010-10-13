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
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.runtime.EndpointListener;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

// Ignore so its not run in the build yet till its working
@Ignore
public class MultiRegTestCase implements EndpointListener {
    private static ExtensionPointRegistry extensionPoints;
    private static AssemblyFactory assemblyFactory;
    private static SCABindingFactory scaBindingFactory;

    @BeforeClass
    public static void init() {
        extensionPoints = new DefaultExtensionPointRegistry();
        FactoryExtensionPoint factories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        assemblyFactory = factories.getFactory(AssemblyFactory.class);
        scaBindingFactory = factories.getFactory(SCABindingFactory.class);
    }

    @Test
    public void testReplication() throws Exception {
        RuntimeEndpoint ep1 = createEndpoint("ep1uri");

        // String host = InetAddress.getLocalHost().getHostAddress();
        String bind = "127.0.0.1"; // "9.65.158.31";
        String port1 = "8085";
        String port2 = "8086";
        String port3 = "8087";
        String range = "1";

        Map<String, String> attrs1 = new HashMap<String, String>();
        // attrs1.put("nomcast", "true");
        attrs1.put("bind", bind);
        attrs1.put("receiverPort", port1);
        attrs1.put("receiverAutoBind", range);
        // attrs1.put("routes", host + ":" + port2 + " " + host + ":" + port3);
        ReplicatedEndpointRegistry reg1 = new ReplicatedEndpointRegistry(extensionPoints, attrs1, "foo", "bar");
        reg1.addListener(this);
        reg1.start();

        Map<String, String> attrs2 = new HashMap<String, String>();
        // attrs2.put("nomcast", "true");
        attrs2.put("bind", bind);
        attrs2.put("receiverPort", port2);
        attrs2.put("receiverAutoBind", range);
        // attrs2.put("routes", host + ":"+port1);
        ReplicatedEndpointRegistry reg2 = new ReplicatedEndpointRegistry(extensionPoints, attrs2, "foo", "bar");
        reg2.addListener(this);
        reg2.start();

        Map<String, String> attrs3 = new HashMap<String, String>();
        // attrs3.put("nomcast", "true");
        attrs3.put("bind", bind);
        attrs3.put("receiverPort", port3);
        attrs3.put("receiverAutoBind", range);
        // attrs3.put("routes", host + ":"+port1);
        ReplicatedEndpointRegistry reg3 = new ReplicatedEndpointRegistry(extensionPoints, attrs3, "foo", "bar");
        reg3.addListener(this);
        reg3.start();

        ep1.bind(extensionPoints, reg1);
        reg1.addEndpoint(ep1);
        assertExists(reg1, "ep1uri");
        assertExists(reg2, "ep1uri");
        assertExists(reg3, "ep1uri");

        RuntimeEndpoint ep2 = createEndpoint("ep2uri");
        ep2.bind(extensionPoints, reg2);
        reg2.addEndpoint(ep2);
        assertExists(reg2, "ep2uri");
        assertExists(reg1, "ep2uri");
        assertExists(reg3, "ep2uri");

        reg1.stop();
        Thread.sleep(6000);
        Assert.assertNull(reg2.getEndpoint("ep1uri"));
        Assert.assertNull(reg3.getEndpoint("ep1uri"));
        assertExists(reg2, "ep2uri");
        assertExists(reg3, "ep2uri");
        
        reg1.start();
        ep1.bind(extensionPoints, reg1);
        reg1.addEndpoint(ep1);
        assertExists(reg1, "ep1uri");
        assertExists(reg2, "ep1uri");
        assertExists(reg3, "ep1uri");
        
        reg1.stop();
        reg2.stop();
        reg3.stop();
        System.out.println(); // closed
    }

    private Endpoint assertExists(ReplicatedEndpointRegistry reg, String uri) throws InterruptedException {
        Endpoint ep = null;
        int count = 0;
        while (ep == null && count < 15) {
            ep = reg.getEndpoint(uri);
            Thread.sleep(1000);
            count++;
            System.out.println(reg + ": tries=" + count);
        }
        Assert.assertNotNull(ep);
        Assert.assertEquals(uri, ep.getURI());
        return ep;
    }

    private RuntimeEndpoint createEndpoint(String uri) {
        RuntimeEndpoint ep = (RuntimeEndpoint) assemblyFactory.createEndpoint();
        Component comp = assemblyFactory.createComponent();
        ep.setComponent(comp);
        ep.setService(assemblyFactory.createComponentService());
        Binding b = scaBindingFactory.createSCABinding();
        ep.setBinding(b);
        ep.setURI(uri);
        return ep;
    }
    
    private void print(String prefix, Endpoint ep) {
        System.out.println(prefix + ": "+ep);
    }

    public void endpointAdded(Endpoint endpoint) {
        print("Added", endpoint);
    }

    public void endpointRemoved(Endpoint endpoint) {
        print("Removed", endpoint);
    }

    public void endpointUpdated(Endpoint oldEndpoint, Endpoint newEndpoint) {
        print("Updated", newEndpoint);
    }

}
