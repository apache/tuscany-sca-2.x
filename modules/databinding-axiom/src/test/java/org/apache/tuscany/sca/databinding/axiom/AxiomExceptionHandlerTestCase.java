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

package org.apache.tuscany.sca.databinding.axiom;

import java.io.StringReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * Test case for SDOExceptionHandler
 */
public class AxiomExceptionHandlerTestCase extends TestCase {
    private static final String IPO_XML =
        "<?xml version=\"1.0\"?>" + "<ipo:purchaseOrder"
            + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + "  xmlns:ipo=\"http://www.example.com/IPO\""
            + "  xsi:schemaLocation=\"http://www.example.com/IPO ipo.xsd\""
            + "  orderDate=\"1999-12-01\">"
            + "  <shipTo exportCode=\"1\" xsi:type=\"ipo:UKAddress\">"
            + "    <name>Helen Zoe</name>"
            + "    <street>47 Eden Street</street>"
            + "    <city>Cambridge</city>"
            + "    <postcode>CB1 1JR</postcode>"
            + "  </shipTo>"
            + "  <billTo xsi:type=\"ipo:USAddress\">"
            + "    <name>Robert Smith</name>"
            + "    <street>8 Oak Avenue</street>"
            + "    <city>Old Town</city>"
            + "    <state>PA</state>"
            + "    <zip>95819</zip>"
            + "  </billTo>"
            + "  <items>"
            + "    <item partNum=\"833-AA\">"
            + "      <productName>Lapis necklace</productName>"
            + "      <quantity>1</quantity>"
            + "      <USPrice>99.95</USPrice>"
            + "      <ipo:comment>Want this for the holidays</ipo:comment>"
            + "      <shipDate>1999-12-05</shipDate>"
            + "    </item>"
            + "  </items>"
            + "</ipo:purchaseOrder>";

    private AxiomExceptionHandler handler;
    private OMElement faultElement;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.handler = new AxiomExceptionHandler();
        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(IPO_XML));
        StAXOMBuilder builder = new StAXOMBuilder(reader);
        faultElement = builder.getDocumentElement();
    }

    public void testGetFaultType() {
        DataType<?> dataType = handler.getFaultType(new DataTypeImpl<XMLType>(FaultException.class, null));
        assertEquals(OMElement.class, dataType.getPhysical());
        assertEquals(XMLType.UNKNOWN, dataType.getLogical());
        assertEquals(AxiomDataBinding.NAME, dataType.getDataBinding());
        dataType = handler.getFaultType(new DataTypeImpl<XMLType>(Exception.class, null));
        assertNull(dataType);
    }

    public void testCreate() {

        Exception ex = handler.createException(null, "Order", faultElement, null);
        assertTrue(ex instanceof FaultException);
        FaultException exception = (FaultException)ex;
        assertEquals("Order", exception.getMessage());
        assertSame(faultElement, exception.getFaultInfo());
    }

    public void testGetFaultInfo() {
        FaultException exception = new FaultException("Order", faultElement, null);
        Object faultInfo = handler.getFaultInfo(exception);
        assertSame(faultElement, faultInfo);
    }

}
