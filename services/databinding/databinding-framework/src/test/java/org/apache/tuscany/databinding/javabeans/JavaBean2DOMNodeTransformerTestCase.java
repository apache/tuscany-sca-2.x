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

import java.io.StringWriter;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

import org.apache.tuscany.databinding.javabeans.JavaBean2DOMNodeTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.model.DataType;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * Testcase to test the XMLTypeMapperExtension which is the back bone for all transformations supported by the JavaBeans
 * Databinding.
 *
 * @version $Rev$ $Date$
 */
public class JavaBean2DOMNodeTransformerTestCase extends TestCase {
    private JavaBean2DOMNodeTransformer aTransformer = new JavaBean2DOMNodeTransformer();

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testTranformation() throws Exception {
        TransformationContext context = EasyMock.createMock(TransformationContext.class);
        DataType<Class> dataType = new DataType<Class>(null, SamplePropertyBean.class);
        EasyMock.expect(context.getTargetDataType()).andReturn(dataType).anyTimes();
        EasyMock.replay(context);

        javax.xml.transform.Transformer transformer =
            TransformerFactory.newInstance().newTransformer();
        Object data = new int[]{10, 20, 30, 40};
        Node aNode = aTransformer.transform(data, context);
        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(aNode), new StreamResult(sw));

        System.out.println(sw.toString());
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><int_collection><int>10</int><int>20</int>"
            + "<int>30</int><int>40</int></int_collection>",
            sw.toString());
    }

    public static class SamplePropertyBean {
        private float floatNumber = 50;
        private SamplePropertyBean innerProperty;
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
