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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

public class POJOTestCase extends TestCase {
    public void testPOJO() throws Exception {
        JAXBContext context = JAXBContext.newInstance(MyBean.class, MyInterfaceImpl.class);
        StringWriter writer = new StringWriter();
        MyBean bean = new MyBean();
        bean.setName("Test");
        bean.setAge(20);
        bean.getNotes().add("1");
        bean.getNotes().add("2");
        bean.getMap().put("1", 1);
        MyInterface service = new MyInterfaceImpl();
        service.setId("ID001");
        bean.setService(service);
        bean.setOtherService(service);
        JAXBElement<Object> element = new JAXBElement<Object>(new QName("http://ns1", "bean"), Object.class, bean);
        context.createMarshaller().marshal(element, writer);
        // System.out.println(writer.toString());

        Object result = context.createUnmarshaller().unmarshal(new StringReader(writer.toString()));
        assertTrue(result instanceof JAXBElement);
        JAXBElement e2 = (JAXBElement)result;
        assertTrue(e2.getValue() instanceof MyBean);
        MyBean newBean = (MyBean) e2.getValue();
        assertEquals(bean, newBean);
    }

    public void testString() throws Exception {
        JAXBContext context = JAXBContext.newInstance(String.class);
        StringWriter writer = new StringWriter();
        JAXBElement<Object> element = new JAXBElement<Object>(new QName("http://ns1", "bean"), Object.class, "ABC");
        context.createMarshaller().marshal(element, writer);
        // System.out.println(writer.toString());

        Object result = context.createUnmarshaller().unmarshal(new StringReader(writer.toString()));
        assertTrue(result instanceof JAXBElement);
        JAXBElement e2 = (JAXBElement)result;
        assertEquals("ABC", e2.getValue());
    }
    
    public void testNull() throws Exception {
        JAXBContext context = JAXBContext.newInstance(String.class);
        StringWriter writer = new StringWriter();
        JAXBElement<Object> element = new JAXBElement<Object>(new QName("http://ns1", "bean"), Object.class, null);
        element.setNil(true);
        context.createMarshaller().marshal(element, writer);
        // System.out.println(writer.toString());
        StreamSource source = new StreamSource(new StringReader(writer.toString()));
        Object result = context.createUnmarshaller().unmarshal(source, String.class);
        assertTrue(result instanceof JAXBElement);
        JAXBElement e2 = (JAXBElement)result;
        assertNull(e2.getValue());
    }
    
    public void testArray() throws Exception {
        JAXBContext context = JAXBContext.newInstance(String[].class);
        StringWriter writer = new StringWriter();
        JAXBElement<Object> element = new JAXBElement<Object>(new QName("http://ns1", "bean"), Object.class, new String[] {"ABC", "123"});
        context.createMarshaller().marshal(element, writer);
        // System.out.println(writer.toString());

        Object result = context.createUnmarshaller().unmarshal(new StringReader(writer.toString()));
        assertTrue(result instanceof JAXBElement);
        JAXBElement e2 = (JAXBElement)result;
        assertTrue(e2.getValue() instanceof String[]);
    }
    
    public void testPrimitive() throws Exception {
        JAXBContext context = JAXBContext.newInstance(int.class);
        StringWriter writer = new StringWriter();
        JAXBElement<Object> element = new JAXBElement<Object>(new QName("http://ns1", "bean"), Object.class, 1);
        context.createMarshaller().marshal(element, writer);
        // System.out.println(writer.toString());

        Object result = context.createUnmarshaller().unmarshal(new StringReader(writer.toString()));
        assertTrue(result instanceof JAXBElement);
        JAXBElement e2 = (JAXBElement)result;
        assertEquals(1, e2.getValue());
    }

    /*
    public void testException() throws Exception {
        JAXBContext context = JAXBContext.newInstance(IllegalArgumentException.class);
        StringWriter writer = new StringWriter();
        Exception e = new IllegalArgumentException("123");
        JAXBElement<Object> element = new JAXBElement<Object>(new QName("http://ns1", "bean"), Object.class, e);
        context.createMarshaller().marshal(element, writer);
        System.out.println(writer.toString());

        Object result = context.createUnmarshaller().unmarshal(new StringReader(writer.toString()));
        assertTrue(result instanceof JAXBElement);
        JAXBElement e2 = (JAXBElement)result;
        assertTrue(e2.getValue() instanceof Exception);
    }
    */
}
