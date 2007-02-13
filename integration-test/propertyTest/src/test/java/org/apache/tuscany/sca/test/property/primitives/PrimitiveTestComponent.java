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
package org.apache.tuscany.sca.test.property.primitives;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Property;
import junit.framework.TestCase;

/**
 * Test component for checking primitive property values.
 *
 * @version $Rev$ $Date$
 */
public class PrimitiveTestComponent extends TestCase {
    @Reference
    public PrimitiveService primitives;

    @Property
    public String expectedImplementation;

    public void testExpectedImplementation() {
        assertEquals(expectedImplementation, primitives.getImplementationName());
    }

    public void testBoolean() {
        assertEquals(PrimitiveService.BOOLEAN_VALUE, primitives.isBooleanValue());
    }

    public void testByte() {
        assertEquals(PrimitiveService.BYTE_VALUE, primitives.getByteValue());
    }

    public void testShort() {
        assertEquals(PrimitiveService.SHORT_VALUE, primitives.getShortValue());
    }

    public void testInt() {
        assertEquals(PrimitiveService.INT_VALUE, primitives.getIntValue());
    }

    public void testLong() {
        assertEquals(PrimitiveService.LONG_VALUE, primitives.getLongValue());
    }

    public void testFloat() {
        assertEquals(PrimitiveService.FLOAT_VALUE, primitives.getFloatValue());
    }

    public void testDouble() {
        assertEquals(PrimitiveService.DOUBLE_VALUE, primitives.getDoubleValue());
    }
}
