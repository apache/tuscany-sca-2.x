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

package org.apache.tuscany.spi.databinding.extension;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;

import junit.framework.TestCase;

import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.idl.TypeInfo;
import org.apache.tuscany.spi.model.DataType;
import org.easymock.EasyMock;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * 
 */
public class XMLTypeMapperExtensionTestCase extends TestCase {
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testFieldSettings() throws Exception {
        
        XMLTypeMapperExtension extension = new XMLTypeMapperExtension();
        
        String samplePropertyXML = "<property name=\"prop2\" >" 
                                            + "<integerNumber>27</integerNumber>" 
                                            + "<floatNumber>79.34</floatNumber>" 
                                            + "<doubleNumber>184.52</doubleNumber>" 
                                            + "<innerProperty>" 
                                            + "<integerNumber>54</integerNumber>" 
                                            + "<floatNumber>158.68</floatNumber>" 
                                            + "<doubleNumber>369.04</doubleNumber>" 
                                            + "</innerProperty>" 
                                            + "<stringArray>TestString_1</stringArray>" 
                                            + "<stringArray>TestString_2</stringArray>" 
                                            + "<boolArray>true</boolArray>" 
                                            + "<boolArray>false</boolArray>" 
                                            + "</property>";
        
        DocumentBuilder builder = DOMHelper.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(samplePropertyXML));
        Node samplePropertyNode =  builder.parse(inputSource);
        TypeInfo typeInfo = new TypeInfo(null, false, null);
        
        TransformationContext context = EasyMock.createMock(TransformationContext.class);
        DataType<Class> dataType = new DataType<Class>(null, SamplePropertyBean.class);
        EasyMock.expect(context.getTargetDataType()).andReturn(dataType).anyTimes();
        EasyMock.replay(context);
        
        Object javaObject = extension.toJavaObject(typeInfo, samplePropertyNode, context);
        assertTrue(javaObject instanceof SamplePropertyBean);
        
        SamplePropertyBean samplePropBean = (SamplePropertyBean)javaObject;
        assertEquals(samplePropBean.getIntegerNumber(), 27);
        assertEquals((float)79.34, (float)samplePropBean.getFloatNumber());
        assertEquals((double)samplePropBean.getInnerProperty().getDoubleNumber(), (double)369.04);
        
        assertEquals(samplePropBean.getStringArray()[0], "TestString_1");
        assertEquals(samplePropBean.boolArray[0], true);
    }
    
    
    public static class SamplePropertyBean  {
        
        public float floatNumber = 50;
        public double doubleNumber = 75;
        public boolean[] boolArray = null;
        protected int integerNumber = 25;
        private String[] stringArray;
        
        SamplePropertyBean innerProperty;
        
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
