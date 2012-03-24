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
package helloworld;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import java.io.IOException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.tuscany.sca.Node;
import org.apache.tuscany.sca.TuscanyRuntime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.oasisopen.sca.NoSuchServiceException;

import yetanotherpackage.DBean;

import anotherpackage.BBean;

/**
 * Tests that the helloworld server is available
 */
public class WSDLGenTestCase{

    private Node node;

    @Before
	public void startServer() throws Exception {
        node = TuscanyRuntime.newInstance().createNode("default");
        node.installContribution("helloworld", "target/classes", null, null);
        node.startComposite("helloworld", "helloworld.composite");
	}
    
    @Ignore
    @Test
    public void testWaitForInput() {
        System.out.println("Press a key to end");
        try {
            System.in.read();
        } catch (Exception ex) {
        }
        System.out.println("Shutting down");
    }  
    
    @Test
    public void testComponentSCA() throws IOException, NoSuchServiceException {
        HelloWorldService helloWorldService = node.getService(HelloWorldService.class, "HelloWorldServiceComponent/HelloWorldService");
        assertNotNull(helloWorldService);
        
        HelloWorldService helloWorldClient = node.getService(HelloWorldService.class, "HelloWorldClientComponent/HelloWorldService");
        assertNotNull(helloWorldClient);        
        
        assertEquals("Hello Smith", helloWorldService.getGreetings("Smith"));
        assertEquals("Hello Hello Smith", helloWorldClient.getGreetings("Smith"));
        
        BBean bbean = new BBean();
        bbean.setField1("1");
        bbean.setField2("2");
        
        DBean abean = new DBean();
        abean.setField1("3");
        abean.setField2("4");
        abean.setField3(bbean);
            
        assertEquals("Hello Hello 3 4 1 2", helloWorldClient.getGreetingsDBean(abean));
        
        try {
            helloWorldClient.getGreetingsException("Fred");
            fail("exception not returned");
        } catch(Exception ex) {
            
        }  
    }
    
    @Test
    public void testComponentJAXWSwsgen() throws IOException {        
        
        // talk to the service using JAXWS with WSDL generated from this service using wsgen
        // the idea here is to demonstrate that the service is providing a JAXWS compliant 
        // interface
        QName serviceName = new QName("http://helloworld/", "HelloWorldImplService");
        QName portName = new QName("http://helloworld/", "HelloWorldImplPort");
        URL wsdlLocation = this.getClass().getClassLoader().getResource("jaxws/wsgen/wsdl/HelloWorldImplService.wsdl");
        Service webService = Service.create( wsdlLocation, serviceName );
        HelloWorldService wsProxy = (HelloWorldService) webService.getPort(portName, HelloWorldService.class);

        assertEquals("Hello Fred", wsProxy.getGreetings("Fred"));
        
        BBean bbean = new BBean();
        bbean.setField1("1");
        bbean.setField2("2");
        
        DBean abean = new DBean();
        abean.setField1("3");
        abean.setField2("4");
        abean.setField3(bbean);
        
        assertEquals("Hello 3 4 1 2", wsProxy.getGreetingsDBean(abean));

        String byteArrayString = "Hello World";
        assertEquals(byteArrayString, new String(wsProxy.getGreetingsByteArray(byteArrayString.getBytes())));
        
        try {
            wsProxy.getGreetingsException("Fred");
            fail("exception not returned");
        } catch(Exception ex) {
            
        }  
    }  
    
    @Test
    public void testComponentJAXWStuscanygen() throws IOException {   
        // talk to the service using JAXWS with WSDL generated from this service used Tuscany's ?wsdl
        // the idea here is to demonstrate that the service is providing a JAXWS compliant 
        // WSDL
        QName serviceName = new QName("http://helloworld/", "HelloWorldServiceComponent_HelloWorldService");
        QName portName = new QName("http://helloworld/", "HelloWorldServiceSOAP11Port");
        URL wsdlLocation = new URL("http://localhost:8085/HelloWorldServiceComponent?wsdl");
        Service webService = Service.create( wsdlLocation, serviceName );
        HelloWorldService wsProxy = (HelloWorldService) webService.getPort(portName, HelloWorldService.class);

        assertEquals("Hello Fred", wsProxy.getGreetings("Fred"));
        
        BBean bbean = new BBean();
        bbean.setField1("1");
        bbean.setField2("2");
        
        DBean abean = new DBean();
        abean.setField1("3");
        abean.setField2("4");
        abean.setField3(bbean);
        
        assertEquals("Hello 3 4 1 2", wsProxy.getGreetingsDBean(abean));

        String byteArrayString = "Hello World";
        assertEquals(byteArrayString, new String(wsProxy.getGreetingsByteArray(byteArrayString.getBytes())));
        
        try {
            wsProxy.getGreetingsException("Fred");
            fail("exception not returned");
        } catch(Exception ex) {
            
        } 
    }    
    
    // Differences between JAXWS WSDL (J) and Tuscany WSDL (T)
    //
    // Service name 
    // J/ HelloWorldImplService T/ HelloWorldService
    //
    // Port name
    // J/ HelloWorldImplPort T/ HelloWorldServiceSOAP11Port

	@After
	public void stopServer() throws Exception {
            if (node != null) {
                node.stop();
            }
	}

}
