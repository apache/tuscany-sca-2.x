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

package org.apache.tuscany.databinding.jaxb.axiom;

import java.io.ByteArrayInputStream;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.sca.databinding.jaxb.axiom.OMElement2JAXB;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.junit.Test;

import com.example.ipo.jaxb.PurchaseOrderType;

/**
 * @version $Rev$ $Date$
 */
public class OMElement2JAXBTestCase {
    private static final String XML =
        "<ns0:root xmlns:ns0=\"http://ns0\" xmlns:ns2=\"http://www.example.com/IPO\">" + "<ns1:next xmlns:ns1=\"http://ns1\">"
            + "<ns2:purchaseOrder>"
            + "<shipTo xsi:type=\"ns2:USAddress\" "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
            + "<street>ABC St.</street><city>San Jose</city><state>CA</state></shipTo>"
            + "<ns2:comment>123</ns2:comment><items/>"
            + "</ns2:purchaseOrder>"
            + "</ns1:next>"
            + "</ns0:root>";

    @Test
    public void testTransform() throws Exception {
        DataType<?> sourceDataType = new DataTypeImpl<XMLType>(PurchaseOrderType.class, XMLType.UNKNOWN);
        QName qname = new QName("http://www.example.com/IPO", "purchaseOrder");
        DataType<?> targetDataType = new DataTypeImpl<XMLType>(PurchaseOrderType.class, new XMLType(qname, null));
        TransformationContext tContext = new TransformationContextImpl();
        tContext.setSourceDataType(sourceDataType);
        tContext.setTargetDataType(targetDataType);

        StAXOMBuilder builder = new StAXOMBuilder(new ByteArrayInputStream(XML.getBytes("UTF-8")));
        OMElement root = builder.getDocumentElement();
        OMElement next = (OMElement)root.getChildElements().next();
        OMElement po = (OMElement)next.getChildElements().next();
        Object jaxb = new OMElement2JAXB().transform(po, tContext);
        Assert.assertTrue(jaxb instanceof PurchaseOrderType);
    }
}
