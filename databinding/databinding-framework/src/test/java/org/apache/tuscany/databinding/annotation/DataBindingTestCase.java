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
package org.apache.tuscany.databinding.annotation;

import java.lang.reflect.Method;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.api.annotation.DataContext;
import org.apache.tuscany.api.annotation.DataType;

public class DataBindingTestCase extends TestCase {
    @SuppressWarnings("unused")
    public void testDataType() throws Exception {
        Class<Test> testClass = Test.class;
        DataType d = testClass.getAnnotation(DataType.class);
        Assert.assertEquals(d.name(), "sdo");
        Assert.assertEquals(d.context().length, 0);

        Method method = testClass.getMethod("test", new Class[] { Object.class });
        DataType d2 = method.getAnnotation(DataType.class);
        Assert.assertEquals(d2.name(), "jaxb");
        Assert.assertEquals(d2.context()[0].key(), "contextPath");
        Assert.assertEquals(d2.context()[0].value(), "com.example.ipo.jaxb");
    }

    @DataType(name = "sdo")
    private static interface Test {
        @DataType(name = "jaxb", context = { @DataContext(key = "contextPath", value = "com.example.ipo.jaxb") })
        public Object test(Object object);
    }
}
