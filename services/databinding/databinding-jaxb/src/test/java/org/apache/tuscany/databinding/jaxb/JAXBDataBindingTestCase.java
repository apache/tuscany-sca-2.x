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

package org.apache.tuscany.databinding.jaxb;

import java.lang.annotation.Annotation;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.XMLType;

import com.example.ipo.jaxb.ObjectFactory;
import com.example.ipo.jaxb.PurchaseOrderType;
import com.example.ipo.jaxb.USAddress;
import com.example.ipo.jaxb.USState;

/**
 * 
 */
public class JAXBDataBindingTestCase extends TestCase {
    private JAXBDataBinding binding;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        binding = new JAXBDataBinding();
    }

    /**
     * Test method for
     * {@link org.apache.tuscany.databinding.jaxb.JAXBDataBinding#introspect(java.lang.Class, Annotation)}.
     */
    public final void testIntrospect() {
        DataType dataType = new DataType(JAXBElement.class, null);
        boolean yes = binding.introspect(dataType, null);
        assertTrue(yes);
        assertTrue(dataType.getDataBinding().equals(binding.getName()));
        assertTrue(dataType.getPhysical() == JAXBElement.class && dataType.getLogical() == XMLType.UNKNOWN);
        dataType = new DataType(MockJAXBElement.class, null);
        yes = binding.introspect(dataType, null);
        assertTrue(yes);
        assertEquals(MockJAXBElement.class, dataType.getPhysical());
        assertEquals(new QName("http://www.example.com/IPO", "PurchaseOrderType"), ((XMLType)dataType.getLogical())
            .getTypeName());
        dataType = new DataType(USAddress.class, null);
        yes = binding.introspect(dataType, null);
        assertTrue(yes);
        assertEquals(USAddress.class, dataType.getPhysical());
        assertEquals(new QName("http://www.example.com/IPO", "USAddress"), ((XMLType)dataType.getLogical())
            .getTypeName());
        dataType = new DataType(USState.class, null);
        yes = binding.introspect(dataType, null);
        assertTrue(yes);
        assertTrue(dataType.getDataBinding().equals(binding.getName()));
        assertEquals(USState.class, dataType.getPhysical());
        assertEquals(new QName("http://www.example.com/IPO", "USState"), ((XMLType)dataType.getLogical()).getTypeName());

    }

    private static class MockJAXBElement extends JAXBElement<PurchaseOrderType> {

        private static final long serialVersionUID = -2767569071002707973L;

        /**
         * @param elementName
         * @param type
         * @param value
         */
        public MockJAXBElement(QName elementName, Class<PurchaseOrderType> type, PurchaseOrderType value) {
            super(elementName, type, value);
        }

    }

    @SuppressWarnings("unchecked")
    public void testCopy() {
        ObjectFactory factory = new ObjectFactory();
        PurchaseOrderType poType = factory.createPurchaseOrderType();
        JAXBElement<PurchaseOrderType> po = factory.createPurchaseOrder(poType);
        JAXBElement<PurchaseOrderType> copy = (JAXBElement<PurchaseOrderType>)binding.copy(po);
        assertEquals(new QName("http://www.example.com/IPO", "purchaseOrder"), copy.getName());
    }

    @SuppressWarnings("unchecked")
    public void testCopyNonElement() {
        ObjectFactory factory = new ObjectFactory();
        PurchaseOrderType poType = factory.createPurchaseOrderType();
        poType.setComment("Comment");
        PurchaseOrderType copy = (PurchaseOrderType)binding.copy(poType);
        assertTrue(copy instanceof PurchaseOrderType);
        assertEquals("Comment", ((PurchaseOrderType)copy).getComment());
    }

    @SuppressWarnings("unchecked")
    public void testCopyNonRoot() {
        ObjectFactory factory = new ObjectFactory();
        USAddress address = factory.createUSAddress();
        address.setCity("San Jose");
        USAddress copy = (USAddress)binding.copy(address);
        assertTrue(copy instanceof USAddress);
        assertEquals("San Jose", ((USAddress)copy).getCity());

    }
}
