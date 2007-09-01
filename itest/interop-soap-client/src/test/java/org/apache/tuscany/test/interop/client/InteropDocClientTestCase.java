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

import java.awt.CompositeContext;
import java.rmi.RemoteException;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.soapinterop.ArrayOfSimpleDocument;
import org.soapinterop.ChildDocument;
import org.soapinterop.ComplexDocument;
import org.soapinterop.DocTestPortType;
import org.soapinterop.SimpleDocument;
import org.soapinterop.SimpleDocument1;
import org.soapinterop.SingleTag;

import calculator.CalculatorService;

import commonj.sdo.helper.DataFactory;

public class InteropDocClientTestCase extends TestCase {
    private SCADomain scaDomain;
    
    private DataFactory dataFactory;

    private DocTestPortType interopDoc;

    public InteropDocClientTestCase(){};

    
    @Override
    protected void setUp() throws Exception {

        scaDomain = SCADomain.newInstance("default.composite");
        interopDoc = scaDomain.getService(LoopbackInteropDocServiceComponentImpl.class, "CalculatorServiceComponent");

        //Get the SDO DataFactory
        dataFactory = DataFactory.INSTANCE;
    }
    
    
    public void testSingleTag() throws RemoteException {

        // Create the input
        SingleTag input = (SingleTag) dataFactory.create(SingleTag.class);

        // Invoke the service
        SingleTag output = interopDoc.SingleTag(input);

        // Test the results
        assertNotNull(output);

    }

    public void testSimpleDocument() throws RemoteException {

        // Create the input
        SimpleDocument1 input = (SimpleDocument1) dataFactory.create(SimpleDocument1.class);
        input.setValue("123");

        // Invoke the service
        SimpleDocument1 output = interopDoc.SimpleDocument(input);

        // Test the results
        assertNotNull(output);
        assertEquals("123", output.getValue());

    }

    public void testComplexDocument() throws RemoteException {

        // Create the input
        ComplexDocument input = (ComplexDocument) dataFactory.create(ComplexDocument.class);
        input.setAnAttribute("789");
        ChildDocument childDocument = (ChildDocument) dataFactory.create(ChildDocument.class);
        SimpleDocument simpleDocument = (SimpleDocument) dataFactory.create(SimpleDocument.class);
        ;
        SimpleDocument1 simpleDocument1 = (SimpleDocument1) dataFactory.create(SimpleDocument1.class);
        ;
        simpleDocument.setSimpleDocument(simpleDocument1);
        simpleDocument1.setValue("456");
        ArrayOfSimpleDocument arrayOfSimpleDocument = (ArrayOfSimpleDocument) dataFactory.create(ArrayOfSimpleDocument.class);
        ;
        arrayOfSimpleDocument.getSimpleDocument().add(simpleDocument1);
        childDocument.setChildSimpleDoc(arrayOfSimpleDocument);
        input.setChild(childDocument);

        // Invoke the service
        ComplexDocument output = interopDoc.ComplexDocument(input);

        // Test the results
        assertNotNull(output);
        assertEquals("789", output.getAnAttribute());
        assertNotNull(output.getChild());
        assertNotNull(output.getChild().getChildSimpleDoc());

        // FIXME Add more tests of the output document

    }


    /**
     * Locate the interop service to test
     * 
     * @return
     */
    protected DocTestPortType locateInteropDocService() {
        String interopLocation = System.getProperty("interopLocation");

        // Valid service names are:
        // RemoteInteropDocService: the live interop Web Service
        // LocalHostInteropDocService: the interop Web Service hosted by Tuscany on localhost
        // LoopbackInteropDocServiceComponent: a dummy loopback service component

        // To specify the service name run mvn -interopLocation="Remote"

        if (interopLocation == null)
            interopLocation = "Remote";

        CompositeContext compositeContext = CurrentCompositeContext.getContext();

        return (DocTestPortType) compositeContext.locateService(DocTestPortType.class, interopLocation + "InteropDocService");
    }

}
