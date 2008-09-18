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
package org.apache.tuscany.sca.vtest.wsbinding.nowsdl.soapversion;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Binding;
import javax.wsdl.BindingInput;
import javax.wsdl.BindingOperation;
import javax.wsdl.BindingOutput;
import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.http.HTTPBinding;
import javax.wsdl.extensions.http.HTTPOperation;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPHeader;
import javax.wsdl.extensions.soap.SOAPHeaderFault;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import javax.wsdl.extensions.soap12.SOAP12Body;
import javax.wsdl.extensions.soap12.SOAP12Header;
import javax.wsdl.extensions.soap12.SOAP12HeaderFault;
import javax.wsdl.extensions.soap12.SOAP12Operation;

import junit.framework.Assert;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.tuscany.sca.vtest.wsbinding.nowsdl.soapversion.BService;
import org.apache.tuscany.sca.vtest.wsbinding.nowsdl.soapversion.CService;


/**
 * Tests the simplest form of the binding element without WSDL document
 * described in section section 2.3.2, 2.3.3, 2.3.3.1, and 2.3.4 of SCA
 * Web Services Binding V1.00
 */
public class GeneratedWSDLTestCase {

	protected static ServiceFinder serviceFinder;
    protected static String compositeName = "nowsdlsoapversion.composite";
    protected static Definition bWSDL = null;
    protected static Definition cWSDL = null;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
       		bWSDL = ServiceFinder.getWSDLDefinition("BComponent", "BService");
       		cWSDL = ServiceFinder.getWSDLDefinition("CComponent", "CService");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Lines 260-262:<br>
     * A separate WSDL document is generated for each SCA service. Each has
     * its own unique target namespace. This is to ensure that bindings on
     * different services of the same component do not clash.<br>
     */
    @Test
    @Ignore("TUSCANY-2607")
    // Don't know why target namespaces are same
    public void testSoapVersion4() throws Exception {
    	String cTargetNamespace = cWSDL.getTargetNamespace();
    	String bTargetNamespace = bWSDL.getTargetNamespace();
        Assert.assertFalse(bTargetNamespace.equals(cTargetNamespace));
    }
    
