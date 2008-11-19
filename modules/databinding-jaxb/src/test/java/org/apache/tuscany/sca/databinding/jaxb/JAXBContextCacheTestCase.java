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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import junit.framework.Assert;

import org.apache.tuscany.sca.databinding.jaxb.JAXBContextCache;
import org.apache.tuscany.sca.databinding.util.LRUCache;
import org.junit.Test;

import com.example.ipo.jaxb.Address;
import com.example.ipo.jaxb.PurchaseOrderType;

/**
 * @version $Rev$ $Date$
 */
public class JAXBContextCacheTestCase {
    @Test
    public void testCache() throws JAXBException {
        JAXBContextCache cache = new JAXBContextCache();
        JAXBContext context1 = cache.getJAXBContext(String.class);
        JAXBContext context2 = cache.getJAXBContext(int.class);
        JAXBContext context3 = cache.getJAXBContext(String[].class);
        JAXBContext context4 = cache.getJAXBContext(Source.class);
        Assert.assertSame(context1, context2);
        Assert.assertNotSame(context2, context3);
        Assert.assertSame(context1, context4);

        QName name = new QName("http://example.com/ns1", "e1");
        JAXBElement<String> element = new JAXBElement<String>(name, String.class, "123");
        StringWriter sw = new StringWriter();
        context4.createMarshaller().marshal(element, sw);
        StreamSource source = new StreamSource(new StringReader(sw.toString()), null);
        context4.createUnmarshaller().unmarshal(source, String.class);

        JAXBContext context5 = cache.getJAXBContext(Address.class);
        JAXBContext context6 = cache.getJAXBContext(PurchaseOrderType.class);
        Assert.assertSame(context5, context6);
    }

    @Test
    public void testLRUCache() {
        LRUCache<String, String> cache = new LRUCache<String, String>(3);
        cache.put("1", "A");
        Assert.assertEquals(1, cache.size());
        cache.put("2", "B");
        Assert.assertEquals(2, cache.size());
        cache.put("3", "C");
        Assert.assertEquals(3, cache.size());
        cache.put("4", "D");
        Assert.assertEquals(3, cache.size());
        String data = cache.get("1");
        Assert.assertNull(data);
        data = cache.get("2");
        Assert.assertEquals("B", data);
        cache.put("5", "E");
        data = cache.get("2");
        Assert.assertEquals("B", data);
    }

    @Test
    public void testPerf() throws JAXBException {
        JAXBContextCache cache = new JAXBContextCache();
        
        // Test JAXBContext for simple java classes
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            JAXBContext context = JAXBContext.newInstance(String.class);
        }
        long end = System.currentTimeMillis();
        long d1 = end - start;
        start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            JAXBContext context = cache.getJAXBContext(String.class);
        }
        end = System.currentTimeMillis();
        long d2 = end - start;
        System.out.println(d1 + "ms vs. " + d2 + "ms");
        
        // Test JAXBContext for generated JAXB classes
        start = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            JAXBContext context = JAXBContext.newInstance(PurchaseOrderType.class);
        }
        end = System.currentTimeMillis();
        d1 = end - start;
        start = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            JAXBContext context = cache.getJAXBContext(PurchaseOrderType.class);
        }
        end = System.currentTimeMillis();
        d2 = end - start;
        System.out.println(d1 + "ms vs. " + d2 + "ms");

    }
}
