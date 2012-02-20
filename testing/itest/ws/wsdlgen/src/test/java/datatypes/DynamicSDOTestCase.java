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
public class DynamicSDOTestCase extends BaseFramework {

    @Test
    public void testGetMessage() throws Exception {
        assertEquals("xs:base64Binary", returnType("getMessage"));
    }

    @Test
    public void testGetMessageList() throws Exception {
        Element retElement = returnElement("getMessageList");
        assertEquals("xs:base64Binary", retElement.getAttribute("type"));
        assertEquals("unbounded", retElement.getAttribute("maxOccurs"));
    }

    @Test
    public void testGetMessageSDO() throws Exception {
        assertEquals("xs:anyType", returnType("getMessageSDO"));
    }

    @Test
    public void testGetMessageListSDOList() throws Exception {
        Element retElement = returnElement("getMessageListSDOList");
        assertEquals("xs:anyType", retElement.getAttribute("type"));
        assertEquals("unbounded", retElement.getAttribute("maxOccurs"));
    }

    @Test
    public void testGetMessageListSDOArr() throws Exception {
        Element retElement = returnElement("getMessageListSDOArr");
        assertEquals("xs:anyType", retElement.getAttribute("type"));
        assertEquals("unbounded", retElement.getAttribute("maxOccurs"));
    }

    @Test
    public void testGetMessageListSDOinSDO() throws Exception {
        assertEquals("xs:anyType", returnType("getMessageListSDOinSDO"));
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        BaseFramework.start("GetDataServiceWithoutException");
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        BaseFramework.stop();
    }
}
