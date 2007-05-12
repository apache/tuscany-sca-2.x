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

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.impl.DOMHelper;
import org.apache.tuscany.interfacedef.DataType;
import org.apache.tuscany.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.easymock.EasyMock;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Testcase to test the XMLTypeMapperExtension which is the back bone for all transformations supported by the JavaBeans
 * Databinding.
 *
 * @version $Rev$ $Date$
 */
public class DOMNode2JavaBeanTransformerTestCase extends TestCase {

   private DOMNode2JavaBeanTransformer dom2JavaTransformer = new DOMNode2JavaBeanTransformer();

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
        DataType<Class> targetDataType = new DataTypeImpl<Class>(SamplePropertyBean.class, SamplePropertyBean.class);
        EasyMock.expect(context.getTargetDataType()).andReturn(targetDataType).anyTimes();

        DataType<XMLType> sourceDataType = new DataTypeImpl<XMLType>(null, new XMLType(typeInfo));
        // ElementInfo eleInfo = new ElementInfo(null, typeInfo);
        // sourceDataType.setMetadata(ElementInfo.class.getName(), eleInfo);
        EasyMock.expect(context.getSourceDataType()).andReturn(sourceDataType).anyTimes();
        EasyMock.replay(context);

        Object javaObject =
            dom2JavaTransformer.transform(((Document) samplePropertyNode).getDocumentElement(), context);

        assertTrue(javaObject instanceof SamplePropertyBean);
        SamplePropertyBean samplePropBean = (SamplePropertyBean) javaObject;
        assertEquals(samplePropBean.getIntegerNumber(), 27);
        assertEquals((float) 79.34, samplePropBean.getFloatNumber());
        assertEquals(samplePropBean.getInnerProperty().getDoubleNumber(), 369.04);

        assertEquals(samplePropBean.getStringArray()[0], "TestString_1");
        assertEquals(samplePropBean.boolArray[0], true);

        /** testing for object to node * */
        javax.xml.transform.Transformer transformer =
            TransformerFactory.newInstance().newTransformer();
        JavaBean2DOMNodeTransformer java2DomTransformer = new JavaBean2DOMNodeTransformer();
        Node aNode = java2DomTransformer.transform(javaObject, context);
        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(aNode), new StreamResult(sw));
        String nodeString = sw.toString();
        //System.out.println(nodeString);

        // testing the case when field and getter method do not have public access
        assertTrue(nodeString.indexOf("<doubleNumber>184.52</doubleNumber>") == -1);
        // test the case for fields that are of array type
        assertTrue(nodeString.indexOf("<stringArray>TestString_1</stringArray>"
            + "<stringArray>TestString_2</stringArray>") != -1);
        // testing the case for non-public field with public getter method
        assertTrue(nodeString.indexOf("<integerNumber>27</integerNumber>") != -1);
        // test the case for public field that is a another java bean .i.e. embeded javabean
        int startIndex = nodeString.indexOf("<innerProperty>");
        int endIndex = nodeString.indexOf("</innerProperty>");
        String fragment = nodeString.substring(startIndex, endIndex);
        assertTrue(fragment.indexOf("<integerNumber>54</integerNumber>") != -1);

        // System.out.println(sw.toString());

    }


    public static class SamplePropertyBean {

        private float floatNumber = 50;
        private SamplePropertyBean innerProperty;
        public boolean[] boolArray;
        private double doubleNumber = 75;
        private int integerNumber = 25;
        private String[] stringArray;

        public SamplePropertyBean() {

        }

        double getDoubleNumber() {
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
