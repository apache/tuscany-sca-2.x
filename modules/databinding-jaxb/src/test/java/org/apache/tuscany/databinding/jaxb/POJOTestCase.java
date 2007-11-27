package org.apache.tuscany.databinding.jaxb;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

public class POJOTestCase extends TestCase {
    public void testPOJO() throws Exception {
        JAXBContext context = JAXBContext.newInstance(MyBean.class);
        StringWriter writer = new StringWriter();
        MyBean bean = new MyBean();
        bean.setName("Test");
        bean.setAge(20);
        bean.getNotes().add("1");
        bean.getNotes().add("2");
        bean.getMap().put("1", 1);
        JAXBElement<Object> element = new JAXBElement<Object>(new QName("http://ns1", "bean"), Object.class, bean);
        context.createMarshaller().marshal(element, writer);
        System.out.println(writer.toString());

        Object result = context.createUnmarshaller().unmarshal(new StringReader(writer.toString()));
        assertTrue(result instanceof JAXBElement);
        JAXBElement e2 = (JAXBElement)result;
        assertTrue(e2.getValue() instanceof MyBean);
    }

    public void testPrimitive() throws Exception {
        JAXBContext context = JAXBContext.newInstance(String.class);
        StringWriter writer = new StringWriter();
        JAXBElement<Object> element = new JAXBElement<Object>(new QName("http://ns1", "bean"), Object.class, "ABC");
        context.createMarshaller().marshal(element, writer);
        System.out.println(writer.toString());

        Object result = context.createUnmarshaller().unmarshal(new StringReader(writer.toString()));
        assertTrue(result instanceof JAXBElement);
        JAXBElement e2 = (JAXBElement)result;
        assertTrue(e2.getValue() instanceof String);
    }

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
}
