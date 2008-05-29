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

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.sca.databinding.jaxb.JAXBWrapperHandler;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;

import com.example.stock.StockQuoteOffer;

/**
 * Test case for JAXBExceptionHandler
 *
 * @version $Rev$ $Date$
 */
public class JAXBWrapperHandlerTestCase extends TestCase {
    private static final QName ELEMENT = new QName("http://www.example.com/stock", "stockQuoteOffer");
    private static final QName INPUT = new QName("", "input");
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
        ElementInfo element = new ElementInfo(ELEMENT, null);
        Object offer = handler.create(element, StockQuoteOffer.class, null);
        Assert.assertTrue(offer instanceof StockQuoteOffer);
    }

    public void testSetChild() {
        StockQuoteOffer wrapper = new StockQuoteOffer();
        handler.setChild(wrapper, 0, new ElementInfo(INPUT, null), "IBM");
        Assert.assertEquals("IBM", wrapper.getInput());
    }

    public void testGetChildren() {
        StockQuoteOffer wrapper = new StockQuoteOffer();
        wrapper.setInput("IBM");
        List<ElementInfo> elements = new ArrayList<ElementInfo>();
        elements.add(new ElementInfo(INPUT, null));
        List children = handler.getChildren(wrapper, elements, null);
        assertNotNull(children);
        assertEquals(1, children.size());
        assertEquals("IBM", children.get(0));
    }
}
