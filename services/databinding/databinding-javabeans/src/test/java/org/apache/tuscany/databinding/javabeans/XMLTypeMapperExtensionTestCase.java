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

package org.apache.tuscany.databinding.javabeans;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.extension.DOMHelper;
import org.apache.tuscany.spi.idl.TypeInfo;
import org.apache.tuscany.spi.model.DataType;
import org.easymock.EasyMock;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Testcase to test the XMLTypeMapperExtension which is the back bone for all transformations supported
 * by the JavaBeans Databinding.
 */
public class XMLTypeMapperExtensionTestCase extends TestCase {

    XMLTypeMapperExtension<Node> extension = new XMLTypeMapperExtension<Node>();

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testFieldSettings() throws Exception {
        String samplePropertyXML =
                "<property name=\"prop2\" >" + "<integerNumber>27</integerNumber>"
                        + "<floatNumber>79.34</floatNumber>"
                        + "<doubleNumber>184.52</doubleNumber>" + "<innerProperty>"
                        + "<integerNumber>54</integerNumber>" + "<floatNumber>158.68</floatNumber>"
                        + "<doubleNumber>369.04</doubleNumber>" + "</innerProperty>"
                        + "<stringArray>TestString_1</stringArray>"
                        + "<stringArray>TestString_2</stringArray>" + "<boolArray>true</boolArray>"
                        + "<boolArray>false</boolArray>" + "</property>";

        DocumentBuilder builder = DOMHelper.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(samplePropertyXML));
        Node samplePropertyNode = builder.parse(inputSource);
        TypeInfo typeInfo = new TypeInfo(null, false, null);

        TransformationContext context = EasyMock.createMock(TransformationContext.class);
        DataType<Class> dataType = new DataType<Class>(null, SamplePropertyBean.class);
        EasyMock.expect(context.getTargetDataType()).andReturn(dataType).anyTimes();
        EasyMock.replay(context);

        Object javaObject = extension.toJavaObject(typeInfo, samplePropertyNode, context);

        assertTrue(javaObject instanceof SamplePropertyBean);
        SamplePropertyBean samplePropBean = (SamplePropertyBean) javaObject;
        assertEquals(samplePropBean.getIntegerNumber(), 27);
        assertEquals((float) 79.34, (float) samplePropBean.getFloatNumber());
        assertEquals((double) samplePropBean.getInnerProperty().getDoubleNumber(), (double) 369.04);

        assertEquals(samplePropBean.getStringArray()[0], "TestString_1");
        assertEquals(samplePropBean.boolArray[0], true);

        javax.xml.transform.Transformer transformer =
                TransformerFactory.newInstance().newTransformer();
        Node aNode = extension.toDOMNode(javaObject, context);
        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(aNode), new StreamResult(sw));
        String nodeString = sw.toString();
        System.out.println(nodeString);

        assertTrue(nodeString.indexOf("<doubleNumber>184.52</doubleNumber>") != -1);
        assertTrue(nodeString.indexOf("<stringArray>TestString_1</stringArray>"
                + "<stringArray>TestString_2</stringArray>") != -1);
        assertTrue(nodeString.indexOf("<integerNumber>27</integerNumber>") != -1);

        int startIndex = nodeString.indexOf("<innerProperty>");
        int endIndex = nodeString.indexOf("</innerProperty>");
        String fragment = nodeString.substring(startIndex, endIndex);
        assertTrue(fragment.indexOf("<integerNumber>54</integerNumber>") != -1);

        //System.out.println(sw.toString());

    }

    public void testJava2NodeMapping() throws Exception {
        SamplePropertyBean propertyBean = new SamplePropertyBean();
        TransformationContext context = EasyMock.createMock(TransformationContext.class);
        DataType<Class> dataType = new DataType<Class>(null, SamplePropertyBean.class);
        EasyMock.expect(context.getTargetDataType()).andReturn(dataType).anyTimes();
        EasyMock.replay(context);

        javax.xml.transform.Transformer transformer =
                TransformerFactory.newInstance().newTransformer();
        Object data = new int[] { 10, 20, 30, 40 };
        Node aNode = extension.toDOMNode(data, context);
        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(aNode), new StreamResult(sw));

        //System.out.println(sw.toString());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><int_collection><int>10</int><int>20</int><int>30</int><int>40</int></int_collection>",
                     sw.toString());
    }

    public static class SamplePropertyBean {

        public float floatNumber = 50;

        public double doubleNumber = 75;

        public boolean[] boolArray;

        private int integerNumber = 25;

        private String[] stringArray;

        public SamplePropertyBean innerProperty;

        public SamplePropertyBean() {

        }

        public double getDoubleNumber() {
            return doubleNumber;
        }

        public void setDoubleNumber(double doubleNumber) {
            this.doubleNumber = doubleNumber;
        }

        public float getFloatNumber() {
            return floatNumber;
        }

        public void setFloatNumber(float floatNumber) {
            this.floatNumber = floatNumber;
        }

        public int getIntegerNumber() {
            return integerNumber;
        }

        public void setIntegerNumber(int integerNumber) {
            this.integerNumber = integerNumber;
        }

        public SamplePropertyBean getInnerProperty() {
            return innerProperty;
        }

        public void setInnerProperty(SamplePropertyBean prop) {
            this.innerProperty = prop;
        }

        public String toString() {
            return Double.toString(integerNumber + floatNumber + doubleNumber) + " & "
                    + ((innerProperty == null) ? "" : innerProperty.toString());
        }

        public String[] getStringArray() {
            return stringArray;
        }

        public void setStringArray(String[] stringArray) {
            this.stringArray = stringArray;
        }

    }
}
