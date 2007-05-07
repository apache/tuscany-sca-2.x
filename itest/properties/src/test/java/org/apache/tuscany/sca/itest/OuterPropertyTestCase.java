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

package org.apache.tuscany.sca.itest;

import static junit.framework.Assert.assertEquals;

import java.util.Iterator;

import org.apache.tuscany.host.embedded.SCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class OuterPropertyTestCase {

    private static SCADomain domain;
    private static ABComponent outerABService;
    
    @BeforeClass
    public static void init() throws Exception {
        domain = SCADomain.newInstance("OuterPropertyTest.composite");
        outerABService = domain.getService(ABComponent.class, "OuterComponent");
    }

    @AfterClass
    public static void destroy() throws Exception {
        domain.close();
    }
    
    @Test
    public void testOverridenA() {
        assertEquals("Overriden A", outerABService.getA());
    }

    @Test
    public void testOverridenB() {
        assertEquals("Overriden B", outerABService.getB());
    }
    
    @Test
    public void testOverridenF() {
        assertEquals("Overriden A", outerABService.getF());
    }

    @Test
    public void testOverridenZ() {
        assertEquals("Overriden Z", outerABService.getZ());
    }

    @Test
    public void testOverridenIntValue() {
        assertEquals(125, outerABService.getIntValue());
    }

    @Test
    public void testDefaultValue() {
        assertEquals(125, outerABService.getIntValue());
    }
    
    @Test
    public void testManySimpleStringValues() {
        Iterator<String> iterator = outerABService.getManyStringValues().iterator();
        assertEquals("Apache", iterator.next());
        assertEquals("Tuscany", iterator.next());
        assertEquals("Java SCA", iterator.next());
    }

    @Test
    public void testManySimpleIntegerValues() {
        Iterator<Integer> iterator = outerABService.getManyIntegers().iterator();
        assertEquals(123, iterator.next().intValue());
        assertEquals(456, iterator.next().intValue());
        assertEquals(789, iterator.next().intValue());
    }
}
