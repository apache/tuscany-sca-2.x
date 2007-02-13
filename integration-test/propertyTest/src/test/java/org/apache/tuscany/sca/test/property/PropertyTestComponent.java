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

package org.apache.tuscany.sca.test.property;

import junit.framework.TestCase;
import org.osoa.sca.annotations.Reference;

public class PropertyTestComponent extends TestCase {
    @Reference
    public ABComponent abService;
    @Reference
    public CDComponent cdService;
    @Reference
    public PropertyComponent propertyService;


    public void testA() {
        assertEquals("a", abService.getA());
    }

    public void testB() {
        assertEquals("b", abService.getB());
    }

    public void testC() {
        assertEquals("c", cdService.getC());
    }

    public void testC2() {
        assertEquals("c", cdService.getC2());
    }

    public void testD() {
        assertEquals("d", cdService.getD());
    }

    public void testF() {
        assertEquals("a", abService.getF());
    }

    public void testZ() {
        assertEquals("z", abService.getZ());
    }


    public void testIntValue() {
        assertEquals(1, abService.getIntValue());
    }

    public void testDefaultValue() {
        assertEquals(1, abService.getIntValue());
    }

    public void testDefaultValueOverride() {
        assertEquals(1, cdService.getOverrideValue());
    }

    public void testNoSource() {
        assertEquals("aValue", cdService.getNoSource());
    }

    public void testFileProperty() {
        assertEquals("fileValue", cdService.getFileProperty());
    }

    public void testDefaultProperty() {
        assertEquals("RTP", propertyService.getLocation());
        assertEquals("2006", propertyService.getYear());

    }

    public void testComplexProperty() {
        ComplexPropertyBean propBean = propertyService.getComplexPropertyOne();
        assertNotNull(propBean);
        assertEquals("TestString_1", propBean.getStringArray()[0]);
        assertEquals(2, propBean.numberSetArray[1].integerNumber);

        propBean = propertyService.getComplexPropertyTwo();
        assertNotNull(propBean);
        assertEquals(10, propBean.intArray[0]);
        assertEquals((float) 22, propBean.numberSetArray[1].floatNumber);
    }
}
