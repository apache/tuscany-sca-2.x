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
package org.apache.tuscany.host.util;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class LaunchHelperTestCase extends TestCase {
    private TestBean instance;

    public void testSetPrimitive() {
        // fixme this does not work yet
//        LaunchHelper.setProperty(instance, "int", 12);
//        assertEquals(12, instance.anInt);
    }

    public void testSetObject() {
        LaunchHelper.setProperty(instance, "string", "Hello");
        Assert.assertEquals("Hello", instance.aString);
    }

    protected void setUp() throws Exception {
        super.setUp();
        instance = new TestBean();
    }

    private static class TestBean {
        private int anInt;
        private String aString;

        public void setInt(int anInt) {
            this.anInt = anInt;
        }

        public void setString(String aString) {
            this.aString = aString;
        }
    }
}
