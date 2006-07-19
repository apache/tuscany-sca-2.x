package org.apache.tuscany.core.loader;

import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.model.Property;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * @version $Rev$ $Date$
 */
public class StringParserPropertyFactoryTestCase extends MockObjectTestCase {


    public void testInteger() throws Exception {
        Mock mock = mock(XMLStreamReader.class);
        mock.expects(once()).method("getElementText").will(returnValue("1"));
        XMLStreamReader reader = (XMLStreamReader) mock.proxy();
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Integer> property = new Property<Integer>();
        property.setJavaType(Integer.class);
        ObjectFactory<Integer> oFactory = factory.createObjectFactory(reader, property);
        assertEquals(1, oFactory.getInstance().intValue());
    }

    public void testPrimitiveInt() throws Exception {
        Mock mock = mock(XMLStreamReader.class);
        mock.expects(once()).method("getElementText").will(returnValue("1"));
        XMLStreamReader reader = (XMLStreamReader) mock.proxy();
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Integer> property = new Property<Integer>();
        property.setJavaType(Integer.TYPE);
        ObjectFactory<Integer> oFactory = factory.createObjectFactory(reader, property);
        assertEquals(1, oFactory.getInstance().intValue());
    }

    public void testString() throws Exception {
        Mock mock = mock(XMLStreamReader.class);
        mock.expects(once()).method("getElementText").will(returnValue("1"));
        XMLStreamReader reader = (XMLStreamReader) mock.proxy();
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<String> property = new Property<String>();
        property.setJavaType(String.class);
        ObjectFactory<String> oFactory = factory.createObjectFactory(reader, property);
        assertEquals("1", oFactory.getInstance());
    }

    public void testByteArray() throws Exception {
        Mock mock = mock(XMLStreamReader.class);
        mock.expects(once()).method("getElementText").will(returnValue("1"));
        XMLStreamReader reader = (XMLStreamReader) mock.proxy();
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<byte[]> property = new Property<byte[]>();
        property.setJavaType(byte[].class);
        ObjectFactory<byte[]> oFactory = factory.createObjectFactory(reader, property);
        byte[] result = oFactory.getInstance();
        byte[] expected = "1".getBytes();
        for (int i = 0; i < result.length; i++) {
            byte b = result[i];
            if (b != expected[i]) {
                fail();
            }
        }
    }

    public void testBoolean() throws Exception {
        Mock mock = mock(XMLStreamReader.class);
        mock.expects(once()).method("getElementText").will(returnValue("true"));
        XMLStreamReader reader = (XMLStreamReader) mock.proxy();
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Boolean> property = new Property<Boolean>();
        property.setJavaType(Boolean.class);
        ObjectFactory<Boolean> oFactory = factory.createObjectFactory(reader, property);
        assertTrue(oFactory.getInstance());
    }

    public void testPrimitiveBoolean() throws Exception {
        Mock mock = mock(XMLStreamReader.class);
        mock.expects(once()).method("getElementText").will(returnValue("true"));
        XMLStreamReader reader = (XMLStreamReader) mock.proxy();
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Boolean> property = new Property<Boolean>();
        property.setJavaType(Boolean.TYPE);
        ObjectFactory<Boolean> oFactory = factory.createObjectFactory(reader, property);
        assertTrue(oFactory.getInstance());
    }

    public void testStringConstructor() throws Exception {
        Mock mock = mock(XMLStreamReader.class);
        mock.expects(once()).method("getElementText").will(returnValue("test"));
        XMLStreamReader reader = (XMLStreamReader) mock.proxy();
        StringParserPropertyFactory factory = new StringParserPropertyFactory();
        Property<Foo> property = new Property<Foo>();
        property.setJavaType(Foo.class);
        ObjectFactory<Foo> oFactory = factory.createObjectFactory(reader, property);
        assertEquals("test", oFactory.getInstance().getFoo());
    }

    private static class Foo {
        private String foo;

        public Foo(String foo) {
            this.foo = foo;
        }

        public String getFoo() {
            return foo;
        }
    }


}
