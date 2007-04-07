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
package org.apache.tuscany.test.interop.client;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import org.apache.tuscany.test.SCATestCase;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;
import org.soapinterop.wsdl.interop.test.doc.lit.WSDLInteropTestDocLitPortType;
import org.soapinterop.xsd.ArrayOfstring_literal;
import org.soapinterop.xsd.SOAPStruct;
import org.soapinterop.xsd.XsdPackage;

import commonj.sdo.helper.DataFactory;

/**
 * This test case is part of the tuscany interop tests. This is a reduced version of the SOAPBuilders InteropTest test, document/literal mode. This
 * version has operations such as echoString, echoArrayOfString and echoStruct.
 * 
 * <p>
 * The WSDL for the external service used in this test case can be downloaded from http://www.mssoapinterop.org/stkV3/wsdl/InteropTestDocLit.wsdl.
 * This is part of the WSDL interop test from Microsoft and more details aobout this test case are found at
 * http://www.mssoapinterop.org/stkV3/wsdl/WSDLInterop-0118.htm
 */
public class InteropTestDocLitTestCase extends SCATestCase {

    private WSDLInteropTestDocLitPortType doc = null;

    private DataFactory dataFactory;

    
    protected void setUp() throws Exception {
        setApplicationSCDL(InteropTestDocLit.class, "META-INF/sca/default.scdl");
        addExtension("test.extensions", InteropTestDocLit.class.getClassLoader().getResource("META-INF/tuscany/extensions/test-extensions.scdl"));

         super.setUp();

        // Get the SDO DataFactory
        dataFactory = DataFactory.INSTANCE;

        // Locate the service to test
        doc = locateInteropDocService();
    }

    /**
     * Locate the interop service to test
     * 
     * @return
     */
    protected WSDLInteropTestDocLitPortType locateInteropDocService() {

        CompositeContext compositeContext = CurrentCompositeContext.getContext();
        return compositeContext.locateService(WSDLInteropTestDocLitPortType.class, "RemoteInteropDocService");

    }

    /**
     * test echo void
     * 
     * @throws RemoteException
     */
    public void testEchoVoid() throws RemoteException {
        doc.echoVoid();
        assertTrue(true);
    }

    /**
     * test echo string
     * 
     * @throws RemoteException
     */
    public void testEchoString() throws RemoteException {
        String input = "a test string";
        String output = doc.echoString(input);
        assertEquals(input, output);
    }

    /**
     * test echo string
     * 
     * @throws RemoteException
     */
    public void testEchoStringArray() throws RemoteException {

        ArrayOfstring_literal input = (ArrayOfstring_literal) dataFactory.create(ArrayOfstring_literal.class);
        List inStrings = Arrays.asList(new String[] { "petra", "sue" });
        input.set(XsdPackage.ARRAY_OFSTRING_LITERAL__STRING, inStrings);

        ArrayOfstring_literal output = doc.echoStringArray(input);

        List outStrings = output.getString();
        assertNotNull(outStrings);
        assertEquals(2, outStrings.size());
        assertEquals("petra", outStrings.get(0));
        assertEquals("sue", outStrings.get(1));
    }

    /**
     * test echo struct
     * 
     * @throws RemoteException
     */
    public void testEchoStruct() throws RemoteException {
        SOAPStruct input = (SOAPStruct) dataFactory.create(SOAPStruct.class);
        input.setVarInt(200);
        input.setVarFloat(.002f);
        input.setVarString("Hello");
        SOAPStruct output = doc.echoStruct(input);
        assertEquals(input.getVarInt(), output.getVarInt());
        assertEquals(input.getVarFloat(), output.getVarFloat());
        assertEquals(input.getVarString(), output.getVarString());
    }

}
