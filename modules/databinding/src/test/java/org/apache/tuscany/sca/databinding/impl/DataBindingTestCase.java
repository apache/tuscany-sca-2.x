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
package org.apache.tuscany.sca.databinding.impl;

import java.lang.reflect.Method;

import org.apache.tuscany.sca.databinding.annotation.DataBinding;

import junit.framework.Assert;
import junit.framework.TestCase;

public class DataBindingTestCase extends TestCase {
    @SuppressWarnings("unused")
    public void testDataType() throws Exception {
        Class<Test> testClass = Test.class;
        DataBinding d = testClass.getAnnotation(DataBinding.class);
        Assert.assertEquals(d.value(), "sdo");

        Method method = testClass.getMethod("test", new Class[] {Object.class});
        DataBinding d2 = method.getAnnotation(DataBinding.class);
        Assert.assertEquals(d2.value(), "jaxb");
    }

    @DataBinding("sdo")
    private static interface Test {
        @DataBinding("jaxb")
        Object test(Object object);
    }
}