    /**
     * Lines 271-273:<br>
     * The target namespace of the WSDL document, and of the service, ports and
     * generated binding elements is:<br>
     * Base System URI for HTTP / Component Name / Service Name<br>
     */
    @Test
    @Ignore("TUSCANY-2607")
    //  The target namespace does not follow the convention
    public void testSoapVersion5() throws Exception {
    	String bTargetNamespace = bWSDL.getTargetNamespace();
    	String cTargetNamespace = cWSDL.getTargetNamespace();
    	Assert.assertTrue(bTargetNamespace.endsWith("BComponent/BService"));
    	Assert.assertTrue(cTargetNamespace.endsWith("CComponent/CService"));
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
     * Lines 262-264, 290-291:<br>
     * The WSDL service has one or more ports for each web service binding on
     * the SCA service that has a SOAP requirement, or that refers to an 
     * existing WSDL binding, depending on the requirements of the web service
     * binding. Each of those ports has a single binding.<br>
     * <p>
     * 2.3.3.1 SOAP versions<br>
     * Where no specific SOAP version is required, then one or more WSDL ports
     * with associated SOAP bindings may be generated, depending on the level(s)
     * supported in the target runtime.<br>
     * <p>
     * BComponent/BService is not specified a SOAP version.
     * It should have following bindings:<br>
     * <li>SoapBinding - PortType and SOAP11</li>
     * <li>SOAP12Binding - SOAP12</li>
     * <li>HTTPBinding - Http</li>
     * <br>and following ports:<br>
     * <li>SoapAddress</li>
     * <li>SOAP12Address</li>
     * <li>HTTPAddress</li>

     */
    @Test
    public void testSoapVersion6() throws Exception {

    	Map pMap = getPorts(bWSDL, "BService");
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
				} else {
					System.out.println("address:" + address.getClass());
				}
    		}
    	}

   		Map bMap = bWSDL.getBindings();
    	int numSoapBinding   = 0;
    	int numSoap12Binding = 0;
    	int numHttpBinding   = 0;
   		for (Iterator iter = bMap.keySet().iterator(); iter.hasNext();) {
   			Object k1 = iter.next();
   			Binding b = (Binding) bMap.get(k1);
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

   		System.out.println("testSoapVersion6");
   		System.out.println("  # of SOAPPort:      " + numSoapPort);
   		System.out.println("  # of SOAP12Port:    " + numSoap12Port);
   		System.out.println("  # of HTTPPort:      " + numHttpPort);
   		System.out.println("  # of SOAPBinding:   " + numSoapBinding);
   		System.out.println("  # of SOAP12Binding: " + numSoap12Binding);
   		System.out.println("  # of HTTPBinding:   " + numHttpBinding);
   		
   		Assert.assertEquals(numSoapPort, 1);
   		Assert.assertEquals(numSoap12Port, 0);
   		Assert.assertTrue(numSoapBinding > 0);
   		Assert.assertEquals(numSoap12Binding, 0);
   		// Skipped to test HTTP

   	}
    
    /**
     * Lines 275-284:<br>
     * 2.3.3 WSDL Bindings<br>
     * The binding elements in the generated WSDL document are either defined
     * within the document, derived from the requirements of the binding, or
     * are imported from existing WSDL documents.<br>
     * Generated bindings have the following fixed assumptions:<br>
     * <li>use="literal" for input and output messages</li>
     * <li>style="document" for the binding</li>
     * <li>All faults map to soap:faults</li>
     * <li>No header or headerFault elements are generated</li>
     * <li>The transport is "http://schemas.xmlsoap.org/soap/http", unless the
     * system provides intents for alternative transports<br>
     * <p>
     */
    @Test
    public void testSoapVersion7() throws Exception {
    	Map bMap = bWSDL.getBindings();
   		for (Iterator iter = bMap.keySet().iterator(); iter.hasNext();) {
   			Object k1 = iter.next();
   			Binding b = (Binding) bMap.get(k1);
   			List eeList = b.getExtensibilityElements();
    		for (int i = 0; i < eeList.size(); i++) {
    			Object binding = eeList.get(i);
    			if (binding instanceof SOAPBinding) {
    				SOAPBinding sb = (SOAPBinding) binding;
    				Assert.assertEquals(sb.getStyle(), "document");
    				Assert.assertEquals(sb.getTransportURI(), "http://schemas.xmlsoap.org/soap/http");
    			} else if (binding instanceof SOAP12Binding) {
    				SOAP12Binding sb = (SOAP12Binding) binding;
    				Assert.assertEquals(sb.getStyle(), "document");
    				Assert.assertEquals(sb.getTransportURI(), "http://schemas.xmlsoap.org/soap/http");
    			} else if (binding instanceof HTTPBinding) {
    			}
    		}
    		List operations = b.getBindingOperations();
    		for (int i = 0; i < eeList.size(); i++) {
    			BindingOperation bop = (BindingOperation) operations.get(i);

    			List oeeList = bop.getExtensibilityElements();
    			for (int j = 0; j < eeList.size(); j++) {
    				Object op = oeeList.get(j);
        			if (op instanceof SOAPOperation) {
        				SOAPOperation sop = (SOAPOperation) op;
        				if (sop.getStyle() != null)
        					Assert.assertEquals(sop.getStyle(), "document");
        			} else if (op instanceof SOAP12Operation) {
        				SOAP12Operation sop = (SOAP12Operation) op;
        				Assert.assertEquals(sop.getStyle(), "document");
        			} else if (op instanceof HTTPOperation) {
        				// HTTPOperation hop = (HTTPOperation) op;
        			}
    			}

    			BindingInput bInput = (BindingInput) bop.getBindingInput();
    			List bInputList = bInput.getExtensibilityElements();
    			for (int j = 0; j < eeList.size(); j++) {
    				Object body = bInputList.get(j);
    				if (body instanceof SOAPBody) {
    					SOAPBody sBody = (SOAPBody) body;
    					Assert.assertEquals(sBody.getUse(), "literal");
    				} else if (body instanceof SOAP12Body) {
    					SOAP12Body sBody = (SOAP12Body) body;
    					Assert.assertEquals(sBody.getUse(), "literal");
    				} else {
    					Assert.assertFalse(body instanceof SOAPHeader);
    					Assert.assertFalse(body instanceof SOAP12Header);
    					Assert.assertFalse(body instanceof SOAPHeaderFault);
    					Assert.assertFalse(body instanceof SOAP12HeaderFault);
    				}
    			}
    			
    			BindingOutput bOutput = (BindingOutput) bop.getBindingOutput();
    			List bOutputList = bOutput.getExtensibilityElements();
    			for (int j = 0; j < eeList.size(); j++) {
    				Object body = bOutputList.get(j);
    				if (body instanceof SOAPBody) {
    					SOAPBody sBody = (SOAPBody) body;
    					Assert.assertEquals(sBody.getUse(), "literal");
    				} else if (body instanceof SOAP12Body) {
    					SOAP12Body sBody = (SOAP12Body) body;
    					Assert.assertEquals(sBody.getUse(), "literal");
    				} else {
    					Assert.assertFalse(body instanceof SOAPHeader);
    					Assert.assertFalse(body instanceof SOAP12Header);
    					Assert.assertFalse(body instanceof SOAPHeaderFault);
    					Assert.assertFalse(body instanceof SOAP12HeaderFault);
    				}
    			}
    		
    			Map bFaults = bop.getBindingFaults();
    			if (bFaults.size() > 0)
    				System.out.println("WARNING: Skipped to test binding faults");
    			
    		}
   		}
   	}
 
    /**
     * Lines 288-289:<br>
     * 2.3.3.1 SOAP versions<br>
     * Where a web service binding requires a specific SOAP version, then a
     * single WSDL port and SOAP binding of the appropriate version is
     * generated.
     */
    @Test
    public void testSoapVersion8() throws Exception {
    	
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

   		System.out.println("testSoapVersion8");
   		System.out.println("  # of SOAPPort:      " + numSoapPort);
   		System.out.println("  # of SOAP12Port:    " + numSoap12Port);
   		System.out.println("  # of HTTPPort:      " + numHttpPort);
   		System.out.println("  # of SOAPBinding:   " + numSoapBinding);
   		System.out.println("  # of SOAP12Binding: " + numSoap12Binding);
   		System.out.println("  # of HTTPBinding:   " + numHttpBinding);
   		
   		Assert.assertTrue(numSoap12Port > 0);
   		Assert.assertTrue(numSoap12Binding > 0);
   		Assert.assertEquals(numSoapPort, 0);
   		Assert.assertEquals(numSoapBinding, 0);
   		// Skipped to test HTTP
    }

    private boolean testOperation(Operation op, Method m[]) {
    	for (int i = 0; i < m.length; i++) {
    		if(m[i].getName().equals(op.getName())) {
    	    	Assert.assertNotNull(op.getInput());
    			Assert.assertNotNull(op.getOutput());
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Lines 293-296:<br>
     * 2.3.4 WSDL PortType<br>
     * An SCA service has a single interface. This interface is always imported
     * into the generated WSDL document. This may be done directly for
     * WSDL-defined interfaces, or indirectly via a WSDL generated from the 
     * interface type for the service.<br>
     */
    @Test
    public void testSoapVersion9() throws Exception {
    	Method bMethod[] = BService.class.getMethods();
    	Map bPTMap = bWSDL.getPortTypes();
    	Assert.assertEquals(bPTMap.size(), 1);
    	for (Iterator iter = bPTMap.keySet().iterator(); iter.hasNext();) {
    		PortType pt = (PortType) bPTMap.get(iter.next());
    		List opList = pt.getOperations();
    		Assert.assertEquals(bMethod.length, opList.size());
    		for (int i = 0; i < opList.size(); i++) {
    			Operation op = (Operation) opList.get(i);
    			Assert.assertTrue(testOperation(op, bMethod));
    		}
    	}
    	
    	Method cMethod[] = CService.class.getMethods();
    	Map cPTMap = cWSDL.getPortTypes();
    	Assert.assertEquals(cPTMap.size(), 1);
    	for (Iterator iter = cPTMap.keySet().iterator(); iter.hasNext();) {
    		PortType pt = (PortType) cPTMap.get(iter.next());
    		List opList = pt.getOperations();
    		Assert.assertEquals(cMethod.length, opList.size());
    		for (int i = 0; i < opList.size(); i++) {
    			Operation op = (Operation) opList.get(i);
    			Assert.assertTrue(testOperation(op, cMethod));
    		}
    	}
    }


    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }
}
