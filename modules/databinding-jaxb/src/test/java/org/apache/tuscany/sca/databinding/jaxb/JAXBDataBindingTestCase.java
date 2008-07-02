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

package org.apache.tuscany.sca.databinding.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.sca.databinding.jaxb.JAXBDataBinding;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

import com.example.ipo.jaxb.ObjectFactory;
import com.example.ipo.jaxb.PurchaseOrderType;
import com.example.ipo.jaxb.USAddress;
import com.example.ipo.jaxb.USState;

/**
 *
 * @version $Rev$ $Date$
 */
public class JAXBDataBindingTestCase extends TestCase {
    private JAXBDataBinding binding;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        binding = new JAXBDataBinding();
    }

    /**
     * Test method for
     * {@link org.apache.tuscany.sca.databinding.jaxb.JAXBDataBinding#introspect(java.lang.Class, Operation)}.
     */
    public final void testIntrospect() {
        DataType dataType = new DataTypeImpl<Class>(JAXBElement.class, null);
        Operation op = null;
        boolean yes = binding.introspect(dataType, op);
        assertTrue(yes);
        assertTrue(dataType.getDataBinding().equals(binding.getName()));
        assertTrue(dataType.getPhysical() == JAXBElement.class && dataType.getLogical() == XMLType.UNKNOWN);
        dataType = new DataTypeImpl<Class>(MockJAXBElement.class, null);
        yes = binding.introspect(dataType, op);
        assertTrue(yes);
        assertEquals(MockJAXBElement.class, dataType.getPhysical());
        assertEquals(new QName("http://www.example.com/IPO", "PurchaseOrderType"), ((XMLType)dataType.getLogical())
            .getTypeName());
        dataType = new DataTypeImpl<Class>(USAddress.class, null);
        yes = binding.introspect(dataType, op);
        assertTrue(yes);
        assertEquals(USAddress.class, dataType.getPhysical());
        assertEquals(new QName("http://www.example.com/IPO", "USAddress"), ((XMLType)dataType.getLogical())
            .getTypeName());
        dataType = new DataTypeImpl<Class>(USState.class, null);
        yes = binding.introspect(dataType, op);
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
        JAXBElement<PurchaseOrderType> copy = (JAXBElement<PurchaseOrderType>)binding.copy(po, null, null);
        assertEquals(new QName("http://www.example.com/IPO", "purchaseOrder"), copy.getName());
    }

    @SuppressWarnings("unchecked")
    public void testCopyNonElement() {
        ObjectFactory factory = new ObjectFactory();
        PurchaseOrderType poType = factory.createPurchaseOrderType();
        poType.setComment("Comment");
        PurchaseOrderType copy = (PurchaseOrderType)binding.copy(poType, null, null);
        assertTrue(copy instanceof PurchaseOrderType);
        assertEquals("Comment", (copy).getComment());
    }

    @SuppressWarnings("unchecked")
    public void testCopyNonRoot() {
        ObjectFactory factory = new ObjectFactory();
        USAddress address = factory.createUSAddress();
        address.setCity("San Jose");
        USAddress copy = (USAddress)binding.copy(address, null, null);
        assertTrue(copy instanceof USAddress);
        assertEquals("San Jose", (copy).getCity());

    }
}
