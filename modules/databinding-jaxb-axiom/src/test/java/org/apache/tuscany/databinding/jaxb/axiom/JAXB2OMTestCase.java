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

import java.io.StringWriter;

import javax.xml.bind.JAXBElement;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.sca.databinding.jaxb.axiom.JAXB2OMElement;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.junit.Test;

import com.example.ipo.jaxb.ObjectFactory;
import com.example.ipo.jaxb.PurchaseOrderType;
import com.example.ipo.jaxb.USAddress;
import com.example.ipo.jaxb.USState;


/**
 * @version $Rev$ $Date$
 */
public class JAXB2OMTestCase {
    @Test
    public void testTransform() throws Exception {
        ObjectFactory factory = new ObjectFactory();
        PurchaseOrderType type = factory.createPurchaseOrderType();
        JAXBElement<PurchaseOrderType> po = factory.createPurchaseOrder(type);
        type.setItems(factory.createItems());
        type.setComment("123");
        USAddress address = factory.createUSAddress();
        address.setCity("San Jose");
        address.setStreet("ABC St.");
        address.setState(USState.CA);
        type.setShipTo(address);
        DataType sourceDataType = new DataTypeImpl<Class>(PurchaseOrderType.class, null);
        TransformationContext tContext = new TransformationContextImpl();
        tContext.setSourceDataType(sourceDataType);
        OMElement om = new JAXB2OMElement().transform(type, tContext);
        StringWriter sw = new StringWriter();
        // serializeAndConsume() will trigger the JAXBDataSource.serialize(Writer, OMOutputFormat)
        om.serializeAndConsume(sw);
        System.out.println(sw.toString());
    }
    
    @Test
    public void testTransform2() throws Exception {
        ObjectFactory factory = new ObjectFactory();
        PurchaseOrderType type = factory.createPurchaseOrderType();
        JAXBElement<PurchaseOrderType> po = factory.createPurchaseOrder(type);
        type.setItems(factory.createItems());
        type.setComment("123");
        USAddress address = factory.createUSAddress();
        address.setCity("San Jose");
        address.setStreet("ABC St.");
        address.setState(USState.CA);
        type.setShipTo(address);
        DataType sourceDataType = new DataTypeImpl<Class>(PurchaseOrderType.class, null);
        TransformationContext tContext = new TransformationContextImpl();
        tContext.setSourceDataType(sourceDataType);
        OMElement om = new JAXB2OMElement().transform(po, tContext);
        StringWriter sw = new StringWriter();
        // serializeAndConsume() will trigger the JAXBDataSource.serialize(Writer, OMOutputFormat)
        om.serializeAndConsume(sw);
        System.out.println(sw.toString());
    }
}
