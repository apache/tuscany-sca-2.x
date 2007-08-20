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

import java.util.List;

import javax.xml.bind.JAXBElement;

import junit.framework.TestCase;

import org.apache.tuscany.sca.databinding.jaxb.JAXBWrapperHandler;

import com.example.ipo.jaxb.ObjectFactory;
import com.example.ipo.jaxb.PurchaseOrderType;

/**
 * Test case for JAXBExceptionHandler
 */
public class JAXBWrapperHandlerTestCase extends TestCase {
    // private static final QName ELEMENT = new QName("http://www.example.com/IPO", "purchaseOrder");
    private JAXBWrapperHandler handler;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.handler = new JAXBWrapperHandler();
    }

    public void testCreate() {
        // ElementInfo element = new ElementInfo(ELEMENT, null);
        // JAXBElement<?> jaxbElement = handler.create(element, null);
    }

    public void testSetChild() {
        ObjectFactory factory = new ObjectFactory();
        PurchaseOrderType po = factory.createPurchaseOrderType();
        JAXBElement<PurchaseOrderType> wrapper = factory.createPurchaseOrder(po);
        handler.setChild(wrapper, 2, null, "Comment");
    }

    public void testGetChildren() {
        ObjectFactory factory = new ObjectFactory();
        PurchaseOrderType po = factory.createPurchaseOrderType();
        po.setComment("Comment");
        JAXBElement<PurchaseOrderType> wrapper = factory.createPurchaseOrder(po);
        List children = handler.getChildren(wrapper);
        assertNotNull(children);
        assertEquals(4, children.size());
        assertEquals("Comment", children.get(2));
        assertNull(children.get(0));
    }
}
