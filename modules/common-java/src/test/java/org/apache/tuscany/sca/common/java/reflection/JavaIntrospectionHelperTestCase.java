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

package org.apache.tuscany.sca.common.java.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import org.apache.tuscany.sca.core.DefaultExtensionPointRegistry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */
public class JavaIntrospectionHelperTestCase {
    private static JavaIntrospectionHelper helper;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        helper = JavaIntrospectionHelper.getInstance(new DefaultExtensionPointRegistry());
    }

    @Test
    public void testGetMethods() {
        Set<Method> methods = helper.getAllNonStaticMethods(SubTestImpl.class);
        System.out.println(methods);
    }

    @Test
    public void testGetFields() {
        Set<Field> fields = helper.getAllNonStaticOrFinalFields(SubTestImpl.class);
        System.out.println(fields);
    }

    @Test
    public void testGetAllInterfaces() {
        Set<Class<?>> interfaces = helper.getAllInterfaces(SubTestImpl.class);
        System.out.println(interfaces);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        helper = null;
    }

}
