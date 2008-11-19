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

package org.apache.tuscany.sca.binding.corba.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.apache.tuscany.sca.binding.corba.impl.util.MethodFinder;
import org.junit.Test;

/**
 * @version $Rev$ $Date$
 * Various tests for MethodFinder
 */
public class MethodFinderTestCase {

    /**
     * Tests finding existing methods
     */
    @Test
    public void test_findExistingMethod() {
        try {
            String methodName = "rotateLeft";
            Method sample = Integer.class.getMethod(methodName, new Class<?>[] {int.class, int.class});
            Method m1 = MethodFinder.findMethod(Integer.class, methodName, new Class<?>[] {int.class, int.class});
            Method m2 =
                MethodFinder.findMethod(Integer.class, methodName, new Class<?>[] {Integer.class, Integer.class});
            assertEquals(sample, m1);
            assertEquals(sample, m2);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests null result value for not existing method
     */
    @Test
    public void test_findNotExistingMethod() {
        try {
            String methodName = "rotateLeft";
            Method method = MethodFinder.findMethod(Integer.class, methodName, new Class<?>[] {});
            assertEquals(null, method);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

}
