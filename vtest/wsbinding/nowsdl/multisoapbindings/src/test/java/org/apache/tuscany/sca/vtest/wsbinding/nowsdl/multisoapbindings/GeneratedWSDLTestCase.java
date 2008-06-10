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
package org.apache.tuscany.sca.vtest.wsbinding.nowsdl.multisoapbindings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.http.HTTPBinding;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Binding;

import junit.framework.Assert;
import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the simplest form of the binding element without WSDL document
 * described in section 2.3.2 of SCA Web Services Binding V1.00
 */
public class GeneratedWSDLTestCase {

    protected static String compositeName = "nowsdlmultisoapbindings.composite";
    protected static Definition cWSDL = null;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
       		cWSDL = ServiceFinder.getWSDLDefinition("CComponent", "CService");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Map getPorts(Definition wsdl, String service) {
    	String targetNamespace = wsdl.getTargetNamespace();
    	HashMap sMap = (HashMap) wsdl.getServices();
    	for (Iterator i = sMap.keySet().iterator(); i.hasNext();) {
    		Object k1 = i.next();
    		Service s = (Service) sMap.get(k1);
    		String qName = "{" + targetNamespace + "}" + service;
    		if (s.getQName().toString().equals(qName)) {
    			return s.getPorts();
    		}
    		// Don't why implmentation changed to add Service at the end of qname
    		if (s.getQName().toString().equals(qName + "Service")) {
    			return s.getPorts();
    		}
    	}
    	return null;
    }
    
    /**
     * Lines 262-264:<br>
     * The WSDL service has one or more ports for each web service binding on
     * the SCA service that has a SOAP requirement, or that refers to an 
     * existing WSDL binding, depending on the requirements of the web service
     * binding. Each of those ports has a single binding.<p>
     * Each service has following bindings:<br>
     * <li>SoapBinding - PortType and SOAP11</li>
     * <li>SOAP12Binding - SOAP12</li>
     * <li>HTTPBinding - Http</li>
     * <br>and following ports:<br>
     * <li>SoapAddress</li>
     * <li>SOAP12Address</li>
     * <li>HTTPAddress</li>
     */
    @Test
    @Ignore
    // Only generate SOAP port and binding
    public void testMultiSoapBindings4() throws Exception {

    	Map pMap = getPorts(cWSDL, "CService");
    	int numSoapPort   = 0;
    	int numSoap12Port = 0;
    	int numHttpPort   = 0;
   		for (Iterator iter = pMap.keySet().iterator(); iter.hasNext();) {
    		Object k2 = iter.next();
    		Port p = (Port) pMap.get(k2);
    		List eeList = p.getExtensibilityElements();
    		for (int i = 0; i < eeList.size(); i++) {
    			Object address = eeList.get(i);
    			if (address instanceof SOAPAddress) {
    				numSoapPort++;
				} else if (address instanceof SOAP12Address) {
    				numSoap12Port++;
				} else if (address instanceof HTTPAddress) {
    				numHttpPort++;
				};
    		}
    	}

   		Map cMap = cWSDL.getBindings();
    	int numSoapBinding   = 0;
    	int numSoap12Binding = 0;
    	int numHttpBinding   = 0;
   		for (Iterator iter = cMap.keySet().iterator(); iter.hasNext();) {
   			Object k1 = iter.next();
   			Binding b = (Binding) cMap.get(k1);
   			List eeList = b.getExtensibilityElements();
    		for (int i = 0; i < eeList.size(); i++) {
    			Object binding = eeList.get(i);
    			if (binding instanceof SOAPBinding) {
    				numSoapBinding++;
    			} else if (binding instanceof SOAP12Binding) {
    				numSoap12Binding++;
    			} else if (binding instanceof HTTPBinding) {
    				numHttpBinding++;
    			}
    		}
   		}

   		System.out.println("testMultiSoapBindings4");
   		System.out.println("  # of SOAPPort:      " + numSoapPort);
   		System.out.println("  # of SOAP12Port:    " + numSoap12Port);
   		System.out.println("  # of HTTPPort:      " + numHttpPort);
   		System.out.println("  # of SOAPBinding:   " + numSoapBinding);
   		System.out.println("  # of SOAP12Binding: " + numSoap12Binding);
   		System.out.println("  # of HTTPBinding:   " + numHttpBinding);
   		
   		Assert.assertEquals(numSoapPort, 1);
   		Assert.assertTrue(numSoap12Port > 0);
   		Assert.assertTrue(numSoapBinding > 0);
   		Assert.assertEquals(numSoap12Binding, 1);
   		// Skipped to test HTTP
   	}
    
    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }
}
