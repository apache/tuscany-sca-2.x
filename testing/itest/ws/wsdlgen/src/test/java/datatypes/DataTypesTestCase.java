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

package datatypes;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Element;
import static org.junit.Assert.assertEquals;

/**
 * Test ?wsdl works and that the returned WSDL is correct
 *
 * @version $Rev: 814373 $ $Date: 2009-09-13 19:06:29 +0100 (Sun, 13 Sep 2009) $
 */
@Ignore("In the process of porting from 1.x")
public class DataTypesTestCase extends BaseFramework {

    @Test
    public void testSimpleInt() throws Exception {
        assertEquals("xs:int", parameterType("testSimpleInt"));
    }

    @Test
    public void testSimpleArrayInt() throws Exception {
        Element paramElement = parameterElement("testSimpleArrayInt");
        assertEquals("xs:int", paramElement.getAttribute("type"));
        assertEquals("unbounded", paramElement.getAttribute("maxOccurs"));
    }

    @Test
    public void testSimpleMultiArrayInt() throws Exception {
        Element paramElement = parameterElement("testSimpleMultiArrayInt");
        // prefix name can vary, so compare only the fixed parts
        assertEquals("ns", paramElement.getAttribute("type").substring(0, 2));
        assertEquals(":intArray", paramElement.getAttribute("type").substring(3));
        assertEquals("unbounded", paramElement.getAttribute("maxOccurs"));
    }

    @Test
    public void testSimpleMulti3ArrayInt() throws Exception {
        Element paramElement = parameterElement("testSimpleMulti3ArrayInt");
        // prefix name can vary, so compare only the fixed parts
        assertEquals("ns", paramElement.getAttribute("type").substring(0, 2));
        assertEquals(":intArrayArray", paramElement.getAttribute("type").substring(3));
        assertEquals("unbounded", paramElement.getAttribute("maxOccurs"));
    }

    @Test
    public void testList() throws Exception {
        Element paramElement = parameterElement("testList");
        assertEquals("xs:anyType", paramElement.getAttribute("type"));
        assertEquals("unbounded", paramElement.getAttribute("maxOccurs"));
    }

    @Test
    public void testSimpleListString() throws Exception {
        Element paramElement = parameterElement("testSimpleListString");
        assertEquals("xs:string", paramElement.getAttribute("type"));
        assertEquals("unbounded", paramElement.getAttribute("maxOccurs"));
    }

    @Test
    public void testReturnSimpleListString() throws Exception {
        Element retElement = returnElement("testReturnSimpleListString");
        assertEquals("xs:string", retElement.getAttribute("type"));
        assertEquals("unbounded", retElement.getAttribute("maxOccurs"));
    }

    @Test
    public void testListByteArray() throws Exception {
        Element paramElement = parameterElement("testListByteArray");
        assertEquals("xs:base64Binary", paramElement.getAttribute("type"));
        assertEquals("unbounded", paramElement.getAttribute("maxOccurs"));
    }

    @Test
    public void testListWildcard() throws Exception {
        Element paramElement = parameterElement("testListWildcard");
        assertEquals("xs:anyType", paramElement.getAttribute("type"));
        assertEquals("unbounded", paramElement.getAttribute("maxOccurs"));
    }

    @Test
    public void testComplex() throws Exception {
        String paramType = parameterType("testComplex");
        assertEquals("tns:complexNumber", paramType);
        assertEquals("xs:double", firstChild(typeDefinition(paramType)).getAttribute("type"));
    }

    @Test
    public void testOtherPackage() throws Exception {
        String paramType = parameterType("testOtherPackage");
        // prefix name can vary, so compare only the fixed parts
        assertEquals("ns", paramType.substring(0, 2));
        assertEquals(":otherPojo", paramType.substring(3));
        assertEquals("xs:double", firstChild(typeDefinition(paramType)).getAttribute("type"));
    }

    @Test
    public void testByteArray() throws Exception {
        assertEquals("xs:base64Binary", parameterType("testByteArray"));
    }

    @Test
    public void testBaseExtension() throws Exception {
        String paramType = parameterType("testBaseExtension");
        assertEquals("tns:extClass", paramType);
        assertEquals("tns:baseClass", extensionElement(typeDefinition(paramType)).getAttribute("base"));
    }

    @Test
    public void testServiceReference() throws Exception {
        assertEquals("xs:anyType", parameterType("testServiceReference"));
    }

    @Test
    public void testException() throws Exception {
        assertEquals("xs:string", faultType("testException", "Exception"));
    }

    @Test
    public void testDynamicSDO() throws Exception {
        assertEquals("xs:anyType", returnType("testDynamicSDO"));
    }

    @Test
    public void testWebParamSDO() throws Exception {
        Element paramElement = parameterElement("testWebParamSDO");
        assertEquals("foo", paramElement.getAttribute("name"));
        assertEquals("xs:anyType", paramElement.getAttribute("type"));
    }

    @Test
    public void testWebParamSDOArray() throws Exception {
        Element paramElement = parameterElement("testWebParamSDOArray");
        assertEquals("foo", paramElement.getAttribute("name"));
        assertEquals("xs:anyType", paramElement.getAttribute("type"));
        assertEquals("unbounded", paramElement.getAttribute("maxOccurs"));
    }
/*

    @Test
    public void testWebParamBare() throws Exception {
        Element paramElement = parameterElement("testWebParamBare");
        assertEquals("simpleInt", paramElement.getAttribute("name"));
        assertEquals("xs:int", paramElement.getAttribute("type"));
    }

    @Test
    @Ignore
    public void testWebParamBareArray() throws Exception {
        Element paramElement = parameterElement("testWebParamBareArray");
        assertEquals("arrayInt", paramElement.getAttribute("name"));
        assertEquals("xs:int", paramElement.getAttribute("type"));
        assertEquals("unbounded", paramElement.getAttribute("maxOccurs"));
    }
*/

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        BaseFramework.start("DataTypes");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        BaseFramework.stop();
    }
}
