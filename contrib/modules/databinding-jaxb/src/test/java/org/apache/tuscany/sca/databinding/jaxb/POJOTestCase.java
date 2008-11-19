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
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.TransformationContextImpl;

import com.sun.xml.bind.v2.model.annotation.RuntimeInlineAnnotationReader;
import com.sun.xml.bind.v2.model.core.Ref;
import com.sun.xml.bind.v2.model.impl.RuntimeModelBuilder;
import com.sun.xml.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimeTypeInfoSet;
import com.sun.xml.bind.v2.runtime.IllegalAnnotationsException;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;

/**
 *
 * @version $Rev$ $Date$
 */
public class POJOTestCase extends TestCase {
    public void testAdapter() throws Exception {
        JAXBContext context = JAXBContext.newInstance(MyJaxbBean.class, MyInterfaceImpl.class);
        StringWriter writer = new StringWriter();
        MyJaxbBean bean = new MyJaxbBean();
        bean.myBean = new MySubBean();
        bean.myBean.setName("Ray");
        bean.myInterface = new MyInterfaceImpl();
        bean.myInterface.setId("001");
        bean.myObject = new MyBean();
        ((MyBean) bean.myObject).setName("Y");
        context.createMarshaller().marshal(bean, writer);
        System.out.println(writer.toString());
        Object result = context.createUnmarshaller().unmarshal(new StringReader(writer.toString()));
        assertTrue(result instanceof MyJaxbBean);
        Map<String, String> schemas = JAXBTypeHelper.generateSchema(context);
        System.out.println(schemas);
    }
    
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
        MyBean newBean = (MyBean)e2.getValue();
        assertEquals(bean, newBean);
    }
    
    public void testPOJOArray() throws Exception {
        JAXBContext context = JAXBContext.newInstance(MyBean[].class, MySubBean.class);
        StringWriter writer = new StringWriter();
        MySubBean bean = new MySubBean();
        bean.setAddtional("SUB");
        bean.setName("Test");
        bean.setAge(20);
        bean.getNotes().add("1");
        bean.getNotes().add("2");
        bean.getMap().put("1", 1);

        JAXBElement<Object> element =
            new JAXBElement<Object>(new QName("http://ns1", "beans"), Object.class, new MyBean[] {bean});
        context.createMarshaller().marshal(element, writer);
        System.out.println(writer.toString());

        Object result =
            context.createUnmarshaller().unmarshal(new StreamSource(new StringReader(writer.toString())),
                                                   MyBean[].class);
        assertTrue(result instanceof JAXBElement);
        JAXBElement e2 = (JAXBElement)result;
        assertTrue(e2.getValue() instanceof MyBean[]);
        MyBean newBean = ((MyBean[])e2.getValue())[0];
        assertTrue(newBean instanceof MySubBean);
    }

    public void testXMLStreamReader() throws Exception {
        JAXBContext context = JAXBContext.newInstance(MyBean.class, MyInterfaceImpl.class);

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
        QName name = new QName("http://ns1", "bean");
        JAXBElement<Object> element = new JAXBElement<Object>(name, Object.class, bean);
        TransformationContext tContext = new TransformationContextImpl();
        XMLStreamReader reader = new JAXB2XMLStreamReader().transform(element, tContext);

//        XMLStreamReader2String t2 = new XMLStreamReader2String();
//        String xml = t2.transform(reader, null);
        // System.out.println(xml);
        Object result = context.createUnmarshaller().unmarshal(reader, MyBean.class);
        assertTrue(result instanceof JAXBElement);
        JAXBElement e2 = (JAXBElement)result;
        assertTrue(e2.getValue() instanceof MyBean);
        MyBean newBean = (MyBean)e2.getValue();
        // FIXME :To be implemented
        // assertEquals(bean, newBean);
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
        JAXBElement<Object> element =
            new JAXBElement<Object>(new QName("http://ns1", "bean"), Object.class, new String[] {"ABC", "123"});
        context.createMarshaller().marshal(element, writer);
        // System.out.println(writer.toString());

        Object result = context.createUnmarshaller().unmarshal(new StringReader(writer.toString()));
        assertTrue(result instanceof JAXBElement);
        JAXBElement e2 = (JAXBElement)result;
        assertTrue(e2.getValue() instanceof String[]);
    }

    public void testByteArray() throws Exception {
        JAXBContext context = JAXBContext.newInstance(byte[].class);
        StringWriter writer = new StringWriter();
        JAXBElement<Object> element =
            new JAXBElement<Object>(new QName("http://ns1", "bean"), Object.class, "ABC".getBytes());
        context.createMarshaller().marshal(element, writer);
        String xml = writer.toString();
        assertTrue(xml.contains("QUJD"));
        assertTrue(xml.contains("base64Binary"));

        Object result = context.createUnmarshaller().unmarshal(new StringReader(xml));
        assertTrue(result instanceof JAXBElement);
        JAXBElement e2 = (JAXBElement)result;
        assertTrue(e2.getValue() instanceof byte[]);
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

    private static RuntimeTypeInfoSet create(Class... classes) throws Exception {
        IllegalAnnotationsException.Builder errorListener = new IllegalAnnotationsException.Builder();
        RuntimeInlineAnnotationReader reader = new RuntimeInlineAnnotationReader();
        JAXBContextImpl context =
            new JAXBContextImpl(classes, null, Collections.<Class, Class> emptyMap(), null, false, reader, false, false);
        RuntimeModelBuilder builder =
            new RuntimeModelBuilder(context, reader, Collections.<Class, Class> emptyMap(), null);
        builder.setErrorHandler(errorListener);
        for (Class c : classes)
            builder.getTypeInfo(new Ref<Type, Class>(c));

        RuntimeTypeInfoSet r = builder.link();
        errorListener.check();
        return r;
    }

    public void testReflection() throws Exception {
        MyBean bean = new MyBean();
        RuntimeTypeInfoSet model = create(MyBean.class);
        RuntimeClassInfo clsInfo = (RuntimeClassInfo)model.getTypeInfo(MyBean.class);
        for (RuntimePropertyInfo p : clsInfo.getProperties()) {
            // System.out.print(p.getName());
            // System.out.println(" " + p.isCollection());
            if (p.getName().equals("notes")) {
                Collection c = (Collection)p.getAccessor().get(bean);
                c.add("123");
            }
        }

    }
}
