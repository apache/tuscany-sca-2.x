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
package org.apache.tuscany.idl.wsdl;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaException;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaType;

/**
 * Verifies the default XSD registry implementation
 * 
 * @version $Rev$ $Date$
 */
public class XMLSchemaRegistryImplTestCase extends TestCase {
    private static final QName PO_ELEMENT_NAME = new QName("http://www.example.com/IPO", "purchaseOrder");
    private static final QName PO_TYPE_NAME = new QName("http://www.example.com/IPO", "PurchaseOrderType");

    private static final String NS = "http://example.com/stockquote.xsd";
    private static final QName GET_LAST_TRADE_PRICE_ELEMENT_NAME = new QName(NS, "getLastTradePrice");

    private XMLSchemaRegistryImpl xsdRegistry;

    private WSDLDefinitionRegistryImpl wsdlRegistry;

    private ClassLoader cl;

    private URL wsdl;

    public void testLoadFromAbsoluteWSDLLocation() {
        try {
            Definition definition = wsdlRegistry.loadDefinition(null, wsdl);
            List<XmlSchema> schemas = xsdRegistry.loadSchemas(definition);
            Assert.assertTrue(schemas.size() == 1);
            XmlSchema schema = schemas.get(0);
            XmlSchemaElement element = schema.getElementByName(GET_LAST_TRADE_PRICE_ELEMENT_NAME);
            Assert.assertNotNull(element);
            XmlSchemaType type = element.getSchemaType();
            XmlSchemaComplexType complexType = (XmlSchemaComplexType) type;
            XmlSchemaSequence sequence = (XmlSchemaSequence) complexType.getParticle();
            XmlSchemaObjectCollection items = sequence.getItems();
            Assert.assertTrue(items.getItem(0) instanceof XmlSchemaElement);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
    
    public void testSchemaLocation() throws IOException {
        String schemaLocation = "http://www.example.com/IPO org/apache/tuscany/idl/wsdl/ipo.xsd";
        XmlSchema schema = xsdRegistry.loadSchema(schemaLocation, cl);
        Assert.assertNotNull(schema);
        Assert.assertNotNull(schema.getElementByName(PO_ELEMENT_NAME));
        Assert.assertNotNull(xsdRegistry.getElement(PO_ELEMENT_NAME));
        Assert.assertNotNull(schema.getTypeByName(PO_TYPE_NAME));
        Assert.assertNotNull(xsdRegistry.getType(PO_TYPE_NAME));

        
        schemaLocation = "http://www.example.com/IPO1 org/apache/tuscany/idl/wsdl/ipo.xsd";
        try {
            schema = xsdRegistry.loadSchema(schemaLocation, cl);
            Assert.fail("");
        } catch (XmlSchemaException e) {
            Assert.assertTrue(e.getMessage().contains("http://www.example.com/IPO1 !="));
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        xsdRegistry = new XMLSchemaRegistryImpl();
        wsdlRegistry = new WSDLDefinitionRegistryImpl();
        wsdlRegistry.setSchemaRegistry(xsdRegistry);
        wsdl = getClass().getResource("stockquote.wsdl");
        cl = getClass().getClassLoader();
    }

}
