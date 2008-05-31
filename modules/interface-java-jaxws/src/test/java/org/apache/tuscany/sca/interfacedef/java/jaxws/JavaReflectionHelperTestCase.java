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

package org.apache.tuscany.sca.interfacedef.java.jaxws;

import java.lang.reflect.Field;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @version $Rev$ $Date$
 */
public class JavaReflectionHelperTestCase {
    @Test
    public void testErasure() throws Exception {
        for (Field f : TestGenericClass.class.getDeclaredFields()) {
            Class<?> cls = CodeGenerationHelper.getErasure(f.getGenericType());
            System.out.println(cls.getName());
            Assert.assertSame(f.getType(), cls);
        }
    }

    @Test
    public void testSignature() throws Exception {
        for (Field f : TestGenericClass.class.getDeclaredFields()) {
            String sig = CodeGenerationHelper.getSignature(f.getGenericType());
            System.out.println(sig);
        }
    }
}
