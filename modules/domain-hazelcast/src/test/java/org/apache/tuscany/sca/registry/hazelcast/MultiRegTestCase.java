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

package org.apache.tuscany.sca.registry.hazelcast;

import java.util.Properties;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Endpoint;
import org.apache.tuscany.sca.assembly.SCABindingFactory;
import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.InterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.registry.hazelcast.HazelcastDomainRegistry;
import org.apache.tuscany.sca.runtime.RuntimeEndpoint;
import org.apache.tuscany.sca.runtime.RuntimeProperties;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class MultiRegTestCase {
    private static ExtensionPointRegistry extensionPoints;
    private static AssemblyFactory assemblyFactory;
    private static SCABindingFactory scaBindingFactory;

    @BeforeClass
    public static void init() {
        extensionPoints = new DefaultExtensionPointRegistry();
        FactoryExtensionPoint factories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        assemblyFactory = factories.getFactory(AssemblyFactory.class);
        scaBindingFactory = factories.getFactory(SCABindingFactory.class);
        Properties properties = extensionPoints.getExtensionPoint(UtilityExtensionPoint.class).getUtility(RuntimeProperties.class).getProperties();
        properties.setProperty("bind", "127.0.0.1");
    }

    @Test
    public void testReplication() throws Exception {

        System.out.println("Starting reg1");
        HazelcastDomainRegistry reg1 = new HazelcastDomainRegistry(extensionPoints, (Properties)null, "tuscany:foo?bind=127.0.0.1:9876&multicast=off", "bar");
        reg1.start();

        System.out.println("Adding ep1");
        RuntimeEndpoint ep1 = createEndpoint("ep1uri");
        ep1.bind(extensionPoints, reg1);
        reg1.addEndpoint(ep1);

        System.out.println("Starting reg3");
        HazelcastDomainRegistry reg2 = new HazelcastDomainRegistry(extensionPoints, (Properties)null, "tuscany:foo?bind=127.0.0.1:9877&multicast=off&wka=127.0.0.1:9876", "bar");
        reg2.start();

        System.out.println("Starting reg2");
        HazelcastDomainRegistry reg3 = new HazelcastDomainRegistry(extensionPoints, (Properties)null, "tuscany:foo?bind=127.0.0.1:9878&multicast=off&wka=127.0.0.1:9877", "bar");
        reg3.start();

        assertExists(reg1, "ep1uri");
        assertExists(reg2, "ep1uri");
        assertExists(reg3, "ep1uri");

        System.out.println("Adding ep2");
        RuntimeEndpoint ep2 = createEndpoint("ep2uri");
        ep2.bind(extensionPoints, reg2);
        reg2.addEndpoint(ep2);

        assertExists(reg2, "ep2uri");
        assertExists(reg1, "ep2uri");
        assertExists(reg3, "ep2uri");
        
        System.out.println("Stopping reg1");
        reg1.stop();
        System.out.println("Stopped reg1");
        Thread.sleep(500);

        Assert.assertNull(reg2.getEndpoint("ep1uri"));
        Assert.assertNull(reg3.getEndpoint("ep1uri"));

        assertExists(reg2, "ep2uri");
        assertExists(reg3, "ep2uri");
        
        System.out.println("Starting reg1");
        reg1.start();
        ep1.bind(extensionPoints, reg1);

        System.out.println("adding ep1");
        reg1.addEndpoint(ep1);
        assertExists(reg1, "ep1uri");
        assertExists(reg2, "ep1uri");
        assertExists(reg3, "ep1uri");
        
        System.out.println("Stopping reg1");
        reg1.stop();
        System.out.println("Stopping reg2");
        reg2.stop();
        System.out.println("Stopping reg3");
        reg3.stop();
        System.out.println("done");
    }

    @Test
    public void testDuplicates() throws Exception {
        HazelcastDomainRegistry reg1 = new HazelcastDomainRegistry(extensionPoints, (Properties)null, "tuscany:foo?bind=127.0.0.1:9876&multicast=off", "bar");
        reg1.start();
        RuntimeEndpoint ep1 = createEndpoint("ep1uri");
        ep1.bind(extensionPoints, reg1);
        reg1.addEndpoint(ep1);

        HazelcastDomainRegistry reg2 = new HazelcastDomainRegistry(extensionPoints, (Properties)null, "tuscany:foo?bind=127.0.0.1:9877&multicast=off&wka=127.0.0.1:9876", "bar");
        reg2.start();

        try {
            reg2.addEndpoint(ep1);
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected
        }
        
        reg1.stop();
        
        Thread.sleep(200);
        
        // now it should work
        reg2.addEndpoint(ep1);

        reg2.stop();
    }

    private Endpoint assertExists(HazelcastDomainRegistry reg, String uri) throws InterruptedException {
        Endpoint ep = reg.getEndpoint(uri);
        Assert.assertNotNull(ep);
        Assert.assertEquals(uri, ep.getURI());
        return ep;
    }

    private RuntimeEndpoint createEndpoint(String uri) {
        RuntimeEndpoint ep = (RuntimeEndpoint) assemblyFactory.createEndpoint();
        Component comp = assemblyFactory.createComponent();
        ep.setComponent(comp);
        ep.setService(assemblyFactory.createComponentService());
        ep.getService().setInterfaceContract(getIC());
        Binding b = scaBindingFactory.createSCABinding();
        ep.setBinding(b);
        ep.setURI(uri);
        return ep;
    }

    private InterfaceContract getIC() {
        InterfaceContract ic = new JavaInterfaceContract(){

            public Object clone() throws CloneNotSupportedException {
                return null;
            }
            @Override
            public Interface getInterface() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setInterface(Interface callInterface) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public Interface getCallbackInterface() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setCallbackInterface(Interface callbackInterface) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public InterfaceContract makeUnidirectional(boolean isCallback) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public InterfaceContract getNormalizedWSDLContract() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setNormalizedWSDLContract(InterfaceContract wsdlInterfaceContract) {
                // TODO Auto-generated method stub
                
            }};
        return ic;
    }
    
}
