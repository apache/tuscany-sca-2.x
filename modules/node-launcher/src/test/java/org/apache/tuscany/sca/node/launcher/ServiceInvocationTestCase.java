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

package org.apache.tuscany.sca.node.launcher;

import java.lang.reflect.InvocationTargetException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */
public class ServiceInvocationTestCase {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    public float add(float x, float y) {
        return x + y;
    }

    @Test
    public void testInvoke() throws Exception {
        String service = "component1/service1#add(1.0, 2.0)";
        Object proxy = this;
        invoke(service, proxy);
    }

    private Object invoke(String service, Object proxy) throws IllegalAccessException, InvocationTargetException {
        String regex = "(#|\\(|,|\\))";
        if (service != null) {
            // componentName/serviceName/bindingName#methodName(arg0, ..., agrN)
            String tokens[] = service.split(regex);
            String serviceName = tokens[0];
            Assert.assertEquals("component1/service1", serviceName);
            String operationName = tokens[1];
            Assert.assertEquals("add", operationName);
            String params[] = new String[tokens.length - 2];
            System.arraycopy(tokens, 2, params, 0, params.length);
            Object result = NodeLauncher.invoke(proxy, operationName, params);
            Assert.assertEquals(new Float(3.0f), result);
            return result;
        }
        return null;
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

}
