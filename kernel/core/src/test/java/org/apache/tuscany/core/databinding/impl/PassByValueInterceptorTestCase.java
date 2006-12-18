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

package org.apache.tuscany.core.databinding.impl;

import java.io.Serializable;

import junit.framework.TestCase;

/**
 * Test case for ByValueInterceptor
 */
public class PassByValueInterceptorTestCase extends TestCase {
    private MySerialiable serialiable = new MySerialiable();
    private String str = "ABC";
    private Integer i = new Integer(1);
    private String[] array = new String[] {"1", "2"};
    private Object[] values;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        values = new Object[] {serialiable, str, i, serialiable, array};
    }

    private static class MySerialiable implements Serializable {
        private static final long serialVersionUID = 7827201707529055310L;
        private final String name = "Serializable";
        private final int age = 100;

        public int getAge() {
            return age;
        }

        public String getName() {
            return name;
        }
    }

    public void testCopy() {
        Object[] copy = new PassByValueInterceptor().copy(values);
        assertTrue(copy[0] instanceof MySerialiable);
        MySerialiable copied = (MySerialiable)copy[0];
        assertNotSame(serialiable, copy[0]);
        assertEquals(serialiable.getName(), copied.getName());
        assertEquals(serialiable.getAge(), copied.getAge());
        assertSame(copy[1], str);
        assertSame(copy[2], i);
        assertSame(copy[0], copy[3]);
    }
}
